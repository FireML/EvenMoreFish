package com.oheers.fish.api.fishing.items;

import com.oheers.fish.api.requirement.Requirement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An interface representing a rarity in the EvenMoreFish plugin.
 * @apiNote This interface should not be implemented by users.
 */
public interface IRarity {

    @NotNull String getId();

    boolean isDisabled();

    // TODO @NotNull EMFMessage getFormat();

    // TODO @NotNull EMFMessage format(@NotNull String name);

    double getWeight();

    boolean getAnnounce();

    boolean getUseConfigCasing();

    // TODO @NotNull EMFMessage getDisplayName();

    // TODO @NotNull EMFMessage getLorePrep();

    @Nullable String getPermission();

    @NotNull Requirement getRequirement();

    boolean isShouldDisableFisherman();

    double getMinSize();

    double getMaxSize();

    boolean hasCompExemptFish();

    /**
     * @return This rarity's original list of loaded fish
     */
    @NotNull List<? extends IFish> getOriginalFishList();

    /**
     * @return This rarity's list of loaded fish, but each fish is a clone of the original
     */
    @NotNull List<? extends IFish> getFishList();

    @Nullable IFish getEditableFish(@NotNull String name);

    @Nullable IFish getFish(@NotNull String name);

    double getWorthMultiplier();

    boolean isFishWeighted();

    void setFishWeighted(boolean fishWeighted);

}
