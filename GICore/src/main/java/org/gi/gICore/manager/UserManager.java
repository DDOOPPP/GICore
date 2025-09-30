package org.gi.gICore.manager;

import io.lumine.mythic.bukkit.utils.lib.jooq.User;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import jdk.jfr.consumer.RecordedStackTrace;
import org.bukkit.OfflinePlayer;
import org.eclipse.aether.RepositorySystemSession;
import org.gi.gICore.GICore;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.model.user.UserWallet;
import org.gi.gICore.model.user.Userdata;
import org.gi.gICore.repository.log.Transaction;
import org.gi.gICore.repository.user.UserRepository;
import org.gi.gICore.repository.user.WalletRepository;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;

import javax.xml.crypto.Data;
import java.lang.ref.PhantomReference;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserManager {
    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private Transaction transaction;
    private ModuleLogger logger;


    public UserManager() {
        this.userRepository = new UserRepository();
        this.walletRepository = new WalletRepository();
        this.transaction = new Transaction();

        this.logger = new ModuleLogger(GICore.getInstance(),"User Manager");
    }

    public Result createUser(OfflinePlayer player) {
        Connection connection = null;
        BigDecimal balance = BigDecimal.valueOf(500);
        Userdata userdata = new Userdata(player);
        UserWallet userWallet = new UserWallet(player.getUniqueId(),balance);

        try{
            connection = DatabaseManager.getconnection();

            connection.setAutoCommit(false);

            Result createUser = userRepository.insertUser(userdata,connection);

            if (!createUser.isSuccess()){
                DatabaseManager.rollback(connection);
                return Result.ERROR("User creation failed");
            }

            Result createWallet = walletRepository.insert(userWallet,connection);
            if (!createWallet.isSuccess()){
                DatabaseManager.rollback(connection);
                return Result.ERROR("User Wallet Create failed");
            }
            TransactionLog log = new TransactionLog(
                    userdata.getPlayerId(),
                    TransactionLog.TransactionType.NEW,
                    balance,
                    BigDecimal.ZERO,
                    balance
            );

            Result logResult = transaction.insert(log,connection);
            if (!logResult.isSuccess()){
                DatabaseManager.rollback(connection);
                return Result.ERROR("Log Insert failed");
            }
            return Result.SUCCESS;

        } catch (SQLException e) {
            try {
                logger.error("UserData Create Failed",e.getMessage());
                DatabaseManager.rollback(connection);
            } catch (SQLException ex) {
                logger.error("UserData RollBack Failed",ex.getMessage());
            }
            return Result.EXCEPTION(e);
        }finally {
            if (connection != null) {
                try {
                    DatabaseManager.close(connection);
                } catch (SQLException ignored) {

                }
            }
        }
    }


}
