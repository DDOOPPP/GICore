package org.gi.gICore.model.guild;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Guild {
    private UUID guildId;
    private String guildName;
    private UUID ownerId;
    private int level;
    private BigDecimal exp;
    private String emblem;

    @Override
    public String toString(){
        return "[Guild_Id: %s] [GuildName: %s] [OwnerId: %s] [Level: %s] [Exp: %s] [Emblem: %s]".formatted(guildId,guildName,ownerId,level,exp,emblem);
    }
}
