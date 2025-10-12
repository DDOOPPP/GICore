package org.gi.gICore.value;

import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Material;

import java.util.List;
import java.util.Set;

public class ValueName {
    //경제
    public static final String AMOUNT = "amount";
    public static final String MONEY = "money";
    public static final String BALANCE = "balance";
    //Common
    public static final String TACK_TIME = "tack_time";
    public static final String ACTION = "action";

    public static final List<String> INFO_LIST = List.of(
            "ATTACK_DAMAGE","ATTACK_SPEED","MOVEMENT_SPEED"
    );

    public static final List<String> SKILL_LIST = List.of(
            "SKILL_LEVEL",
            "COOLDOWN",
            "COUNT",
            "DAMAGE",
            "DURATION",
            "EXTRA",
            "HEAL",
            "IGNITE",
            "KNOCKBACK",
            "LOW",
            "MANA",
            "MANA_NAME",
            "PERCENT",
            "RADIUS",
            "RANGE",
            "RATIO",
            "REDIRECT",
            "REDUCTION",
            "SLOW",
            "STUN"
    );

    public static final List<String> ARMOR_PART_LIST = List.of(
            "helmet",
            "chestplate",
            "leggings",
            "boots"
    );

    public static final List<String> STATUS_STAT_LIST = List.of(
            "attack_damage","attack_speed","movement_speed"
    );

    //Skill
    public static final String SKILL_NAME = "skill_name";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String COOLDOWN = "cooldown";
    public static final String COUNT = "count";
    public static final String DAMAGE = "damage";
    public static final String DURATION = "duration";
    public static final String EXTRA = "extra";
    public static final String HEAL = "heal";
    public static final String IGNITE = "ignite";
    public static final String KNOCKBACK = "knockback";
    public static final String LOW = "low";
    //PlayerData 공용으로 사용
    public static final String MANA = "mana";
    public static final String MANA_NAME = "mana_name";
    public static final String PERCENT = "percent";
    public static final String RADIUS = "radius";
    public static final String RANGE = "range";
    public static final String RATIO = "ratio";
    public static final String REDIRECT = "redirect";
    public static final String REDUCTION = "reduction";
    public static final String SLOW = "slow";
    public static final String STUN = "stun";

    //PlayerData
    public static final String NAME = "name";
    public static final String PROFESS = "profess";
    public static final String LEVEL = "level";
    public static final String EXP = "exp";
    public static final String NEXT_EXP = "next_exp";
    public static final String SKILL_POINTS = "skill_points";
    public static final String MAX_HEALTH = "MAX_HEALTH";
    public static final String MAX_MANA = "MAX_MANA";
    public static final String HEALTH = "health";

    public static final String HELMET = "helmet";
    public static final String LEGGINGS = "leggings";
    public static final String CHESTPLATE = "chestplate";
    public static final String BOOTS = "boots";
    public static final String ARMOR_TYPE = "type";
    public static final String ARMOR_PART = "armor_part";
    public static final String EQUIPMENT = "equipment";

    public static final String DISPLAY = "display_key";
    public static final String LORE_KEY = "lore_key";
    public static final String PL_DATA = "pl_data";

    public static final String WEAPON = "weapon";
    //Stat
    public static final String ATTACK_DAMAGE = "attack_damage";
    public static final String ATTACK_DAMAGE_BASE = "attack_damage_base";
    public static final String ATTACK_DAMAGE_EXTRA = "attack_damage_extra";

    public static final String ATTACK_SPEED = "attack_speed";
    public static final String ATTACK_SPEED_BASE = "attack_speed_base";
    public static final String ATTACK_SPEED_EXTRA = "attack_speed_extra";

    public static final Set<Material> VANILLA_WEAPONS = Set.of(
            Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            Material.TRIDENT, Material.BOW, Material.CROSSBOW,Material.MACE
    );

    public static final Set<Type> MMOITEMS_WEAPONS = Set.of(
            Type.SWORD,
            Type.DAGGER,
            Type.SPEAR,
            Type.HAMMER,
            Type.GAUNTLET,
            Type.WHIP,
            Type.STAFF,
            Type.BOW,
            Type.CROSSBOW,
            Type.MUSKET,
            Type.LUTE,
            Type.CATALYST,
            Type.OFF_CATALYST,
            Type.MAIN_CATALYST
    );
}
