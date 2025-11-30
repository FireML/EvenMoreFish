package com.oheers.fish.commands.main;

import com.oheers.fish.Checks;
import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.economy.Economy;
import com.oheers.fish.commands.CommandUtils;
import com.oheers.fish.commands.HelpMessageBuilder;
import com.oheers.fish.commands.admin.AdminCommand;
import com.oheers.fish.commands.arguments.RarityArgument;
import com.oheers.fish.commands.main.subcommand.JournalSubcommand;
import com.oheers.fish.commands.main.subcommand.ShopSubcommand;
import com.oheers.fish.commands.main.subcommand.ToggleSubcommand;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.database.DatabaseUtil;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.gui.guis.ApplyBaitsGui;
import com.oheers.fish.gui.guis.FishJournalGui;
import com.oheers.fish.gui.guis.MainMenuGui;
import com.oheers.fish.gui.guis.SellGui;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.PrefixType;
import com.oheers.fish.messages.abstracted.EMFMessage;
import com.oheers.fish.permissions.AdminPerms;
import com.oheers.fish.permissions.UserPerms;
import com.oheers.fish.selling.SellHelper;
import net.strokkur.commands.annotations.*;
import net.strokkur.commands.annotations.arguments.CustomArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Command("emf")
public class MainCommand {

    private static final HelpMessageBuilder HELP_MESSAGE = HelpMessageBuilder.create()
        .addUsage(MainConfig.getInstance().getAdminSubCommandName(), ConfigMessage.HELP_GENERAL_ADMIN::getMessage)
        .addUsage(MainConfig.getInstance().getHelpSubCommandName(), ConfigMessage.HELP_GENERAL_HELP::getMessage)
        .addUsage(MainConfig.getInstance().getGuiSubCommandName(), ConfigMessage.HELP_GENERAL_GUI::getMessage)
        .addUsage(MainConfig.getInstance().getTopSubCommandName(), ConfigMessage.HELP_GENERAL_TOP::getMessage)
        .addUsage(MainConfig.getInstance().getSellAllSubCommandName(), ConfigMessage.HELP_GENERAL_SELLALL::getMessage)
        .addUsage(MainConfig.getInstance().getApplyBaitsSubCommandName(), ConfigMessage.HELP_GENERAL_APPLYBAITS::getMessage)
        .addUsage(MainConfig.getInstance().getJournalSubCommandName(), ConfigMessage.HELP_GENERAL_JOURNAL::getMessage)
        .addUsage(MainConfig.getInstance().getNextSubCommandName(), ConfigMessage.HELP_GENERAL_NEXT::getMessage)
        .addUsage(MainConfig.getInstance().getToggleSubCommandName(), ConfigMessage.HELP_GENERAL_TOGGLE::getMessage);


    @Subcommand("admin")
    AdminCommand adminCommand = new AdminCommand();

    @Subcommand("shop")
    ShopSubcommand shopSubcommand = new ShopSubcommand();

    @Subcommand("journal")
    JournalSubcommand journalSubcommand = new JournalSubcommand();

    @Subcommand("toggle")
    ToggleSubcommand toggleSubcommand = new ToggleSubcommand();

    @DefaultExecutes
    public void onDefault(@NotNull CommandSender sender) {
        if (!sender.hasPermission(UserPerms.GUI) || MainConfig.getInstance().useOldBaseCommandBehavior()) {
            sendHelpMessage(sender);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendHelpMessage(sender);
            return;
        }

        new MainMenuGui(player).open();
    }

    @Executes("next")
    @Permission(UserPerms.NEXT)
    public void onNext(CommandSender sender) {
        EMFMessage message = Competition.getNextCompetitionMessage();
        message.prependMessage(PrefixType.DEFAULT.getPrefix());
        message.send(sender);
    }

    @Executes("help")
    @Permission(UserPerms.HELP)
    public void onHelp(CommandSender sender) {
        sendHelpMessage(sender);
    }

    @Executes("gui")
    @Permission(UserPerms.GUI)
    public void onGui(CommandSender sender, @Executor Player player) {
        new MainMenuGui(player).open();
    }

    @Executes("top")
    @Permission(UserPerms.TOP)
    public void onTop(CommandSender sender) {
        Competition active = Competition.getCurrentlyActive();
        if (active == null) {
            ConfigMessage.NO_COMPETITION_RUNNING.getMessage().send(sender);
            return;
        }

        active.sendLeaderboard(sender);
    }

    @Executes("sellall")
    @Permission(UserPerms.SELL_ALL)
    public void onSellAll(CommandSender sender, @Executor Player player) {
        if (CommandUtils.isEconomyEnabled(player)) {
            new SellHelper(player.getInventory(), player).sell();
        }
    }

    @Executes("applybaits")
    @Permission(UserPerms.APPLYBAITS)
    public void onApplyBaits(CommandSender sender, @NotNull @Executor Player player) {
        if (!Checks.canUseRod(player.getInventory().getItemInMainHand())) {
            ConfigMessage.BAIT_INVALID_ROD.getMessage().send(player);
            return;
        }
        new ApplyBaitsGui(player).open();
    }

    public static void sendHelpMessage(CommandSender sender) {
        HELP_MESSAGE.sendMessage(sender);
    }

}
