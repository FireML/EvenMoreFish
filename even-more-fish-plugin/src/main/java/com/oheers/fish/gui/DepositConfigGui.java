package com.oheers.fish.gui;

import com.oheers.fish.messages.EMFSingleMessage;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.StorageGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A GUI for depositing items into a storage.
 * This GUI allows players to deposit items and provides a way to handle the deposited items.
 */
public abstract class DepositConfigGui extends ConfigGui {

    private final Consumer<List<ItemStack>> consumer;

    public DepositConfigGui(@Nullable Section config, @NotNull Player player, @NotNull Consumer<List<ItemStack>> consumer) {
        super(config, player);
        this.consumer = consumer;
    }

    protected abstract @NotNull Consumer<List<ItemStack>> consumer();

    @Override
    protected BaseGui createGui(@Nullable Map<String, ?> replacements) {
        Section config = getGuiConfig();

        EMFSingleMessage title = EMFSingleMessage.fromString(
            config.getString("title", "Gui")
        );
        title.setVariables(replacements);

        StorageGui gui = Gui.storage()
            .disableAllInteractions()
            .title(title.getComponentMessage())
            .rows(config.getInt("rows", 6))
            .create();

        gui.setCloseGuiAction(event -> {
            List<ItemStack> contents = Arrays.stream(event.getInventory().getStorageContents())
                .filter(Objects::nonNull)
                .toList();
            if (contents.isEmpty() || event.getPlayer() != player) {
                return;
            }
            consumer.accept(contents);
            event.getInventory().clear();
        });

        // Load filler
        loadFiller(gui);

        // Load configured items
        loadItems(gui, replacements);

        return gui;
    }

}
