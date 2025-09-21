package com.oheers.fish.gui;

import com.oheers.fish.FishUtils;
import com.oheers.fish.config.GuiFillerConfig;
import com.oheers.fish.items.ItemFactory;
import com.oheers.fish.messages.EMFSingleMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import com.oheers.fish.utils.ItemUtils;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiStorageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.BaseGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class ConfigGuiOld<T extends BaseGui> {

    protected final Section config;
    protected final Player player;
    private final @NotNull Map<String, EMFMessage> replacements = new HashMap<>();

    private T gui;

    // TODO Bring the action map to this class when we switch to Triumph
    public ConfigGuiOld(@Nullable Section config, @NotNull HumanEntity player) {
        this.config = config;
        // HumanEntity's only subclass is Player, so this is a safe cast
        this.player = (Player) player;
        this.gui = createGui();
    }

    public void addReplacement(@NotNull String variable, @NotNull String replacement) {
        this.replacements.put(variable, EMFSingleMessage.fromString(replacement));
    }

    public void addReplacement(@NotNull String variable, @NotNull Component replacement) {
        this.replacements.put(variable, EMFSingleMessage.of(replacement));
    }

    public void addReplacement(@NotNull String variable, @NotNull EMFSingleMessage replacement) {
        this.replacements.put(variable, replacement);
    }

    public void addReplacements(@NotNull Map<String, EMFSingleMessage> replacements) {
        this.replacements.putAll(replacements);
    }

    public @NotNull T getGui() {
        if (this.gui == null) {
            throw new IllegalStateException(getClass().getSimpleName() + "#createGui has not been called!");
        }
        return this.gui;
    }

    public Section getGuiConfig() {
        return config;
    }

    public void open() {
        getGui().open(this.player);
    }

    public abstract T createGui();

    protected void loadFiller(@NotNull InventoryGui gui, @NotNull Section config) {
        String fillerStr = config.getString("filler");
        if (fillerStr == null) {
            return;
        }
        Material filler = ItemUtils.getMaterial(fillerStr);
        if (filler == null) {
            return;
        }
        ItemStack item = new ItemStack(filler);
        item.editMeta(meta -> meta.displayName(Component.empty()));
        gui.setFiller(item);
        gui.addElements(GuiFillerConfig.getInstance().getDefaultFillerItems(this));
    }

    private void loadItems(@NotNull InventoryGui gui, @NotNull Section config) {
        // This needs to be done first so individual configs can override them
        gui.addElements(
            GuiUtils.getFirstPageButton(),
            GuiUtils.getPreviousPageButton(),
            GuiUtils.getNextPageButton(),
            GuiUtils.getLastPageButton()
        );
        config.getRoutesAsStrings(false).forEach(key -> {
            Section itemSection = config.getSection(key);
            if (itemSection == null || !itemSection.contains("item")) {
                return;
            }
            addGuiItem(gui, itemSection);
        });
    }

    protected void addGuiItem(@NotNull InventoryGui gui, @NotNull Section itemSection) {
        char character = FishUtils.getCharFromString(itemSection.getString("character", "#"), '#');
        if (character == '#') {
            return;
        }
        ItemFactory factory = ItemFactory.itemFactory(itemSection);
        ItemStack item = factory.createItem(this.player.getUniqueId(), this.replacements);
        if (item.getType() == Material.AIR) {
            return;
        }
        Section actionSection = itemSection.getSection("click-action");
        if (actionSection != null) {
            StaticGuiElement actionElement = new StaticGuiElement(character, item, click -> {
                BiConsumer<ConfigGuiOld, GuiElement.Click> action = switch (click.getType()) {
                    case LEFT -> actions.get(actionSection.getString("left", ""));
                    case RIGHT -> actions.get(actionSection.getString("right", ""));
                    case MIDDLE -> actions.get(actionSection.getString("middle", ""));
                    case DROP -> actions.get(actionSection.getString("drop", ""));
                    default -> null;
                };
                if (action != null) {
                    action.accept(this, click);
                }
                itemSection.getStringList("click-commands").forEach(command ->
                    Bukkit.dispatchCommand(click.getWhoClicked(), command)
                );
                return true;
            });
            gui.addElement(actionElement);
        } else {
            StaticGuiElement element = new StaticGuiElement(character, item, click -> {
                BiConsumer<ConfigGuiOld, GuiElement.Click> action = actions.get(itemSection.getString("click-action", ""));
                if (action != null) {
                    action.accept(this, click);
                }
                itemSection.getStringList("click-commands").forEach(command ->
                    Bukkit.dispatchCommand(click.getWhoClicked(), command)
                );
                return true;
            });
            gui.addElement(element);
        }
    }

    public void doRescue() {
        gui.getElements().forEach(element -> {
            if (!(element instanceof GuiStorageElement storageElement)) {
                return;
            }
            Inventory inv = storageElement.getStorage();
            if (inv.isEmpty()) {
                return;
            }
            FishUtils.giveItems(inv.getStorageContents(), player);
            inv.clear();
        });
    }

}
