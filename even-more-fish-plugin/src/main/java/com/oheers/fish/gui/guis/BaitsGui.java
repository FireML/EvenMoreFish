package com.oheers.fish.gui.guis;

import com.oheers.fish.baits.manager.BaitManager;
import com.oheers.fish.gui.config.GuiConfig;
import com.oheers.fish.gui.types.PaginatedConfigGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BaitsGui extends PaginatedConfigGui {

    public BaitsGui(@NotNull Player player) {
        super(
            player,
            GuiConfig.getInstance().getBaits().getConfig()
        );

        init(gui -> gui.addItem(getGuiItems()));
    }

    private GuiItem[] getGuiItems() {
        return BaitManager.getInstance().getItemMap().values()
            .stream()
            .map(bait -> bait.create(player))
            .map(GuiItem::new)
            .toArray(GuiItem[]::new);
    }

}
