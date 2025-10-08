package org.gi.gICore.manager;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.value.ValueName;

import java.util.HashMap;
import java.util.Map;

public class DataService {
    private static EconomyManager economyManager = new EconomyManager();
    private static ComponentBuilder builder = new ComponentBuilder();
    public static Map<String,String> getEconomyData(EconomyResponse economyResponse) {
        Map<String,String> data = new HashMap<>();
        data.put(ValueName.AMOUNT, String.valueOf(economyResponse.amount));
        return data;
    }

    public static Map<String,Object> getPlayerData(OfflinePlayer player) {
        Map<String,Object> data = new HashMap<>();
        PlayerData playerData = PlayerData.get(player);

        data.put(ValueName.NAME,player.getName());
        data.put(ValueName.LEVEL,playerData.getLevel());
        data.put(ValueName.EXP,playerData.getExperience());

        String professKey = playerData.getProfess().getName();
        Component component = builder.style(professKey).gold().build();
        data.put(ValueName.PROFESS,PlainTextComponentSerializer.plainText().serialize(component));

        Component balance = economyManager.format(economyManager.getBalance(player));
        data.put(ValueName.BALANCE,PlainTextComponentSerializer.plainText().serialize(balance));

        var maxHealth = playerData.getStats().getStat(ValueName.MAX_HEALTH.toUpperCase());
        var maxmana = playerData.getStats().getStat(ValueName.MAX_MANA.toUpperCase());

        data.put(ValueName.MAX_MANA,maxmana);
        data.put(ValueName.MAX_HEALTH,maxHealth);
        data.put(ValueName.HEALTH,playerData.getCachedHealth());
        data.put(ValueName.MANA,playerData.getMana());
        data.put(ValueName.NEXT_EXP,playerData.getLevelUpExperience());

        String manaKey = playerData.getProfess().getManaDisplay().getName();
        Component manaNameComp = builder.style(manaKey).red().build();
        data.put(ValueName.MANA_NAME,PlainTextComponentSerializer.plainText().serialize(manaNameComp));
        return data;
    }

    public static Map<String,String> getSkillData(OfflinePlayer player, ClassSkill skill) {
        Map<String,String> data = new HashMap<>();

        PlayerData playerData = PlayerData.get(player);

        for (String key : ValueName.SKILL_LIST){
            key = key.toLowerCase();

            data.putIfAbsent(ValueName.SKILL_NAME,skill.getSkill().getName());
            data.putIfAbsent(ValueName.SKILL_LEVEL,String.valueOf(playerData.getSkillLevel(skill.getSkill())));
            String value = getSkillParameter(playerData,skill,key);

            data.put(key,value);
        }
        return data;
    }

    private static String getSkillParameter(PlayerData player, ClassSkill skill, String parameterName){
        var parameterInfo = skill.getSkill().getParameterInfo(parameterName);
        if (parameterInfo == null) {
            return null;
        }
        return parameterInfo.getDisplay(player.getSkillLevel(skill.getSkill()));
    }
}
