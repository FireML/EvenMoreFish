package com.oheers.fish.gui.guis;

import com.oheers.fish.config.GuiConfig;
import com.oheers.fish.gui.ConfigGui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MainMenuGui extends ConfigGui {

    public MainMenuGui(@NotNull HumanEntity viewer) {
        super(
            GuiConfig.getInstance().getConfig().getSection("main-menu"),
            (Player) viewer
        );
    }

    @Override
    public @NotNull Map<String, ?> getReplacements() {
        return Map.of();
    }

}
