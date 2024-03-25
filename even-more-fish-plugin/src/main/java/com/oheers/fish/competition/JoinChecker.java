package com.oheers.fish.competition;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.config.messages.ConfigMessage;
import com.oheers.fish.config.messages.Message;
import com.oheers.fish.database.DataManager;
import com.oheers.fish.database.FishReport;
import com.oheers.fish.database.Table;
import com.oheers.fish.database.UserReport;
import com.oheers.fish.exceptions.InvalidTableException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class JoinChecker implements Listener {
    
    /**
     * Reads all the database information for the user specified.
     *
     * @param userUUID The user UUID having their data read.
     * @param userName the in-game username of the user having their data read.
     */
    public void databaseRegistration(UUID userUUID, String userName) {
        if (MainConfig.getInstance().isDatabaseOnline()) {
            EvenMoreFish.getScheduler().runTaskAsynchronously(() -> {
                List<FishReport> fishReports = new ArrayList<>();

                try {
                    if (EvenMoreFish.getInstance().getDatabaseV3().hasUser(userUUID, Table.EMF_FISH_LOG)) {
                        fishReports = EvenMoreFish.getInstance().getDatabaseV3().getFishReports(userUUID);
                    } else {
                        fishReports = new ArrayList<>();
                        if (MainConfig.getInstance().doDBVerbose())
                            EvenMoreFish.getInstance().getLogger().info(userName + " has joined for the first time, creating new data handle for them.");
                    }
                } catch (InvalidTableException exception) {
                    EvenMoreFish.getInstance().getLogger().severe("Failed to check database existence of user " + userUUID);
                    exception.printStackTrace();
                }

                UserReport userReport;

                userReport = EvenMoreFish.getInstance().getDatabaseV3().readUserReport(userUUID);
                if (userReport == null) {
                    EvenMoreFish.getInstance().getDatabaseV3().createUser(userUUID);
                    userReport = EvenMoreFish.getInstance().getDatabaseV3().readUserReport(userUUID);
                }

                if (fishReports != null && userReport != null) {
                    DataManager.getInstance().cacheUser(userUUID, userReport, fishReports);
                } else {
                    EvenMoreFish.getInstance().getLogger().severe("Null value when fetching data for user (" + userName + "),\n" +
                            "UserReport: " + (userReport == null) +
                            ",\nFishReports: " + (fishReports != null && !fishReports.isEmpty()));
                }
            });
        }
    }
    
    // Gives the player the active fishing bar if there's a fishing event cracking off
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Competition.isActive()) {
            EvenMoreFish.getInstance().getActiveCompetition().getStatusBar().addPlayer(event.getPlayer());
            Message startMessage = EvenMoreFish.getInstance().getActiveCompetition().getStartMessage();
            if (startMessage != null) startMessage.setMessage(ConfigMessage.COMPETITION_JOIN);
            EvenMoreFish.getScheduler().runTaskLater(() -> EvenMoreFish.getInstance().getActiveCompetition().getStartMessage().broadcast(event.getPlayer(), true, true), 20 * 3);
        }

        EvenMoreFish.getScheduler().runTaskAsynchronously(() -> databaseRegistration(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
    }
    
    // Removes the player from the bar list if they leave the server
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        
        if (Competition.isActive()) {
            EvenMoreFish.getInstance().getActiveCompetition().getStatusBar().removePlayer(event.getPlayer());
        }
        
        if (MainConfig.getInstance().isDatabaseOnline()) {
            EvenMoreFish.getScheduler().runTaskAsynchronously(() -> {
                UUID userUUID = event.getPlayer().getUniqueId();
                try {
                    if (!EvenMoreFish.getInstance().getDatabaseV3().hasUser(userUUID, Table.EMF_USERS)) {
                        EvenMoreFish.getInstance().getDatabaseV3().createUser(userUUID);
                    }
                } catch (InvalidTableException exception) {
                    EvenMoreFish.getInstance().getLogger().severe("Fatal error when running database checks for " + event.getPlayer().getName() + ", deleting data in primary storage.");
                    exception.printStackTrace();
                    return;
                }

                List<FishReport> fishReports = DataManager.getInstance().getFishReportsIfExists(userUUID);
                if (fishReports != null) {

                    EvenMoreFish.getInstance().getDatabaseV3().writeFishReports(userUUID, fishReports);

                }

                UserReport userReport = DataManager.getInstance().getUserReportIfExists(userUUID);
                if (userReport != null) {

                    EvenMoreFish.getInstance().getDatabaseV3().writeUserReport(userUUID, userReport);

                }

                DataManager.getInstance().uncacheUser(userUUID);
            });
        }
    }
}
