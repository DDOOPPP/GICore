package org.gi.gICore.loader;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.GICore;
import org.gi.gICore.component.adapter.GIEconomy;
import org.gi.gICore.util.ModuleLogger;

public class VaultLoader {
    private static JavaPlugin plugin;
    private static GIEconomy giEconomy;
    private static Economy economy;
    private static ModuleLogger logger;

    public static boolean loadVault(JavaPlugin core) {
        plugin = core;
        logger = new ModuleLogger(plugin,"VaultLoader");

        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            logger.error("Vault is not found");
            return false;
        }
        giEconomy = new GIEconomy();

        plugin.getServer().getServicesManager().register(Economy.class, giEconomy, plugin, ServicePriority.High);

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            logger.error("Cannot find Vault economy service provider");
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public static GIEconomy getGiEconomy() {
        return giEconomy;
    }

    public static Economy getEconomy() {
        return economy;
    }
}
