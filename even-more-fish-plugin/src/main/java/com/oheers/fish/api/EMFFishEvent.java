package com.oheers.fish.api;

import com.oheers.fish.fishing.items.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;

public class EMFFishEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Fish fish;
    private final Player player;
    private final Event originalEvent;
    private boolean cancel;

    public EMFFishEvent(@NotNull Fish fish, @NotNull Player player, @NotNull Event originalEvent) {
        this.fish = fish;
        this.player = player;
        this.originalEvent = originalEvent;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return The fish that the player is receiving
     */
    public @NotNull Fish getFish() {
        return fish;
    }

    /**
     * @return The player that has fished the fish
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Event getOriginalEvent() {
        return originalEvent;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}