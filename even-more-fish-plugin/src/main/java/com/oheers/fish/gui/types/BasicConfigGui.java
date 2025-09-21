package com.oheers.fish.gui.types;

import com.oheers.fish.exceptions.InvalidGuiException;
import com.oheers.fish.gui.base.BaseConfigGui;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicConfigGui extends BaseConfigGui<Gui> {

    public BasicConfigGui(@NotNull Player player, @Nullable Section config) throws InvalidGuiException {
        super(player, config);
    }

    @Override
    public Gui createGui() {
        return Gui.gui()
            .disableAllInteractions()
            .title(getTitle().getComponentMessage())
            .rows(getRows())
            .type(getGuiType())
            .create();
    }

    @Override
    public void doRescue() {}

}
