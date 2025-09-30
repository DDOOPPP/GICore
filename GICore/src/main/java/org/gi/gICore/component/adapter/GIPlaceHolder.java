package org.gi.gICore.component.adapter;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gi.gICore.manager.EconomyManager;
import org.jetbrains.annotations.NotNull;

public class GIPlaceHolder extends PlaceholderExpansion {
    private EconomyManager economyManager;

    public GIPlaceHolder(){
        economyManager = new EconomyManager();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "gi";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "ysm";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "0.0.1";
    }
    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("balance")){
            double balance = economyManager.getBalance(player);
            Component formatted = economyManager.format(balance);
            return PlainTextComponentSerializer.plainText().serialize(formatted);
        }
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("balance")){
            double balance = economyManager.getBalance(player);
            Component formatted = economyManager.format(balance);
            return PlainTextComponentSerializer.plainText().serialize(formatted);
        }
        return null;
    }
}
