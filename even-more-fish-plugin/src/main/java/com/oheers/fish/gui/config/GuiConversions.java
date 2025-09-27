package com.oheers.fish.gui.config;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.Logging;
import com.oheers.fish.config.ConfigBase;
import com.oheers.fish.config.ConfigUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO map character and item to row/column. Basic math but i'm dumb
public class GuiConversions {

    private final String guiYml = "test.yml";
    private final String guiFillerYml = "gui-fillers.yml";

    public void performCheck() {
        // TODO aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
        if (!new File(EvenMoreFish.getInstance().getDataFolder(), this.guiYml).exists()) {
            return;
        }
        ConfigBase guiYml = resolveConfigBase(this.guiYml);
        ConfigBase fillerYml = resolveConfigBase(this.guiFillerYml);
        Section generalSection = ConfigUtils.getOrCreateSection(guiYml.getConfig(), "general");

        handleSection(guiYml, "main-menu", "guis/main-menu.yml");
        handleSection(guiYml, "sell-menu-normal", "guis/sell-normal.yml");
        handleSection(guiYml, "sell-menu-confirm", "guis/sell-confirm.yml");
        handleSection(guiYml, "baits-menu", "guis/baits.yml");
        handleSection(guiYml, "apply-baits-menu", "guis/apply-baits.yml");
        handleSection(guiYml, "journal-menu", "guis/journal-main.yml");
        handleSection(guiYml, "journal-rarity", "guis/journal-rarity.yml");
    }

    private void handleSection(@NotNull ConfigBase guiYml, @NotNull String sectionName, @NotNull String fileName) {
        Section section = guiYml.getConfig().getSection(sectionName);
        if (section != null) {
            Map<Character, List<String>> mappedChars = mapCharacterToSlots(section.getStringList("layout"));
            ConfigBase base = resolveConfigBase(fileName);
            mapSectionToFile(section, base, mappedChars);
        }
    }

    private ConfigBase resolveConfigBase(@NotNull String name) {
        return new ConfigBase(resolveFile(name), EvenMoreFish.getInstance(), false);
    }

    private File resolveFile(@NotNull String name) {
        return new File(EvenMoreFish.getInstance().getDataFolder(), name);
    }

    private void finalizeConversion(@NotNull ConfigBase guisConfig) {
        // Rename the file to guis.yml.old
        File file = guisConfig.getFile();
        file.renameTo(new File(EvenMoreFish.getInstance().getDataFolder(), guiYml + ".old"));
        file.delete();

        Logging.info("<yellow>Your GUI configs have been automatically converted to the new format.");
    }

    private void mapSectionToFile(@NotNull Section section, @NotNull ConfigBase file, @NotNull Map<Character, List<String>> mappedChars) {
        YamlDocument config = file.getConfig();
        config.setAll(section.getRouteMappedValues(true));

        for (String sectionName : config.getRoutesAsStrings(false)) {
            if (config.get(sectionName + ".item") == null) {
                continue;
            }
            config.move(sectionName, "items." + sectionName);
            Section subsection = config.getSection("items." + sectionName);
            updateItemFormat(subsection, mappedChars);
        }

        file.save();
    }

    private void updateItemFormat(@NotNull Section itemSection, @NotNull Map<Character, List<String>> mappedChars) {
        Character character = itemSection.getChar("character");
        if (character == null || !mappedChars.containsKey(character)) {
            // Wipes the section.
            itemSection.clear();
            return;
        }
        itemSection.remove("character");
        itemSection.set("locations", mappedChars.getOrDefault(character, List.of()));
    }

    // This could maybe be optimized? But it does work as intended, so I cannot complain.
    private Map<Character, List<String>> mapCharacterToSlots(@NotNull List<String> format) {
        Map<Character, List<String>> map = new HashMap<>();

        int checkingRow = 1;
        for (String line : format) {
            int checkingColumn = 1;
            for (char character : line.toCharArray()) {
                List<String> pairs = map.computeIfAbsent(character, ArrayList::new);
                pairs.add(checkingRow + "," + checkingColumn);
                checkingColumn++;
            }
            checkingRow++;
        }

        return map;
    }

}
