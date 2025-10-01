package org.gi.gICore.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gi.gICore.GICore;
import org.gi.gICore.component.adapter.GIPlayer;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.manager.DataService;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.manager.LogManager;
import org.gi.gICore.manager.UserManager;
import org.gi.gICore.model.log.LOG_TAG;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.value.MessageName;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class EconomyCommand {
    private static EconomyManager economyManager = new EconomyManager();
    private static UserManager userManager = new UserManager();
    private static LogManager logManager = new LogManager();
    private static ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"Command");
    private static GIPlayer giPlayer = new GIPlayer();
    public static boolean infoCommand(CommandSender sender, String[] args){
        if (!(sender instanceof Player player)){
            return false;
        }
        String local = player.getLocale();
        if (args.length < 1){
            String message = MessagePack.getMessage(local, MessageName.INFO);

            message = PlaceholderAPI.setPlaceholders(player, message);

            player.sendMessage(message);
            return true;
        }else if (args.length == 1 && player.hasPermission("gicore.admin")){
            OfflinePlayer target = player.getServer().getOfflinePlayer(args[0]);
            String message = MessagePack.getMessage(local, MessageName.INFO);

            message = PlaceholderAPI.setPlaceholders(target, message);

            player.sendMessage(message);
            return true;
        }
        return false;
    }

    public static boolean depositCommand(CommandSender sender, String[] args){
        if (!(sender instanceof Player player)){
            return false;
        }

        if (!player.isOp() && !player.hasPermission("gicore.admin")){
            return false;
        }
        String local = player.getLocale();
        if (args.length ==2){
            double amount = 0f;
            OfflinePlayer target = player.getServer().getOfflinePlayer(args[0]);

            try{
                amount = Double.parseDouble(args[1]);
            }catch (NumberFormatException e){
                player.sendMessage(MessagePack.getMessage(local,MessageName.NUMBER_ERROR));
                return true;
            }

            BigDecimal oldBalance = userManager.getUserWallet(target.getUniqueId());

            String message = "";
            var deposit = economyManager.depositPlayer(target, amount);
            if (deposit.transactionSuccess()) {
                message = MessagePack.getMessage(local, deposit.errorMessage);
                message = PlaceholderAPI.setPlaceholders(target, message);

                Map<String ,String > data = DataService.getEconomyData(deposit);

                giPlayer.sendMessage(target,message,data);

                message = MessagePack.getMessage(local, MessageName.DEPOSIT_ADMIN_SUCCESS);
                message = PlaceholderAPI.setPlaceholders(target, message);

                giPlayer.sendMessage(player,message,data);

                return true;
            } else {
                message = MessagePack.getMessage(local, MessageName.DEPOSIT_ADMIN_FAIL);
                message = PlaceholderAPI.setPlaceholders(target, message);
                giPlayer.sendMessage(player,message,null);
                return false;
            }

        }
        return false;
    }

    public static boolean withdrawCommand(CommandSender sender, String[] args){
        if (!(sender instanceof Player player)){
            return false;
        }

        String local = player.getLocale();

        double amount = 0f;
        String message = "";
        //아이템도 생성 해야함
        if (args.length == 1){
            try{
                amount = Double.parseDouble(args[0]);
            }catch (NumberFormatException e){
                player.sendMessage(MessagePack.getMessage(local, MessageName.NUMBER_ERROR));
                return true;
            }
            var withdraw = economyManager.withdrawPlayer(player, amount);

            if (withdraw.transactionSuccess()) {
                message = MessagePack.getMessage(local, withdraw.errorMessage);
                message = PlaceholderAPI.setPlaceholders(player, message);

                var data = DataService.getEconomyData(withdraw);

                giPlayer.sendMessage(player,message,data);

                return true;
            } else {
                message = MessagePack.getMessage(local, withdraw.errorMessage);

                giPlayer.sendMessage(player,message,null);
                return false;
            }
        } else if (args.length == 2 && player.hasPermission("gicore.admin")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            try{
                amount = Double.parseDouble(args[1]);
            }catch (NumberFormatException e){
                player.sendMessage(MessagePack.getMessage(local, MessageName.NUMBER_ERROR));
                return true;
            }
            var withdraw = economyManager.withdrawPlayer(target, amount);

            if (withdraw.transactionSuccess()) {
                message = MessagePack.getMessage(local, withdraw.errorMessage);
                message = PlaceholderAPI.setPlaceholders(target, message);

                Map<String ,String > data = DataService.getEconomyData(withdraw);

                giPlayer.sendMessage(target,message,data);

                message = MessagePack.getMessage(local, MessageName.WITHDRAW_ADMIN_SUCCESS);
                message = PlaceholderAPI.setPlaceholders(target, message);

                giPlayer.sendMessage(player,message,data);

                return true;
            } else {
                message = MessagePack.getMessage(local, MessageName.WITHDRAW_ADMIN_FAIL);
                message = PlaceholderAPI.setPlaceholders(target, message);
                giPlayer.sendMessage(player,message,null);
                return false;
            }
        }
        return false;
    }
}
