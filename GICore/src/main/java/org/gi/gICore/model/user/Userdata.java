package org.gi.gICore.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Userdata {
    private UUID playerId;
    private String playerName;
    private String guildName;
    private String profession;
    private int level;
    private int farmLevel;
    private int mineLevel;
    private int fishLevel;
    private boolean tutorial;

    public Userdata (OfflinePlayer player){
        this.playerId = player.getUniqueId();
        this.playerName = player.getName();
        this.guildName = "NONE";
        PlayerData playerData = PlayerData.get(player);

        this.profession = playerData.getProfess().getName();
        this.level = playerData.getLevel();
        this.farmLevel = 1;
        this.mineLevel = 1;
        this.fishLevel = 1;
        this.tutorial = false;
    }
}
