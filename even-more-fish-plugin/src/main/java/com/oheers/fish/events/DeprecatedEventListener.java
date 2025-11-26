package com.oheers.fish.events;

import com.oheers.fish.api.EMFFishEvent;
import com.oheers.fish.api.events.EMFFishCaughtEvent;
import com.oheers.fish.api.events.EMFFishHuntEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Used for handling deprecated EMF events until they are removed.
 */
@SuppressWarnings("removal")
public class DeprecatedEventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCaughtEvent(EMFFishCaughtEvent event) {
        EMFFishEvent deprecated = new EMFFishEvent(event.getFish(), event.getPlayer(), event.getCatchTime());
        if (!deprecated.callEvent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHuntEvent(EMFFishHuntEvent event) {
        com.oheers.fish.api.EMFFishHuntEvent deprecated = new com.oheers.fish.api.EMFFishHuntEvent(event.getFish(), event.getPlayer(), event.getHuntTime());
        if (!deprecated.callEvent()) {
            event.setCancelled(true);
        }
    }

}
