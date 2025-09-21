package com.oheers.fish.gui.types;

import com.oheers.fish.gui.base.BaseConfigGui;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaginatedConfigGui extends BaseConfigGui<PaginatedGui> {

    public PaginatedConfigGui(@NotNull Player player, @NotNull Section config) {
        super(player, config);
        actions.put("next-page", event -> getGui().next());
        actions.put("previous-page", event -> getGui().previous());
    }

    @Override
    public PaginatedGui createGui() {
        return Gui.paginated()
            .disableAllInteractions()
            .title(getTitle().getComponentMessage())
            .pageSize(getPageSize())
            .rows(getRows())
            .create();
    }

    @Override
    public void doRescue() {}

}
