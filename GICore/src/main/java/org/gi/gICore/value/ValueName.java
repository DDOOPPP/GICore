package org.gi.gICore.value;

import java.util.List;

public class ValueName {
    //경제
    public static final String AMOUNT = "amount";
    public static final String MONEY = "money";
    public static final String BALANCE = "balance";
    //Common
    public static final String TACK_TIME = "tack_time";
    public static final String ACTION = "action";

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
}
