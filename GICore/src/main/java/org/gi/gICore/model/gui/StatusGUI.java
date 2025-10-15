package org.gi.gICore.model.gui;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.component.adapter.GIPlayer;
import org.gi.gICore.component.adapter.ItemPack;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.manager.DataService;
import org.gi.gICore.model.item.StatusItem;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.PlayerDataUtil;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.StringUtil;
import org.gi.gICore.util.TaskUtil;
import org.gi.gICore.value.MessageName;
import org.gi.gICore.value.ValueName;

import io.lumine.mythic.bukkit.utils.items.nbt.reee;
import lombok.Getter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusGUI extends GUIHolder {
    private ConfigCore configCore;
    private ModuleLogger logger;
    private ComponentBuilder builder = new ComponentBuilder();
    private GIPlayer giPlayer = new GIPlayer();

    public StatusGUI(ConfigCore configCore) {
        super(configCore);
        this.configCore = configCore;
        this.logger = new ModuleLogger(GICore.getInstance(), "StatusGUI");
    }

    @Override
    public Inventory load(Inventory inventory, Player player) {
        ConfigurationSection section = configCore.getSection("items");
        if (section == null) {
            logger.error("Config is Not Valid");
            logger.error("Please Check Config File: Not found Key : items");
            return inventory;
        }
        getItemDataMap().putIfAbsent(ValueName.WEAPON, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            if (itemSection == null) {
                logger.error("Not found Key : %s", key);
                continue;
            }
            String item_key = itemSection.getString("key");
            List<Integer> slots = itemSection.getIntegerList("slots");

            if (item_key == null || slots == null) {
                logger.error(key + " Data Error");
                continue;
            }
            ItemStack icon = new ItemStack(Material.AIR);

            StatusItem item = (StatusItem) ItemPack.getItem(item_key);

            if (key.equals("info")) {
                icon = item.buildPlayerInfo(player);

            } else if (key.endsWith("_slot") && !key.equals("weapon_slot")) {
                String armorType = key.replace("_slot", "");

                icon = item.buildArmorSlot(player, armorType);
                if (!icon.getType().equals(Material.BLACK_STAINED_GLASS_PANE)) {
                    ItemUtil.setString(icon, ValueName.ARMOR_PART, armorType.toUpperCase());
                }

            } else if (key.equals("weapon_slot")) {
                Object raw = getData().get(ValueName.ISFIRST);
                boolean isFirst = (raw instanceof Boolean b) ? b : false;

                if (isFirst) {
                    ItemStack mainHand = player.getInventory().getItemInMainHand();
                    if (mainHand == null || mainHand.getType().equals(Material.AIR)) {
                        icon = getItemDataMap().get(ValueName.WEAPON);
                    }
                    if (PlayerDataUtil.canEquip(player, mainHand).isSuccess()) {
                        icon = mainHand;
                    }
                } else {
                    icon = getItemDataMap().get(ValueName.WEAPON);
                }
                icon = item.buildWeaponSlot(player, icon);
            }
            for (int slot : slots) {
                inventory.setItem(slot, icon);
            }
        }
        return inventory;
    }

    @Override
    public void onClick(Player player, int slot, ItemStack clickedItem, ClickType clickType) {
        Map<String, Object> placeholder = new HashMap<>();
        String local = player.getLocale();
        String message = "";
        PlayerData playerData = PlayerData.get(player);
        // GUI 내부 클릭 (ValueName.ACTION 존재)
        if (ItemUtil.hasKey(clickedItem, ValueName.ACTION, PersistentDataType.STRING)) {
            String action = ItemUtil.getString(clickedItem, ValueName.ACTION);

            switch (action) {
                case StatusAction.INFO -> {
                    if (clickType.isLeftClick()) {
                        playerData.getStats().updateStats();
                    }
                }

                case StatusAction.ARMOR_SLOT -> {
                    if (!clickType.isRightClick())
                        break;

                    placeholder = unEquip(player, clickedItem);
                    if (placeholder == null || placeholder.isEmpty()) {
                        player.sendMessage(MessagePack.getMessage(local, MessageName.EQUIP_ERROR));
                        logger.error("%s UnEquip Error".formatted(player.getName()));
                        logger.transData_Json(clickedItem.serialize());
                        break;
                    }

                    Component component = builder.translateNamed(
                            player,
                            MessagePack.getMessage(local, MessageName.REMOVE_EQUIP_ARMOR),
                            placeholder);
                    player.sendMessage(component);

                    open(player, getData(), getItemDataMap());

                    return;
                }

                case StatusAction.WEAPON_SLOT -> {
                    if (clickType.isRightClick()) {
                        ItemStack mainHand = player.getInventory().getItemInMainHand();

                        giPlayer.sendItem(player, mainHand, true);

                        player.getInventory().setItemInMainHand(null);

                        ItemUtil.deleteKey(clickedItem, ValueName.ACTION);
                        getItemDataMap().put(ValueName.WEAPON, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

                        playerData.getStats().updateStats();
                        break;
                    }
                    return;
                }

                default -> {
                    playerData.getStats().updateStats();
                }
            }

            delayOpen(player, placeholder, getItemDataMap());
            return;
        }

        if (!clickType.isLeftClick())
            return;

        if (ItemUtil.isArmor(clickedItem)) {
            Result result = PlayerDataUtil.canEquip(player, clickedItem);
            if (!result.isSuccess()) {
                message = MessagePack.getMessage(local, result.getMessage());
                player.sendMessage(message);
                return;
            }

            placeholder = equip(player, clickedItem, slot);
            if (placeholder == null || placeholder.isEmpty()) {
                player.sendMessage(MessagePack.getMessage(local, MessageName.EQUIP_ERROR));
                logger.error("%s Equip Error".formatted(player.getName()));
                logger.transData_Json(clickedItem.serialize());
                return;
            }

            Component component = builder.translateNamed(
                    player,
                    MessagePack.getMessage(local, MessageName.EQUIP_ARMOR),
                    placeholder);
            player.sendMessage(component);
            open(player, getData(), getItemDataMap());
            return;
        }

        if (ItemUtil.isCombatItems(clickedItem)) {
            Result result = PlayerDataUtil.canEquip(player, clickedItem);
            if (!result.isSuccess()) {
                message = MessagePack.getMessage(local, result.getMessage());
                player.sendMessage(message);
                return;
            }
            playerData.getStats().updateStats();
            
            ItemStack oldMain = player.getInventory().getItemInMainHand();

            player.getInventory().setItem(slot, oldMain);

            player.getInventory().setItemInMainHand(clickedItem);
            getItemDataMap().put(ValueName.WEAPON, clickedItem);
        }
        delayOpen(player, placeholder, getItemDataMap());
    }

    private Map<String, Object> unEquip(Player player, ItemStack item) {
        if (!ItemUtil.hasKey(item, ValueName.ARMOR_PART, PersistentDataType.STRING)) {
            return Map.of();
        }
        Map<String, Object> data = new HashMap<>();
        String armorPart = ItemUtil.getValue(item, ValueName.ARMOR_PART, PersistentDataType.STRING);
        ItemStack equip = DataService.getEquipmentData(player).get(armorPart.toLowerCase());
        switch (armorPart) {
            case "HELMET":
                player.getInventory().setHelmet(null);
                break;
            case "CHESTPLATE":
                player.getInventory().setChestplate(null);
                break;
            case "LEGGINGS":
                player.getInventory().setLeggings(null);
                break;
            case "BOOTS":
                player.getInventory().setBoots(null);
                break;
        }
        Component component = translate(item);
        if (component == null) {
            return Map.of();
        }
        if (ItemUtil.hasKey(equip, ValueName.ACTION, PersistentDataType.STRING)) {
            ItemUtil.deleteKey(equip, ValueName.ACTION);
        }
        data.put(ValueName.EQUIPMENT, component);
        giPlayer.sendItem(player, equip, true);
        return data;
    }

    private Map<String, Object> equip(Player player, ItemStack item, int slot) {
        Map<String, Object> data = new HashMap<>();
        String amrormPart = ItemUtil.getArmorString(item);
        ItemStack equip = DataService.getEquipmentData(player).get(amrormPart.toLowerCase());
        switch (amrormPart) {
            case "HELMET":
                player.getInventory().setHelmet(item);
                break;
            case "CHESTPLATE":
                player.getInventory().setChestplate(item);
                break;
            case "LEGGINGS":
                player.getInventory().setLeggings(item);
                break;
            case "BOOTS":
                player.getInventory().setBoots(item);
                break;
            default:
                return Map.of();
        }

        Component component = translate(item);
        if (component == null) {
            return Map.of();
        }

        data.put(ValueName.EQUIPMENT, component);
        if (equip != null) {
            player.getInventory().setItem(slot, equip);
            return data;
        }

        player.getInventory().setItem(slot, null);
        return data;
    }

    private Component translate(ItemStack clickedItem) {
        String key = "";
        Component x = null;
        if (ItemUtil.isMMOItem(clickedItem)) {
            key = clickedItem.getItemMeta().getDisplayName();

            key = StringUtil.decolorize(key);

            if (key.contains(":")) {
                key = key.split(":")[1].trim();
            }

            x = builder.translate(key);
        } else {
            key = clickedItem.getType().translationKey();
            x = Component.translatable(key);
        }
        return x;
    }

    @Getter
    private static class StatusAction {
        private static final String INFO = "INFO";
        private static final String ARMOR_SLOT = "ARMOR_SLOT";
        private static final String WEAPON_SLOT = "WEAPON_SLOT";
    }
}
