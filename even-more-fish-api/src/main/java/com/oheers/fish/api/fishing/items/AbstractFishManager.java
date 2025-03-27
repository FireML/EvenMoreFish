package com.oheers.fish.api.fishing.items;

import org.bukkit.Location;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * An abstract class representing the FishManager in the EvenMoreFish plugin.
 * Do not extend this class or things will break.
 */
@ApiStatus.Internal
public abstract class AbstractFishManager {

    private static AbstractFishManager instance;

    protected AbstractFishManager() {
        if (instance != null) {
            throw new IllegalStateException(getClass().getSimpleName() + " has already been instantiated.");
        }
        instance = this;
    }

    public static AbstractFishManager get() {
        if (instance == null) {
            throw new IllegalStateException("AbstractFishManager has not been instantiated.");
        }
        return instance;
    }

    public abstract void reload();

    public abstract boolean isLoaded();

    public abstract @Nullable IRarity getRarity(@NotNull String rarityName);

    public abstract @Nullable IFish getFish(@NotNull String rarityName, @NotNull String fishName);

    public abstract @NotNull TreeMap<String, ? extends IRarity> getRarityMap();

}
