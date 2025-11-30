package com.oheers.fish.commands.main.subcommand;

import com.oheers.fish.api.economy.Economy;
import com.oheers.fish.commands.main.MainCommand;
import com.oheers.fish.gui.SellState;
import com.oheers.fish.gui.guis.SellGui;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import com.oheers.fish.permissions.UserPerms;
import net.strokkur.commands.annotations.DefaultExecutes;
import net.strokkur.commands.annotations.Executes;
import net.strokkur.commands.annotations.Executor;
import net.strokkur.commands.annotations.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Permission(UserPerms.SHOP)
public class ShopSubcommand {

    @DefaultExecutes
    public void onDefault(CommandSender sender) {
        MainCommand.sendHelpMessage(sender);
    }

    @Executes
    public void execute(CommandSender sender, @Executor Player player, Player target) {
        if (!Economy.getInstance().isEnabled()) {
            ConfigMessage.ECONOMY_DISABLED.getMessage().send(player);
            return;
        }
        new SellGui(target, SellState.NORMAL, null).open();

        if (!target.equals(player)) {
            EMFMessage message = ConfigMessage.ADMIN_OPEN_FISH_SHOP.getMessage();
            message.setPlayer(target);
            message.send(player);
        }
    }

    @Executes
    public void execute(CommandSender sender, @Executor Player player) {
        execute(sender, player, player);
    }

}
