package com.oheers.fish.gui.base;

import com.oheers.fish.FishUtils;
import com.oheers.fish.messages.EMFSingleMessage;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;
import java.util.function.Consumer;

public abstract class BaseConfigGui<T extends BaseGui> {

    private T gui;
    protected final HumanEntity player;
    protected final Section config;
    protected final TreeMap<String, Consumer<InventoryClickEvent>> actions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public BaseConfigGui(@NotNull HumanEntity player, @NotNull Section config) {
        this.player = player;
        this.config = config;
    }

    public void init() {
        if (this.gui != null) {
            return;
        }
        this.gui = createGui();
        FillerLoader.getInstance().load(this);
        ItemLoader.getInstance().load(this);
    }

    public abstract T createGui();

    public @NotNull T getGui() {
        if (this.gui == null) {
            throw new IllegalStateException(getClass().getSimpleName() + "#init has not been called!");
        }
        return this.gui;
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

}
