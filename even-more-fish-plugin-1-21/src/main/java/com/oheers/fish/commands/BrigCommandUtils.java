package com.oheers.fish.commands;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;

@SuppressWarnings("UnstableApiUsage")
public class BrigCommandUtils {

    public static final SimpleCommandExceptionType ERROR_NO_PLAYERS = new SimpleCommandExceptionType(
        MessageComponentSerializer.message().serialize(Component.text("No players selected."))
    );

}
