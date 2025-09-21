package com.oheers.fish.gui.guis;

import com.oheers.fish.config.GuiConfig;
import com.oheers.fish.gui.ConfigGuiOld;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class MainMenuGui extends ConfigGuiOld {

    public MainMenuGui(@NotNull HumanEntity viewer) {
        super(
            GuiConfig.getInstance().getConfig().getSection("main-menu"),
            viewer
        );
        createGui();
    }

}
