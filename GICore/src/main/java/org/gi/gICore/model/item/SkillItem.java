package org.gi.gICore.model.item;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.binding.BoundSkillInfo;
import net.Indyuce.mmocore.skill.binding.SkillSlot;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.s;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.component.adapter.ItemPack;
import org.gi.gICore.manager.ComponentManager;
import org.gi.gICore.manager.DataService;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.ValueName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillItem extends CustomItem {
    ComponentManager componentManager = ComponentManager.getInstance();
    ComponentBuilder builder = new ComponentBuilder();
    ModuleLogger logger = new ModuleLogger(GICore.getInstance(), "SkillItem");

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
        Component manaName = builder.translate(playerData.getProfess().getManaDisplay().getName());

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
            ItemStack icon = getIcon(DataService.reBuildkey(skill.getUnlockNamespacedKey()),skill);
     
            Map<String, Object> data = DataService.getSkillData(playerData, skill);
            data.put(ValueName.MANA_NAME, manaName);
            Component display = builder.translateNamed(player, getDisplay(), data);
            List<Component> components = new ArrayList<>();
            List<String> lore = skill.getSkill().getLore();

            for (String line : lore) {
                components.add(builder.translateNamed(player, line, data));
            }

            icon = ItemUtil.parseItem(icon, display, components);

            ItemUtil.setString(icon, ValueName.SKILL_ID, skill.getUnlockNamespacedKey());
            ItemUtil.setString(icon, ValueName.ACTION, getType());
            icons.add(icon);
        }
        return icons;
    }

    public List<ItemStack> buildBindSlot(OfflinePlayer player) {
        PlayerData playerData = PlayerData.get(player);
        List<ItemStack> icons = new ArrayList<>();
        PlayerClass playerClass = playerData.getProfess();

        List<SkillSlot> slots = playerClass.getSlots();
        logger.info("Slot Count: " + slots.size());
        Map<Integer, BoundSkillInfo> boundSkillInfoMap = playerData.getBoundSkills();

        for (SkillSlot slot : slots) {
            ItemStack icon = getItem().clone();
            Map<String, Object> data = new HashMap<>();
            List<Component> components = new ArrayList<>();
            int num = slot.getSlot();
            data.put(ValueName.SKILL_SLOT_NUMBER, num);

            BoundSkillInfo info = boundSkillInfoMap.get(num);
            Component component = null;
            if (info == null) {
                component = builder.translate(ValueName.NONE_ITEM_KEY);
                ItemUtil.setString(icon, ValueName.SKILL_ID, "NONE");
            } else {
                ClassSkill skill = info.getClassSkill();

                icon = getIcon(DataService.reBuildkey(skill.getUnlockNamespacedKey()),skill);
                String name = info.getClassSkill().getSkill().getName();

                component = builder.translate(name);

                ItemUtil.setString(icon, ValueName.SKILL_ID, info.getClassSkill().getUnlockNamespacedKey());
            }
            data.put(ValueName.SKILL_NAME, component);

            for (String line : getLore()) {
                components.add(builder.translate(line));
            }

            Component display = builder.translateNamed(player, getDisplay(), data);
            ItemUtil.setInteger(icon, ValueName.SKILL_SLOT_NUMBER, num);
            icon = ItemUtil.parseItem(icon, display, components);
            ItemUtil.setString(icon, ValueName.ACTION, getType());
            icons.add(icon);
        }
        return icons;
    }

    public ItemStack buildSelectSkillItem(OfflinePlayer player, Map<String, Object> data) {
        ItemStack itemStack = getItem().clone();
        PlayerData playerData = PlayerData.get(player);
        Map<String, Object> values = new HashMap<>();

        if (data == null) {
            logger.debug("data is Null");
            return itemStack;
        }
        Object obj = data.get(ValueName.SELECT_SKILL);
        if (obj == null) {
            Component component = builder.translate(ValueName.NONE_ITEM_KEY);

            values.put(ValueName.SKILL_NAME, component);
        } else {
            String id = obj.toString();
            ClassSkill skill = DataService.getSkill(playerData, id);

            if (skill == null) {
                itemStack = getItem().clone();
                logger.error("Skill not found");
                Component component = builder.translate(ValueName.NONE_ITEM_KEY);

                values.put(ValueName.SKILL_NAME, component);
            } else {
                values = DataService.getSkillName(player, id);

                itemStack = getIcon(DataService.reBuildkey(skill.getUnlockNamespacedKey()),skill);
                ItemUtil.setString(itemStack, ValueName.SKILL_ID, id);
            }
        }
        ItemUtil.setString(itemStack, ValueName.ACTION, getType());

        Component display = builder.translateNamed(player, getDisplay(), values);

        itemStack = ItemUtil.parseItem(itemStack, display, null);
        return itemStack;
    }

    @Override
    public boolean action(Player player, ItemStack item) {
        return false;
    }

    private ItemStack getIcon(String unlock, ClassSkill skill) {
        String key = DataService.reBuildkey(skill.getUnlockNamespacedKey());
        logger.info(key);
        String textureKey = key.toLowerCase();
        logger.info(textureKey);
        ItemStack icon = null;
        if (ItemPack.getTexture(textureKey) == null) {
            return icon = skill.getSkill().getIcon().clone();
        } else {
            String texture = ItemPack.getTexture(textureKey);
            if (ItemUtil.isCustom(texture)) {
            
                logger.info(texture);
                return icon = ItemUtil.getCustomItem(texture);
            } else {
                return icon = skill.getSkill().getIcon().clone();
            }
        }
    }
}
