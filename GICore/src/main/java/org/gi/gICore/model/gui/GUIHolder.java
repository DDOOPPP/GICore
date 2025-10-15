package org.gi.gICore.model.gui;

import io.lumine.mythic.bukkit.utils.lib.jooq.impl.QOM;
import io.lumine.mythic.lib.listener.option.GameIndicators;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.GICore;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.TaskUtil;
import org.gi.gICore.value.ValueName;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class GUIHolder implements InventoryHolder {
    @Setter
    private Inventory inventory;
    @Getter
    @Setter
    private Map<String, Object> data = new HashMap<>();
    @Getter
    private Map<String, ItemStack> itemDataMap = new HashMap<>();
    private ModuleLogger logger;

    public GUIHolder(ConfigCore configCore) {
        logger = new ModuleLogger(GICore.getInstance(), "GUIHolder");
        Component title = Component.translatable(configCore.getString("title"));
        int size = configCore.getInt("size");
        String typeS = configCore.getString("type");
        if (size <= 0) {
            InventoryType type = InventoryType.valueOf(typeS.toUpperCase());
            if (type == null) {
                type = InventoryType.CHEST;
            }
            inventory = Bukkit.createInventory(this, type, title);
        } else {
            if (!(size % 9 == 0)) {
                logger.error("Config is Not Valid: %s", size);
                size = 9;
            }
            this.inventory = Bukkit.createInventory(this, size, title);
        }

    }

    public void open(Player player) {
        getData().put(ValueName.ISFIRST, true);
        inventory = load(inventory, player);
        getData().put(ValueName.ISFIRST, false);
        player.openInventory(inventory);
    }

    public void open(Player player, Map<String, Object> data, Map<String, ItemStack> itemDataMap) {
        this.data = data;
        this.itemDataMap = itemDataMap;
        inventory = load(inventory, player);
        player.openInventory(inventory);
    }

    public void delayOpen(Player player, Map<String, Object> data, Map<String, ItemStack> itemDataMap) {
        TaskUtil.runSyncLater(8L, () -> {
            this.data = data;
            this.itemDataMap = itemDataMap;
            inventory = load(inventory, player);
            player.openInventory(inventory);
        });
    }

    public abstract Inventory load(Inventory inventory, Player player);

    public abstract void onClick(Player player, int slot, ItemStack clickedItem, ClickType clickType);

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
