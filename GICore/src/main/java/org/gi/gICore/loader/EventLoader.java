package org.gi.gICore.loader;

import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.events.PlayerEvents;
import org.gi.gICore.util.ModuleLogger;

public class EventLoader {
    private static JavaPlugin plugin;
    private static ModuleLogger logger;
    public static void loadEvent(JavaPlugin core) {
        plugin = core;
        logger = new ModuleLogger(plugin,"EventLoader");

        plugin.getServer().getPluginManager().registerEvents(new PlayerEvents(plugin),plugin);


        logger.info("Event registered");
    }
}
