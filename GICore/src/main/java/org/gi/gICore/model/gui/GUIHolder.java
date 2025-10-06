package org.gi.gICore.model.gui;

import io.lumine.mythic.bukkit.utils.lib.jooq.impl.QOM;
import io.lumine.mythic.lib.listener.option.GameIndicators;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.GICore;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.util.ModuleLogger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class GUIHolder implements InventoryHolder {
    private Inventory inventory;
    private UUID player_id;
    private Map<String, Object> data = new HashMap<>();
    private ModuleLogger logger;

    public GUIHolder(ConfigCore configCore, UUID player_id) {
        logger = new ModuleLogger(GICore.getInstance(),"GUIHolder");
        Component title = Component.translatable(configCore.getString("title"));
        int size = configCore.getInt("size");
        if (size <= 0){
            InventoryType type = InventoryType.valueOf(configCore.getString("type").toUpperCase());
            inventory = Bukkit.createInventory(this, type, title);
        }else {
            if (!(size / 9 == 0)){
                logger.error("Config is Not Valid: %s",size);
                size = 9;
            }
            this.inventory = Bukkit.createInventory(this,size,title);
        }
        this.player_id = player_id;

    }

    public void open(Player player) {
        inventory = load(inventory, player);
        player.openInventory(inventory);
    }

    public void open(Player player,Map<String, Object> data) {
        this.data = data;
        inventory = load(inventory, player);
        player.openInventory(inventory);
    }

    public abstract Inventory load(Inventory inventory,Player player);

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
