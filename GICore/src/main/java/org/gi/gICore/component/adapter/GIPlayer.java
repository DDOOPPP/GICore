package org.gi.gICore.component.adapter;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gi.gICore.util.StringUtil;

import java.util.Map;

public class GIPlayer {
    public void sendMessage(OfflinePlayer player, String msg, Map<String, String> values) {
        if (!player.isOnline()) {
            return;
        }
        if (values != null) {
            player.getPlayer().sendMessage(StringUtil.replacePlaceholders(msg, values));
            return;
        }
        player.getPlayer().sendMessage(msg);
    }

}
