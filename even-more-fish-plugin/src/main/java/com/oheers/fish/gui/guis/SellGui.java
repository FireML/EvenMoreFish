package com.oheers.fish.gui.guis;

import com.oheers.fish.FishUtils;
import com.oheers.fish.api.economy.Economy;
import com.oheers.fish.config.GuiConfig;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.gui.ConfigGui;
import com.oheers.fish.selling.SellHelper;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

// TODO look into dynamically updating the sell items when a fish is added/removed - AFTER we switch to TriumphGui
public class SellGui extends ConfigGui {

    private final Inventory fishInventory;

    public SellGui(@NotNull Player player, @NotNull SellState sellState, @Nullable Inventory fishInventory) {
        super(sellState.getGuiConfig(), player);
        this.fishInventory = Objects.requireNonNullElseGet(fishInventory, () -> Bukkit.createInventory(player, 54));

        actions.put("sell-inventory", event -> new SellGui(player, SellState.CONFIRM, this.fishInventory));
        actions.put("sell-inventory-confirm", event -> {
            new SellHelper(event.getInventory(), player).sellFish();
            event.getWhoClicked().closeInventory();
        });
        actions.put("sell-shop", event -> new SellGui(player, SellState.CONFIRM, this.fishInventory).open());
        actions.put("sell-shop-confirm", event -> {
            new SellHelper(this.fishInventory, player).sellFish();
            event.getWhoClicked().closeInventory();
        });

        Economy economy = Economy.getInstance();

        SellHelper shopHelper = new SellHelper(this.fishInventory, player);
        SellHelper playerHelper = new SellHelper(player.getInventory(), player);

        createGui(Map.of(
            "{sell-price}", economy.getWorthFormat(shopHelper.getTotalWorth(), true),
            "{sell-all-price}", economy.getWorthFormat(playerHelper.getTotalWorth(), true)
        ));

        gui.setCloseGuiAction(event -> {
            if (MainConfig.getInstance().sellOverDrop()) {
                new SellHelper(this.fishInventory, this.player).sellFish();
            }
            FishUtils.giveItems(event.getInventory().getStorageContents(), player);
        });
    }

    public Inventory getFishInventory() {
        return this.fishInventory;
    }

    public enum SellState {
        NORMAL("sell-menu-normal"),
        CONFIRM("sell-menu-confirm");

        private final String configLocation;

        SellState(@NotNull String configLocation) {
            this.configLocation = configLocation;
        }

        public Section getGuiConfig() {
            return GuiConfig.getInstance().getConfig().getSection(configLocation);
        }
    }

}
