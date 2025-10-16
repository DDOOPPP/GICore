package org.gi.gICore.model.item;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.ValueName;

import java.util.*;

@Getter
public class GUIITem extends CustomItem {
    private static final ComponentBuilder componentBuilder = new ComponentBuilder();

    public GUIITem(ConfigurationSection section) {
        super(section);
    }

    @Override
    public ItemStack buildItem(OfflinePlayer player, Object... arg) {
        ItemStack icon = getItem();
        icon = defaultData(icon);

        ItemUtil.setString(icon, ValueName.ACTION, getType());
        return icon;
    }

    private ItemStack defaultData(ItemStack icon) {
        Component display = componentBuilder.style(getDisplay()).build();
        List<Component> lore = new ArrayList<>();
        for (String s : getLore()) {
            lore.add(componentBuilder.style(s).build());
        }

        return ItemUtil.parseItem(icon, display, lore);
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
