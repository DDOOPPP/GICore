package org.gi.gICore.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MailBox {
    private UUID playerId;
    private String name;
    private Map<Integer,Map<String,Object>> mailbox = new HashMap<>();
}
