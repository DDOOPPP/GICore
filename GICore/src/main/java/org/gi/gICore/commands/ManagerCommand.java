package org.gi.gICore.commands;

import jdk.dynalink.NamedOperation;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.GICore;
import org.gi.gICore.component.adapter.GIPlayer;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.loader.VaultLoader;
import org.gi.gICore.manager.ComponentManager;
import org.gi.gICore.manager.DatabaseManager;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.manager.UserManager;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.repository.log.Transaction;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.StringUtil;
import org.gi.gICore.util.TimeUtil;
import org.gi.gICore.value.MessageName;
import org.gi.gICore.value.ValueName;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerCommand {
    private static ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"Command");
    private static UserManager userManager = new UserManager();
    private static GIPlayer giPlayer = new GIPlayer();
    private static EconomyManager economyManager = new EconomyManager();
    private static Transaction transaction = new Transaction();
    private static ComponentManager componentManager = new ComponentManager();


    public static boolean onCommand(CommandSender sender,String[] arg){
        if (!(sender instanceof Player player)){
            return false;
        }

        if (!player.isOp()){
            return false;
        }
        String command = arg[0];

        if (command.equalsIgnoreCase("reload")){
            return reload(player);
        } else if (command.equalsIgnoreCase("econ")) {
            return playerBalanceSet(player,arg);
        }
        return false;
    }

    public static boolean reload(Player player){
        String local = player.getLocale();
        logger.info("Reload Start");
        player.sendMessage(MessagePack.getMessage(local, MessageName.RELOAD_START));
        LocalDateTime start = TimeUtil.now();
        JavaPlugin plugin = GICore.getInstance();

        VaultLoader.loadVault(plugin);
        componentManager.reload();
        MessagePack.reload(plugin);
        LocalDateTime end = TimeUtil.now();

        Duration duration = Duration.between(start,end);
        Map<String,String> data = Map.of(ValueName.TACK_TIME,String.valueOf(duration.toMillis()));
        String message = MessagePack.getMessage(local,MessageName.RELOAD_END);

        message = StringUtil.replacePlaceholders(message,data);
        player.sendMessage(message);
        logger.info("Reload Complete: %s",duration.toMillis());
        return true;
    }

    public static boolean playerBalanceSet(Player player,String[] args){
        String local = player.getLocale();

        OfflinePlayer target = player.getServer().getOfflinePlayer(args[1]);
        BigDecimal amount = BigDecimal.ZERO;

        try{
            amount = BigDecimal.valueOf(Double.parseDouble(args[2]));
        }catch (NumberFormatException e){
            player.sendMessage(MessagePack.getMessage(local, MessageName.NUMBER_ERROR));
            return true;
        }

        Result result = userManager.updateUserWallet(target.getUniqueId(),amount);

        if (result.isSuccess()){

            Result logResult = insertLog(target,amount);
            if (!logResult.isSuccess()){
                String message = MessagePack.getMessage(local,logResult.getMessage());
                player.sendMessage(message);
                return true;
            }
            Map<String ,String > data = new HashMap<>();
            Component value = economyManager.format(amount.doubleValue());
            String text = PlainTextComponentSerializer.plainText().serialize(value);

            data.put(ValueName.AMOUNT, text);

            if (target.isOnline()){
                Player onlinePlayer = target.getPlayer();

                String targetMessage = MessagePack.getMessage(onlinePlayer.getLocale(),MessageName.SET_BALANCE);
                targetMessage = StringUtil.replacePlaceholders(targetMessage,data);

                onlinePlayer.sendMessage(targetMessage);
            }

            String message = MessagePack.getMessage(local,MessageName.BALANCE_SET_OK);
            message = StringUtil.replacePlaceholders(message,data);
            message = PlaceholderAPI.setPlaceholders(target,message);

            player.sendMessage(message);

            return true;
        }
        logger.error(result.getMessage());
        player.sendMessage(MessagePack.getMessage(local,MessageName.BALANCE_SET_FAIL));
        return true;
    }

    private static Result insertLog(OfflinePlayer target, BigDecimal amount){
        BigDecimal balance = userManager.getUserWallet(target.getUniqueId());
        try(Connection connection = DatabaseManager.getconnection()){
            connection.setAutoCommit(false);
            try{
                TransactionLog transactionLog = new TransactionLog(
                        target.getUniqueId(),
                        TransactionLog.TransactionType.SET,
                        amount,
                        balance,
                        amount
                );

                Result result = transaction.insert(transactionLog,connection);
                if (!result.isSuccess()){
                    DatabaseManager.rollback(connection);
                    logger.error("Error: %s %s",target.getName(),result.getMessage());
                    return Result.ERROR(MessageName.BALANCE_SET_FAIL);
                };
                connection.commit();
                return Result.SUCCESS;
            }catch (SQLException e){
                DatabaseManager.rollback(connection);
                return Result.EXCEPTION(e);
            }
        } catch (SQLException e) {
            return Result.EXCEPTION(e);
        }
    }

    public static List<String> tabComplete(CommandSender sender, String[] args){
        if (!(sender instanceof Player player)){
            return List.of();
        }

        if (!player.isOp()){
            return List.of();
        }
        if (args.length == 1){
            return List.of("reload","econ");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("econ")){
            return giPlayer.getPlayers();
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("econ")){
            return List.of("1000","10000","50000");
        }
        return List.of("reload","econ");
    }
}
