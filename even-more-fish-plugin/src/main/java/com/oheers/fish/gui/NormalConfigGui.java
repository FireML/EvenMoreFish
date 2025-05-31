package com.oheers.fish.gui;

import com.oheers.fish.FishUtils;
import com.oheers.fish.messages.EMFSingleMessage;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class NormalConfigGui extends ConfigGui<Gui> {

    public NormalConfigGui(@Nullable Section config, @NotNull Player player) {
        super(config, player);
    }

    @Override
    protected Gui createGui(@Nullable Map<String, ?> replacements) {
        if (config == null) {
            return Gui.gui()
                .title(Component.text("Invalid GUI"))
                .disableAllInteractions()
                .create();
        }
        GuiType type = FishUtils.getEnumValue(
            GuiType.class,
            config.getString("type", "CHEST"),
            GuiType.CHEST
        );

        EMFSingleMessage title = EMFSingleMessage.fromString(
            config.getString("title", "Gui")
        );
        title.setVariables(replacements);

        return Gui.gui(type)
            .disableAllInteractions()
            .title(title.getComponentMessage())
            .rows(config.getInt("rows", 6))
            .create();
    }

}
