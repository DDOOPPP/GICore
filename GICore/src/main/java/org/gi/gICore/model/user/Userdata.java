package org.gi.gICore.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
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

    public void setTutorial(boolean tutorial){
        this.tutorial = tutorial;
    }

    public enum LevelType {
        MAIN("level"),FARM("farm_level"), MINE("mine_level"), FISH("fishing_level");

        private String name;

        LevelType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
