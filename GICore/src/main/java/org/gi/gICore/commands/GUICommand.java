package org.gi.gICore.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gi.gICore.manager.GUIManager;

public class GUICommand {
    public static boolean onCommand(CommandSender sender){
        if (!(sender instanceof Player player)){
            return false;
        }

        GUIManager.getMainMenu().open(player);
        return true;
    }
}
