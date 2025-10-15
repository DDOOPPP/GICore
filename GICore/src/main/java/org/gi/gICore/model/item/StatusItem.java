package org.gi.gICore.model.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.manager.ComponentManager;
import org.gi.gICore.manager.DataService;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.StringUtil;
import org.gi.gICore.value.ValueName;
import net.kyori.adventure.text.Component;

public class StatusItem extends CustomItem {
    private ComponentBuilder builder = new ComponentBuilder();
    private ModuleLogger logger = new ModuleLogger(GICore.getInstance(), "StatusItem");
    private ComponentManager componentManager = ComponentManager.getInstance();

    public StatusItem(ConfigurationSection section) {
        super(section);
    }

    @Override
    public ItemStack buildItem(OfflinePlayer player, Object... arg) {
        return null;
    }

    @Override
    public Result destroyItem(Player player, ItemStack itemStack) {
        return Result.FAILURE;
    }

    @Override
    public boolean action(Player player, ItemStack item) {
        return false;
    }

    public ItemStack buildPlayerInfo(OfflinePlayer player) {
        ItemStack icon = getItem(player);

        List<Component> lore = new ArrayList<>();
        Map<String, Object> statusData = DataService.getPlayerData(player);

        Component display = builder.translate(getDisplay());

        for (String s : getLore()) {
            lore.add(builder.translateNamed(player, s, statusData));
        }
        ItemUtil.setString(icon, ValueName.ACTION, getType());
        return ItemUtil.parseItem(icon, display, lore);
    }

    public ItemStack buildArmorSlot(OfflinePlayer player, String armorType) {
        ItemStack icon = getItem();
        Map<String, ItemStack> equipmentMap = DataService.getEquipmentData(player);
        Map<String, Object> data = new HashMap<>();
        List<Component> lore = new ArrayList<>();

        // Armor Type 이름 (번역)
        String typeKey = "gi.data.armor.type.%s".formatted(armorType);
        String typeName = componentManager.getText(player, typeKey);
        data.put(ValueName.ARMOR_TYPE, typeName);

        // 장착 장비 확인
        ItemStack equipped = equipmentMap != null ? equipmentMap.get(armorType) : null;
        if (equipped == null || equipped.getType().isAir()) {
            data.put(ValueName.EQUIPMENT, builder.translate(ValueName.NONE_ITEM_KEY));
        } else {
            icon = equipped.clone();
            Component nameComp;

            if (ItemUtil.isMMOItem(equipped)) {
                String displayName = StringUtil.decolorize(equipped.getItemMeta().getDisplayName());
                nameComp = builder.translate(displayName);
            } else {
                nameComp = Component.translatable(equipped.getType().translationKey());
            }

            data.put(ValueName.EQUIPMENT, nameComp);
        }

        Component display = builder.translateNamed(player, getDisplay(), data);
        for (String s : getLore()) {
            lore.add(builder.translateNamed(player, s, data));
        }

        ItemUtil.setString(icon, ValueName.ACTION, getType());
        return ItemUtil.parseItem(icon, display, lore);
    }

    public ItemStack buildWeaponSlot(OfflinePlayer player, ItemStack icon) {
        if (icon != null && !icon.getType().equals(Material.BLACK_STAINED_GLASS_PANE)) {
            ItemUtil.setString(icon, ValueName.ACTION, getType());
            return icon;
        }
        Map<String, Object> data = new HashMap<>();

        data.put(ValueName.WEAPON, builder.translate(ValueName.NONE_ITEM_KEY));

        List<Component> lore = new ArrayList<>();
        Component display = builder.translateNamed(player, getDisplay(), data);
        for (String s : getLore()) {
            lore.add(builder.translateNamed(player, s, data));
        }

        ItemUtil.setString(icon, ValueName.ACTION, getType());
        return ItemUtil.parseItem(icon, display, lore);
    }
}
