package org.gi.gICore.manager;

import net.milkbowl.vault.economy.EconomyResponse;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.value.ValueName;

import java.util.HashMap;
import java.util.Map;

public class DataService {
    public static Map<String,String> getEconomyData(EconomyResponse economyResponse) {
        Map<String,String> data = new HashMap<>();

        data.put(ValueName.AMOUNT, String.valueOf(economyResponse.amount));

        return data;
    }
}
