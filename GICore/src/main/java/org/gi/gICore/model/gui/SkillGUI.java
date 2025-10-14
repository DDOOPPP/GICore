package org.gi.gICore.model.gui;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.kyori.adventure.text.Component;
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
import org.gi.gICore.model.item.SkillItem;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.StringUtil;
import org.gi.gICore.value.MessageName;
import org.gi.gICore.value.ValueName;

import java.util.List;

public class SkillGUI extends GUIHolder {
    private ConfigCore configCore;
    private ModuleLogger logger;
    private ComponentBuilder builder = new ComponentBuilder();
    private GIPlayer giPlayer = new GIPlayer();

    public SkillGUI(ConfigCore configCore) {
        super(configCore);
        this.configCore = configCore;
        this.logger = new ModuleLogger(GICore.getInstance(),"SkillGUI");
    }

    @Override
    public Inventory load(Inventory inventory, Player player) {
        ConfigurationSection section = configCore.getSection("items");
        if (section == null){
            logger.error("Config is Not Valid");
            logger.error("Please Check Config File");
            return inventory;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);

            String itemKey = itemSection.getString("key");
            List<Integer> slots = itemSection.getIntegerList("slot");
            getData().putIfAbsent(ValueName.SELECT_SKILL,null);
            SkillItem item = (SkillItem) ItemPack.getItem(itemKey);
            if (key.equals("skill_slot")) {

                List<ItemStack> skills = item.buildSkillItem(player);

                for (int i = 0; i < Math.min(skills.size(),slots.size()); i++) {
                    inventory.setItem(slots.get(i), skills.get(i));
                }
            }else if (key.equals("bind_slot")) {
                List<ItemStack> bindSlot = item.buildBindSlot(player);

                for (int i = 0; i < Math.min(bindSlot.size(),slots.size()); i++) {
                    inventory.setItem(slots.get(i), bindSlot.get(i));
                }
            }else if(key.equals("select_slot")){
                ItemStack icon = item.buildSelectSkillItem(player,getData());
                if (icon != null) {
                    inventory.setItem(slots.get(0), icon);
                }
            
            }
        }
        return inventory;
    }

    @Override
    public void open(Player player) {
        PlayerData data = PlayerData.get(player);
        if (data.getProfess().getName().equals("gi.profess.newbie.name")){
            String message = MessagePack.getPlayerMessage(MessageName.NOT_PROFESSION,player.getLocale());

            player.sendMessage(message);
            return;
        }
        setInventory(load(getInventory(), player));
        player.openInventory(getInventory());
    }

    @Override
    public void onClick(Player player, int slot, ItemStack clickedItem, ClickType clickType) {
        String local = player.getLocale();

        if (!ItemUtil.hasKey(clickedItem,ValueName.ACTION, PersistentDataType.STRING)){
            return;
        }
        player.sendMessage(ItemUtil.getString(clickedItem,ValueName.ACTION));

        String action = ItemUtil.getString(clickedItem,ValueName.ACTION);
        switch (action){
            case "SKILL":
            case "VIEW":
                if (clickType.isLeftClick()){
                    if (action == "VIEW"){
                        return;
                    }
                    String id = selectSKill(clickedItem);
                    if (id == null){
                        player.sendMessage(MessagePack.getMessage(local,MessageName.SELECT_SKILL_ERROR));
                        return;
                    }
                    getData().put(ValueName.SELECT_SKILL,id);

                    var data = DataService.getSkillName(id);

                    Component message = builder.translateNamed(player,MessagePack.getMessage(local, MessageName.SELECT_SKILL),data);
                    player.sendMessage(message);
                }else if (clickType.isRightClick()){
                    if (getData().get(ValueName.SELECT_SKILL) == null){
                        return;
                    }
                    String id = selectSKill(clickedItem);
                    var data = DataService.getSkillName(id);
                    getData().put(ValueName.SELECT_SKILL,null);

                    Component message = builder.translateNamed(player,MessagePack.getMessage(local, MessageName.REMOVE_SKILL),data);
                    player.sendMessage(message);
                }
                break;
        }
        open(player,getData(),getItemDataMap());
        return;
    }

    private String selectSKill(ItemStack item){
        return ItemUtil.getString(item,ValueName.SKILL_ID);
    }
}
