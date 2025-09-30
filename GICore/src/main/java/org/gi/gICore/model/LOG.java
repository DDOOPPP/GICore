package org.gi.gICore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public abstract class LOG {
    private UUID playerId;
}
