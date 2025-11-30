package com.oheers.fish.gui.guis;

import com.oheers.fish.gui.SellState;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.gui.GuiManager;
import com.oheers.fish.gui.types.DepositConfigGui;
import com.oheers.fish.selling.SellHelper;
import dev.triumphteam.gui.guis.StorageGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

// TODO look into dynamically updating the sell items when a fish is added/removed - AFTER we switch to TriumphGui
public class SellGui extends DepositConfigGui {

    public SellGui(@NotNull Player player, @NotNull SellState sellState, @Nullable Inventory fishInventory) {
        super(
            player,
            sellState.getGuiConfig().getConfig()
        );
        addActions();

        init();
        StorageGui gui = getGui();
        gui.setCloseGuiAction(event -> {
            if (MainConfig.getInstance().sellOverDrop()) {
                new SellHelper(getGui().getInventory(), this.player).sell();
            }
            doRescue();
        });
        if (fishInventory != null) {
            ItemStack[] add = Arrays.stream(fishInventory.getContents())
                .filter(Objects::nonNull)
                .toArray(ItemStack[]::new);
            gui.getInventory().addItem(add);
        }
    }

    private void addActions() {
        actions.put("sell-inventory", event -> {
            new SellGui(player, SellState.CONFIRM, getGui().getInventory()).open();
        });
        actions.put("sell-inventory-confirm", event -> {
            new SellHelper(player.getInventory(), player).sell();
            doRescue();
            player.closeInventory();
        });
        actions.put("sell-shop", event -> {
            new SellGui(player, SellState.CONFIRM, getGui().getInventory()).open();
        });
        actions.put("sell-shop-confirm", event -> {
            new SellHelper(getGui().getInventory(), player).sell();
            doRescue();
            player.closeInventory();
        });
    }

}
