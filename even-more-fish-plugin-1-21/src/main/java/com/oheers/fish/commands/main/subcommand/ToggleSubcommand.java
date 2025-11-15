package com.oheers.fish.commands.main.subcommand;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.permissions.UserPerms;
import net.strokkur.commands.annotations.DefaultExecutes;
import net.strokkur.commands.annotations.Executes;
import net.strokkur.commands.annotations.Executor;
import net.strokkur.commands.annotations.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Permission(UserPerms.TOGGLE)
public class ToggleSubcommand {

    @DefaultExecutes
    public void onDefault(CommandSender sender, @Executor Player player) {
        EvenMoreFish.getInstance().getToggle().performFishToggle(player);
    }

    @Executes
    public void executes(CommandSender sender, @Executor Player player) {
        onDefault(sender, player);
    }

    @Executes("fishing")
    public void onFishing(CommandSender sender, @Executor Player player) {
        onDefault(sender, player);
    }

    @Executes("bossbar")
    public void onBossBar(CommandSender sender, @Executor Player player) {
        EvenMoreFish.getInstance().getToggle().performBossBarToggle(player);
    }

}
