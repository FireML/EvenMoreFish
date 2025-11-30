package com.oheers.evenmorefish.addons;

import com.oheers.fish.api.addons.AddonLoader;
import com.oheers.fish.api.plugin.EMFPlugin;
import com.oheers.fish.api.utils.system.JavaSpecVersion;
import com.oheers.fish.api.utils.system.SystemUtils;
import com.oheers.fish.items.ItemConfigResolver;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;

import java.io.File;


public class ItemModelAddonLoader extends AddonLoader {

    public ItemModelAddonLoader(EMFPlugin plugin, File addonFile) {
        super(plugin, addonFile);
    }

    @Override
    public boolean canLoad() {
        return SystemUtils.isJavaVersionAtLeast(JavaSpecVersion.JAVA_21) && MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_21_R2);
    }

    @Override
    public void loadAddons() {
        System.out.println("Loading ItemModel Item Config.");
        ItemConfigResolver.getInstance().setItemModelResolver(ItemModelItemConfig::new);
    }


}
