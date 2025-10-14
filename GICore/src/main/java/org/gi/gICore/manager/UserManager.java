package org.gi.gICore.manager;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.gi.gICore.GICore;
import org.gi.gICore.model.log.LOG_TAG;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.model.user.UserWallet;
import org.gi.gICore.model.user.Userdata;
import org.gi.gICore.repository.log.Transaction;
import org.gi.gICore.repository.user.UserRepository;
import org.gi.gICore.repository.user.WalletRepository;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.TaskUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserManager {
    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private ModuleLogger logger;
    private Transaction transaction;

    public UserManager() {
        this.userRepository = new UserRepository();
        this.walletRepository = new WalletRepository();
        this.transaction = new Transaction();
        this.logger = new ModuleLogger(GICore.getInstance(),"User Manager");
    }

    public CompletableFuture<Result> createUser(OfflinePlayer player, BigDecimal balance) {
        CompletableFuture<Result> re = TaskUtil.supplyAsync(()-> {
            Userdata userdata = new Userdata(player);
            UserWallet userWallet = new UserWallet(player.getUniqueId(),balance);

            try(Connection connection = DatabaseManager.getconnection()) {
                connection.setAutoCommit(false);

                try{
                    Result user = userRepository.insertUser(userdata,connection);

                    if (!user.isSuccess()){
                        DatabaseManager.rollback(connection);
                        logger.error(player.getName(),user.getMessage());
                        return Result.ERROR(user.getMessage());
                    }

                    Result wallet = walletRepository.insert(userWallet,connection);
                    if (!wallet.isSuccess()){
                        DatabaseManager.rollback(connection);
                        logger.error(player.getName(),wallet.getMessage());

                        return Result.ERROR(wallet.getMessage());
                    }
                    TransactionLog log = new TransactionLog(
                            player.getUniqueId(),
                            TransactionLog.TransactionType.NEW,
                            balance,
                            BigDecimal.ZERO,
                            balance
                    );
                    Result logResult = transaction.insert(log,connection);
                    if (!logResult.isSuccess()){
                        DatabaseManager.rollback(connection);
                        logger.error(player.getName(),logResult.getMessage());

                        return Result.ERROR(logResult.getMessage());
                    }
                }catch (SQLException e){
                    DatabaseManager.rollback(connection);
                    return Result.EXCEPTION(e);
                }
                connection.commit();
                logger.info("User %s created".formatted(player.getName()));
                return Result.SUCCESS;

            } catch (SQLException e) {
                logger.error("Connection acquisition failed", e);
                return Result.EXCEPTION(e);
            }
        });
        return re;
    }

    public Result updateUser(UUID uuid, int level,  Userdata.LevelType type){
        try(Connection connection = DatabaseManager.getconnection()) {
            connection.setAutoCommit(false);

            try{
                Result result = userRepository.updateLevel(uuid,level,connection,type);
                if (!result.isSuccess()){
                    DatabaseManager.rollback(connection);
                    OfflinePlayer player = Bukkit.getPlayer(uuid);
                    logger.error(player.getName(),result.getMessage());
                    return Result.ERROR(result.getMessage());
                }
                connection.commit();
                return Result.SUCCESS;
            }catch (SQLException e){
                DatabaseManager.rollback(connection);
                return Result.EXCEPTION(e);
            }
        } catch (SQLException e) {
            logger.error("Connection acquisition failed", e);
            return Result.EXCEPTION(e);
        }
    }

    public Result deleteUser(UUID uuid){
        try(Connection connection = DatabaseManager.getconnection()) {
            connection.setAutoCommit(false);

            try{
                Result result = userRepository.deleteUser(uuid,connection);
                if (!result.isSuccess()){
                    DatabaseManager.rollback(connection);
                    OfflinePlayer player = Bukkit.getPlayer(uuid);
                    logger.error(player.getName(),result.getMessage());
                    return Result.ERROR(result.getMessage());
                }
                connection.commit();
                return Result.SUCCESS;
            }catch (SQLException e){
                DatabaseManager.rollback(connection);
                return Result.EXCEPTION(e);
            }
        } catch (SQLException e) {
            logger.error("Connection acquisition failed", e);
            return Result.EXCEPTION(e);
        }
    }

    public Result updateGuildName(UUID uuid,String guildName){
        try(Connection connection = DatabaseManager.getconnection()) {
            connection.setAutoCommit(false);

            try{
                Result result = userRepository.updateGuildName(uuid,guildName,connection);
                if (!result.isSuccess()){
                    DatabaseManager.rollback(connection);
                    OfflinePlayer player = Bukkit.getPlayer(uuid);
                    logger.error(player.getName(),result.getMessage());
                    return Result.ERROR(result.getMessage());
                }
                connection.commit();
                return Result.SUCCESS;
            }catch (SQLException e){
                DatabaseManager.rollback(connection);
                return Result.EXCEPTION(e);
            }
        } catch (SQLException e) {
            logger.error("Connection acquisition failed", e);
            return Result.EXCEPTION(e);
        }
    }

    public Result updateProfess(OfflinePlayer player) {
        PlayerData playerData = PlayerData.get(player);

        try (Connection connection = DatabaseManager.getconnection()) {
            connection.setAutoCommit(false);
            try {
                Result result = userRepository.updateProfession(player.getUniqueId(),playerData.getProfess().getName(),connection);

                if (!result.isSuccess()){
                    DatabaseManager.rollback(connection);
                    logger.error(player.getName(),result.getMessage());
                    return Result.ERROR(result.getMessage());
                }
                connection.commit();
                return Result.SUCCESS;
            }catch (SQLException e){
                DatabaseManager.rollback(connection);
                return Result.EXCEPTION(e);
            }
        }catch (SQLException e){
            logger.error("Connection acquisition failed", e);
            return Result.EXCEPTION(e);
        }
    }

    public Userdata getUser(UUID uuid){
        try(Connection connection = DatabaseManager.getconnection()){
            return userRepository.getUser(uuid,connection);
        } catch (SQLException e) {
            logger.error("Connection acquisition failed", e);
            return null;
        }
    }

    public Result insertUserWallet(UUID uuid, BigDecimal balance){
        try(Connection connection = DatabaseManager.getconnection()) {
            connection.setAutoCommit(false);

            UserWallet userWallet = new UserWallet(uuid,balance);

            try{
                Result result = walletRepository.insert(userWallet,connection);

                if (!result.isSuccess()){
                    DatabaseManager.rollback(connection);
                    OfflinePlayer player = Bukkit.getPlayer(uuid);
                    logger.error(player.getName(),result.getMessage());
                    return Result.ERROR(result.getMessage());
                }

                connection.commit();

                return Result.SUCCESS;
            }catch (SQLException e){
                DatabaseManager.rollback(connection);
                return Result.EXCEPTION(e);
            }

        } catch (SQLException e) {
            logger.error("Connection acquisition failed", e);
            return Result.EXCEPTION(e);
        }
    }

    public Result updateUserWallet(UUID uuid, BigDecimal balance){
        try(Connection connection = DatabaseManager.getconnection()) {
            connection.setAutoCommit(false);

            try{
                Result result = walletRepository.update(uuid,balance,connection);
                if (!result.isSuccess()){
                    DatabaseManager.rollback(connection);
                    OfflinePlayer player = Bukkit.getPlayer(uuid);
                    logger.error(player.getName(),result.getMessage());
                    return Result.ERROR(result.getMessage());
                }

                connection.commit();

                return Result.SUCCESS;
            }catch (SQLException e){
                DatabaseManager.rollback(connection);
                return Result.EXCEPTION(e);
            }
        } catch (SQLException e) {
            logger.error("Connection acquisition failed", e);
            return Result.EXCEPTION(e);
        }
    }

    public BigDecimal getUserWallet(UUID uuid){
        try(Connection connection = DatabaseManager.getconnection()){
            return walletRepository.getBalance(uuid,connection);
        } catch (SQLException e) {
            logger.error("Connection acquisition failed", e);
            return BigDecimal.valueOf(-99999);
        }
    }
}