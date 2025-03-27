package com.oheers.fish.api.fishing.items;

import com.oheers.fish.api.requirement.Requirement;
import com.oheers.fish.api.reward.Reward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface IFish {

    double getWorthMultiplier();

    boolean hasEatRewards();

    boolean hasFishRewards();

    boolean hasSellRewards();

    boolean hasInteractRewards();

    void init();

    void checkDisplayName();

    void checkEatEvent();

    void checkFishEvent();

    void checkSellEvent();

    void checkInteractEvent();

    void checkSilent();

    @NotNull IFish createCopy();

    boolean hasFishermanDisabled();

    @Nullable UUID getFisherman();

    void setFisherman(UUID fisherman);

    boolean isCompExemptFish();

    void setCompExemptFish(boolean compExemptFish);

    double getSetWorth();

    @NotNull String getName();

    @NotNull IRarity getRarity();

    float getLength();

    void setLength(@Nullable Float length);

    @NotNull List<Reward> getActionRewards();

    @NotNull List<Reward> getFishRewards();

    @NotNull List<Reward> getSellRewards();

    double getWeight();

    void setWeight(double weight);

    // TODO @NotNull EMFMessage getDisplayName();

    @NotNull Requirement getRequirement();

    void setRequirement(@NotNull Requirement requirement);

    boolean isWasBaited();

    void setWasBaited(boolean wasBaited);

    int getDay();

    void setDay(int day);

    boolean isSilent();

    void setSilent(boolean silent);

    @NotNull String parseEventPlaceholders(@NotNull String rewardString);

}
