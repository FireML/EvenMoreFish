package com.oheers.fish.gui.types;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.commands.MainCommand;
import com.oheers.fish.database.DatabaseUtil;
import com.oheers.fish.exceptions.InvalidGuiException;
import com.oheers.fish.gui.base.BaseConfigGui;
import com.oheers.fish.gui.guis.FishJournalGui;
import com.oheers.fish.gui.guis.MainMenuGui;
import com.oheers.fish.gui.guis.SellGui;
import com.oheers.fish.messages.ConfigMessage;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicConfigGui extends BaseConfigGui<Gui> {

    public BasicConfigGui(@NotNull HumanEntity player, @Nullable Section config) throws InvalidGuiException {
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
