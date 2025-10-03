package org.gi.gICore.component.adapter;

import jdk.jfr.consumer.RecordedStackTrace;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.StringUtil;
import org.gi.gICore.util.TaskUtil;
import org.gi.gICore.value.MessageName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static net.Indyuce.mmocore.manager.InventoryManager.list;

public class GIPlayer {
    public void sendMessage(OfflinePlayer player, String msg) {
        if (!player.isOnline()) {
            return;
        }
        player.getPlayer().sendMessage(msg);
    }

    public Result sendItem(OfflinePlayer player, ItemStack item) {
        if (!player.isOnline()) {
            return Result.FAILURE(MessageName.PLAYER_IS_OFFLINE);
        }

        Inventory inventory = player.getPlayer().getInventory();
        HashMap<Integer, ItemStack> over = inventory.addItem(item);
        if (!over.isEmpty()) {
            return sendMailBox(player,item);
        }
        return Result.SUCCESS;
    }

    public Result sendMailBox(OfflinePlayer player, ItemStack item) {
        return Result.SUCCESS;
    }

    public List<String> getPlayers(){
       return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
