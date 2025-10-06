package org.gi.gICore.model.item;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.component.adapter.GIPlayer;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.manager.DataService;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.manager.ResourcePackManager;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.MessageUtil;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.MessageName;
import org.gi.gICore.value.ValueName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoneyItem extends Item{
    private EconomyManager economyManager;
    private ComponentBuilder componentBuilder;
    private GIPlayer giPlayer;

    public MoneyItem(ConfigurationSection section) {
        super(section);
        this.economyManager = new EconomyManager();
        this.componentBuilder = new ComponentBuilder();
        giPlayer = new GIPlayer();
    }

    @Override
    public ItemStack buildItem(OfflinePlayer player, Object... arg) {
        return null;
    }

    @Override
    public ItemStack buildItem(Object... arg) {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(arg[0].toString()));

        ItemStack icon = getItem();

        Component display = componentBuilder.style(getDisplay()).with(amount.doubleValue()).gold().build();
        List<Component> lore = new ArrayList<>();

        lore.add(componentBuilder.style(getLore()).with(amount.doubleValue()).build());

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
    public void action(Player player, ItemStack item) {
        if (!ItemUtil.hasKey(item, ValueName.MONEY, PersistentDataType.BOOLEAN)){
            return;
        }
        double amount = ItemUtil.getValue(item, ValueName.AMOUNT, PersistentDataType.DOUBLE);
        String message = "";
        String local = player.getLocale();
        var result = economyManager.depositPlayer(player,amount);

        if (!result.transactionSuccess()){
            message = MessagePack.getMessage(local,result.errorMessage);
            player.sendMessage(message);
            return;
        }
        message = MessagePack.getMessage(local, MessageName.DEPOSIT_SUCCESS);

        Map<String ,String > data = DataService.getEconomyData(result);
        message = MessageUtil.parse(message,player,data);
        player.sendMessage(message);

        Result isRemoved = destroyItem(player, item);
        if (isRemoved.isSuccess()){
            //메세지 새로 뽑을것
            return;
        }
        return;
    }
}
