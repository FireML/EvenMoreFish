package com.oheers.fish.gui.guis;

import com.oheers.fish.gui.config.GuiConfig;
import com.oheers.fish.gui.types.BasicConfigGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MainMenuGui extends BasicConfigGui {

    public MainMenuGui(@NotNull Player viewer) {
        super(
            viewer,
            GuiConfig.getInstance().getMainMenu().getConfig()
        );
        init();
    }

}
