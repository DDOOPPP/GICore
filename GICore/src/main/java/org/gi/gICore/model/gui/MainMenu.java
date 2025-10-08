package org.gi.gICore.model.gui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.gi.gICore.GICore;
import org.gi.gICore.component.adapter.ItemPack;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.manager.GUIManager;
import org.gi.gICore.model.item.GUIITem;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.value.ValueName;

import java.util.List;

public class MainMenu extends GUIHolder{
    private ConfigCore configCore;
    private ModuleLogger logger;

    public MainMenu(ConfigCore configCore) {
        super(configCore);
        this.configCore = configCore;
        this.logger = new ModuleLogger(GICore.getInstance(),"MainMenu");
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
            List<Integer> slots = itemSection.getIntegerList("slots");

            GUIITem item = (GUIITem) ItemPack.getItem(key);

            ItemStack icon = item.buildItem(player);

            for (int slot : slots){
                inventory.setItem(slot,icon);
            }
        }
        return inventory;
    }

    @Override
    public void onClick(Player player, int slot, ItemStack clickedItem, ClickType clickType) {
        if (!ItemUtil.hasKey(clickedItem, ValueName.ACTION, PersistentDataType.STRING)){
            return;
        }
        String action = ItemUtil.getValue(clickedItem, ValueName.ACTION, PersistentDataType.STRING);

        switch (action){
            case "STATUS":
                GUIManager.getStatusGUI().open(player);
                return;
        }
        player.sendMessage(action);
    }
}
