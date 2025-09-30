package com.oheers.fish.gui.base;

import com.oheers.fish.FishUtils;
import com.oheers.fish.api.Logging;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.components.exception.GuiException;
import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FillerLoader {

    private static final FillerLoader instance = new FillerLoader();

    public static FillerLoader getInstance() {
        return instance;
    }

    public void load(BaseConfigGui<?> gui) throws IllegalStateException {
        Section fillerSection = gui.config.getSection("filler");
        if (fillerSection == null) {
            return;
        }

        // Prepare the filler item
        ItemStack fillerItem = FishUtils.getItem(fillerSection.getString("material"));
        if (fillerItem == null || fillerItem.isEmpty()) {
            return;
        }
        fillerItem.editMeta(meta -> meta.displayName(Component.empty()));

        GuiItem item = new GuiItem(fillerItem);

        // Put in pre-configured locations (these ignore FillerType so we can do it before)
        fillerSection.getStringList("locations").forEach(location -> {
            String[] splitLocation = location.split(",", 2);
            String rowStr = FishUtils.getOrDefault(splitLocation, 0, null);
            String columnStr = FishUtils.getOrDefault(splitLocation, 1, null);
            int column = FishUtils.getIntOrDefault(columnStr, -1);
            int row = FishUtils.getIntOrDefault(rowStr, -1);
            try {
                gui.getGui().setItem(row, column, item);
            } catch (GuiException exception) {
                Logging.error("Invalid location provided in GUI filler config: " + location + ". Skipping...");
            }
        });
    }

}
