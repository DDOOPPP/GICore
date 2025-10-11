package org.gi.gICore.manager;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.TaskUtil;
import org.gi.gICore.value.ValueName;

import java.util.HashMap;
import java.util.Map;

public class DataService {
    private static EconomyManager economyManager = new EconomyManager();
    private static ComponentManager componentManager = ComponentManager.getInstance();
    private static ComponentBuilder builder = new ComponentBuilder();
    private static ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"DataService");

    public static Map<String,String> getEconomyData(EconomyResponse economyResponse) {
        Map<String,String> data = new HashMap<>();
        data.put(ValueName.AMOUNT, String.valueOf(economyResponse.amount));
        return data;
    }

    public static Map<String,Object> getPlayerData(OfflinePlayer player) {
        Map<String,Object> data = new HashMap<>();
        PlayerData playerData = PlayerData.get(player);
        playerData.getStats().updateStats();
        data.put(ValueName.NAME,player.getName());
        data.put(ValueName.LEVEL,playerData.getLevel());
        data.put(ValueName.EXP,playerData.getExperience());

        String professKey = playerData.getProfess().getName();
        String professName = componentManager.getText(professKey);;
        data.put(ValueName.PROFESS,professName);

        Component balance = economyManager.format(economyManager.getBalance(player));
        data.put(ValueName.BALANCE,PlainTextComponentSerializer.plainText().serialize(balance));

//        var maxHealth = playerData.getStats().getStat(ValueName.MAX_HEALTH.toUpperCase());
//        var maxMana = playerData.getStats().getStat(ValueName.MAX_MANA.toUpperCase());

//        data.put(ValueName.MAX_MANA, maxMana);
//        data.put(ValueName.MAX_HEALTH, maxHealth);
//        data.put(ValueName.HEALTH, playerData.getCachedHealth());
//        data.put(ValueName.MANA, playerData.getMana());
        data.put(ValueName.NEXT_EXP,playerData.getLevelUpExperience());

//        String manaKey = playerData.getProfess().getManaDisplay().getName();
//        String manaName = componentManager.getText(manaKey);
//        data.put(ValueName.MANA_NAME,manaName);
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

    public static Map<String,ItemStack> getEquipmentData(OfflinePlayer player) {
        Map<String,ItemStack> data = new HashMap<>();

        if (!player.isOnline()){
            //오프라인 유저에게서 Player를 받아와서 데이터출력이 가능한지?
            return data;
        }
        Player online = player.getPlayer();
        data.put(ValueName.HELMET,online.getEquipment().getHelmet() != null ? online.getEquipment().getHelmet() : null);
        data.put(ValueName.CHESTPLATE,online.getEquipment().getChestplate() != null ? online.getEquipment().getChestplate() : null);
        data.put(ValueName.LEGGINGS,online.getEquipment().getLeggings() != null ? online.getEquipment().getLeggings() : null);
        data.put(ValueName.BOOTS,online.getEquipment().getBoots() != null ? online.getEquipment().getBoots() : null);

        return data;
    }
}
