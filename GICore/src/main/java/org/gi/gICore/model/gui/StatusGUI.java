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
import org.gi.gICore.component.adapter.ItemPack;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.model.item.GUIITem;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.StringUtil;
import org.gi.gICore.value.MessageName;
import org.gi.gICore.value.ValueName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusGUI extends GUIHolder{
    private ConfigCore configCore;
    private ModuleLogger logger;
    private ComponentBuilder builder = new ComponentBuilder();

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
        for (String key : section.getKeys(false)){
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            String itemKey = itemSection.getString("key");
            GUIITem item = (GUIITem) ItemPack.getItem(itemKey);
            ItemStack icon = null;
            List<Integer> slots = itemSection.getIntegerList("slots");
            if (key.contains("slot")){
                String armorType = key.replace("_slot","");
                logger.info("Load Armor: %s",armorType);

                icon = item.buildItem(player,armorType);

                if (!icon.getType().equals(Material.BLACK_STAINED_GLASS_PANE)){
                    ItemUtil.setString(icon,ValueName.ARMOR_PART,armorType.toUpperCase());
                }
            }else{
                icon = item.buildItem(player);
            }

            for (int slot : slots){
                inventory.setItem(slot,icon);
            }
        }
        GUIHolder holder = (GUIHolder) getInventory().getHolder();
        if (holder.getData() == null){
            return inventory;
        }
        logger.info("Load Data: %s",holder.getData().get("TEST"));
        return inventory;
    }

    @Override
    public void onClick(Player player, int slot, ItemStack clickedItem, ClickType clickType) {
        GUIHolder holder = (GUIHolder) getInventory().getHolder();
        holder.getData().put("TEST","TEST");
        String message = "";
        if (!ItemUtil.hasKey(clickedItem, ValueName.ACTION, PersistentDataType.STRING)){
            if (!ItemUtil.isArmor(clickedItem)){
                return;
            }
            if (clickType.isLeftClick()){
                var data = equip(player,clickedItem,slot);
                if (data == null || data.isEmpty()){
                    player.sendMessage(MessagePack.getMessage(player.getLocale(),MessageName.EQUIP_ERROR));
                    return;
                }
                Component component = builder.translateNamed(MessagePack.getMessage(player.getLocale(), MessageName.EQUIP_ARMOR),data);
                player.sendMessage(component);

                holder.open(player,getData());
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
                if (clickType.isRightClick()){
                    unEquip(player,clickedItem);
                }
                if (clickType.isLeftClick()){
                    //아이템 상세스펙
                }
                break;
            default:
                return;
        }
        holder.open(player,getData());
        return;
    }

    private boolean unEquip(Player player,ItemStack item){
        if (!ItemUtil.hasKey(item,ValueName.ARMOR_PART,PersistentDataType.STRING)){
            return false;
        }
        String armorPart = ItemUtil.getValue(item,ValueName.ARMOR_PART,PersistentDataType.STRING);
        player.sendMessage("UnEquip: "+armorPart);

        return true;
    }
    private Map<String,Object> equip(Player player,ItemStack item,int slot){
        Map<String ,Object> data = new HashMap<>();
        String amrormPart = ItemUtil.getArmorString(item);
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
        String key = "";
        if (ItemUtil.isMMOItem(item)){
            key = item.getItemMeta().getDisplayName();

            key = StringUtil.decolorize(key);

            logger.info(key);

            Component component = builder.translate(key);
            data.put(ValueName.EQUIPMENT,component);
        }else{
            key = item.getType().translationKey();
            Component component = Component.translatable(key);
            data.put(ValueName.EQUIPMENT,component);
            String text = PlainTextComponentSerializer.plainText().serialize(component);
            logger.info(text);
        }
        player.getInventory().setItem(slot,null);
        return data;
    }
}
