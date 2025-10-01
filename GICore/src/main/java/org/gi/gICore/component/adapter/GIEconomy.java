package org.gi.gICore.component.adapter;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.gi.gICore.GICore;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.manager.ConfigManager;
import org.gi.gICore.manager.LogManager;
import org.gi.gICore.manager.UserManager;
import org.gi.gICore.model.log.LOG_TAG;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.repository.log.Transaction;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.MessageName;

import java.math.BigDecimal;
import java.util.List;

public class GIEconomy implements Economy {
    private ConfigCore config;
    private UserManager userManager;
    private double start;
    private final String unit;
    private final LogManager logManager;
    private ModuleLogger logger;
    public GIEconomy(){
        this.config = ConfigManager.getConfig("config.yml");
        this.start = config.getDouble("economy.start");
        this.unit = config.getString("economy.unit");
        logger = new ModuleLogger(GICore.getInstance(),"Economy");
        userManager = new UserManager();
        logManager = new LogManager();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "GICore";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return amount + unit;
    }

    @Override
    public String currencyNamePlural() {
        return unit;
    }

    @Override
    public String currencyNameSingular() {
        return unit;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return hasAccount(player,null);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return userManager.getUser(player.getUniqueId()) != null;
    }

    @Override
    public double getBalance(String playerName) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(player,null);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        BigDecimal balance = userManager.getUserWallet(player.getUniqueId());

        // null = 계정 없음
        if (balance == null) {
            logger.error("Wallet not found for: " + player.getName());
            return 0.0;
        }

        // 음수 = 데이터 무결성 오류 (절대 발생하면 안됨!)
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("CRITICAL: Negative balance for " + player.getName() + ": " + balance);
            return balance.doubleValue(); // 음수 그대로 반환하여 상위에서 감지
        }

        return balance.doubleValue();
    }

    @Override
    public boolean has(String playerName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(player,null,amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        if (amount < 0) return false;

        if (getBalance(player,worldName) >= amount) {
            return true;
        }
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(player,null,amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        if (amount < 0) return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE, MessageName.SMALL_THEN_ZERO);

        if (!has(player,worldName,amount)) {
            return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE,MessageName.NOT_ENOUGH_MONEY);
        }

        BigDecimal old_balance = BigDecimal.valueOf(getBalance(player,worldName));
        BigDecimal new_balance = old_balance.subtract(BigDecimal.valueOf(amount));

        Result result = userManager.updateUserWallet(player.getUniqueId(),new_balance);
        if (result.isSuccess()){
            TransactionLog transactionLog = new TransactionLog(
                    player.getUniqueId(),
                    TransactionLog.TransactionType.WITHDRAW,
                    BigDecimal.valueOf(amount),
                    old_balance,
                    new_balance
            );

            logManager.logInsert(transactionLog, LOG_TAG.TRANSACTION);
            return new EconomyResponse(amount,new_balance.doubleValue(),EconomyResponse.ResponseType.SUCCESS,MessageName.WITHDRAW_SUCCESS);
        }
        return new EconomyResponse(amount,old_balance.doubleValue(),EconomyResponse.ResponseType.FAILURE,MessageName.WITHDRAW_FAIL);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(player,null,amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        if (amount < 0) return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE,MessageName.SMALL_THEN_ZERO);

        BigDecimal old_balance = BigDecimal.valueOf(getBalance(player,worldName));
        if (old_balance.doubleValue() < 0) {
            return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE,MessageName.CALL_MANAGER);
        }

        BigDecimal new_balance = old_balance.add(BigDecimal.valueOf(amount));

        Result result = userManager.updateUserWallet(player.getUniqueId(),new_balance);
        if (result.isSuccess()){
            TransactionLog transactionLog = new TransactionLog(
                    player.getUniqueId(),
                    TransactionLog.TransactionType.DEPOSIT,
                    BigDecimal.valueOf(amount),
                    old_balance,
                    new_balance
            );

            logManager.logInsert(transactionLog, LOG_TAG.TRANSACTION);
            return new EconomyResponse(amount,new_balance.doubleValue(),EconomyResponse.ResponseType.SUCCESS,MessageName.DEPOSIT_SUCCESS);
        }
        return new EconomyResponse(amount,old_balance.doubleValue(),EconomyResponse.ResponseType.FAILURE,MessageName.DEPOSIT_FAIL);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player,null);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        if (hasAccount(player,worldName)) return true;
        var result = userManager.createUser(player,BigDecimal.valueOf(start));
        Result mere = result.join();
        if (!mere.isSuccess()){
            logger.error("Create User Error: " + mere.getMessage());
            return false;
        }
        return true;
    }
}