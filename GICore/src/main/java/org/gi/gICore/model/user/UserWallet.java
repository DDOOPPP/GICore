package org.gi.gICore.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserWallet {
    private UUID playerId;
    private BigDecimal balance;
}
