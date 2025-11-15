package com.oheers.fish.commands.main.subcommand;

import com.oheers.fish.commands.arguments.RarityArgument;
import com.oheers.fish.commands.main.MainCommand;
import com.oheers.fish.database.DatabaseUtil;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.gui.guis.FishJournalGui;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.permissions.UserPerms;
import net.strokkur.commands.annotations.DefaultExecutes;
import net.strokkur.commands.annotations.Executes;
import net.strokkur.commands.annotations.Executor;
import net.strokkur.commands.annotations.Permission;
import net.strokkur.commands.annotations.arguments.CustomArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Permission(UserPerms.JOURNAL)
public class JournalSubcommand {

    @DefaultExecutes
    public void onDefault(CommandSender sender) {
        MainCommand.sendHelpMessage(sender);
    }

    @Executes
    public void execute(CommandSender sender, @Executor Player player, @CustomArg(RarityArgument.class) Rarity rarity) {
        if (!DatabaseUtil.isDatabaseOnline()) {
            ConfigMessage.JOURNAL_DISABLED.getMessage().send(player);
            return;
        }
        new FishJournalGui(player, rarity).open();
    }

    @Executes
    public void execute(CommandSender sender, @Executor Player player) {
        execute(sender, player, null);
    }

}
