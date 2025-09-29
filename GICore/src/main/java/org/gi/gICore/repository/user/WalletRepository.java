package org.gi.gICore.repository.user;

import org.gi.gICore.GICore;
import org.gi.gICore.model.table.UserTables;
import org.gi.gICore.model.user.UserWallet;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class WalletRepository {
    private QueryBuilder builder;
    private ModuleLogger logger;

    public WalletRepository() {
        this.builder = new QueryBuilder(UserTables.USER_WALLTES);
        this.logger = new ModuleLogger(GICore.getInstance(),"Wallet");
    }

    public Result insert(UserWallet wallet, Connection connection) {
        String query = builder.insert(List.of("player_id","balance"),List.of("balance"),true);
        logger.info(query);
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,wallet.getPlayerId().toString());
            statement.setBigDecimal(2, wallet.getBalance());
            statement.setBigDecimal(3, wallet.getBalance());


            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("User Wallet Insert Failed");
        } catch (SQLException e) {
            return Result.EXCEPTION(e);
        }
    }

    public Result delete(UUID player_id, Connection connection) {
        String query = builder.delete().where("player_id").build();
        logger.info(query);

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,player_id.toString());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("User Wallet Delete Failed");
        } catch (SQLException e) {
            return Result.EXCEPTION(e);
        }
    }

    public BigDecimal getBalance(UUID player_id, Connection connection) {
        String query = builder.select().where("player_id").build();
        BigDecimal balance = BigDecimal.ZERO;
        logger.info(query);
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,player_id.toString());

            try(ResultSet set = statement.executeQuery()){
                if (set.next()) {
                    balance = set.getBigDecimal("balance");
                }
            }
            return balance;
        } catch (SQLException e) {
            return BigDecimal.valueOf(-99999);
        }
    }
}
