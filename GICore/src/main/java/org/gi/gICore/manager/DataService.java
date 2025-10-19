package org.gi.gICore.manager;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.SkillManager;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skill.binding.BoundSkillInfo;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.value.MessageName;
import org.gi.gICore.value.ValueName;

import io.lumine.mythic.bukkit.utils.items.nbt.reee;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataService {
    private static EconomyManager economyManager = new EconomyManager();
    private static ComponentManager componentManager = ComponentManager.getInstance();
    private static ComponentBuilder builder = new ComponentBuilder();
    private static DecimalFormat statFormat = new DecimalFormat("#.#");
    private static ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"DataService");
    private static SkillManager skillManager = new SkillManager();

    public static Map<String,String> getEconomyData(EconomyResponse economyResponse) {
        Map<String,String> data = new HashMap<>();
        data.put(ValueName.AMOUNT, String.valueOf(economyResponse.amount));
        return data;
    }

    public static Map<String,ItemStack> getEquipmentData(OfflinePlayer player) {
        Map<String,ItemStack> data = new HashMap<>();

        if (!player.isOnline()){
            return data;
        }
        Player online = player.getPlayer();
        data.put(ValueName.HELMET,online.getEquipment().getHelmet() != null ? online.getEquipment().getHelmet() : null);
        data.put(ValueName.CHESTPLATE,online.getEquipment().getChestplate() != null ? online.getEquipment().getChestplate() : null);
        data.put(ValueName.LEGGINGS,online.getEquipment().getLeggings() != null ? online.getEquipment().getLeggings() : null);
        data.put(ValueName.BOOTS,online.getEquipment().getBoots() != null ? online.getEquipment().getBoots() : null);

        return data;
    }

    public static Map<String,Object> getPlayerData(OfflinePlayer player) {
        Map<String,Object> data = new HashMap<>();
        PlayerData playerData = PlayerData.get(player);
        
        playerData.getStats().updateStats();
        for (String statKey : ValueName.INFO_LIST){
            String key = statKey.toLowerCase();

            data.put(key,getStat(playerData,statKey.toUpperCase()));
            data.put(key+"_base",getBase(playerData,statKey.toUpperCase()));
            data.put(key+"_extra",getExtra(playerData,statKey.toUpperCase()));
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

    public static String getExtra(PlayerData playerData,String key) {
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

    public static Map<String,Object> getSkillData(PlayerData playerData, ClassSkill skill) {
        Map<String,Object> data = new HashMap<>();

        Component component = builder.translate(skill.getSkill().getName());
        int player_level = playerData.getLevel();
        data.putIfAbsent(ValueName.SKILL_NAME,component);
        data.putIfAbsent(ValueName.SKILL_LEVEL,playerData.getSkillLevel(skill.getSkill()));
        int unlockLevel = skill.getUnlockLevel();
        String unlock = "";

        if (player_level >= unlockLevel){
            unlock = "<green>"+unlock+"</green>";
        }else{
            unlock = "<red>"+unlock+"</red>";
        }
        data.put(ValueName.UNLOCK_LEVEL,unlock);

        for (String key : ValueName.SKILL_LIST){
            key = key.toLowerCase();
            String value = getSkillParameter(playerData,skill,key);
            if (value == null) {
                continue;
            }
            data.put(key,value);
        
        }
        return data;
    }

    private static String getSkillParameter(PlayerData player, ClassSkill skill, String parameterName){
        var parameterInfo = skill.getSkill().getParameterInfo(parameterName);
        if (parameterInfo == null) {
            parameterInfo = skill.getSkill().getParameterInfo(parameterName.toLowerCase());
        }

        if (parameterInfo == null) {
            return null;
        }
        String display = parameterInfo.getDisplay(player.getSkillLevel(skill.getSkill()));
        if (display == null) {
            return null;
        }
        return display;
    }

    public static String getTranslateId(ClassSkill skill) {
        if (skill == null) return null;
        String id = skill.getSkill().getHandler().getId().toLowerCase();
        return "gi.skill.%s.name".formatted(id);
    }

    public static List<String> getTranslateName(ClassSkill skill) {
        return skill.getSkill().getLore();
    }

    public static Map<String ,Object> getSkillName(OfflinePlayer player,String key){
        PlayerData playerData = PlayerData.get(player);
        String id = reBuildkey(key);
        ClassSkill skill = playerData.getProfess().getSkill(id);
        if (skill == null) {
            if (!player.isOnline()) {
                return Map.of();
            }
            String message = MessagePack.getMessage(player.getPlayer().getLocale(), MessageName.SKILL_NOT_FOUND);

            player.getPlayer().sendMessage(message);
            return Map.of();
        }

        RegisteredSkill registeredSkill = skill.getSkill();

        Map<String ,Object> data = new HashMap<>();
        Component component = builder.translate(registeredSkill.getName());

        data.put(ValueName.SKILL_NAME,component);
        return data;
    }

    public static ClassSkill getSkill(PlayerData playerData, String key){
        String temp = reBuildkey(key);

        ClassSkill skill = playerData.getProfess().getSkill(temp);

        return skill;
    }

    public Map<Integer, BoundSkillInfo> getBoundSkillList(PlayerData playerData){
       return playerData.getBoundSkills();
    }

    public static String reBuildkey(String key){
        return key.replace("skill:", "").toUpperCase();
    }
}
