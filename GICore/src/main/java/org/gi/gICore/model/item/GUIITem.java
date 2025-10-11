package org.gi.gICore.model.item;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.component.adapter.GIPlayer;
import org.gi.gICore.manager.ComponentManager;
import org.gi.gICore.manager.DataService;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.StringUtil;
import org.gi.gICore.value.ValueName;

import java.util.*;

@Getter
public class GUIITem extends CustomItem{
    private static final EconomyManager economyManager = new EconomyManager();
    private static final ComponentBuilder componentBuilder = new ComponentBuilder();
    private static final GIPlayer giPlayer = new GIPlayer();
    private static final ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"GUIITem");
    private static final ComponentManager componentManager = ComponentManager.getInstance();
    private static final GIPlayer giplayer = new GIPlayer();
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
                icon = getItem(player);
                icon = playerData(icon,player);
                break;
            case "ARMOR_SLOT":
                icon = armorSlot(icon,player,arg[0].toString());
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

        return ItemUtil.parseItem(icon,display,lore);

    }


    private ItemStack armorSlot(ItemStack icon, OfflinePlayer player,String armorType){
        Map<String,ItemStack> equipmentMap = DataService.getEquipmentData(player);
        Map<String,Object> data = new HashMap<>();
        List<Component> lore = new ArrayList<>();
        String typeKey = "gi.data.armor.type.%s".formatted(armorType);
        String noneKey = "gi.item.name.none";
        String TypeName = componentManager.getText(typeKey);
        data.put(ValueName.ARMOR_TYPE,TypeName);

        if (equipmentMap.containsKey(armorType)){
            ItemStack itemStack = equipmentMap.get(armorType);
            if (itemStack == null){
                String none = componentManager.getText(noneKey);
                data.put(ValueName.EQUIPMENT,none);
            }else{
                String key = "";
                ItemStack armor = equipmentMap.get(armorType);
                icon = armor.clone();

                if (ItemUtil.isMMOItem(armor)){
                    key = armor.getItemMeta().getDisplayName();

                    key = StringUtil.decolorize(key);

                    Component component = componentBuilder.translate(key);
                    data.put(ValueName.EQUIPMENT,component);
                }else{
                    key = icon.getType().translationKey();
                    Component component = Component.translatable(key);
                    data.put(ValueName.EQUIPMENT,component);
                }
            }
        }else{
            String none = componentManager.getText(noneKey);
            data.put(ValueName.EQUIPMENT,none);
        }

        Component display = componentBuilder.translateNamed(getDisplay(),data);
        for (String s : getLore()){
            lore.add(componentBuilder.translateNamed(s,data));
        }

        return ItemUtil.parseItem(icon,display,lore);
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
