package org.gi.gICore.model.guild;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class GuildMember {
    private UUID memberId;
    private String memberName;
    private UUID guildId;
    private GuildEnum.GuildRole role;
}
