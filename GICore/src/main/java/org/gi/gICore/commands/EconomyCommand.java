package org.gi.gICore.commands;

import io.r2dbc.spi.Result;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.events.PlayerEvents;
import org.gi.gICore.loader.PlaceHolderLoader;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.model.message.EconomyMessage;

public class EconomyCommand {
    private EconomyManager economyManager = new EconomyManager();

    public static boolean infoCommand(CommandSender sender, String[] args){
        if (!(sender instanceof Player player)){
            return false;
        }
        String local = player.getLocale();
        if (args.length < 1){
            String message = MessagePack.getMessage(local, EconomyMessage.INFO);

            message = PlaceholderAPI.setPlaceholders(player, message);

            player.sendMessage(message);
            return true;
        }else if (args.length == 1 && player.hasPermission("gicore.admin")){
            OfflinePlayer target = player.getServer().getOfflinePlayer(args[0]);
            String message = MessagePack.getMessage(local, EconomyMessage.INFO);

            message = PlaceholderAPI.setPlaceholders(target, message);

            player.sendMessage(message);
            return true;
        }
        return false;
    }
}
