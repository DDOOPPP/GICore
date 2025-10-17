package org.gi.gICore.repository.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.QueryBuilder;
import org.gi.gICore.model.log.GuildFundLog;
import org.gi.gICore.model.table.GuildTables;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;

public class FundRepository {
    private ModuleLogger logger;
    private QueryBuilder builder;

    public FundRepository(){
        if (logger == null) {
            logger = new ModuleLogger(GICore.getInstance(), "FundRepository");
        }
        builder = new QueryBuilder(GuildTables.GUILD_FUND_LOG);
    }

    public Result insertGuildLog(Connection connection, GuildFundLog log){
        String query = builder.insert("guild_id","member_id","member_name","action_type","amount","balance_after");

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, log.getGuildId().toString());
            statement.setString(2, log.getMemberId().toString());
            statement.setString(3, log.getMemberName());
            statement.setString(4, log.getTransactionType().getDisplay());
            statement.setBigDecimal(5, log.getAmount());
            statement.setBigDecimal(6, log.getBalance());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILURE;
        } catch (SQLException e) {
            logger.error("Fund Insert Fail: [%s]".formatted(e.getErrorCode()));
            logger.error(e.getMessage());
            return Result.EXCEPTION(e);
        }
    }
}
