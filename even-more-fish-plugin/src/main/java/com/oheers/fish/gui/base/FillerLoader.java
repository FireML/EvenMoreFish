package com.oheers.fish.gui.base;

import com.oheers.fish.FishUtils;
import com.oheers.fish.api.Logging;
import com.oheers.fish.gui.FillerType;
import dev.dejvokep.boostedyaml.block.implementation.Section;
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

        // Prepare Enum
        FillerType fillerType = FishUtils.getEnumValue(
            FillerType.class,
            fillerSection.getString("type")
        );
        if (fillerType == null) {
            return;
        }

        // Prepare the filler item
        ItemStack fillerItem = FishUtils.getItem(fillerSection.getString("material"));
        if (fillerItem == null || fillerItem.isEmpty()) {
            return;
        }
        fillerItem.editMeta(meta -> meta.displayName(Component.empty()));

        // Handle filler
        GuiItem item = new GuiItem(fillerItem);
        BaseGui underlying = gui.getGui();
        GuiFiller filler = underlying.getFiller();

        switch (fillerType) {
            case ALL -> {
                if (underlying instanceof PaginatedGui) {
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
