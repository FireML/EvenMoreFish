package com.oheers.fish.gui.guis;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.config.GuiConfig;
import com.oheers.fish.database.Database;
import com.oheers.fish.database.data.FishRarityKey;
import com.oheers.fish.database.data.UserFishRarityKey;
import com.oheers.fish.database.model.fish.FishStats;
import com.oheers.fish.database.model.user.UserFishStats;
import com.oheers.fish.exceptions.InvalidGuiException;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.FishManager;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.gui.ConfigGuiOld;
import com.oheers.fish.gui.types.PaginatedConfigGui;
import com.oheers.fish.items.ItemFactory;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.EMFListMessage;
import com.oheers.fish.messages.EMFSingleMessage;
import com.oheers.fish.api.Logging;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.StaticGuiElement;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

public class FishJournalGui extends PaginatedConfigGui {

    private final Rarity rarity;

    public FishJournalGui(@NotNull Player player, @Nullable Rarity rarity) throws InvalidGuiException {
        super(
            player,
            GuiConfig.getInstance().getConfig().getSection(
                rarity == null ? "journal-menu" : "journal-rarity"
            )
        );
        this.rarity = rarity;

        init(gui -> gui.addItem(getGuiItems(getConfig())));
    }

    private GuiItem[] getGuiItems(Section config) {
        if (this.rarity == null) {
            return getRarityItems(config);
        } else {
            return getFishItems(config);
        }
    }

    private GuiItem[] getFishItems(Section section) {
        return this.rarity.getFishList().stream()
            .map(fish -> getFishItem(fish, section))
            .map(GuiItem::new)
            .toArray(GuiItem[]::new);
    }

    private ItemStack getFishItem(Fish fish, Section section) {
        final Database database = requireDatabase("Can not show fish in the Journal Menu, please enable the database!");

        if (database == null) {
            return ItemFactory.itemFactory(section, "undiscovered-fish").createItem(player.getUniqueId());
        }

        boolean hideUndiscovered = section.getBoolean("hide-undiscovered-fish", true);
        // If undiscovered fish should be hidden
        if (hideUndiscovered && !database.userHasFish(fish, player)) {
            return ItemFactory.itemFactory(section, "undiscovered-fish").createItem(player.getUniqueId());
        }

        final ItemStack item = fish.give();

        item.editMeta(meta -> {
            ItemFactory factory = ItemFactory.itemFactory(section, "fish-item");
            EMFSingleMessage display = prepareDisplay(factory, fish);
            if (display != null) {
                meta.displayName(display.getComponentMessage(player));
            }
            meta.lore(prepareLore(factory, fish).getComponentListMessage(player));
        });

        return item;
    }

    private @Nullable EMFSingleMessage prepareDisplay(@NotNull ItemFactory factory, @NotNull Fish fish) {
        final String displayStr = factory.getDisplayName().getConfiguredValue();
        if (displayStr == null) {
            return null;
        }
        EMFSingleMessage display = EMFSingleMessage.fromString(displayStr);
        display.setVariable("{fishname}", fish.getDisplayName());
        return display;
    }

    private @NotNull EMFListMessage prepareLore(@NotNull ItemFactory factory, @NotNull Fish fish) {
        final int userId = EvenMoreFish.getInstance().getPluginDataManager().getUserManager().getUserId(player.getUniqueId());

        final UserFishStats userFishStats = EvenMoreFish.getInstance().getPluginDataManager().getUserFishStatsDataManager().get(UserFishRarityKey.of(userId, fish).toString());
        final FishStats fishStats = EvenMoreFish.getInstance().getPluginDataManager().getFishStatsDataManager().get(FishRarityKey.of(fish).toString());

        final String discoverDate = getValueOrUnknown(() -> userFishStats.getFirstCatchTime().format(DateTimeFormatter.ISO_DATE));
        final String discoverer = getValueOrUnknown(() -> FishUtils.getPlayerName(fishStats.getDiscoverer()));

        EMFListMessage lore = EMFListMessage.fromStringList(
                Optional.ofNullable(factory.getLore().getConfiguredValue())
                        .orElse(Collections.emptyList())
        );

        lore.setVariable("{times-caught}", getValueOrUnknown(() -> Integer.toString(userFishStats.getQuantity())));
        lore.setVariable("{largest-size}", getValueOrUnknown(() -> String.valueOf(userFishStats.getLongestLength())));
        lore.setVariable("{smallest-size}", getValueOrUnknown(() -> String.valueOf(userFishStats.getShortestLength())));
        lore.setVariable("{discover-date}", discoverDate);
        lore.setVariable("{discoverer}", discoverer);
        lore.setVariable("{server-largest}", getValueOrUnknown(() -> String.valueOf(fishStats.getLongestLength())));
        lore.setVariable("{server-smallest}", getValueOrUnknown(() -> String.valueOf(fishStats.getShortestLength())));
        lore.setVariable("{server-caught}", getValueOrUnknown(() -> String.valueOf(fishStats.getQuantity())));

        return lore;
    }

    @NotNull
    private String getValueOrUnknown(Supplier<String> supplier) {
        return Optional.ofNullable(supplier.get()).orElse("Unknown");
    }


    private GuiItem[] getRarityItems(Section section) {
        return FishManager.getInstance().getRarityMap().values().stream()
            .map(rarity -> {
                ItemStack item = getRarityItem(rarity, section);
                return new GuiItem(item, event -> {
                    try {
                        new FishJournalGui(player, rarity).open();
                    } catch (InvalidGuiException exception) {
                        ConfigMessage.INVALID_GUI.getMessage().send(player);
                    }
                });
            })
            .toArray(GuiItem[]::new);
    }

    private ItemStack getRarityItem(Rarity rarity, Section section) {
        final Database database = requireDatabase("Can not show rarities in the Journal Menu, please enable the database!");

        if (database == null) {
            return ItemFactory.itemFactory(section, "undiscovered-rarity").createItem(player.getUniqueId());
        }

        boolean hideUndiscovered = section.getBoolean("hide-undiscovered-rarities", true);
        if (hideUndiscovered && !database.userHasRarity(rarity, player)) {
            return ItemFactory.itemFactory(section, "undiscovered-rarity").createItem(player.getUniqueId());
        }

        final ItemStack rarityItem = rarity.getMaterial();
        final ItemStack configuredItem = ItemFactory.itemFactory(section, "rarity-item").createItem(player.getUniqueId());

        // Carry the configured item's lore and display name to the rarity item
        ItemMeta configuredMeta = configuredItem.getItemMeta();
        if (configuredMeta != null) {
            rarityItem.editMeta(meta -> {
                Component configuredDisplay = configuredMeta.displayName();
                if (configuredDisplay != null) {
                    EMFSingleMessage display = EMFSingleMessage.of(configuredDisplay);
                    display.setRarity(rarity.getDisplayName());
                    meta.displayName(display.getComponentMessage(player));
                }
                meta.lore(configuredMeta.lore());
                if (configuredMeta.hasCustomModelData()) {
                    meta.setCustomModelData(configuredMeta.getCustomModelData());
                }
            });
        }

        return rarityItem;
    }

    private @Nullable Database requireDatabase(String logMessage) {
        Database db = EvenMoreFish.getInstance().getPluginDataManager().getDatabase();
        if (db == null) {
            Logging.warn(logMessage);
        }
        return db;
    }

}
