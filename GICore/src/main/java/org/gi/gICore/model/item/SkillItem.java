package org.gi.gICore.model.item;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skill.binding.BoundSkillInfo;
import net.Indyuce.mmocore.skill.binding.SkillSlot;
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
            ItemStack icon = skill.getSkill().getIcon().clone();
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

    public List<ItemStack> buildBindSlot(OfflinePlayer player){
        PlayerData playerData = PlayerData.get(player);
        List<ItemStack> icons = new ArrayList<>();
        PlayerClass playerClass = playerData.getProfess();

        List<SkillSlot> slots = playerClass.getSlots();
        logger.info("Slot Count: " + slots.size());
        Map<Integer, BoundSkillInfo> boundSkillInfoMap = playerData.getBoundSkills();

        for (SkillSlot slot : slots) {
            ItemStack icon = getItem().clone();
            Map<String,Object> data = new HashMap<>();
            List<Component> components = new ArrayList<>();
            int num = slot.getSlot();
            data.put(ValueName.SKILL_SLOT_NUMBER,num);

            BoundSkillInfo info = boundSkillInfoMap.get(num);
            Component component = null;
            if (info == null) {
                component = builder.translate(ValueName.NONE_ITEM_KEY);

                ItemUtil.setString(icon, ValueName.SKILL_ID, null);
            }else{
                icon = info.getClassSkill().getSkill().getIcon().clone();
                String name = info.getClassSkill().getSkill().getName();

                component = builder.translate(name);
                ItemUtil.setString(icon, ValueName.SKILL_ID, info.getClassSkill().getUnlockNamespacedKey());
            }
            data.put(ValueName.SKILL_NAME,component);
            for (String line : slot.getLore()) {
                components.add(builder.translate(line));
            }

            Component display = builder.translateNamed(player,getDisplay(),data);

            icon = ItemUtil.parseItem(icon,display,components);

            icons.add(icon);
        }
        return icons;
    }

    public ItemStack buildSelectSkillItem(OfflinePlayer player,Map<String,Object> data){
        ItemStack itemStack = getItem().clone();
        Map<String,Object> values = new HashMap<>();
        if (!data.containsKey(ValueName.SELECT_SKILL)) {
            Component component = builder.translate(ValueName.NONE_ITEM_KEY);

            ItemUtil.setString(itemStack, ValueName.SKILL_ID, null);
            values.put(ValueName.SKILL_NAME, component);
        }else{
            String id = data.get(ValueName.SELECT_SKILL).toString();

            RegisteredSkill skill = DataService.getSkill(id);

            if (skill == null) {
                itemStack = getItem().clone();
                logger.error("Skill not found");
                Component component = builder.translate(ValueName.NONE_ITEM_KEY);

                values.put(ValueName.SKILL_NAME, component);
                ItemUtil.setString(itemStack, ValueName.SKILL_ID, null);
            }else{
                values = DataService.getSkillName(id);

                itemStack = skill.getIcon().clone();
                ItemUtil.setString(itemStack, ValueName.SKILL_ID, id);
            }
        }
        ItemUtil.setString(itemStack,ValueName.ACTION,"VIEW");

        Component display  = builder.translate(getDisplay(),values);

        itemStack = ItemUtil.parseItem(itemStack,display,null);
        return itemStack;
    }
    @Override
    public boolean action(Player player, ItemStack item) {
        return false;
    }
}
