package org.gi.gICore.loader;

import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.component.adapter.GIPlaceHolder;
import org.gi.gICore.util.ModuleLogger;

public class PlaceHolderLoader {
    private static ModuleLogger logger;
    private static JavaPlugin plugin;
    public static boolean PlaceHolderLoad(JavaPlugin core){
        plugin = core;

        if (logger == null){
            logger = new ModuleLogger(plugin,"PlaceHolderLoader");
        }

        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null){
            logger.error("PlaceholderAPI is not found");
            return false;
        }

        new GIPlaceHolder().register();
        logger.info("PlaceHolder registered");
        return true;
    }
}
