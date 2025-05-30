package com.oheers.fish.gui;

import com.oheers.fish.messages.EMFSingleMessage;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A version of ConfigGui that supports pages.
 * This GUI allows multiple pages of items to be displayed.
 */
public class PaginatedConfigGui extends ConfigGui {

    public PaginatedConfigGui(@Nullable Section config, @NotNull Player player) {
        super(config, player);
        super.actions.put("next-page", event -> {
            if (getGui() instanceof PaginatedGui pages) {
                pages.next();
            }
            event.setCancelled(true);
        });
        super.actions.put("previous-page", event -> {
            if (getGui() instanceof PaginatedGui pages) {
                pages.previous();
            }
            event.setCancelled(true);
        });
    }

    @Override
    protected BaseGui createGui(@Nullable Map<String, ?> replacements) {
        Section config = getGuiConfig();

        EMFSingleMessage title = EMFSingleMessage.fromString(
            config.getString("title", "Gui")
        );
        title.setVariables(replacements);

        PaginatedGui gui = Gui.paginated()
            .disableAllInteractions()
            .title(title.getComponentMessage())
            .rows(config.getInt("rows", 6))
            .pageSize(config.getInt("page-size", 45))
            .create();

        // Load filler
        loadFiller(gui);

        // Load configured items
        loadItems(gui, replacements);

        return gui;
    }

}
