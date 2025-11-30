package com.oheers.fish.selling;

import com.oheers.fish.FishUtils;
import com.oheers.fish.fishing.items.Fish;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SoldFish {

    private final Fish fish;
    private final String name;
    private final String rarity;
    private int amount;
    
    private double totalValue;
    private final double length;

    /**
     * Create a SoldFish instance from an ItemStack
     * @param item The ItemStack representing the fish being sold
     * @throws IllegalArgumentException if the ItemStack does not represent a valid fish
     */
    public SoldFish(@NotNull ItemStack item) throws IllegalArgumentException {
        Fish fish = FishUtils.getFish(item);
        if (fish == null) {
            throw new IllegalArgumentException("Item is not a fish.");
        }
        Optional<Double> worth = WorthNBT.getValue(fish);
        if (worth.isEmpty()) {
            throw new IllegalArgumentException("Fish has no worth.");
        }
        this.fish = fish;
        this.name = fish.getName();
        this.rarity = fish.getRarity().getId();
        this.length = fish.getLength();
        this.amount = item.getAmount();
        this.totalValue = worth.get() * this.amount;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }
    
    public @NotNull String getName() {
        return name;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public double getTotalValue() {
        return totalValue;
    }
    
    public @NotNull String getRarity() {
        return rarity;
    }
    
    public double getLength() {
        return length;
    }

    public @NotNull Fish getFish() {
        return fish;
    }

}
