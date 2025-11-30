package com.oheers.fish.utils;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.jetbrains.annotations.NotNull;

public class MinecraftVersionHelper {

    public static boolean isAtLeastVersion(@NotNull String versionStr) {
        MinecraftVersion version;
        try {
            version = MinecraftVersion.valueOf(versionStr.toUpperCase());
            return MinecraftVersion.isAtLeastVersion(version);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

}
