package org.gi.gICore.model.log;

import java.util.UUID;

import org.gi.gICore.model.guild.GuildEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class GuildEventLog {
    private UUID guildId;
    private UUID actorUUID;
    private String actorName;
    private UUID targetUUID;
    private String targetName;
    private GuildEnum.LogAction type;
}
