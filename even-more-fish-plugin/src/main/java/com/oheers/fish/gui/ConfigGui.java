package com.oheers.fish.gui;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.commands.MainCommand;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.gui.guis.BaitsGui;
import com.oheers.fish.gui.guis.FishJournalGui;
import com.oheers.fish.gui.guis.MainMenuGui;
import com.oheers.fish.gui.guis.SellGui;
import com.oheers.fish.items.ItemFactory;
import com.oheers.fish.messages.EMFSingleMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import com.oheers.fish.utils.ItemUtils;
import com.oheers.fish.utils.Logging;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

// TODO figure out dynamically updating icons
public class ConfigGui {

    protected final TreeMap<String, Consumer<InventoryClickEvent>> actions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    protected final Section config;
    protected final Player player;

    protected BaseGui gui;

    public ConfigGui(@Nullable Section config, @NotNull Player player) {
        actions.put("close", event -> event.getWhoClicked().closeInventory());
        actions.put("full-exit", event -> event.getWhoClicked().closeInventory());
        actions.put("open-main-menu", event -> new MainMenuGui(event.getWhoClicked()).open());
        actions.put("fish-toggle", event -> EvenMoreFish.getInstance().performFishToggle((Player) event.getWhoClicked()));
        actions.put("open-shop", event -> new SellGui((Player) event.getWhoClicked(), SellGui.SellState.NORMAL, null).open());
        actions.put("show-command-help", event -> Bukkit.dispatchCommand(event.getWhoClicked(), "emf help"));
        actions.put("open-baits-menu", event -> new BaitsGui(event.getWhoClicked()).open());
        actions.put("open-journal-menu", event -> {
            if (!MainConfig.getInstance().isDatabaseOnline()) {
                return;
            }
            new FishJournalGui(event.getWhoClicked(), null).open();
        });

        this.config = config;
        this.player = player;

        if (config == null) {
            this.gui = Gui.gui()
                .disableAllInteractions()
                .rows(6)
                .create();
        }
    }

    public BaseGui getGui() {
        if (this.gui == null) {
            this.gui = createGui();
        }
        return this.gui;
    }

    public Section getGuiConfig() {
        return config;
    }

    public void open() {
        getGui().open(this.player);
    }

    public void addActions(@NotNull Map<String, Consumer<InventoryClickEvent>> actions) {
        actions.forEach(this::addAction);
    }

    public void addAction(@NotNull String name, @NotNull Consumer<InventoryClickEvent> action) {
        this.actions.putIfAbsent(name, action);
    }

    // Loading Things

    public BaseGui createGui() {
        return createGui(null);
    }

    protected BaseGui createGui(@Nullable Map<String, ?> replacements) {
        GuiType type = FishUtils.getEnumValue(
            GuiType.class,
            config.getString("type", "CHEST"),
            GuiType.CHEST
        );

        EMFSingleMessage title = EMFSingleMessage.fromString(
            config.getString("title", "Gui")
        );
        title.setVariables(replacements);

        BaseGui gui = Gui.gui(type)
            .disableAllInteractions()
            .title(title.getComponentMessage())
            .rows(config.getInt("rows", 6))
            .create();

        // Load filler
        loadFiller(gui);

        // Load configured items
        loadItems(gui, replacements);

        return gui;
    }

    protected void loadItems(@NotNull BaseGui gui, @Nullable Map<String, ?> replacements) {
        Section itemSection = this.config.getSection("items");
        if (itemSection == null) {
            return;
        }
        itemSection.getRoutesAsStrings(false).forEach(key -> {
            Section section = itemSection.getSection(key);
            if (section == null) {
                return;
            }
            addGuiItem(gui, section, replacements);
        });
    }

    protected void addGuiItem(@NotNull BaseGui gui, @NotNull Section itemSection, @Nullable Map<String, ?> replacements) {
        ItemStack item = ItemFactory.itemFactory(itemSection).createItem(replacements);
        if (item.getType().isEmpty()) {
            return;
        }
        GuiItem guiItem;
        Section actionSection = itemSection.getSection("click-actions");
        if (actionSection != null) {
            guiItem = new GuiItem(item, event -> {
                Consumer<InventoryClickEvent> action = switch (event.getClick()) {
                    case LEFT -> actions.get(actionSection.getString("left", ""));
                    case RIGHT -> actions.get(actionSection.getString("right", ""));
                    case MIDDLE -> actions.get(actionSection.getString("middle", ""));
                    case DROP -> actions.get(actionSection.getString("drop", ""));
                    default -> null;
                };
                if (action == null) {
                    event.setCancelled(true);
                    return;
                }
                action.accept(event);
            });
        } else {
            guiItem = new GuiItem(item, event -> event.setCancelled(true));
        }
        // Put the item in all of its configured locations
        itemSection.getStringList("locations").forEach(locationStr -> {
            Integer slot = FishUtils.getInteger(locationStr);
            if (slot == null) {
                Logging.warn("Invalid slot in GUI item configuration: " + locationStr);
                return;
            }
            gui.setItem(slot, guiItem);
        });
    }

    protected void loadFiller(@NotNull BaseGui gui) {
        Section fillerSection = this.config.getSection("filler");
        if (fillerSection == null) {
            return;
        }

        // Prepare Enum
        FillerType fillerType = FishUtils.getEnumValue(
            FillerType.class,
            fillerSection.getString("type")
        );
        if (fillerType == null) {
            return;
        }

        // Prepare the filler item
        ItemStack fillerItem = new ItemStack(
            ItemUtils.getMaterial(fillerSection.getString("material"), Material.AIR)
        );
        fillerItem.editMeta(meta -> meta.displayName(Component.empty()));

        // Handle filler
        GuiItem item = new GuiItem(fillerItem);
        GuiFiller filler = gui.getFiller();

        switch (fillerType) {
            case ALL -> {
                if (gui instanceof PaginatedGui) {
                    Logging.warn("Paginated GUIs cannot use FillerType.ALL");
                    return;
                }
                filler.fill(item);
            }
            case BORDER -> filler.fillBorder(item);
            case SIDE -> {
                GuiFiller.Side side = FishUtils.getEnumValue(
                    GuiFiller.Side.class,
                    fillerSection.getString("side")
                );
                if (side == null) {
                    return;
                }
                filler.fillSide(side, List.of(item));
            }
            case BETWEEN -> {
                int rowFrom = fillerSection.getInt("between-points.rowFrom", -1);
                int columnFrom = fillerSection.getInt("between-points.columnFrom", -1);
                int rowTo = fillerSection.getInt("between-points.rowTo", -1);
                int columnTo = fillerSection.getInt("between-points.columnTo", -1);
                if (rowFrom == -1 || columnFrom == -1 || rowTo == -1 || columnTo == -1) {
                    return;
                }
                filler.fillBetweenPoints(rowFrom, columnFrom, rowTo, columnTo, item);
            }
        }
    }

}
