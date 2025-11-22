package com.oheers.fish.items.configs;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public class ItemModelItemConfig extends ItemConfig<String> {

    public ItemModelItemConfig(@NotNull Section section) {
        super(section);
    }

    @Override
    public String getConfiguredValue() {
        return null;
    }

    @Override
    protected BiConsumer<ItemStack, String> applyToItem(@Nullable Map<String, ?> replacements) {
        return (itemStack, string) -> {
            // Not possible with 1.20.1 API.
        };
    }

}
