package org.gi.gICore.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

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


}
