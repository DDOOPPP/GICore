package org.gi.gICore.events;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.GICore;
import org.gi.gICore.component.adapter.GIEconomy;
import org.gi.gICore.component.adapter.ItemPack;
import org.gi.gICore.loader.VaultLoader;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.manager.LogManager;
import org.gi.gICore.manager.ResourcePackManager;
import org.gi.gICore.manager.UserManager;
import org.gi.gICore.model.item.Item;
import org.gi.gICore.model.item.MoneyItem;
import org.gi.gICore.model.log.LOG_TAG;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.util.ItemUtil;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.ValueName;

import java.math.BigDecimal;

public class PlayerEvents implements Listener {
    private EconomyManager economy;
    private LogManager logManager;
    private ModuleLogger logger;
    public PlayerEvents(JavaPlugin plugin) {
        economy = new EconomyManager();
        this.logManager = new LogManager();
        if (logger == null) {
            logger = new ModuleLogger(plugin,"PlayerEvents");
        }

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
            Item iTem = ItemPack.getItem("money");
            if (iTem != null && iTem instanceof MoneyItem){
                MoneyItem moneyItem = (MoneyItem) iTem;
                moneyItem.action(event.getPlayer(),itemStack);
            }
            event.setCancelled(true);
        }
    }
}
