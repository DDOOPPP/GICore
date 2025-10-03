package org.gi.gICore.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.value.ValueName;

import java.util.HashMap;
import java.util.Map;

public class DataService {
    private static EconomyManager economyManager = new EconomyManager();

    public static Map<String,String> getEconomyData(EconomyResponse economyResponse) {
        Map<String,String> data = new HashMap<>();
        Component amount = economyManager.format(economyResponse.amount);
        String text = PlainTextComponentSerializer.plainText().serialize(amount);

        data.put(ValueName.AMOUNT, text);
        return data;
    }
}
