package com.oheers.fish.gui.guis;

import com.oheers.fish.FishUtils;
import com.oheers.fish.baits.manager.BaitManager;
import com.oheers.fish.config.GuiConfig;
import com.oheers.fish.exceptions.InvalidGuiException;
import com.oheers.fish.gui.ConfigGuiOld;
import com.oheers.fish.gui.types.PaginatedConfigGui;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.StaticGuiElement;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BaitsGui extends PaginatedConfigGui {

    public BaitsGui(@NotNull HumanEntity player) throws InvalidGuiException {
        super(
            player,
            GuiConfig.getInstance().getConfig().getSection("baits-menu")
        );

        init(gui -> gui.addItem(getGuiItems()));
    }

    private GuiItem[] getGuiItems() {
        return BaitManager.getInstance().getItemMap().values()
            .stream()
            .map(bait -> bait.create((Player) player))
            .map(GuiItem::new)
            .toArray(GuiItem[]::new);
    }

}
