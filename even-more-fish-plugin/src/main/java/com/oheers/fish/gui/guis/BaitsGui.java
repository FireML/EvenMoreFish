package com.oheers.fish.gui.guis;

import com.oheers.fish.baits.BaitManager;
import com.oheers.fish.config.GuiConfig;
import com.oheers.fish.gui.PaginatedConfigGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BaitsGui extends PaginatedConfigGui {

    public BaitsGui(@NotNull HumanEntity player) {
        super(
            GuiConfig.getInstance().getConfig().getSection("baits-menu"),
            (Player) player
        );

        createGui();

        loadBaits();
    }

    private void loadBaits() {
        BaitManager.getInstance().getBaitMap().values().forEach(bait -> {
            ItemStack baitItem = bait.create(player);
            if (baitItem != null) {
                getGui().addItem(new GuiItem(baitItem));
            }
        });
    }

}
