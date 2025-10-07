package org.gi.gICore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.management.LockInfo;
import java.util.List;

public class CommandCore implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String commandName = command.getName();
        switch (commandName){
            case "관리":
                return ManagerCommand.onCommand(sender,args);
            case "조회":
                return EconomyCommand.infoCommand(sender,args);
            case "입금":
                return EconomyCommand.depositCommand(sender,args);
            case "출금":
                return EconomyCommand.withdrawCommand(sender,args);
            case "menu":
                return GUICommand.onCommand(sender);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String commandName = command.getName();
        switch (commandName){
            case "관리":
                return ManagerCommand.tabComplete(sender,args);
            case "조회":
                return EconomyCommand.infoTab(sender,args);
            case "입금":
                return EconomyCommand.depositTab(sender,args);
            case "출금":
                return EconomyCommand.withdrawTab(sender,args);
            default:
                return List.of();
        }
    }
}
