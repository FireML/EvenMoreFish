package com.oheers.fish.gui.config;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class GuiConfig {

    private static GuiConfig instance;

    private final ConfigBase applyBaits;
    private final ConfigBase baits;
    private final ConfigBase journalMain;
    private final ConfigBase journalRarity;
    private final ConfigBase mainMenu;
    private final ConfigBase sellNormal;
    private final ConfigBase sellConfirm;

    public GuiConfig() {
        instance = this;
        applyBaits = new ConfigBase("guis/apply-baits.yml", "guis/apply-baits.yml", EvenMoreFish.getInstance(), false);
        baits = new ConfigBase("guis/baits.yml", "guis/baits.yml", EvenMoreFish.getInstance(), false);
        journalMain = new ConfigBase("guis/journal-main.yml", "guis/journal-main.yml", EvenMoreFish.getInstance(), false);
        journalRarity = new ConfigBase("guis/journal-rarity.yml", "guis/journal-rarity.yml", EvenMoreFish.getInstance(), false);
        mainMenu = new ConfigBase("guis/main-menu.yml", "guis/main-menu.yml", EvenMoreFish.getInstance(), false);
        sellNormal = new ConfigBase("guis/sell-normal.yml", "guis/sell-normal.yml", EvenMoreFish.getInstance(), false);
        sellConfirm = new ConfigBase("guis/sell-confirm.yml", "guis/sell-confirm.yml", EvenMoreFish.getInstance(), false);
    }

    public static @NotNull GuiConfig getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GuiConfig has not been initialized! (This is a bug!)");
        }
        return instance;
    }

    public void reload() {
        applyBaits.reload();
        baits.reload();
        journalMain.reload();
        journalRarity.reload();
        mainMenu.reload();
        sellNormal.reload();
        sellConfirm.reload();
    }

    public ConfigBase getApplyBaits() {
        return applyBaits;
    }

    public ConfigBase getBaits() {
        return baits;
    }

    public ConfigBase getJournalMain() {
        return journalMain;
    }

    public ConfigBase getJournalRarity() {
        return journalRarity;
    }

    public ConfigBase getMainMenu() {
        return mainMenu;
    }

    public ConfigBase getSellNormal() {
        return sellNormal;
    }

    public ConfigBase getSellConfirm() {
        return sellConfirm;
    }

}
