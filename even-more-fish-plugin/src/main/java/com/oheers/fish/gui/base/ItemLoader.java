package com.oheers.fish.gui.base;

import com.oheers.fish.FishUtils;
import com.oheers.fish.items.ItemFactory;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemLoader {

    private static final ItemLoader instance = new ItemLoader();

    public static ItemLoader getInstance() {
        return instance;
    }

    public void loadItems(BaseConfigGui<?> gui) {
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
        Section actionSection = itemSection.getSection("click-actions");
        if (actionSection != null) {
            guiItem = new GuiItem(item, event -> {
                Consumer<InventoryClickEvent> action = switch (event.getClick()) {
                    case LEFT -> gui.actions.get(actionSection.getString("left", ""));
                    case RIGHT -> gui.actions.get(actionSection.getString("right", ""));
                    case MIDDLE -> gui.actions.get(actionSection.getString("middle", ""));
                    case DROP -> gui.actions.get(actionSection.getString("drop", ""));
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
        itemSection.getStringList("locations").forEach(location -> {
            String[] splitLocation = location.split(",", 2);
            String columnStr = FishUtils.getOrDefault(splitLocation, 0, null);
            String rowStr = FishUtils.getOrDefault(splitLocation, 1, null);
            int column = FishUtils.getIntOrDefault(columnStr, -1);
            int row = FishUtils.getIntOrDefault(rowStr, -1);
            gui.getGui().setItem(row, column, guiItem);
        });
    }

}
