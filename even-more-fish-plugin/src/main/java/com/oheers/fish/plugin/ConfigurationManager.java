package com.oheers.fish.plugin;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.config.MessageConfig;
import com.oheers.fish.gui.config.GuiConfig;
import com.oheers.fish.messages.EMFListMessage;
import com.oheers.fish.messages.EMFSingleMessage;
import uk.firedev.messagelib.ObjectProcessor;

import java.util.logging.Level;

public class ConfigurationManager {
    private final EvenMoreFish plugin;

    public ConfigurationManager(EvenMoreFish plugin) {
        this.plugin = plugin;
    }

    public void loadConfigurations() {
        try {
            prepareMessageLib();

            new MainConfig();
            new MessageConfig();
            new GuiConfig();

            plugin.getLogger().info("Successfully loaded all configurations");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load configurations", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public void reloadConfigurations() {
        try {
            plugin.reloadConfig();
            plugin.saveDefaultConfig();

            MainConfig.getInstance().reload();
            MessageConfig.getInstance().reload();
            GuiConfig.getInstance().reload();

            plugin.getLogger().info("Successfully reloaded all configurations");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to reload configurations", e);
        }
    }

    private void prepareMessageLib() {
        ObjectProcessor.registerProcessor(
            EMFSingleMessage.class,
            EMFSingleMessage::getComponentListMessage
        );
        ObjectProcessor.registerProcessor(
            EMFListMessage.class,
            EMFListMessage::getComponentListMessage
        );
    }

}