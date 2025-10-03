package org.gi.gICore.builder;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.MessageUtil;
import org.gi.gICore.value.ValueName;

import java.math.BigDecimal;
import java.util.List;

public class ItemBuilder {

    public ItemStack buildItem(String itemName) {
        if (itemName == null) return null;

        if (Material.getMaterial(itemName) != null) {
            return ItemUtil.getItem(itemName);
        }

        if (ItemUtil.isCustom(itemName)) {
            return ItemUtil.getCustomItem(itemName);
        }
        return null;
    }

    public MMOItem buildMMOItem(String itemName, Type type) {
        return MMOItems.plugin.getMMOItem(type,itemName);
    }

    public boolean isMMOItem(ItemStack itemStack) {
        NBTItem item = NBTItem.get(itemStack);

        return item.getType() != null;
    }

    public Type findType(ItemStack itemStack) {
        NBTItem item = NBTItem.get(itemStack);
        return Type.get(item);
    }

    public Type findType (String typename){
        String upper = typename.toUpperCase();
        if (!Type.isValid(upper)) {
            return null;
        }
        return Type.get(upper);
    }

    public String getID (ItemStack itemStack) {
        NBTItem item = NBTItem.get(itemStack);
        LiveMMOItem mmoItem = new LiveMMOItem(item);
        return mmoItem.getId();
    }

    public MMOItem buildMMOItem(ItemStack itemStack, Player player) {
        if (!isMMOItem(itemStack)) {
            return null;
        }
        if (player == null) {
            return new LiveMMOItem(itemStack);
        };

        PlayerData data = PlayerData.get(player);

        return MMOItems.plugin.getMMOItem(findType(itemStack),getID(itemStack),data);
    }

    public ItemStack buildMoney(BigDecimal amount) {
        ItemStack item = new ItemStack(Material.PAPER);
        if (ItemUtil.isCustom("gi:money")) {
            item = ItemUtil.getCustomItem("gi:money");
        }

        String unit = EconomyManager.getUnit();
        String loreKey = EconomyManager.getLoreKey();

        Component display = new EconomyManager().format(amount.doubleValue());

        List<Component> lore = MessageUtil.list(loreKey);

        item = ItemUtil.parseItem(item,display,lore);

        ItemUtil.setDouble(item, ValueName.AMOUNT, amount.doubleValue());
        ItemUtil.setBoolean(item,ValueName.MONEY,true);

        return item;
    }
}
