package com.oheers.fish.fishing.items.rarities;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.requirement.Requirement;
import com.oheers.fish.config.ConfigBase;
import com.oheers.fish.exceptions.InvalidFishException;
import com.oheers.fish.fishing.items.Fish;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rarity extends ConfigBase {

    private static final Logger logger = EvenMoreFish.getInstance().getLogger();

    private boolean fishWeighted;
    private Requirement requirement = new Requirement();
    private final List<Fish> fishList;

    /**
     * Constructs a Rarity from its config file.
     * @param section The file for this rarity.
     */
    public Rarity(@NotNull File file) throws InvalidConfigurationException {
        super(file, EvenMoreFish.getInstance(), false);
        performRequiredConfigChecks();
        updateRequirementFormats();
        fishList = loadFish();
    }

    // Current required config: id
    private void performRequiredConfigChecks() throws InvalidConfigurationException {
        if (getConfig().getString("id") == null) {
            logger.warning("Rarity invalid: 'id' missing in " + getFileName());
            throw new InvalidConfigurationException("An ID has not been found in " + getFileName() + ". Please correct this.");
        }
    }

    // Config getters

    public @NotNull String getId() {
        return Objects.requireNonNull(getConfig().getString("id"));
    }

    public boolean isDisabled() {
        return getConfig().getBoolean("disabled");
    }

    public @NotNull String getColour() {
        return getConfig().getString("colour", "&f");
    }

    public double getWeight() {
        return getConfig().getDouble("weight");
    }

    public boolean getAnnounce() {
        return getConfig().getBoolean("broadcast");
    }

    public boolean getUseConfigCasing() {
        return getConfig().getBoolean("use-this-casing");
    }

    public @NotNull String getDisplayName() {
        String displayName = getConfig().getString("displayname");
        if (displayName == null) {
            return getId();
        }
        return displayName;
    }

    public @NotNull String getLorePrep() {
        String loreOverride = getConfig().getString("override-lore");
        if (loreOverride != null) {
            return loreOverride;
        }
        String displayName = getConfig().getString("displayname");
        if (displayName != null) {
            return displayName;
        }
        String finalName = getId();
        if (!getUseConfigCasing()) {
            finalName = finalName.toUpperCase();
        }
        return this.getColour() + "&l" + finalName;
    }

    public @Nullable String getPermission() {
        return getConfig().getString("permission");
    }

    public Requirement getRequirement() {
        if (requirement == null) {
            requirement = loadRequirements();
        }
        return requirement;
    }

    public boolean isShouldDisableFisherman() {
        return getConfig().getBoolean("disable-fisherman", false);
    }

    public double getMinSize() {
        return getConfig().getDouble("size.minSize");
    }

    public double getMaxSize() {
        return getConfig().getDouble("size.maxSize");
    }

    // TODO this was set to always be false at some point, we need to re-add the removed code.
    public boolean hasCompExemptFish() {
        return false;
    }

    public List<Fish> getFish() {
        return fishList;
    }

    // External variables

    public boolean isFishWeighted() {
        return fishWeighted;
    }

    public void setFishWeighted(boolean fishWeighted) {
        this.fishWeighted = fishWeighted;
    }

    // Loading stuff

    private List<Fish> loadFish() {
        Section rootFishSection = getConfig().getSection("fish");
        if (rootFishSection == null) {
            return List.of();
        }
        List<Fish> fishList = new ArrayList<>();
        rootFishSection.getRoutesAsStrings(false).forEach(fishStr -> {
            Section fishSection = rootFishSection.getSection(fishStr);
            if (fishSection == null) {
                fishSection = rootFishSection.createSection(fishStr);
            }
            try {
                fishList.add(new Fish(this, fishSection));
            } catch (InvalidFishException exception) {
                EvenMoreFish.getInstance().getLogger().log(Level.WARNING, exception.getMessage(), exception);
            }
        });
        // Creates an immutable list.
        return List.copyOf(fishList);
    }

    private Requirement loadRequirements() {
        Section requirementSection = getConfig().getSection("requirements");
        Requirement requirement = new Requirement();
        if (requirementSection == null) {
            return requirement;
        }
        requirementSection.getRoutesAsStrings(false).forEach(requirementString -> {
            List<String> values = new ArrayList<>();
            if (requirementSection.isList(requirementString)) {
                values.addAll(requirementSection.getStringList(requirementString));
            } else {
                values.add(requirementSection.getString(requirementString));
            }
            requirement.add(requirementString, values);
        });
        return requirement;
    }

    private void updateRequirementFormats() {
        Section ingameSection = getConfig().getSection("requirements.ingame-time");
        if (ingameSection != null) {
            int min = ingameSection.getInt("minTime");
            int max = ingameSection.getInt("maxTime");
            ingameSection.remove("minTime");
            ingameSection.remove("maxTime");
            getConfig().set("requirements.ingame-time", min + "-" + max);
        }
        Section irlSection = getConfig().getSection("requirements.irl-time");
        if (irlSection != null) {
            String min = irlSection.getString("minTime");
            String max = irlSection.getString("maxTime");
            irlSection.remove("minTime");
            irlSection.remove("maxTime");
            getConfig().set("requirements.irl-time", min + "-" + max);
        }
        save();
    }

}