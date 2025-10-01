package org.gi.gICore.manager;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.gi.gICore.loader.VaultLoader;

public class EconomyManager {
    private Economy economy;

    public EconomyManager() {
        economy = VaultLoader.getEconomy();
    }

    public Component format(double amount){
        String unit = economy.currencyNameSingular();
        Component amountComponent = Component.text(String.format("%.0f", amount));
        Component unitComponent = Component.translatable(unit);
        return amountComponent.append(Component.text(" ")).append(unitComponent);
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

}
