package org.gi.gICore.loader;

import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;
import org.gi.gICore.commands.CommandCore;
import org.gi.gICore.util.ModuleLogger;

public class CommandLoader {
    private static JavaPlugin plugin;
    private static ModuleLogger logger;

    public static void loadCommand(JavaPlugin core){
        plugin = core;

        if (logger == null){
            logger = new ModuleLogger(plugin,"CommandLoader");
        }


        plugin.getCommand("조회").setExecutor(new CommandCore());
        logger.info("Command registered");
    }
}
