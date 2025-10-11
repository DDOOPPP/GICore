package org.gi.gICore.manager;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.StatManager;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DataService {
    private static EconomyManager economyManager = new EconomyManager();
    private static ComponentManager componentManager = ComponentManager.getInstance();
    private static ComponentBuilder builder = new ComponentBuilder();
    private static DecimalFormat statFormat = new DecimalFormat("#.#");
    private static ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"DataService");

    public static Map<String,String> getEconomyData(EconomyResponse economyResponse) {
        Map<String,String> data = new HashMap<>();
        data.put(ValueName.AMOUNT, String.valueOf(economyResponse.amount));
        return data;
    }

    public static Map<String,Object> getPlayerData(OfflinePlayer player) {
        Map<String,Object> data = new HashMap<>();
        PlayerData playerData = PlayerData.get(player);

        for (String statKey : ValueName.STATUS_STAT_LIST){
            data.put(statKey,getStat(playerData,statKey.toUpperCase()));
            data.put(statKey+"_base",getBase(playerData,statKey.toUpperCase()));
            data.put(statKey+"_extra",getExtra(playerData,statKey.toUpperCase()));
        }

        return data;
    }

    private static String getStat(PlayerData playerData, String key) {
        Double stat = playerData.getStats().getStat(key);
        return setStatColor(stat,true);
    }

    private static String getBase(PlayerData playerData,String key){
        Double base = playerData.getStats().getBase(key);

        return setStatColor(base,false);
    }

    public static String getExtra(PlayerData playerData,String key){
        Double base = playerData.getStats().getBase(key);
        Double main = playerData.getStats().getStat(key);
        Double extra = main-base;
        String value = statFormat.format(Math.abs(extra));
        if (extra < 0){
            return "<red>-"+value+"</red>";
        }
        return "<yellow>+"+value+"</yellow>";
    }

    private static String setStatColor(Double stat,boolean plus){
        if (stat == 0) {
            String value = statFormat.format(stat);
            if (!plus){
               return "<gray>"+value+"</gray>";
            }
            return "<gray>+"+value+"</gray>";
        }

        if (stat > 0) {
            String value = statFormat.format(stat);
            if (!plus){
                return "<green>"+value+"</green>";
            }
            return "<green>+" + value + "</green>";
        }
        else {
            String value = statFormat.format(Math.abs(stat));
            if (!plus){
                return "<red>-"+value+"</red>";
            }
            return "<red>-" + value + "</red>";
        }
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
