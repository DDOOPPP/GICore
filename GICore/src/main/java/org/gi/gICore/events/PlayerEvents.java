package org.gi.gICore.events;

import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmoitems.api.event.item.ItemEquipEvent;

import org.bukkit.Bukkit;
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
import org.gi.gICore.GICore;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerEvents implements Listener {
    private EconomyManager economy;
    private LogManager logManager;
    private ModuleLogger logger;
    private GIPlayer giPlayer;

    public PlayerEvents(JavaPlugin plugin) {
        economy = new EconomyManager();
        this.logManager = new LogManager();
        if (logger == null) {
            logger = new ModuleLogger(plugin, "PlayerEvents");
        }
        this.giPlayer = new GIPlayer();

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean result = false;
        if (!economy.hasAccount(player)) {
            result = economy.createAccount(player);

            if (!result) {
                logger.error("Failed to create account: ", player.getName());
                player.kickPlayer("Failed to create account");
                return;
            }
            logger.info("Accepted");
            TransactionLog log = new TransactionLog(
                    player.getUniqueId(),
                    TransactionLog.TransactionType.NEW,
                    BigDecimal.valueOf(economy.getBalance(player)),
                    BigDecimal.ZERO,
                    BigDecimal.valueOf(economy.getBalance(player)));
            logManager.logInsert(log, LOG_TAG.TRANSACTION);
        }
        logger.info("%s Connected".formatted(player.getName()));
    }

    // @EventHandler
    // public void onResourcePack(PlayerResourcePackStatusEvent event) {
    //     Player player = event.getPlayer();
    //     UUID uuid = player.getUniqueId();
    //     PlayerResourcePackStatusEvent.Status status = event.getStatus();

    //     if (waitingForGIResourcePack.contains(uuid)) {
    //         switch (status) {
    //             case SUCCESSFULLY_LOADED -> {
                
    //                 logger.info("ItemsAdder pack loaded for " + player.getName());

    
    //                 Bukkit.getScheduler().runTaskLater(GICore.getInstance(), () -> {
    //                     ResourcePackManager.downloadResourcePack(player);
    //                     logger.info("Sent GICore pack after ItemsAdder for " + player.getName());
    //                 }, 40L); 

    //                 waitingForGIResourcePack.remove(uuid);
    //             }
    //             case DECLINED, FAILED_DOWNLOAD -> {
    //                 logger.warn("ItemsAdder pack not loaded properly: " + player.getName());
    //                 waitingForGIResourcePack.remove(uuid);
    //             }
    //             default -> {
    //             }
    //         }
    //     } else {
    //         // 3️⃣ GICore 리소스팩 결과 로그 처리
    //         switch (status) {
    //             case SUCCESSFULLY_LOADED -> logger.info("GICore pack loaded: " + player.getName());
    //             case DECLINED -> player.kickPlayer("You must accept GICore resource pack");
    //             case FAILED_DOWNLOAD -> player.kickPlayer("GICore resource pack download failed");
    //             default -> {
    //             }
    //         }
    //     }
    // }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;

        if (ItemUtil.hasKey(itemStack, ValueName.MONEY, PersistentDataType.BOOLEAN)) {
            CustomItem iTem = ItemPack.getItem("money");
            if (iTem != null && iTem instanceof MoneyItem) {
                MoneyItem moneyItem = (MoneyItem) iTem;
                if (!moneyItem.action(event.getPlayer(), itemStack)) {
                    return;
                }
                giPlayer.removeItem(event.getPlayer(), itemStack);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GUIHolder) {
            GUIHolder guiHolder = (GUIHolder) holder;
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            int slot = event.getSlot();
            ClickType type = event.getClick();

            guiHolder.onClick(player, slot, clickedItem, type);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            GUIManager.getMainMenu().open(player);
            return;
        }
    }

    @EventHandler
    public void onChangeProfess(PlayerChangeClassEvent event) {
        event.setCancelled(true);
        return;
    }

    @EventHandler
    public void onEquipEvent(ItemEquipEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Result result = PlayerDataUtil.canEquip(player, itemStack);
        if (!result.isSuccess()) {
            String message = MessagePack.getMessage(player.getLocale(), result.getMessage());
            player.sendMessage(message);
            player.getInventory().addItem(itemStack);
            event.setCancelled(true);
        }
    }
}
