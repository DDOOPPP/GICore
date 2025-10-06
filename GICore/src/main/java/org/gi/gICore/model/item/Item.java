package org.gi.gICore.model.item;


import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.util.Result;


@Getter
public abstract class Item {
    private String display;
    private String lore;
    private String namespace;
    private Material material;


    public Item(ConfigurationSection section) {
        this.display = section.getString("display");
        this.lore = section.getString("lore");
        this.namespace = section.getString("namespace");
        this.material = Material.valueOf(section.getString("material"));
    }

    public ItemStack getItem() {
        if (namespace == null || namespace.isEmpty()) {
            return new ItemStack(material);
        } else {
            return CustomStack.getInstance(namespace).getItemStack();
        }
    }

    public abstract ItemStack buildItem(OfflinePlayer player,Object... arg);

    public abstract ItemStack buildItem(Object... arg);

    public abstract Result destroyItem(Player player, ItemStack itemStack);

    public abstract void action(Player player, ItemStack item);
}
