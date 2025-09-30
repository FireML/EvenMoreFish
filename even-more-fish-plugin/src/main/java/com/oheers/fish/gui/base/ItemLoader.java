package com.oheers.fish.gui.base;

import com.oheers.fish.FishUtils;
import com.oheers.fish.api.Logging;
import com.oheers.fish.items.ItemFactory;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.StaticGuiElement;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.components.exception.GuiException;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemLoader {

    private static final ItemLoader instance = new ItemLoader();

    public static ItemLoader getInstance() {
        return instance;
    }

    public void load(BaseConfigGui<?> gui) {
        Section itemSection = gui.config.getSection("items");
        if (itemSection == null) {
            return;
        }
        itemSection.getRoutesAsStrings(false).forEach(key -> {
            Section section = itemSection.getSection(key);
            if (section == null) {
                return;
            }
            addGuiItem(gui, section);
        });
    }

    private void addGuiItem(@NotNull BaseConfigGui<?> gui, @NotNull Section itemSection) {
        ItemStack item = ItemFactory.itemFactory(itemSection).createItem();
        if (item.isEmpty()) {
            return;
        }
        GuiItem guiItem;

        // We could possibly clean this up?
        Section actionSection = itemSection.getSection("click-action");
        if (actionSection != null) {
            guiItem = new GuiItem(item, event -> {
                Consumer<InventoryClickEvent> action = switch (event.getClick()) {
                    case LEFT -> gui.actions.get(actionSection.getString("left", ""));
                    case RIGHT -> gui.actions.get(actionSection.getString("right", ""));
                    case MIDDLE -> gui.actions.get(actionSection.getString("middle", ""));
                    case DROP -> gui.actions.get(actionSection.getString("drop", ""));
                    default -> null;
                };
                if (action != null) {
                    action.accept(event);
                }
                itemSection.getStringList("click-commands").forEach(command ->
                    Bukkit.dispatchCommand(event.getWhoClicked(), command)
                );
            });
        } else {
            guiItem = new GuiItem(item, event -> {
                Consumer<InventoryClickEvent> action = gui.actions.get(itemSection.getString("click-action", ""));
                if (action != null) {
                    action.accept(event);
                }
                itemSection.getStringList("click-commands").forEach(command ->
                    Bukkit.dispatchCommand(event.getWhoClicked(), command)
                );
            });
        }

        // Put the item in all of its configured locations
        itemSection.getStringList("locations").forEach(location -> {
            String[] splitLocation = location.split(",", 2);
            String rowStr = FishUtils.getOrDefault(splitLocation, 0, null);
            String columnStr = FishUtils.getOrDefault(splitLocation, 1, null);
            int column = FishUtils.getIntOrDefault(columnStr, -1);
            int row = FishUtils.getIntOrDefault(rowStr, -1);
            try {
                gui.getGui().setItem(row, column, guiItem);
            } catch (GuiException exception) {
                Logging.error("Invalid location provided in GUI config: " + location + ". Skipping...");
            }
        });
    }

    private void handleClickEvent(@NotNull InventoryClickEvent event, @Nullable Consumer<InventoryClickEvent> action) {
        if (action == null) {
            event.setCancelled(true);
            return;
        }
        action.accept(event);
    }

}
