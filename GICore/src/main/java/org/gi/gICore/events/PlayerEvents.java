package org.gi.gICore.events;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.GICore;
import org.gi.gICore.component.adapter.GIEconomy;
import org.gi.gICore.loader.VaultLoader;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.manager.LogManager;
import org.gi.gICore.manager.UserManager;
import org.gi.gICore.model.log.LOG_TAG;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;

import java.math.BigDecimal;

public class PlayerEvents implements Listener {
    private EconomyManager economy;
    private LogManager logManager;
    private ModuleLogger logger;
    public PlayerEvents(JavaPlugin plugin) {
        economy = new EconomyManager();
        this.logManager = new LogManager();
        this.logger = new ModuleLogger(plugin,"PlayerEvents");
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
    }
}
