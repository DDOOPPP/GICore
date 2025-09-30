package org.gi.gICore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandCore implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String commandName = command.getName();
        switch (commandName){
            case "gicore":
                return true;
            case "조회":
                return EconomyCommand.infoCommand(sender,args);
        }
        return false;
    }
}
