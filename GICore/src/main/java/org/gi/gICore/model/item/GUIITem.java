package org.gi.gICore.model.item;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.component.adapter.GIPlayer;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.ValueName;

import java.util.ArrayList;
import java.util.List;

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

        Component display = componentBuilder.style(getDisplay()).build();
        List<Component> lore = new ArrayList<>();
        lore.add(componentBuilder.style(getLore()).build());

        icon = ItemUtil.parseItem(icon,display,lore);

        ItemUtil.setString(icon,ValueName.ACTION,type);
        return icon;
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
