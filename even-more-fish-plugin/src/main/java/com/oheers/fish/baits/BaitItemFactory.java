package com.oheers.fish.baits;

import com.oheers.fish.baits.manager.BaitNBTManager;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.items.ItemFactory;
import com.oheers.fish.items.configs.DisplayNameItemConfig;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.EMFListMessage;
import com.oheers.fish.messages.EMFSingleMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class BaitItemFactory {
    private final String baitId;
    private final List<Rarity> rarities;
    private final List<Fish> fish;
    private final YamlDocument config;

    public BaitItemFactory(String baitId, List<Rarity> rarities, List<Fish> fish, YamlDocument config) {
        this.baitId = baitId;
        this.rarities = rarities;
        this.fish = fish;
        this.config = config;
    }

    public ItemFactory createFactory() {
        ItemFactory factory = ItemFactory.itemFactory(config);

        DisplayNameItemConfig displayNameConfig = factory.getDisplayName();
        displayNameConfig.setEnabled(true);
        displayNameConfig.setDefault("<yellow>" + baitId);

        factory.setFinalChanges(item -> {
            item.setAmount(config.getInt("drop-quantity", 1));
            item.editMeta(meta -> meta.lore(createBoostLore(factory)));
            BaitNBTManager.applyBaitNBT(item, baitId);
        });

        return factory;
    }

    /**
     * This fetches the boost's lore from the config and inserts the boost-rates into the {boosts} variable. This needs
     * to be called after the bait theme is set and the boosts have been initialized, since it uses those variables.
     *
     * @return A list of formatted Adventure components for the bait's lore
     */
    private @NotNull List<Component> createBoostLore(ItemFactory factory) {
        final EMFListMessage lore = getBaseLoreTemplate();
        lore.setVariableWithListInsertion("{boosts}", createBoostsVariable());
        lore.setVariableWithListInsertion("{lore}", createItemLoreVariable(factory).get());
        lore.setVariable("{bait_theme}", Component.empty());

        return lore.getComponentListMessage();
    }

    private EMFListMessage getBaseLoreTemplate() {
        return ConfigMessage.BAIT_BAIT_LORE.getMessage().toListMessage();
    }

    private @NotNull EMFMessage createBoostsVariable() {
        EMFMessage boostsMessage = EMFSingleMessage.empty();
        appendRarityBoosts(boostsMessage);
        appendFishBoosts(boostsMessage);
        return boostsMessage;
    }

    private void appendRarityBoosts(EMFMessage message) {
        if (rarities.isEmpty()) return;

        ConfigMessage boostMessage = rarities.size() > 1
                ? ConfigMessage.BAIT_BOOSTS_RARITIES
                : ConfigMessage.BAIT_BOOSTS_RARITY;

        message.appendMessage(boostMessage.getMessage());
        message.setAmount(Integer.toString(rarities.size()));
    }

    private void appendFishBoosts(EMFMessage message) {
        if (fish.isEmpty()) return;

        message.appendMessage(ConfigMessage.BAIT_BOOSTS_FISH.getMessage());
        message.setAmount(Integer.toString(fish.size()));
    }


    @Contract(pure = true)
    private @NotNull Supplier<EMFListMessage> createItemLoreVariable(ItemFactory factory) {
        return () -> EMFListMessage.fromStringList(
                factory.getLore().getConfiguredValue()
        );
    }



}
