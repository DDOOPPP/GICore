package org.gi.gICore.manager;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.loader.VaultLoader;
import org.gi.gICore.value.ValueName;

import java.util.Map;

public class EconomyManager {
    private static ComponentBuilder componentBuilder;
    private static Economy economy;
    private static String unit;
    private static String loreKey;
    public EconomyManager() {
        economy = VaultLoader.getEconomy();
        unit = economy.currencyNameSingular();
        loreKey = economy.currencyNamePlural();
        componentBuilder = new ComponentBuilder();
    }

    public Component format(double amount){
        return componentBuilder.translateNamed(getUnit(), Map.of(ValueName.AMOUNT,amount));
    }

    public boolean hasAccount(OfflinePlayer player){
        return economy.hasAccount(player);
    }

    public boolean createAccount(OfflinePlayer player){
        return economy.createPlayerAccount(player);
    }

    public double getBalance(OfflinePlayer player){
        return economy.getBalance(player);
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount){
        return economy.withdrawPlayer(player, amount);
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, double amount){
        return economy.depositPlayer(player, amount);
    }

    public boolean has(OfflinePlayer player, double amount){
        return economy.has(player, amount);
    }

    public static String getUnit(){
        if(unit != null){
            return unit;
        }

        if(economy == null){
            economy = VaultLoader.getEconomy();
        }
        return economy.currencyNameSingular();
    }

    public static String getLoreKey(){
        if (loreKey != null){
            return loreKey;
        }

        if(economy == null){
            economy = VaultLoader.getEconomy();
        }
        return economy.currencyNamePlural();
    }
}
