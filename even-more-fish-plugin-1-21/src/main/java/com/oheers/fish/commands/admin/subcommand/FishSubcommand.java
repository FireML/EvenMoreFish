package com.oheers.fish.commands.admin.subcommand;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.oheers.fish.FishUtils;
import com.oheers.fish.commands.BrigCommandUtils;
import com.oheers.fish.commands.CommandUtils;
import com.oheers.fish.commands.admin.AdminCommand;
import com.oheers.fish.commands.arguments.FishArgument;
import com.oheers.fish.commands.arguments.RarityArgument;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.strokkur.commands.annotations.DefaultExecutes;
import net.strokkur.commands.annotations.Executes;
import net.strokkur.commands.annotations.arguments.CustomArg;
import net.strokkur.commands.annotations.arguments.IntArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FishSubcommand {

    @DefaultExecutes
    public void onDefault(CommandSender sender) {
        AdminCommand.sendHelpMessage(sender);
    }

    @Executes
    public void execute(CommandSender sender, @CustomArg(RarityArgument.class) Rarity rarity, @CustomArg(FishArgument.class) String fishStr, @IntArg(min = 1) int amount, List<Player> targets) throws CommandSyntaxException {
        if (targets.isEmpty()) {
            throw BrigCommandUtils.ERROR_NO_PLAYERS.create();
        }

        Fish initialFish = FishArgument.resolveFish(rarity, fishStr);
        if (initialFish == null) {
            throw FishArgument.INVALID_FISH.create(fishStr);
        }

        targets.forEach(target -> {
            Fish fish = initialFish.createCopy();
            fish.init();

            if (fish.hasFishRewards()) {
                fish.getFishRewards().forEach(reward -> reward.rewardPlayer(target, target.getLocation()));
            }

            fish.setFisherman(target.getUniqueId());

            final ItemStack fishItem = fish.give();
            fishItem.setAmount(amount);

            FishUtils.giveItem(fishItem, target);
        });

        EMFMessage message = ConfigMessage.ADMIN_GIVE_PLAYER_FISH.getMessage();
        message.setVariable("{player}", CommandUtils.getPlayersVariable(targets));

        message.setFishCaught(initialFish.getName());
        message.send(sender);
    }

    @Executes
    public void execute(CommandSender sender, @CustomArg(RarityArgument.class) Rarity rarity, @CustomArg(FishArgument.class) String fishStr, @IntArg(min = 1) Integer amount) throws CommandSyntaxException {
        if (sender instanceof Player player) {
            execute(sender, rarity, fishStr, amount, List.of(player));
            return;
        }
        ConfigMessage.ADMIN_CANT_BE_CONSOLE.getMessage().send(sender);
    }

    @Executes
    public void execute(CommandSender sender, @CustomArg(RarityArgument.class) Rarity rarity, @CustomArg(FishArgument.class) String fishStr) throws CommandSyntaxException {
        execute(sender, rarity, fishStr, 1);
    }

}
