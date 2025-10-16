package org.gi.gICore.repository.guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.checkerframework.checker.units.qual.s;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.QueryBuilder;
import org.gi.gICore.model.guild.GuildFund;
import org.gi.gICore.model.table.GuildTables;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;

import io.lumine.mythic.bukkit.utils.items.nbt.reee;


public class GuildFundRepository {
    private QueryBuilder queryBuilder;
    private ModuleLogger logger;

    public GuildFundRepository(){
        if (logger == null) {
            logger = new ModuleLogger(GICore.getInstance(), "FundRepository");
        }
        queryBuilder = new QueryBuilder(GuildTables.GUILD_FUNDS);
    }

    public Result insertFund(Connection connection, GuildFund fund){
        String query = queryBuilder.insert("guild_id","fund");

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, fund.getGuildId().toString());
            statement.setBigDecimal(2, fund.getFund());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("Guild Fund Insert Fail");
        } catch (SQLException e) {
            logger.error("Guild Fund Insert Fail");
            logger.error(e.getLocalizedMessage());
            logger.error(e.getMessage());
            return Result.EXCEPTION(e);
        }
    }
}
