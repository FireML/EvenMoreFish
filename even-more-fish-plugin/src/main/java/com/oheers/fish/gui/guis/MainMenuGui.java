package com.oheers.fish.gui.guis;

import com.oheers.fish.config.GuiConfig;
import com.oheers.fish.exceptions.InvalidGuiException;
import com.oheers.fish.gui.ConfigGuiOld;
import com.oheers.fish.gui.types.BasicConfigGui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MainMenuGui extends BasicConfigGui {

    public MainMenuGui(@NotNull Player viewer) throws InvalidGuiException {
        super(
            viewer,
            GuiConfig.getInstance().getConfig().getSection("main-menu")
        );
        init();
    }

}
