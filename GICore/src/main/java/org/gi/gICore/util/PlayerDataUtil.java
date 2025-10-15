package org.gi.gICore.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.t;
import org.gi.gICore.GICore;
import org.gi.gICore.value.MessageName;

import io.lumine.mythic.bukkit.utils.items.nbt.reee;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.StringListData;

public class PlayerDataUtil {
    private static ModuleLogger logger = new ModuleLogger(GICore.getInstance(), "PlayerDataUtil");

    public static Result canEquip(OfflinePlayer player, ItemStack itemStack){
        if (ItemUtil.isMMOItem(itemStack)) { //MMOItems랑 일반이랑 별개 처리
            PlayerData playerData = PlayerData.get(player);

            NBTItem item = NBTItem.get(itemStack);
            LiveMMOItem mmoItem = new LiveMMOItem(item);
            if (ItemUtil.isArmor(itemStack) || ItemUtil.isCombatItems(itemStack)) {
                boolean jobResult = false;
                boolean levelResult = false;
                if (mmoItem.hasData(ItemStats.REQUIRED_CLASS)) {
                    jobResult = hasProfession(playerData, mmoItem); //직업 확인
                    if (!jobResult) {
                        return Result.FAILURE(MessageName.WRONG_CLASS);
                    }
                }
                if (mmoItem.hasData(ItemStats.REQUIRED_LEVEL)){
                    levelResult = hasRequiredLevel(playerData, mmoItem); //레벨 확인
                    if (!levelResult) {
                        return Result.FAILURE(MessageName.NOT_ENOUGH_LEVEL);
                    }
                }
                return Result.SUCCESS; //성공시는 사용하는 곳에서 메세지를 사용
            }
        }else{
            if(ItemUtil.isArmor(itemStack) || ItemUtil.isCombatItems(itemStack)){ //방어구 이거나 아님 무기류인경우 성공
                return Result.SUCCESS;
            }
        }
        return Result.FAILURE;
    }

    public static boolean hasRequiredLevel(PlayerData playerData,LiveMMOItem liveMMOItem){
        int level = playerData.getLevel();

        DoubleData reqLevelData = (DoubleData) liveMMOItem.getData(ItemStats.REQUIRED_LEVEL);
        if (reqLevelData == null) return true; // 요구 레벨 없음 → 통과

        double req_level = reqLevelData.getValue();

        return level >= req_level;
    } 

    public static boolean hasProfession(PlayerData playerData,LiveMMOItem liveMMOItem){
        String id = playerData.getProfess().getId();

        var data = (StringListData) liveMMOItem.getData(ItemStats.REQUIRED_CLASS);
        if (data == null) return true;
        for(String job : data.getList()){
            if (id.equals(job)) {
                return true;
            }
        }
        return false;
    }
}
