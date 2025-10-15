package org.gi.gICore.component.adapter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.MessageName;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
public class GIPlayer {
    public void sendMessage(OfflinePlayer player, String msg) {
        if (!player.isOnline()) {
            return;
        }
        player.getPlayer().sendMessage(msg);
    }

    public Locale getLocale(Player player){
        switch (player.getLocale()){
            case "ko_kr":
                return Locale.KOREA;
            case "ja_jp":
                return Locale.JAPAN;
            case "en_us":
            default:
                return Locale.ENGLISH;
        }
    }

    public Result sendItem(OfflinePlayer player, ItemStack item, boolean isDrop) {
        if (!player.isOnline()) {
            return Result.FAILURE(MessageName.PLAYER_IS_OFFLINE);
        }

        Inventory inventory = player.getPlayer().getInventory();
        HashMap<Integer, ItemStack> over = inventory.addItem(item);
        if (!over.isEmpty()) {
            if (isDrop) {
                Location location = player.getLocation();
                
                player.getPlayer().getWorld().dropItem(location, item);

                
            }else{
                return sendMailBox(player,item);
            }
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

    public Result removeItem(OfflinePlayer player, ItemStack target) {
        if (!player.isOnline()) {
            return Result.FAILURE;
        }
        Inventory inventory = player.getPlayer().getInventory();

        //두 아이템이 같을까?
        for (ItemStack item: inventory.getContents()){
            if (target.isSimilar(item)) {
                int i =  item.getAmount();
                player.getPlayer().sendMessage(String.valueOf(i));
                item.setAmount(item.getAmount()-1);

                break;
            }
        }
        return Result.SUCCESS;
    }
}
