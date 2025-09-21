package com.oheers.fish.gui.types;

import com.oheers.fish.FishUtils;
import com.oheers.fish.gui.base.BaseConfigGui;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.StorageGui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class DepositConfigGui extends BaseConfigGui<StorageGui> {

    public DepositConfigGui(@NotNull HumanEntity player, @NotNull Section config) {
        super(player, config);
    }

    @Override
    public StorageGui createGui() {
        return Gui.storage()
            .title(getTitle().getComponentMessage())
            .rows(getRows())
            .create();
    }

    @Override
    public void doRescue() {
        Inventory inv = getGui().getInventory();
        if (inv.isEmpty() || !(player instanceof Player p)) {
            return;
        }
        FishUtils.giveItems(inv.getStorageContents(), p);
        inv.clear();
    }

}
