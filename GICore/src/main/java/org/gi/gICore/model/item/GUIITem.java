package org.gi.gICore.model.item;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.component.adapter.GIPlayer;
import org.gi.gICore.component.adapter.ItemPack;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.manager.DataService;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.ValueName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class GUIITem extends CustomItem{
    private static final EconomyManager economyManager = new EconomyManager();
    private static final ComponentBuilder componentBuilder = new ComponentBuilder();
    private static final GIPlayer giPlayer = new GIPlayer();
    private String type;
    public GUIITem(ConfigurationSection section) {
        super(section);
        type = section.getString("type");

    }

    @Override
    public ItemStack buildItem(OfflinePlayer player, Object... arg) {
        ItemStack icon = getItem();
        switch (type){
            case "INFO":
                icon = playerData(icon,player);
                break;
            case "ARMOR_SLOT":
                break;
            default:
                icon = defaultData(icon);
                break;
        }

        ItemUtil.setString(icon,ValueName.ACTION,type);
        return icon;
    }

    private ItemStack playerData(ItemStack icon, OfflinePlayer player){
        List<Component> lore = new ArrayList<>();
        Map<String ,Object > data = DataService.getPlayerData(player);
        Component display = componentBuilder.translateNamed(getDisplay(),data);
        for (String s : getLore()){
            lore.add(componentBuilder.translateNamed(s,data));
        }

        return  ItemUtil.parseItem(icon,display,lore);
    }


    private ItemStack defaultData(ItemStack icon){
        Component display = componentBuilder.style(getDisplay()).build();
        List<Component> lore = new ArrayList<>();
        for (String s : getLore()){
            lore.add(componentBuilder.style(s).build());
        }

        return  ItemUtil.parseItem(icon,display,lore);
    }

    @Override
    public Result destroyItem(Player player, ItemStack itemStack) {
        return null;
    }

    @Override
    public boolean action(Player player, ItemStack item) {
        return false;
    }
}
