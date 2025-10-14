package org.gi.gICore.model.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import org.gi.gICore.model.item.GUIITem;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.StringUtil;
import org.gi.gICore.util.TaskUtil;
import org.gi.gICore.value.MessageName;
import org.gi.gICore.value.ValueName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusGUI extends GUIHolder{
    private ConfigCore configCore;
    private ModuleLogger logger;
    private ComponentBuilder builder = new ComponentBuilder();
    private GIPlayer giPlayer = new GIPlayer();

    public StatusGUI(ConfigCore configCore) {
        super(configCore);
        this.configCore = configCore;
        this.logger = new ModuleLogger(GICore.getInstance(),"StatusGUI");
    }

    @Override
    public Inventory load(Inventory inventory, Player player) {
        ConfigurationSection section = configCore.getSection("items");
        if (section == null){
            logger.error("Config is Not Valid");
            logger.error("Please Check Config File");
            return inventory;
        }
        GUIHolder holder = (GUIHolder) getInventory().getHolder();
        holder.getItemDataMap().putIfAbsent(ValueName.WEAPON,new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        for (String key : section.getKeys(false)){
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            String itemKey = itemSection.getString("key");
            GUIITem item = (GUIITem) ItemPack.getItem(itemKey);
            ItemStack icon = null;
            List<Integer> slots = itemSection.getIntegerList("slots");
            if (key.contains("slot")){
                if (key.equals("weapon_slot")){
                    icon = item.getWeapon(player,holder.getItemDataMap().get(ValueName.WEAPON));
                }else {
                    String armorType = key.replace("_slot","");

                    icon = item.buildItem(player,armorType);

                    if (!icon.getType().equals(Material.BLACK_STAINED_GLASS_PANE)){
                        ItemUtil.setString(icon,ValueName.ARMOR_PART,armorType.toUpperCase());
                    }
                }
            }else{
                    icon = item.buildItem(player);
            }

            for (int slot : slots){
                inventory.setItem(slot,icon);
            }
        }
        return inventory;
    }

    @Override
    public void onClick(Player player, int slot, ItemStack clickedItem, ClickType clickType) {
        GUIHolder holder = (GUIHolder) getInventory().getHolder();
        String message = "";
        if (!ItemUtil.hasKey(clickedItem, ValueName.ACTION, PersistentDataType.STRING)){
            if (clickType.isLeftClick()){
                if (ItemUtil.isArmor(clickedItem)){
                    var data = equip(player,clickedItem,slot);
                    if (data == null || data.isEmpty()){
                        player.sendMessage(MessagePack.getMessage(player.getLocale(),MessageName.EQUIP_ERROR));
                        logger.error("%s Equip Error".formatted(player.getName()));

                        logger.transData_Json(clickedItem.serialize());
                        return;
                    }
                    Component component = builder.translateNamed(player,MessagePack.getMessage(player.getLocale(), MessageName.EQUIP_ARMOR),data);
                    player.sendMessage(component);
                    holder.open(player, getData(),holder.getItemDataMap());
                }

                if (ItemUtil.isCombatItems(clickedItem)){
                    ItemStack oldMain = player.getInventory().getItemInMainHand();

                    getItemDataMap().put(ValueName.WEAPON,clickedItem);
                    player.getInventory().setItemInMainHand(clickedItem);
                    player.getInventory().setItem(slot,oldMain);

                    holder.open(player, getData(),holder.getItemDataMap());
                }
            }else{
                return;
            }
            return;
        }
        String action = ItemUtil.getValue(clickedItem, ValueName.ACTION, PersistentDataType.STRING);

        String local = player.getLocale();
        switch (action){
            case "ARMOR_SLOT":
                if (clickedItem.getType().equals(Material.BLACK_STAINED_GLASS_PANE)){
                    message = MessagePack.getMessage(local, MessageName.NOT_EQUIPMENT_ARMOR);
                    player.sendMessage(message);
                    return;
                }

                if (!ItemUtil.isArmor(clickedItem)){
                    return;
                }
                if (clickType.isRightClick()){
                   var data = unEquip(player,clickedItem);
                    if (data == null || data.isEmpty()){
                        player.sendMessage(MessagePack.getMessage(player.getLocale(),MessageName.EQUIP_ERROR));

                        logger.error("%s UnEquip Error".formatted(player.getName()));
                        logger.transData_Json(clickedItem.serialize());
                        return;
                    }

                    Component component = builder.translateNamed(player,MessagePack.getMessage(player.getLocale(), MessageName.REMOVE_EQUIP_ARMOR),data);
                    player.sendMessage(component);
                }
                if (clickType.isLeftClick()){
                    //아이템 상세스펙
                }
                break;
            case "WEAPON_SLOT":
                if (clickType.isRightClick()){
                    holder.getItemDataMap().put(ValueName.WEAPON,new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                    break;
                }
                return;
            default:
                return;
        }
        ;
        holder.open(player, getData(),holder.getItemDataMap());
        return;
    }

    private  Map<String,Object> unEquip(Player player,ItemStack item){
        if (!ItemUtil.hasKey(item,ValueName.ARMOR_PART,PersistentDataType.STRING)){
            return Map.of();
        }
        Map<String ,Object> data = new HashMap<>();
        String armorPart = ItemUtil.getValue(item,ValueName.ARMOR_PART,PersistentDataType.STRING);
        ItemStack equip = DataService.getEquipmentData(player).get(armorPart.toLowerCase());
        switch (armorPart){
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
        if (component == null){
            return Map.of();
        }
        if (ItemUtil.hasKey(equip,ValueName.ACTION,PersistentDataType.STRING)){
            ItemUtil.deleteKey(equip,ValueName.ACTION);
        }
        data.put(ValueName.EQUIPMENT,component);
        giPlayer.sendItem(player,equip);
        return data;
    }

    private Map<String,Object> equip(Player player,ItemStack item,int slot){
        Map<String ,Object> data = new HashMap<>();
        String amrormPart = ItemUtil.getArmorString(item);
        ItemStack equip = DataService.getEquipmentData(player).get(amrormPart.toLowerCase());
        switch (amrormPart){
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
        if (component == null){
            return Map.of();
        }

        data.put(ValueName.EQUIPMENT,component);
        if (equip != null){
            player.getInventory().setItem(slot,equip);
            return data;
        }

        player.getInventory().setItem(slot,null);
        return data;
    }

    private Component translate(ItemStack clickedItem){
        String key = "";
        Component x = null;
        if (ItemUtil.isMMOItem(clickedItem)){
            key = clickedItem.getItemMeta().getDisplayName();

            key = StringUtil.decolorize(key);

            if (key.contains(":")){
                key = key.split(":")[1].trim();
            }

            x = builder.translate(key);
        }else{
            key = clickedItem.getType().translationKey();
            x = Component.translatable(key);
        }
        return x;
    }
}
