package com.oheers.fish.gui;

import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.fishing.items.config.RarityConversions;
import com.oheers.fish.gui.guis.ApplyBaitsGui;
import com.oheers.fish.gui.guis.BaitsGui;
import com.oheers.fish.gui.guis.FishJournalGui;
import com.oheers.fish.gui.guis.MainMenuGui;
import com.oheers.fish.gui.guis.SellGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiManager {

    private static final GuiManager instance = new GuiManager();

    public static GuiManager getInstance() {
        return instance;
    }

    public void openApplyBaitsMenu(@NotNull Player player) {
        new ApplyBaitsGui(player).open();
    }

    public void openBaitsMenu(@NotNull Player player) {
        new BaitsGui(player).open();
    }

    public void openMainMenu(@NotNull Player player) {
        new MainMenuGui(player).open();
    }

    public void openJournalMenu(@NotNull Player player) {
        new FishJournalGui(player, null).open();
    }

    public void openJournalMenu(@NotNull Player player, @Nullable Rarity rarity) {
        new FishJournalGui(player, rarity).open();
    }

    public void openSellMenu(@NotNull Player player) {
        new SellGui(player, SellGui.SellState.NORMAL, null).open();
    }

    public void openSellMenu(@NotNull Player player, @NotNull SellGui.SellState state) {
        new SellGui(player, state, null).open();
    }

    public void openSellMenu(@NotNull Player player, @NotNull SellGui.SellState state, @Nullable Inventory inventory) {
        new SellGui(player, state, inventory).open();
    }

}
