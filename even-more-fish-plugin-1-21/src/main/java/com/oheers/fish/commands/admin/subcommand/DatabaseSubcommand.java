package com.oheers.fish.commands.admin.subcommand;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.commands.CommandUtils;
import com.oheers.fish.commands.admin.AdminCommand;
import net.strokkur.commands.annotations.DefaultExecutes;
import net.strokkur.commands.annotations.Executes;
import net.strokkur.commands.annotations.Permission;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DatabaseSubcommand {

    @DefaultExecutes
    public void onDefault(CommandSender sender) {
        AdminCommand.sendHelpMessage(sender);
    }

    @Executes("drop-flyway")
    @Permission("emf.admin.debug.database.flyway")
    public void onDropFlyway(CommandSender sender) {
        if (CommandUtils.isLogDbError(sender)) {
            return;
        }
        EvenMoreFish.getInstance().getPluginDataManager().getDatabase().getMigrationManager().dropFlywaySchemaHistory();
        sender.sendMessage("Dropped flyway schema history.");
    }

    @Executes("repair-flyway")
    @Permission("emf.admin.debug.database.flyway")
    public void onRepairFlyway(CommandSender sender) {
        if (CommandUtils.isLogDbError(sender)) {
            return;
        }
        sender.sendMessage("Attempting to repair the migrations, check the logs.");
        EvenMoreFish.getInstance().getPluginDataManager().getDatabase().getMigrationManager().repairFlyway();
    }

    @Executes("clean-flyway")
    @Permission("emf.admin.debug.database.clean")
    public void onCleanFlyway(CommandSender sender) {
        if (CommandUtils.isLogDbError(sender)) {
            return;
        }
        sender.sendMessage("Attempting to clean flyway, check the logs.");
        EvenMoreFish.getInstance().getPluginDataManager().getDatabase().getMigrationManager().cleanFlyway();
    }

    @Executes("migrate-to-latest")
    @Permission("emf.admin.debug.database.migrate")
    public void onMigrateToLatest(CommandSender sender) {
        if (CommandUtils.isLogDbError(sender)) {
            return;
        }
        EvenMoreFish.getInstance().getPluginDataManager().getDatabase().migrateFromDatabaseVersionToLatest();
    }

    @DefaultExecutes
    public void onHelp(CommandSender sender) {
        sender.sendMessage("Available Commands: migrate-to-latest, clean-flyway, repair-flyway, drop-flyway");
    }
}
