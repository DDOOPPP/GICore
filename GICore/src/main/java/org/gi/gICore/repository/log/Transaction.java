package org.gi.gICore.repository.log;

import org.gi.gICore.GICore;
import org.gi.gICore.model.log.TransactionLog;
import org.gi.gICore.model.table.LogTables;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transaction {
    private QueryBuilder builder;
    private ModuleLogger logger;

    public Transaction() {
        this.builder = new QueryBuilder(LogTables.TRANSACTION_LOG);
        this.logger = new ModuleLogger(GICore.getInstance(),"Transaction");
    }

    public Result insert(final TransactionLog log, Connection connection) {
        String query = builder.insert(
                "player_id",
                "type",
                "amount",
                "previous",
                "current");

        logger.info(query);

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1,log.getPlayerId().toString());
            statement.setString(2,log.getType().getDisplay());
            statement.setBigDecimal(3,log.getAmount());
            statement.setBigDecimal(4,log.getPrevious());
            statement.setBigDecimal(5,log.getCurrent());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("Transaction failed");
        } catch (SQLException e) {
            return Result.EXCEPTION(e);
        }
    }
}
