package com.oheers.fish.fishing.items;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.api.FileUtil;
import com.oheers.fish.api.requirement.Requirement;
import com.oheers.fish.api.requirement.RequirementContext;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.fishing.Processor;
import com.oheers.fish.fishing.items.config.FishConversions;
import com.oheers.fish.fishing.items.config.RarityConversions;
import com.oheers.fish.fishing.rods.CustomRod;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class FishManager {

    private static FishManager instance;

    private final TreeMap<String, Rarity> rarityMap;
    private boolean loaded = false;

    private FishManager() {
        rarityMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        new RarityConversions().performCheck();
        new FishConversions().performCheck();
    }

    public static FishManager getInstance() {
        if (instance == null) {
            instance = new FishManager();
        }
        return instance;
    }

    public void load() {
        if (isLoaded()) {
            return;
        }
        loadRarities();
        logLoadedItems();
        loaded = true;
    }

    public void reload() {
        if (!isLoaded()) {
            return;
        }
        rarityMap.clear();
        loadRarities();
        logLoadedItems();
    }

    public void unload() {
        if (!isLoaded()) {
            return;
        }
        rarityMap.clear();
        loaded = false;
    }

    public boolean isLoaded() {
        return loaded;
    }

    // Getters for Rarities and Fish

    public @Nullable Rarity getRarity(@NotNull String rarityName) {
        return rarityMap.get(rarityName);
    }

    public @Nullable Fish getFish(@NotNull String rarityName, @NotNull String fishName) {
        Rarity rarity = getRarity(rarityName);
        if (rarity == null) {
            return null;
        }
        return rarity.getFish(fishName);
    }

    // TODO cleanup
    public Rarity getRandomWeightedRarity(Player fisher, double boostRate, Set<Rarity> boostedRarities, Set<Rarity> totalRarities, @Nullable CustomRod customRod) {
        Map<UUID, Rarity> decidedRarities = EvenMoreFish.getInstance().getDecidedRarities();
        if (fisher != null && decidedRarities.containsKey(fisher.getUniqueId())) {
            Rarity chosenRarity = decidedRarities.get(fisher.getUniqueId());
            decidedRarities.remove(fisher.getUniqueId());
            return chosenRarity;
        }

        List<Rarity> allowedRarities = new ArrayList<>();

        if (fisher != null) {
            String region = FishUtils.getRegionName(fisher.getLocation());
            for (Rarity rarity : rarityMap.values()) {
                if (boostedRarities != null && boostRate == -1 && !boostedRarities.contains(rarity)) {
                    continue;
                }

                if (!(rarity.getPermission() == null || fisher.hasPermission(rarity.getPermission()))) {
                    continue;
                }

                Requirement requirement = rarity.getRequirement();
                RequirementContext context = new RequirementContext(fisher.getWorld(), fisher.getLocation(), fisher, null, null);
                if (requirement.meetsRequirements(context)) {
                    double regionBoost = MainConfig.getInstance().getRegionBoost(region, rarity.getId());
                    for (int i = 0; i < regionBoost; i++) {
                        allowedRarities.add(rarity);
                    }
                }
            }
        } else {
            allowedRarities.addAll(totalRarities);
        }

        // Remove all rarities that are not allowed by the custom rod.
        if (customRod != null) {
            allowedRarities.retainAll(customRod.getAllowedRarities());
        }

        if (allowedRarities.isEmpty()) {
            String fisherName = fisher == null ? "N/A" : fisher.getName();
            EvenMoreFish.getInstance().getLogger().severe("There are no rarities for the user " + fisherName + " to fish. They have received no fish.");
            return null;
        }

        double totalWeight = 0;

        for (Rarity r : allowedRarities) {
            if (boostRate != -1.0 && boostedRarities != null && boostedRarities.contains(r)) {
                totalWeight += (r.getWeight() * boostRate);
            } else {
                totalWeight += r.getWeight();
            }
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < allowedRarities.size() - 1; ++idx) {
            if (boostRate != -1.0 && boostedRarities != null && boostedRarities.contains(allowedRarities.get(idx))) {
                r -= allowedRarities.get(idx).getWeight() * boostRate;
            } else {
                r -= allowedRarities.get(idx).getWeight();
            }
            if (r <= 0.0) break;
        }

        if (!Competition.isActive() && EvenMoreFish.getInstance().isRaritiesCompCheckExempt()) {
            if (allowedRarities.get(idx).hasCompExemptFish()) return allowedRarities.get(idx);
        } else if (Competition.isActive() || !MainConfig.getInstance().isFishCatchOnlyInCompetition()) {
            return allowedRarities.get(idx);
        }

        return null;
    }

    public Fish getRandomWeightedFish(List<Fish> fishList, double boostRate, List<Fish> boostedFish) {
        final double totalWeight = FishUtils.getTotalWeight(fishList, boostRate, boostedFish);

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < fishList.size() - 1; ++idx) {
            double weight = fishList.get(idx).getWeight();
            if (weight == 0.0d) {
                weight = 1;
            }
            if (boostRate != -1 && boostedFish != null && boostedFish.contains(fishList.get(idx))) {
                r -= weight * boostRate;
            } else {
                r -= weight;
            }

            if (r <= 0.0) break;
        }

        return fishList.get(idx);
    }

    public Fish getFish(Rarity r, Location l, Player p, double boostRate, List<Fish> boostedFish, boolean doRequirementChecks, @Nullable Processor<?> processor, @Nullable CustomRod customRod) {
        if (r == null) return null;
        // will store all the fish that match the player's biome or don't discriminate biomes

        // Protection against /emf admin reload causing the plugin to be unable to get the rarity
        if (r.getOriginalFishList().isEmpty()) {
            r = getRandomWeightedRarity(p, 1, null, Set.copyOf(rarityMap.values()), customRod);
        }

        List<Fish> customRodFish = customRod == null ? List.of() : customRod.getAllowedFish();

        World world = l == null ? null : l.getWorld();
        RequirementContext context = new RequirementContext(world, l, p, null, null);

        List<Fish> available = r.getFishList().stream()
            .filter(fish -> {
                if (!customRodFish.isEmpty() && !customRodFish.contains(fish)) {
                    return false;
                }
                if (!(boostRate != -1 || boostedFish == null || boostedFish.contains(fish))) {
                    return false;
                }
                if (processor != null && !processor.canUseFish(fish)) {
                    return false;
                }
                if (doRequirementChecks) {
                    Requirement requirement = fish.getRequirement();
                    return requirement.meetsRequirements(context);
                }
                return true;
            })
            .toList();

        // if the config doesn't define any fish that can be fished in this biome.
        if (available.isEmpty()) {
            EvenMoreFish.getInstance().getLogger().warning("There are no fish of the rarity " + r.getId() + " that can be fished at (x=" + l.getX() + ", y=" + l.getY() + ", z=" + l.getZ() + ")");
            return null;
        }

        Fish returningFish;

        // checks whether weight calculations need doing for fish
        returningFish = getRandomWeightedFish(available, boostRate, boostedFish);

        if (Competition.isActive() || !MainConfig.getInstance().isFishCatchOnlyInCompetition() || (EvenMoreFish.getInstance().isRaritiesCompCheckExempt() && returningFish.isCompExemptFish())) {
            return returningFish;
        } else {
            return null;
        }
    }

    public TreeMap<String, Rarity> getRarityMap() {
        return rarityMap;
    }

    // Loading things

    private void logLoadedItems() {
        int allFish = 0;
        for (Rarity rarity : rarityMap.values()) {
            allFish += rarity.getOriginalFishList().size();
        }
        EvenMoreFish.getInstance().getLogger().info("Loaded FishManager with " + rarityMap.size() + " Rarities and " + allFish + " Fish.");
    }

    private void loadRarities() {

        rarityMap.clear();

        File raritiesFolder = new File(EvenMoreFish.getInstance().getDataFolder(), "rarities");
        if (EvenMoreFish.getInstance().isFirstLoad()) {
            loadDefaultFiles(raritiesFolder);
        }
        regenExampleFile(raritiesFolder);
        List<File> rarityFiles = FileUtil.getFilesInDirectory(raritiesFolder, true, true);

        if (rarityFiles.isEmpty()) {
            return;
        }

        rarityFiles.forEach(file -> {
            EvenMoreFish.getInstance().debug("Loading " + file.getName() + " rarity");
            Rarity rarity;
            try {
                rarity = new Rarity(file);
                // Skip invalid configs.
            } catch (InvalidConfigurationException exception) {
                return;
            }
            // Skip disabled files.
            if (rarity.isDisabled()) {
                return;
            }
            // Skip duplicate IDs
            String id = rarity.getId();
            if (rarityMap.containsKey(id)) {
                EvenMoreFish.getInstance().getLogger().warning("A rarity with the id: " + id + " already exists! Skipping.");
                return;
            }
            rarityMap.put(id, rarity);
        });
    }

    private void regenExampleFile(@NotNull File targetDirectory) {
        new File(targetDirectory, "_example.yml").delete();
        FileUtil.loadFileOrResource(targetDirectory, "_example.yml", "rarities/_example.yml", EvenMoreFish.getInstance());
    }

    private void loadDefaultFiles(@NotNull File targetDirectory) {
        EvenMoreFish.getInstance().getLogger().info("Loading default rarity configs.");
        FileUtil.loadFileOrResource(targetDirectory, "common.yml", "rarities/common.yml", EvenMoreFish.getInstance());
        FileUtil.loadFileOrResource(targetDirectory, "junk.yml", "rarities/junk.yml", EvenMoreFish.getInstance());
        FileUtil.loadFileOrResource(targetDirectory, "rare.yml", "rarities/rare.yml", EvenMoreFish.getInstance());
        FileUtil.loadFileOrResource(targetDirectory, "epic.yml", "rarities/epic.yml", EvenMoreFish.getInstance());
        FileUtil.loadFileOrResource(targetDirectory, "legendary.yml", "rarities/legendary.yml", EvenMoreFish.getInstance());
    }

}