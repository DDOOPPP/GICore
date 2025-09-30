package org.gi.gICore.manager;


import org.bukkit.OfflinePlayer;
import org.gi.gICore.GICore;
import org.gi.gICore.model.user.UserWallet;
import org.gi.gICore.model.user.Userdata;
import org.gi.gICore.repository.log.Transaction;
import org.gi.gICore.repository.user.UserRepository;
import org.gi.gICore.repository.user.WalletRepository;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class UserManager {
    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private ModuleLogger logger;


    public UserManager() {
        this.userRepository = new UserRepository();
        this.walletRepository = new WalletRepository();
        this.logger = new ModuleLogger(GICore.getInstance(),"User Manager");
    }

    public Result createUser(OfflinePlayer player) {
        BigDecimal balance = BigDecimal.valueOf(500);
        Userdata userdata = new Userdata(player);
        UserWallet userWallet = new UserWallet(player.getUniqueId(),balance);

        try(Connection connection = DatabaseManager.getconnection()) {
            connection.setAutoCommit(false);

            try{
                Result user = userRepository.insertUser(userdata,connection);

                if (!user.isSuccess()){
                    DatabaseManager.rollback(connection);
                    return Result.ERROR(user.getMessage());
                }

                Result wallet = walletRepository.insert(userWallet,connection);
                if (!wallet.isSuccess()){
                    DatabaseManager.rollback(connection);
                    return Result.ERROR(wallet.getMessage());
                }
            }catch (SQLException e){
                DatabaseManager.rollback(connection);
                return Result.EXCEPTION(e);
            }
            connection.commit();
            return Result.SUCCESS;

        } catch (SQLException e) {
            logger.error("Connection acquisition failed", e);
            return Result.EXCEPTION(e);
        }
    }

    public Result updateUser(UUID uuid, int level,  Userdata.LevelType type){
        try(Connection connection = DatabaseManager.getconnection()) {
            connection.setAutoCommit(false);

            try{
                Result result = userRepository.updateLevel(uuid,level,connection,type);
                if (!result.isSuccess()){
                    DatabaseManager.rollback(connection);
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
}
