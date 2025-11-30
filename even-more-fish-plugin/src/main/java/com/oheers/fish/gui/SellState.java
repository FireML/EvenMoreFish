package com.oheers.fish.gui;

import com.oheers.fish.api.config.ConfigBase;
import com.oheers.fish.gui.config.GuiConfig;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum SellState {

    NORMAL(GuiConfig.getInstance()::getSellNormal),
    CONFIRM(GuiConfig.getInstance()::getSellConfirm);

    private final Supplier<ConfigBase> configSupplier;

    SellState(@NotNull Supplier<ConfigBase> configSupplier) {
        this.configSupplier = configSupplier;
    }

    public ConfigBase getGuiConfig() {
        return configSupplier.get();
    }

}
