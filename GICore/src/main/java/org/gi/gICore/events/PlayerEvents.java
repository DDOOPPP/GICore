package org.gi.gICore.events;

import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmoitems.api.event.item.ItemEquipEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.component.adapter.GIPlayer;
import org.gi.gICore.component.adapter.ItemPack;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.manager.GUIManager;
import org.gi.gICore.manager.LogManager;
import org.gi.gICore.manager.ResourcePackManager;
import org.gi.gICore.model.gui.GUIHolder;
import org.gi.gICore.model.item.CustomItem;
import org.gi.gICore.model.item.MoneyItem;
import org.gi.gICore.model.log.LOG_TAG;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.PlayerDataUtil;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.ValueName;

import java.math.BigDecimal;

public class PlayerEvents implements Listener {
    private EconomyManager economy;
    private LogManager logManager;
    private ModuleLogger logger;
    private GIPlayer giPlayer;
    public PlayerEvents(JavaPlugin plugin) {
        economy = new EconomyManager();
        this.logManager = new LogManager();
        if (logger == null) {
            logger = new ModuleLogger(plugin,"PlayerEvents");
        }
        this.giPlayer = new GIPlayer();

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        boolean result = false;
        if (!economy.hasAccount(player)){
            result = economy.createAccount(player);

            if (!result){
                logger.error("Failed to create account: ",player.getName());
                player.kickPlayer("Failed to create account");
                return;
            }
            logger.info("Accepted");
            TransactionLog log = new TransactionLog(
                    player.getUniqueId(),
                    TransactionLog.TransactionType.NEW,
                    BigDecimal.valueOf(economy.getBalance(player)),
                    BigDecimal.ZERO,
                    BigDecimal.valueOf(economy.getBalance(player))
            );
            logManager.logInsert(log, LOG_TAG.TRANSACTION);
        }
        logger.info("%s Connected".formatted(player.getName()));

        ResourcePackManager.downloadResourcePack(player);
    }

    @EventHandler
    public void onResourcePack(PlayerResourcePackStatusEvent event){
        Player player = event.getPlayer();

        PlayerResourcePackStatusEvent.Status status = event.getStatus();

        switch (status){
            case SUCCESSFULLY_LOADED:
                logger.info("Resource Pack loaded: %s",player.getName());
                break;
            case DECLINED:
                logger.warn("Resource Pack declined: %s",player.getName());
                player.kickPlayer("Resource Pack Declined");
                break;
            case FAILED_DOWNLOAD:
                logger.error("Resource Pack download failed: %s",player.getName());
                player.kickPlayer("Resource Pack Download Failed");
                break;
            case ACCEPTED:
                logger.info("Resource Pack accepted: %s",player.getName());
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (ItemUtil.hasKey(itemStack, ValueName.MONEY, PersistentDataType.BOOLEAN)){
            CustomItem iTem = ItemPack.getItem("money");
            if (iTem != null && iTem instanceof MoneyItem){
                MoneyItem moneyItem = (MoneyItem) iTem;
                if (!moneyItem.action(event.getPlayer(),itemStack)){
                    return;
                }
                giPlayer.removeItem(event.getPlayer(),itemStack);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GUIHolder){
            GUIHolder guiHolder = (GUIHolder) holder;
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            int slot = event.getSlot();
            ClickType type = event.getClick();

            guiHolder.onClick(player, slot, clickedItem,type);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        if (player.isSneaking()){
            GUIManager.getMainMenu().open(player);
            return;
        }
    }

    @EventHandler
    public void onChangeProfess(PlayerChangeClassEvent event){
        event.setCancelled(true);
        return;
    }

    @EventHandler
    public void onEquipEvent(ItemEquipEvent event){
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Result result = PlayerDataUtil.canEquip(player, itemStack);
        if (!result.isSuccess()) {
            String message = MessagePack.getMessage(player.getLocale(),result.getMessage());
            player.sendMessage(message);
            event.setCancelled(true);
        }
    }
}
