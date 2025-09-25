package org.gi.gICore;

import org.bukkit.plugin.java.JavaPlugin;

public final class GICore extends JavaPlugin {
    private static   GICore instance;
    @Override
    public void onEnable(){
        instance = this;

    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static GICore getInstance() {
        return instance;
    }
}
