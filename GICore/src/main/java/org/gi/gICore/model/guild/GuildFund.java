package org.gi.gICore.model.guild;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GuildFund {
    private UUID guildId;
    private BigDecimal fund;
}
