package org.gi.gICore.model.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class GuildFundLog {
    private final UUID guildId;
    private final UUID memberId;
    private final String memberName;
    private final TransactionLog.TransactionType transactionType;
    private final BigDecimal amount;
    private final BigDecimal balance;
}
