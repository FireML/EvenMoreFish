package com.oheers.fish.gui.config;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.Logging;
import com.oheers.fish.config.ConfigBase;
import com.oheers.fish.config.ConfigUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.Comments;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.utils.format.NodeRole;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// TODO map filler and general items to the correct areas, and convert the filler key to the new section.
public class GuiConversions {

    private final String guiYml = "guis.yml";
    private final ConfigBase gui = resolveConfigBase(guiYml);
    private final ConfigBase guiFiller = resolveConfigBase("gui-fillers.yml");
    private final Section generalSection;

    public GuiConversions() {
        guiFiller.getConfig().remove("version");
        generalSection = ConfigUtils.getOrCreateSection(gui.getConfig(), "general");
        if (generalSection.contains("first-page")) {
            generalSection.set("first-page.character", "f");
        }
        if (generalSection.contains("previous-page")) {
            generalSection.set("previous-page.character", "p");
        }
        if (generalSection.contains("next-page")) {
            generalSection.set("next-page.character", "n");
        }
        if (generalSection.contains("last-page")) {
            generalSection.set("last-page.character", "l");
        }
    }

    public void performCheck() {
        // TODO aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
        if (!new File(EvenMoreFish.getInstance().getDataFolder(), this.guiYml).exists()) {
            return;
        }

        handleSection("main-menu", "guis/main-menu.yml");
        handleSection("sell-menu-normal", "guis/sell-normal.yml", section -> {
            section.remove("deposit-character");
        });
        handleSection("sell-menu-confirm", "guis/sell-confirm.yml", section -> {
            section.remove("deposit-character");
        });
        handleSection("baits-menu", "guis/baits.yml", section -> {
            section.remove("bait-character");
        });
        handleSection("apply-baits-menu", "guis/apply-baits.yml", section -> {
            section.remove("bait-character");
        });
        handleSection("journal-menu", "guis/journal-main.yml", section -> {
            section.remove("rarity-character");
        });
        handleSection("journal-rarity", "guis/journal-rarity.yml", section -> {
            section.remove("fish-character");
        });

        finalizeConversion();
    }

    private void handleSection(@NotNull String sectionName, @NotNull String fileName) {
        handleSection(sectionName, fileName, section -> {});
    }

    private void handleSection(@NotNull String sectionName, @NotNull String fileName, @NotNull Consumer<Section> initialChanges) {
        Section section = gui.getConfig().getSection(sectionName);
        if (section != null) {
            initialChanges.accept(section);
            Map<Character, List<String>> mappedChars = mapCharacterToSlots(section.getStringList("layout"));
            section.remove("layout");
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

    private void finalizeConversion() {
        // Rename the file to guis.yml.old
        File file = gui.getFile();
        file.renameTo(new File(EvenMoreFish.getInstance().getDataFolder(), guiYml + ".old"));
        file.delete();

        File fillerFile = guiFiller.getFile();
        fillerFile.renameTo(new File(EvenMoreFish.getInstance().getDataFolder(), "gui-fillers.yml.old"));
        fillerFile.delete();

        Logging.info("<yellow>Your GUI configs have been automatically converted to the new format.");
    }

    private void mapSectionToFile(@NotNull Section section, @NotNull ConfigBase file, @NotNull Map<Character, List<String>> mappedChars) {
        YamlDocument config = file.getConfig();
        config.setAll(guiFiller.getConfig().getRouteMappedValues(true));
        config.setAll(generalSection.getRouteMappedValues(true));
        config.setAll(section.getRouteMappedValues(true));

        updateFillerFormat(config);

        for (String sectionName : config.getRoutesAsStrings(false)) {
            if (config.get(sectionName + ".item") == null || config.get(sectionName + ".character") == null) {
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
            // Removes the invalid item from the config (please work)
            itemSection.getParent().remove(itemSection.getNameAsRoute());
            return;
        }
        itemSection.remove("character");
        List<String> locations = mappedChars.get(character);
        if (locations == null || locations.isEmpty()) {
            itemSection.getParent().remove(itemSection.getNameAsRoute());
            return;
        }
        itemSection.set("locations", locations);
    }

    private void updateFillerFormat(@NotNull Section section) {
        String material = section.getString("filler");
        if (material == null) {
            return;
        }
        section.remove("filler");
        Section fillerSection = section.createSection("filler");
        fillerSection.set("material", material);
        fillerSection.set("type", "ALL");
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
