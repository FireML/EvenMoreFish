package com.oheers.fish.fishing.items;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.fishing.items.IFish;
import com.oheers.fish.api.requirement.Requirement;
import com.oheers.fish.api.reward.Reward;
import com.oheers.fish.exceptions.InvalidFishException;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.EMFMessage;
import com.oheers.fish.selling.WorthNBT;
import com.oheers.fish.utils.ItemFactory;
import de.tr7zw.changeme.nbtapi.NBT;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class Fish implements IFish {

    private final @NotNull Section section;
    private final String name;
    private final Rarity rarity;
    private final ItemFactory factory;
    private UUID fisherman;
    private Float length;

    private String displayName;

    private List<Reward> actionRewards;
    private List<Reward> fishRewards;
    private List<Reward> sellRewards;
    private String eventType;

    private Requirement requirement = new Requirement();

    private boolean wasBaited;
    private boolean silent;

    private double weight;

    private double minSize;
    private double maxSize;

    private boolean isCompExemptFish;

    private final boolean disableFisherman;

    private int day = -1;
    private final double setWorth;

    private Fish(@NotNull Rarity rarity, @NotNull Section section) {
        this.section = section;
        this.rarity = rarity;
        // This should never be null, but we have this check just to be safe.
        this.name = Objects.requireNonNull(section.getNameAsString());

        this.weight = section.getDouble("weight");
        if (this.weight != 0) {
            rarity.setFishWeighted(true);
        }

        this.length = -1F;

        this.disableFisherman = section.getBoolean("disable-fisherman", rarity.isShouldDisableFisherman());

        this.setWorth = section.getDouble("set-worth");

        this.factory = new ItemFactory(null, section);
        checkDisplayName();

        // These settings don't mean these will be applied, but they will be considered if the settings exist.
        factory.enableDefaultChecks();
        factory.setItemDisplayNameCheck(this.displayName != null);
        factory.setItemLoreCheck(!section.getBoolean("disable-lore", false));

        setSize();
        checkEatEvent();
        checkInteractEvent();
        checkSilent();

        checkFishEvent();

        checkSellEvent();
        handleRequirements();
    }

    /**
     * Creates a Fish from its config section.
     * @param section The section for this fish.
     */
    public static Fish create(@NotNull Rarity rarity, @NotNull Section section) {
        return new Fish(rarity, section);
    }

    /**
     * Creates a Fish from its config section.
     * @param section The section for this fish.
     * @throws InvalidFishException When section is null.
     */
    public static Fish createOrThrow(@NotNull Rarity rarity, @Nullable Section section) throws InvalidFishException {
        if (section == null) {
            throw new InvalidFishException("Fish could not be fetched from the config.");
        }
        return new Fish(rarity, section);
    }

    private void handleRequirements() {
        Section requirementSection = section.getSection("requirements");
        requirement = new Requirement();
        if (requirementSection == null) {
            return;
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
    }

    /**
     * Returns the item stack version of the fish to be given to the player.
     *
     * @param randomIndex If the value isn't -1 then a specific index of the random results of the fish will be chosen,
     *                    relying on the fact that it's a fish doing random choices. If it is -1 then a random one will
     *                    be rolled.
     * @return An ItemStack version of the fish.
     */
    public ItemStack give(int randomIndex) {
        ItemStack fish = factory.createItem(getFishermanPlayer(), randomIndex);
        if (factory.isRawMaterial()) {
            return fish;
        }

        fish.editMeta(meta -> {
            meta.displayName(getDisplayName().getComponentMessage());
            if (!section.getBoolean("disable-lore", false)) {
                meta.lore(getFishLore());
            }
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        });
        WorthNBT.setNBT(fish, this);

        return fish;
    }

    private OfflinePlayer getFishermanPlayer() {
        return fisherman == null ? null : Bukkit.getOfflinePlayer(fisherman);
    }

    private void setSize() {
        this.minSize = section.getDouble("size.minSize");
        this.maxSize = section.getDouble("size.maxSize");

        // are min & max size changed? If not, there's no fish-specific value. Check the rarity's value
        if (minSize == 0.0 && maxSize == 0.0) {
            this.minSize = rarity.getMinSize();
            this.maxSize = rarity.getMaxSize();
        }

        // If there's no rarity-specific value (or max is smaller than min), to avoid being in a pickle we just set min default to 0 and max default to 10
        if ((minSize == 0.0 && maxSize == 0.0) || minSize > maxSize) {
            this.minSize = 0.0;
            this.maxSize = 10.0;
        }
    }

    private void generateSize() {
        if (minSize < 0) {
            this.length = -1f;
        } else {
            // Calculate the range for the random number (scaled by 10 to preserve decimal precision)
            int range = (int) ((maxSize - minSize) * 10);

            // Generate a random integer within the range (0 to range-1)
            int randomValue = EvenMoreFish.getInstance().getRandom().nextInt(range + 1); // nextInt(bound) ensures a positive value

            // Calculate the length, scaling back down by dividing by 10
            this.length = (float) (randomValue + minSize * 10) / 10;
        }
    }

    @Override
    public double getWorthMultiplier() {
        return section.getDouble("worth-multiplier", 0.0D);
    }

    @Override
    public boolean hasEatRewards() {
        if (eventType != null) {
            return eventType.equals("eat");
        } else {
            return false;
        }
    }

    @Override
    public boolean hasFishRewards() {
        return !fishRewards.isEmpty();
    }

    @Override
    public boolean hasSellRewards() {
        return !sellRewards.isEmpty();
    }

    @Override
    public boolean hasInteractRewards() {
        if (eventType != null) {
            return eventType.equals("int");
        } else {
            return false;
        }
    }

    // checks if the config contains a message to be displayed when the fish is fished
    private void checkMessage() {
        String msg = section.getString("message");

        if (msg == null) {
            return;
        }
        if (fisherman == null) {
            return;
        }
        Player player = Bukkit.getPlayer(fisherman);
        if (player != null) {
            EMFMessage.fromString(msg).send(player);
        }
    }

    private void checkEffects() {

        String effectConfig = section.getString("effect");

        // if the config doesn't have an effect stated to be given
        if (effectConfig == null) {
            return;
        }

        String[] separated = effectConfig.split(":");

        // Check if fisherman is null
        if (this.fisherman == null) {
            return;
        }
        // Check if the requested player is null
        Player player = Bukkit.getPlayer(this.fisherman);
        if (player == null) {
            return;
        }

        Runnable fallback = () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
            EvenMoreFish.getInstance().getLogger().warning("Invalid potion effect specified. Defaulting to Speed 2 for 5 seconds.");
        };

        // if it's formatted wrong, it'll just give the player this as a stock effect
        if (separated.length < 3) {
            fallback.run();
            return;
        }

        PotionEffectType effect = PotionEffectType.getByName(separated[0].toUpperCase());
        // Handle the effect type being null.
        if (effect == null) {
            fallback.run();
            return;
        }
        int amplitude = Integer.parseInt(separated[1]);
        // *20 to bring it to seconds rather than ticks
        int time = Integer.parseInt(separated[2]) * 20;

        try {
            player.addPotionEffect(new PotionEffect(effect, time, amplitude));
        } catch (IllegalArgumentException e) {
            EvenMoreFish.getInstance().getLogger().log(Level.SEVERE, e.getMessage(), e);
            EvenMoreFish.getInstance().getLogger().log(Level.SEVERE, "ATTENTION! There was an error adding the effect from the " + this.name + " fish.");
            EvenMoreFish.getInstance().getLogger().log(Level.SEVERE, "ATTENTION! Check your config files and ensure spelling of the effect name is correct.");
            EvenMoreFish.getInstance().getLogger().log(Level.SEVERE, "ATTENTION! If the problem persists, ask for help on the support discord server.");
        }
    }

    // prepares it to be given to the player
    @Override
    public void init() {
        generateSize();
        checkMessage();
        checkEffects();
    }

    /**
     * From the new method of fetching the lore, where the admin specifies exactly how they want the lore to be set up,
     * letting them modify the order, add a twist to how they want extra details and so on.
     * <p>
     * It goes through each line of the Messages' getFishLoreFormat, if the line is just {fish_lore} then it gets replaced
     * with a fish's lore value, if not then nothing is done.
     *
     * @return A lore to be used by fetching data from the old messages.yml set-up.
     */
    private List<Component> getFishLore() {
        List<String> loreOverride = section.getStringList("lore-override");
        EMFMessage newLoreLine;
        if (!loreOverride.isEmpty()) {
            newLoreLine = EMFMessage.fromStringList(loreOverride);
        } else {
            newLoreLine = ConfigMessage.FISH_LORE.getMessage();
        }

        List<String> fishLore = section.getStringList("lore");
        String replacement = fishLore.isEmpty() ? "" : String.join("\n", fishLore);

        newLoreLine.setVariable(
                "\n{fish_lore}",
                replacement
        );

        newLoreLine.setVariable("{fisherman_lore}",
                !disableFisherman && getFishermanPlayer() != null ?
                        (ConfigMessage.FISHERMAN_LORE.getMessage())
                        : ""
        );

        if (!disableFisherman && getFishermanPlayer() != null) newLoreLine.setPlayer(getFishermanPlayer());

        newLoreLine.setVariable("{length_lore}",
                length > 0 ?
                        ConfigMessage.LENGTH_LORE.getMessage()
                        : ""
        );

        if (length > 0) newLoreLine.setLength(Float.toString(length));

        newLoreLine.setRarity(this.rarity.getLorePrep());

        OfflinePlayer fisherman = getFishermanPlayer();
        if (fisherman != null) {
            newLoreLine.setPlayer(fisherman);
            newLoreLine.formatPlaceholderAPI();
        }

        return newLoreLine.getComponentListMessage();
    }

    @Override
    public void checkDisplayName() {
        this.displayName = section.getString("displayname");
    }

    @Override
    public void checkEatEvent() {
        List<String> configRewards = section.getStringList("eat-event");
        // Checks if the player has actually set rewards for an eat event
        if (!configRewards.isEmpty()) {
            // Informs the main class to load up an PlayerItemConsumeEvent listener
            EvenMoreFish.getInstance().setCheckingEatEvent(true);
            this.eventType = "eat";
            actionRewards = new ArrayList<>();

            // Translates all the rewards into Reward objects and adds them to the fish.
            configRewards.forEach(reward -> {
                reward = parseEventPlaceholders(reward);
                this.actionRewards.add(new Reward(reward));
            });
        }
    }

    @Override
    public void checkFishEvent() {
        fishRewards = new ArrayList<>();
        List<String> configRewards = section.getStringList("catch-event");
        if (!configRewards.isEmpty()) {
            // Translates all the rewards into Reward objects and adds them to the fish.
            configRewards.forEach(reward -> {
                reward = parseEventPlaceholders(reward);
                this.fishRewards.add(new Reward(reward));
            });
        }
    }

    @Override
    public void checkSellEvent() {
        sellRewards = new ArrayList<>();
        List<String> configRewards = section.getStringList("sell-event");
        if (!configRewards.isEmpty())  {
            configRewards.forEach(reward -> {
                reward = parseEventPlaceholders(reward);
                this.sellRewards.add(new Reward(reward));
            });
        }
    }

    @Override
    public void checkInteractEvent() {
        List<String> configRewards = section.getStringList("interact-event");
        // Checks if the player has actually set rewards for an interact event
        if (!configRewards.isEmpty()) {
            // Informs the main class to load up an PlayerItemConsumeEvent listener
            EvenMoreFish.getInstance().setCheckingIntEvent(true);
            this.eventType = "int";
            actionRewards = new ArrayList<>();

            // Translates all the rewards into Reward objects and adds them to the fish.
            configRewards.forEach(reward -> {
                reward = parseEventPlaceholders(reward);
                this.actionRewards.add(new Reward(reward));
            });
        }
    }

    /**
     * Checks if the fish has silent: true enabled, which stops the "You caught ... fish" from being broadcasted to anyone.
     */
    @Override
    public void checkSilent() {
        this.silent = section.getBoolean("silent", false);
    }

    @Override
    public @NotNull Fish createCopy() {
        return create(rarity, section);
    }

    @Override
    public boolean hasFishermanDisabled() {
        return disableFisherman;
    }

    @Override
    public @Nullable UUID getFisherman() {
        return fisherman;
    }

    @Override
    public void setFisherman(UUID fisherman) {
        this.fisherman = fisherman;
    }

    @Override
    public boolean isCompExemptFish() {
        return isCompExemptFish;
    }

    @Override
    public void setCompExemptFish(boolean compExemptFish) {
        isCompExemptFish = compExemptFish;
    }

    @Override
    public double getSetWorth() {
        return setWorth;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Rarity getRarity() {
        return rarity;
    }

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public void setLength(@Nullable Float length) {
        this.length = length == null ? -1 : length;
    }

    @Override
    public @NotNull List<Reward> getActionRewards() {
        return actionRewards;
    }

    @Override
    public @NotNull List<Reward> getFishRewards() {
        return fishRewards;
    }

    @Override
    public @NotNull List<Reward> getSellRewards() { return sellRewards; }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @NotNull
    public EMFMessage getDisplayName() {
        if (displayName == null) {
            return rarity.format(name);
        }
        return rarity.format(displayName);
    }

    public ItemFactory getFactory() {
        return factory;
    }

    @Override
    public @NotNull Requirement getRequirement() {
        return this.requirement;
    }

    @Override
    public void setRequirement(@NotNull Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public boolean isWasBaited() {
        return wasBaited;
    }

    @Override
    public void setWasBaited(boolean wasBaited) {
        this.wasBaited = wasBaited;
    }

    @Override
    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public boolean isSilent() {
        return silent;
    }

    @Override
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    @Override
    public @NotNull String parseEventPlaceholders(@NotNull String rewardString) {
        // {length} Placeholder
        rewardString = rewardString.replace("{length}", String.valueOf(length));

        // {rarity} Placeholder
        String rarityReplacement = "";
        if (rarity != null) {
            rarityReplacement = rarity.getId();
        }
        rewardString = rewardString.replace("{rarity}", rarityReplacement);

        // {displayname} Placeholder
        String displayNameReplacement = "";
        if (displayName != null) {
            displayNameReplacement = displayName;
        }
        rewardString = rewardString.replace("{displayname}", displayNameReplacement);

        // {name} Placeholder
        String nameReplacement = "";
        if (name != null) {
            nameReplacement = name;
        }
        rewardString = rewardString.replace("{name}", nameReplacement);

        return rewardString;
    }

}