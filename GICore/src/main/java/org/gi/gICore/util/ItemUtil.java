package org.gi.gICore.util;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.manager.StatManager;
import net.Indyuce.mmoitems.manager.TypeManager;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.value.ValueName;

import java.util.*;

public class ItemUtil {
    private static ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"ItemUtil");

    private static ComponentBuilder builder = new ComponentBuilder();

    public static ItemStack parseItem(ItemStack item, Component component, List<Component> lore) {
        if (item == null || item.getType() == Material.AIR) {
            return item;
        }
        if (component == null) {
            return item;
        }
        if (lore == null) {
            item.editMeta(itemMeta -> {
                itemMeta.displayName(component);
            });
            return item;
        }
        item.editMeta(itemMeta -> {
            itemMeta.displayName(component);
            itemMeta.lore(lore);
        });
        return item;
    }

    public static boolean isCustom(String item_id) {
        return CustomStack.isInRegistry(item_id);
    }

    public static ItemStack getItem(String material) {
        return new ItemStack(Material.valueOf(material.toUpperCase()));
    }

    public static ItemStack getItem(Material material) {
        return new ItemStack(material);
    }

    public static ItemStack getItem(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    public static boolean isMMOItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        NBTItem nbtItem = NBTItem.get(item);

        return nbtItem.hasType();
    }

    public static Type getItemType(String id) {
        TypeManager manager = new TypeManager();

        return manager.has(id.toUpperCase()) ? manager.get(id.toUpperCase()) : null;
    }

    public static ItemStack getMMOItem(String itemID, Type type) {
        if (type == null) {
            return new ItemStack(Material.AIR);
        }
        MMOItems mmoItems = new MMOItems();
        MMOItem item = mmoItems.getMMOItem(type,itemID);

        if (item == null) {
            return new ItemStack(Material.AIR);
        }
        return item.newBuilder().getItemStack();
    }

    public static ItemStack getMMOItem(ItemStack item) {
        NBTItem nbtItem = NBTItem.get(item);

        LiveMMOItem liveMMOItem = new LiveMMOItem(nbtItem);
        return liveMMOItem.clone().newBuilder().getItemStack();
    }

    public static ItemStack getCustomItem(String namespace_id) {
        return CustomStack.getInstance(namespace_id).getItemStack();
    }

    public static boolean isCustomItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        return CustomStack.byItemStack(item) != null;
    }

    public static ItemStack getPlayerHead(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack ItemDeserialize(Map<String,Object> map) {
        return ItemStack.deserialize(map);
    }

    public static Map<String,Object> ItemSerialize(ItemStack itemStack) {
        itemStack.clone();
        return itemStack.serialize();
    }

    public static NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey("gi", key);
    }

    public static <P,C> void edit(ItemStack itemStack, String key, PersistentDataType<P,C> type, C value) {
        NamespacedKey namespacedKey = getNamespacedKey(key);

        itemStack.editMeta(meta ->{
            meta.getPersistentDataContainer().set(namespacedKey, type, value);
        });
    }

    public static void deleteKey(ItemStack itemStack, String key){
        NamespacedKey namespacedKey = getNamespacedKey(key);

        itemStack.editMeta(meta ->{
            meta.getPersistentDataContainer().remove(namespacedKey);
        });
        return;
    }

    public static <P,C> C getValue(ItemStack itemStack, String key, PersistentDataType<P,C> type) {
        if (itemStack == null || !itemStack.hasItemMeta()) return null;

        ItemMeta meta = itemStack.getItemMeta();

        NamespacedKey namespacedKey = getNamespacedKey(key);
        return meta.getPersistentDataContainer().get(namespacedKey, type);
    }

    public static boolean hasKey(ItemStack itemStack, String key,PersistentDataType<?,?> type) {
        if (itemStack == null || !itemStack.hasItemMeta()) return false;
        ItemMeta meta = itemStack.getItemMeta();

        NamespacedKey namespacedKey = getNamespacedKey(key);

        return meta.getPersistentDataContainer().has(namespacedKey, type);
    }

    public static String getString(ItemStack itemStack, String key) {
        String value = getValue(itemStack, key, PersistentDataType.STRING);
        return value == null ? "" : value;
    }

    public static int getInt(ItemStack itemStack, String key) {
        int value = getValue(itemStack, key, PersistentDataType.INTEGER);
        return value == -1 ? 0 : value;
    }

    public static Double getDouble(ItemStack itemStack, String key) {
        double value = getValue(itemStack, key, PersistentDataType.DOUBLE);
        return value == -1 ? null : value;
    }

    public static boolean getBoolean(ItemStack itemStack, String key) {
        Boolean value = getValue(itemStack, key, PersistentDataType.BOOLEAN);
        return value != null && value;
    }

    public static void setString(ItemStack item, String key, String value) {
        edit(item, key, PersistentDataType.STRING, value);
    }

    public static void setInteger(ItemStack item, String key, int value) {
        edit(item, key, PersistentDataType.INTEGER, value);
    }

    public static void setDouble(ItemStack item, String key, double value) {
        edit(item, key, PersistentDataType.DOUBLE, value);
    }

    public static void setBoolean(ItemStack item, String key, boolean value) {
        edit(item, key, PersistentDataType.BOOLEAN, value);
    }

    public static ItemStack create(ItemStack item,String display, List<String> lore){
        ItemStack itemStack = item.clone();
        itemStack.editMeta(meta->{
            meta.setDisplayName(display);
            meta.setLore(lore);
        });
        return itemStack;
    }

    public static boolean isArmor(ItemStack itemStack) {
        if (itemStack == null ){
            return false;
        }
        switch (itemStack.getType()) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
            case TURTLE_HELMET:
                return true;
            default:
                return false;
        }
    }

    public static Map<String, Component> extractStat(ItemStack itemStack) {
        Map<String, Component> result = new HashMap<>();

        StatManager statManager = new StatManager();
        Collection<ItemStat<?, ?>> allStats = statManager.getAll();

        NBTItem nbt = NBTItem.get(itemStack);

        for (ItemStat<?, ?> stat : allStats) {
            String id = stat.getId(); // ex: ATTACK_DAMAGE
            String nbtKey = "MMOITEMS_" + id.toUpperCase();

            if (!nbt.hasTag(nbtKey)) continue;

            String value = null;
            try {
                // DoubleData, NumericStatData 등 대부분 숫자 기반
                value = String.valueOf(nbt.getDouble(nbtKey));
            } catch (Exception e) {
                try {
                    value = String.valueOf(nbt.getInteger(nbtKey));
                } catch (Exception e2) {
                    value = nbt.getString(nbtKey);
                }
            }

            result.put(id, Component.text(value));
        }
        return result;
    }

    public static String getArmorString(ItemStack item) {
        String armor_type = "";
        if (item.getType().name().contains("HELMET")){
            armor_type = "HELMET";
        }else if (item.getType().name().contains("CHESTPLATE")){
            armor_type = "CHESTPLATE";
        }else if (item.getType().name().contains("LEGGINGS")){
            armor_type = "LEGGINGS";
        }else if (item.getType().name().contains("BOOTS")){
            armor_type = "BOOTS";
        }
        return armor_type;
    }

    public static boolean isAcc(ItemStack item){
        Type type = Type.get(item);

        if (type == null) return false;

        if (type.equals(Type.ACCESSORY)) return true;
        return false;
    }

    public static ItemStack locallizedItem(Player player, ItemStack itemStack){
        String display = getString(itemStack, ValueName.DISPLAY);
        String loreKey = getString(itemStack, ValueName.LORE_KEY);
        String dataKey = getString(itemStack, ValueName.PL_DATA);

        List<String> lore = JsonUtil.fromJson(loreKey,new TypeReference<List<String>>() {});

        Map<String,Object> data = JsonUtil.fromJson(loreKey,new TypeReference<Map<String,Object>>() {});

        Component displayComponent = builder.translateNamed(player,display,data);

        List<Component> loreComponent = new ArrayList<>();
        for (String s : lore) {
            Component component = builder.translateNamed(player,s,data);
            loreComponent.add(component);
        }

        return parseItem(itemStack,displayComponent,loreComponent);
    }

    public static boolean isCombatItems(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) return false;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        if (isMMOItem(itemStack)) {
            NBTItem nbtItem = NBTItem.get(itemStack);

            Type type = Type.get(nbtItem);

            return ValueName.MMOITEMS_WEAPONS.contains(type);
        }else{
            return ValueName.VANILLA_WEAPONS.contains(itemStack.getType());
        }
    }

    public static double exportData(ItemStack itemStack, String key) {
        if (isMMOItem(itemStack)) {
            NBTItem nbtItem = NBTItem.get(itemStack);
            LiveMMOItem mmoItem = new LiveMMOItem(nbtItem);

            ItemStat stat = ValueName.ITEM_STAT_MAP.get(key);
            if (!mmoItem.hasData(stat)) {
                logger.error("Not found stat " + key);
                return 0;
            }
            DoubleData value = (DoubleData) mmoItem.getData(stat);
            logger.info(stat+": "+value.getValue());
            if (value == null) return 0;
            if (mmoItem.getGemstones().isEmpty()){
                return value.getValue();
            }
            int level = mmoItem.getUpgradeLevel();
            if (level == 0) {
                return value.getValue();
            }
            StatHistory history = mmoItem.getStatHistory(stat);

            value = (DoubleData) history.recalculate(true,level);

            return value.getValue();
        }else{
            if (!ValueName.ATTRIBUTES.containsKey(key)){
                logger.error("Attribute Not Found: %s",key);
                return 0.0;
            }

            Attribute attribute = ValueName.ATTRIBUTES.get(key);
            if (attribute == null) {
                logger.error("Attribute Not Found: %s", key);
                return 0.0;
            }
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) return 0.0;

            // 먼저 실제 AttributeModifiers 확인
            Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(attribute);

            if (modifiers != null && !modifiers.isEmpty()) {
                double value = 0.0;
                for (AttributeModifier modifier : modifiers) {
                    EquipmentSlot slot = modifier.getSlot();
                    // null이거나 HAND/MAINHAND인 경우 포함
                    if (slot == null || slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND) {
                        value += modifier.getAmount();
                    }
                }
                return value;
            }

            // AttributeModifiers가 없으면 새 아이템 생성해서 기본값 확인
            ItemStack freshItem = new ItemStack(itemStack.getType());
            ItemMeta freshMeta = freshItem.getItemMeta();
            if (freshMeta == null) return 0.0;

            Collection<AttributeModifier> defaultModifiers = freshMeta.getAttributeModifiers(attribute);
            if (defaultModifiers != null && !defaultModifiers.isEmpty()) {
                double value = 0.0;
                for (AttributeModifier modifier : defaultModifiers) {
                    EquipmentSlot slot = modifier.getSlot();
                    if (slot == null || slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND) {
                        value += modifier.getAmount();
                    }
                }
                return value;
            }
            return 0.0;
        }
    }
}
