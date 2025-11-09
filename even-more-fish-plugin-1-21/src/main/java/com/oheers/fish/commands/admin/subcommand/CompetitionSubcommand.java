package com.oheers.fish.commands.admin.subcommand;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.commands.admin.AdminCommand;
import com.oheers.fish.commands.arguments.CompetitionFileArgument;
import com.oheers.fish.commands.arguments.CompetitionTypeArgument;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.competition.CompetitionType;
import com.oheers.fish.competition.configs.CompetitionFile;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import com.oheers.fish.utils.DurationFormatter;
import net.strokkur.commands.annotations.DefaultExecutes;
import net.strokkur.commands.annotations.Executes;
import net.strokkur.commands.annotations.arguments.CustomArg;
import net.strokkur.commands.annotations.arguments.IntArg;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class CompetitionSubcommand {

    @DefaultExecutes
    public void onDefault(CommandSender sender) {
        AdminCommand.sendHelpMessage(sender);
    }

    @Executes("start")
    public void onStart(CommandSender sender, @CustomArg(CompetitionFileArgument.class) CompetitionFile file, @IntArg(min = 1) Integer duration) {
        if (Competition.isActive()) {
            ConfigMessage.COMPETITION_ALREADY_RUNNING.getMessage().send(sender);
            return;
        }
        Competition competition = new Competition(file);
        competition.setAdminStarted(true);
        if (duration != null) {
            competition.setMaxDuration(duration);
        }
        competition.begin();
    }

    @Executes("start")
    public void onStart(CommandSender sender, @CustomArg(CompetitionFileArgument.class) CompetitionFile file) {
        onStart(sender, file, null);
    }

    @Executes("end")
    public void onEnd(CommandSender sender) {
        Competition active = Competition.getCurrentlyActive();
        if (active == null) {
            ConfigMessage.NO_COMPETITION_RUNNING.getMessage().send(sender);
            return;
        }
        active.end(false);
    }

    @Executes("test")
    public void onTest(CommandSender sender, @IntArg(min = 1) int duration, @CustomArg(CompetitionTypeArgument.class) CompetitionType type) {
        if (Competition.isActive()) {
            ConfigMessage.COMPETITION_ALREADY_RUNNING.getMessage().send(sender);
            return;
        }
        CompetitionFile file = new CompetitionFile("adminTest", type, duration);
        Competition competition = new Competition(file);
        competition.setAdminStarted(true);
        competition.begin();
    }

    @Executes("test")
    public void onTest(CommandSender sender, @IntArg(min = 1) int duration) {
        onTest(sender, duration, CompetitionType.LARGEST_FISH);
    }

    @Executes("test")
    public void onTest(CommandSender sender) {
        onTest(sender, 60, CompetitionType.LARGEST_FISH);
    }

    @Executes("extend")
    public void onExtend(CommandSender sender, @IntArg(min = 1) int duration) {
        Competition active = Competition.getCurrentlyActive();
        if (active == null) {
            ConfigMessage.NO_COMPETITION_RUNNING.getMessage().send(sender);
            return;
        }
        active.addTime(duration);

        EMFMessage message = ConfigMessage.COMPETITION_TIME_EXTENDED.getMessage();
        message.setVariable("{duration}", DurationFormatter.formatSeconds(duration));
        message.broadcast();
    }

}
