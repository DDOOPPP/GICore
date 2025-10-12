package org.gi.gICore.model.item;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.C;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.component.adapter.GIPlayer;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.manager.DataService;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.JsonUtil;
import org.gi.gICore.util.MessageUtil;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.MessageName;
import org.gi.gICore.value.ValueName;

import java.awt.color.ICC_ColorSpace;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoneyItem extends CustomItem {
    private static final EconomyManager economyManager = new EconomyManager();
    private static final ComponentBuilder componentBuilder = new ComponentBuilder();
    private static final GIPlayer giPlayer = new GIPlayer();

    public MoneyItem(ConfigurationSection section) {
        super(section);
    }

    @Override
    public ItemStack buildItem(OfflinePlayer player, Object... arg) {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(arg[0].toString()));

        Component display = componentBuilder.style(getDisplay()).with(amount.doubleValue()).gold().build() ;
        List<Component> lore = new ArrayList<>();

        for (String s : getLore()){
               lore.add(componentBuilder.style(s).with(display).white().build());
        }
        ItemStack icon = getItem();
        icon = ItemUtil.parseItem(icon,display,lore);
        ItemUtil.setDouble(icon, ValueName.AMOUNT, amount.doubleValue());
        ItemUtil.setBoolean(icon,ValueName.MONEY,true);

        return icon;
    }

    @Override
    public Result destroyItem(Player player, ItemStack itemStack) {
        return giPlayer.removeItem(player,itemStack);
    }

    @Override
    public boolean action(Player player, ItemStack item) {
        if (!ItemUtil.hasKey(item, ValueName.MONEY, PersistentDataType.BOOLEAN)){
             return false;
        }
        double amount = ItemUtil.getValue(item, ValueName.AMOUNT, PersistentDataType.DOUBLE);
        String message = "";
        String local = player.getLocale();
        var result = economyManager.depositPlayer(player,amount);

        if (!result.transactionSuccess()){
            message = MessagePack.getMessage(local,result.errorMessage);
            player.sendMessage(message);
            return false;
        }
        message = MessagePack.getMessage(local, MessageName.DEPOSIT_SUCCESS);

        Map<String ,String > data = DataService.getEconomyData(result);
        message = MessageUtil.parse(message,player,data);
        player.sendMessage(message);
        return true;
    }
}
