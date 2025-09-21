package com.oheers.fish.gui;

import com.oheers.fish.gui.base.BaseConfigGui;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class PaginatedConfigGui extends BaseConfigGui<PaginatedGui> {

    public PaginatedConfigGui(@NotNull HumanEntity player, @NotNull Section config) {
        super(player, config);
    }

    @Override
    public PaginatedGui createGui() {
        return Gui.paginated().create();
    }

}
