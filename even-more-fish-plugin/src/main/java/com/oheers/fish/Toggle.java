package com.oheers.fish;

import com.oheers.fish.messages.ConfigMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class Toggle {

    private final EvenMoreFish plugin;
    private final NamespacedKey fishToggleKey;
    private final NamespacedKey bossBarToggleKey;

    public Toggle(@NotNull EvenMoreFish plugin) {
        this.plugin = plugin;
        this.fishToggleKey = new NamespacedKey(plugin, "fish-disabled");
        this.bossBarToggleKey = new NamespacedKey(plugin, "bossbar-disabled");
    }

    // Fish Toggle

    public void performFishToggle(@NotNull Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        // If custom fishing is disabled
        if (isCustomFishingDisabled(player)) {
            // Set fish-disabled to false
            pdc.set(fishToggleKey, PersistentDataType.BOOLEAN, false);
            ConfigMessage.TOGGLE_FISHING_ON.getMessage().send(player);
        } else {
            // Set fish-disabled to true
            pdc.set(fishToggleKey, PersistentDataType.BOOLEAN, true);
            ConfigMessage.TOGGLE_FISHING_OFF.getMessage().send(player);
        }
    }


    public boolean isCustomFishingDisabled(@NotNull Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(fishToggleKey, PersistentDataType.BOOLEAN, false);
    }

    // Bossbar Toggle

    public void performBossBarToggle(@NotNull Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        // If custom fishing is disabled
        if (isBossBarDisabled(player)) {
            // Set fish-disabled to false
            pdc.set(bossBarToggleKey, PersistentDataType.BOOLEAN, false);
            ConfigMessage.TOGGLE_BOSSBAR_ON.getMessage().send(player);
        } else {
            // Set fish-disabled to true
            pdc.set(bossBarToggleKey, PersistentDataType.BOOLEAN, true);
            ConfigMessage.TOGGLE_BOSSBAR_OFF.getMessage().send(player);
        }
    }


    public boolean isBossBarDisabled(@NotNull Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(bossBarToggleKey, PersistentDataType.BOOLEAN, false);
    }

}
