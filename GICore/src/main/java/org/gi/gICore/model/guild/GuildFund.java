package org.gi.gICore.model.guild;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class GuildFund {
    private UUID guildId;
    private String guildName;
    private BigDecimal fund;
}
