package com.oheers.fish.gui.guis;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.config.GuiConfig;
import com.oheers.fish.database.Database;
import com.oheers.fish.database.data.FishRarityKey;
import com.oheers.fish.database.data.UserFishRarityKey;
import com.oheers.fish.database.model.fish.FishStats;
import com.oheers.fish.database.model.user.UserFishStats;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.FishManager;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.gui.ConfigGui;
import com.oheers.fish.messages.EMFListMessage;
import com.oheers.fish.messages.EMFSingleMessage;
import com.oheers.fish.items.ItemFactory;
import com.oheers.fish.utils.ItemUtils;
import com.oheers.fish.utils.Logging;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FishJournalGui extends ConfigGui {

    private final Rarity rarity;

    public FishJournalGui(@NotNull HumanEntity player, @Nullable Rarity rarity) {
        super(
                GuiConfig.getInstance().getConfig().getSection(
                        rarity == null ? "journal-menu" : "journal-rarity"
                ),
            (Player) player
        );

        this.rarity = rarity;
    }

    @Override
    protected void loadItems(@NotNull BaseGui gui, @Nullable Map<String, ?> replacements) {
        Section config = getGuiConfig();
        if (config == null) {
            Logging.warn("No config found for FishJournalGui, unable to load items.");
            return;
        }
        if (rarity == null) {
            loadFishGroup(gui, config, replacements);
        } else {
            loadRarityGroup(gui, config, replacements);
        }
    }

    @Override
    public @NotNull Map<String, ?> getReplacements() {
        return Map.of();
    }

    private void loadFishGroup(@NotNull BaseGui gui, @NotNull Section section, @Nullable Map<String, ?> replacements) {
        this.rarity.getFishList().forEach(fish -> {
            ItemStack item = getFishItem(fish, section, replacements);
            gui.addItem(new GuiItem(item));
        });
    }

    private ItemStack getFishItem(Fish fish, Section section, @Nullable Map<String, ?> replacements) {
        Database database = EvenMoreFish.getInstance().getDatabase();

        if (database == null) {
            Logging.warn("Can not show fish in the Journal Menu, please enable the database!");
            ItemFactory factory = ItemFactory.itemFactory(section, "undiscovered-fish");
            return factory.createItem(player.getUniqueId(), replacements);
        }

        boolean hideUndiscovered = section.getBoolean("hide-undiscovered-fish", true);
        // If undiscovered fish should be hidden
        if (hideUndiscovered && !database.userHasFish(fish, player)) {
            ItemFactory factory = ItemFactory.itemFactory(section, "undiscovered-fish");
            return factory.createItem(player.getUniqueId(), replacements);
        }

        ItemStack item = fish.getFactory().createItem(player.getUniqueId(), replacements);

        item.editMeta(meta -> {
            EMFSingleMessage display = prepareDisplay(section, fish);
            if (display != null) {
                meta.displayName(display.getComponentMessage());
            }
            meta.lore(prepareLore(section, fish).getComponentListMessage());
        });

        return item;
    }

    private @Nullable EMFSingleMessage prepareDisplay(@NotNull Section section, @NotNull Fish fish) {
        ItemFactory factory = ItemFactory.itemFactory(section, "fish-item");
        final String displayStr = factory.getDisplayName().getActualValue();
        if (displayStr == null) {
            return null;
        }
        EMFSingleMessage display = EMFSingleMessage.fromString(displayStr);
        display.setVariable("{fishname}", fish.getDisplayName());
        return display;
    }

    private @NotNull EMFListMessage prepareLore(@NotNull Section section, @NotNull Fish fish) {
        final int userId = EvenMoreFish.getInstance().getUserManager().getUserId(player.getUniqueId());

        final UserFishStats userFishStats = EvenMoreFish.getInstance().getUserFishStatsDataManager().get(UserFishRarityKey.of(userId, fish).toString());
        final FishStats fishStats = EvenMoreFish.getInstance().getFishStatsDataManager().get(FishRarityKey.of(fish).toString());

        final String discoverDate = getValueOrUnknown(() -> userFishStats.getFirstCatchTime().format(DateTimeFormatter.ISO_DATE));
        final String discoverer = getValueOrUnknown(() -> FishUtils.getPlayerName(fishStats.getDiscoverer()));

        ItemFactory factory = ItemFactory.itemFactory(section, "fish-item");
        List<String> loreCfg = factory.getLore().getActualValue();

        EMFListMessage lore = EMFListMessage.fromStringList(
            loreCfg
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
        try {
            String value = supplier.get();
            return (value == null) ? "Unknown" : value;
        } catch (NullPointerException e) {
            return "Unknown";
        }
    }


    private void loadRarityGroup(@NotNull BaseGui gui, @NotNull Section section, @Nullable Map<String, ?> replacements) {
        FishManager.getInstance().getRarityMap().values().forEach(rarity -> {
            ItemStack item = getRarityItem(rarity, section, replacements);
            gui.addItem(new GuiItem(item, click -> new FishJournalGui(player, rarity).open()));
        });
    }

    private ItemStack getRarityItem(Rarity rarity, Section section, @Nullable Map<String, ?> replacements) {
        Database database = EvenMoreFish.getInstance().getDatabase();
        boolean hideUndiscovered = section.getBoolean("hide-undiscovered-rarities", true);

        if (database == null) {
            Logging.warn("Can not show rarities in the Journal Menu, please enable the database!");
            ItemFactory factory = ItemFactory.itemFactory(section, "undiscovered-rarity");
            return factory.createItem(player.getUniqueId(), replacements);
        }

        if (hideUndiscovered && !database.userHasRarity(rarity, player)) {
            ItemFactory factory = ItemFactory.itemFactory(section, "undiscovered-rarity");
            return factory.createItem(player.getUniqueId(), replacements);
        }

        final ItemFactory factory = ItemFactory.itemFactory(section, "rarity-item");
        ItemStack item = factory.createItem(player.getUniqueId(), replacements);
        item = ItemUtils.changeMaterial(item, rarity.getMaterial());

        item.editMeta(meta -> {
            Component originalDisplay = meta.displayName();
            if (originalDisplay != null) {
                EMFSingleMessage display = EMFSingleMessage.of(originalDisplay);
                display.setRarity(rarity.getDisplayName());
                display.setVariables(replacements);
                meta.displayName(display.getComponentMessage());
            }
        });

        return item;
    }

}
