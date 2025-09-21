package com.oheers.fish.gui.types;

import com.oheers.fish.gui.base.BaseConfigGui;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.StorageGui;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class DepositConfigGui extends BaseConfigGui<StorageGui> {

    public DepositConfigGui(@NotNull HumanEntity player, @NotNull Section config) {
        super(player, config);
    }

    @Override
    public StorageGui createGui() {
        return Gui.storage()
            .title(getTitle().getComponentMessage())
            .rows(getRows())
            .create();
    }

}
