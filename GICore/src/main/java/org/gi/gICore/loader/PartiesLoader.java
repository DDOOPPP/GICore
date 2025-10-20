package org.gi.gICore.loader;

import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.util.ModuleLogger;

public class PartiesLoader {
    private static ModuleLogger logger;
    private static JavaPlugin plugin;
    public static boolean load(JavaPlugin core){
        logger = new ModuleLogger(core,"PartiesLoader");
        plugin = core;

        if (plugin.getServer().getPluginManager().getPlugin("Parties") == null){
            logger.error("PartiesLoader plugin not found");

            return false;
        }
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Parties")){
            logger.error("PartiesLoader plugin not Loaded");

            return false;
        }
        plugin.getLogger().info("PartiesLoader loaded");
        return true;
    }
}
