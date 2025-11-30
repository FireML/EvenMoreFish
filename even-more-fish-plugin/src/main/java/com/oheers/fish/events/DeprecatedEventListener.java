package com.oheers.fish.events;

import com.oheers.fish.api.EMFFishEvent;
import com.oheers.fish.api.events.EMFFishCaughtEvent;
import com.oheers.fish.api.events.EMFFishHuntEvent;
import com.oheers.fish.fishing.items.Fish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Used for handling deprecated EMF events until they are removed.
 */
@SuppressWarnings("removal")
public class DeprecatedEventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCaughtEvent(EMFFishCaughtEvent event) {
        if (!(event.getFish() instanceof Fish emfFish)) {
            return;
        }
        EMFFishEvent deprecated = new EMFFishEvent(emfFish, event.getPlayer(), event.getCatchTime());
        if (!deprecated.callEvent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHuntEvent(EMFFishHuntEvent event) {
        if (!(event.getFish() instanceof Fish emfFish)) {
            return;
        }
        com.oheers.fish.api.EMFFishHuntEvent deprecated = new com.oheers.fish.api.EMFFishHuntEvent(emfFish, event.getPlayer(), event.getHuntTime());
        if (!deprecated.callEvent()) {
            event.setCancelled(true);
        }
    }

}
