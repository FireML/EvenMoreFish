package com.oheers.fish.gui.base;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.commands.MainCommand;
import com.oheers.fish.database.DatabaseUtil;
import com.oheers.fish.gui.guis.BaitsGui;
import com.oheers.fish.gui.guis.FishJournalGui;
import com.oheers.fish.gui.guis.MainMenuGui;
import com.oheers.fish.gui.guis.SellGui;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.EMFSingleMessage;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import uk.firedev.messagelib.replacer.Replacer;

import java.util.TreeMap;
import java.util.function.Consumer;

public abstract class BaseConfigGui<T extends BaseGui> {

    private T gui;
    private final Replacer replacer = Replacer.replacer();
    protected final @NotNull Player player;
    protected final @NotNull Section config;
    protected final @NotNull TreeMap<String, Consumer<InventoryClickEvent>> actions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public BaseConfigGui(@NotNull Player player, @NotNull Section config) {
        this.player = player;
        this.config = config;
        loadDefaultActions();
    }

    public void init() {
        if (this.gui != null) {
            return;
        }
        this.gui = createGui();
        FillerLoader.getInstance().load(this);
        ItemLoader.getInstance().load(this);
    }

    public void init(@NotNull Consumer<T> initLogic) {
        init();
        initLogic.accept(gui);
    }

    public abstract T createGui();

    public @NotNull T getGui() {
        if (this.gui == null) {
            throw new IllegalStateException(getClass().getSimpleName() + "#init has not been called!");
        }
        return this.gui;
    }

    public void resetGui() {
        this.gui = null;
        init();
    }

    public @NotNull Section getConfig() {
        return this.config;
    }

    public void open() {
        getGui().open(player);
    }

    public EMFSingleMessage getTitle() {
        return EMFSingleMessage.fromString(config.getString("title", "Inventory"));
    }

    public GuiType getGuiType() {
        return FishUtils.getEnumValue(GuiType.class, config.getString("type"), GuiType.CHEST);
    }

    public int getRows() {
        return config.getInt("rows", 6);
    }

    public int getPageSize() {
        return config.getInt("page-size", 54);
    }

    public void editReplacer(Consumer<Replacer> edit) {
        edit.accept(replacer);
    }

    public abstract void doRescue();

    // Loading actions. Big single method :D

    private void loadDefaultActions() {
        actions.put("close", event -> {
            doRescue();
            event.getWhoClicked().closeInventory();
        });
        actions.put("full-exit", event -> {
            doRescue();
            event.getWhoClicked().closeInventory();
        });
        actions.put("open-main-menu", event -> {
            doRescue();
            new MainMenuGui(player).open();
        });
        actions.put("fish-toggle", event -> {
            EvenMoreFish.getInstance().performFishToggle(player);
            // TODO look into possible performance issues.
            resetGui();
            open();
        });
        actions.put("open-shop", event -> {
            doRescue();
            new SellGui(player, SellGui.SellState.NORMAL, null).open();
        });
        actions.put("show-command-help", event -> {
            doRescue();
            player.closeInventory();
            MainCommand.HELP_MESSAGE.sendMessage(player);
        });
        actions.put("open-journal-menu", event -> {
            doRescue();
            if (!DatabaseUtil.isDatabaseOnline()) {
                ConfigMessage.JOURNAL_DISABLED.getMessage().send(player);
                player.closeInventory();
                return;
            }
            new FishJournalGui(player, null).open();
        });
        actions.put("open-baits-menu", event -> {
            doRescue();
            new BaitsGui(player).open();
        });
    }

}
