package org.gi.gICore.model.item;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.manager.ComponentManager;
import org.gi.gICore.manager.DataService;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.ValueName;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillItem extends CustomItem{
    ComponentManager componentManager = ComponentManager.getInstance();
    ComponentBuilder builder = new ComponentBuilder();
    ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"SkillItem");
    public SkillItem(ConfigurationSection section) {
        super(section);
    }

    @Override
    public ItemStack buildItem(OfflinePlayer player, Object... arg) {
        return null;
    }

    @Override
    public Result destroyItem(Player player, ItemStack itemStack) {
        return null;
    }

    public List<ItemStack> buildSkillItem(OfflinePlayer player) {
        PlayerData playerData = PlayerData.get(player);

        List<ClassSkill> skills = playerData
                .getProfess()
                .getSkills()
                .stream()
                .filter(skill -> skill.isUnlockedByDefault())
                .collect(Collectors.toList());

        if (skills.isEmpty()) {
            return List.of();
        }
        List<ItemStack> icons = new ArrayList<>();

        for (ClassSkill skill : skills) {
            ItemStack icon = skill.getSkill().getIcon();
            Map<String,Object> data = DataService.getSkillData(player, skill);

            Component display = builder.translateNamed(player,getDisplay(),data);
            List<Component> components = new ArrayList<>();
            List<String> lore = skill.getSkill().getLore();

            for (String line : lore) {
                components.add( builder.translateNamed(player,line,data));
            }

            icon = ItemUtil.parseItem(icon,display,components);

            ItemUtil.setString(icon, ValueName.SKILL_ID, skill.getUnlockNamespacedKey());
            ItemUtil.setString(icon, ValueName.ACTION, "SKILL");
            icons.add(icon);
        }
        return icons;
    }

    @Override
    public boolean action(Player player, ItemStack item) {
        return false;
    }
}
