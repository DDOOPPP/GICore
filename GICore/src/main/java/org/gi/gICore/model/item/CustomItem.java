package org.gi.gICore.model.item;


import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.s;
import org.gi.gICore.manager.GUIManager;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.Result;

import java.util.List;


@Getter
public abstract class CustomItem {
    private String display;
    private List<String> lore;
    private String namespace;
    private Material material;
    private String type;
    private String action;

    public CustomItem(ConfigurationSection section) {
        this.display = section.getString("display");
        this.lore = section.getStringList("lore");
        this.namespace = section.getString("namespace");
        this.material = Material.valueOf(section.getString("material"));
        this.type = section.getString("type");
        this.action = section.getString("action");
    }

    public ItemStack getItem() {
        if (namespace == null || namespace.isEmpty()) {
            return new ItemStack(material);
        } else {
            return CustomStack.getInstance(namespace).getItemStack();
        }
    }

    public abstract ItemStack buildItem(OfflinePlayer player,Object... arg);

    public abstract Result destroyItem(Player player, ItemStack itemStack);

    public abstract boolean action(Player player, ItemStack item);

    public ItemStack getItem(OfflinePlayer player){
        return ItemUtil.getPlayerHead(player);
    }
    //뒤로가기 버튼 쓸대 쓸거임
    public void openMainMenu(Player player){
        GUIManager.getMainMenu().open(player);
    }
}
