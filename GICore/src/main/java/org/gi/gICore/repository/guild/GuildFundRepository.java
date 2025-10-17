package org.gi.gICore.repository.guild;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.QueryBuilder;
import org.gi.gICore.model.guild.GuildFund;
import org.gi.gICore.model.table.GuildTables;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;

public class GuildFundRepository {
    private ModuleLogger logger;
    private QueryBuilder builder;

    public GuildFundRepository() {
        if (logger == null) {
            this.logger = new ModuleLogger(GICore.getInstance(), "GuildFund");
        }

        this.builder = new QueryBuilder(GuildTables.GUILD_FUNDS);
    }

    public Result insert(Connection connection, GuildFund fund) {
        if (fund == null) {
            return Result.ERROR("Fund is Null");
        }

        String query = builder.insert("guild_id", "guild_name", "fund");

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, fund.getGuildId().toString());
            statement.setString(2, fund.getGuildName());
            statement.setBigDecimal(3, fund.getFund());

            return statement.executeUpdate() > 0 ? Result.SUCCESS
                    : Result.ERROR("Guild Fund Insert Fail: %s".formatted(fund.toString()));
        } catch (SQLException e) {
            logger.error("Guild Fund Insert Fail: %s".formatted(fund.toString()));
            return Result.EXCEPTION(e);
        }
    }

    public BigDecimal getFund(Connection connection, UUID guildId) {
        String query = builder.select("fund").where("guild_id").build();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, guildId.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return BigDecimal.ZERO;
                }
                BigDecimal fund = resultSet.getBigDecimal("fund");
                return fund;
            }
        } catch (SQLException e) {
            logger.error("Guild Fund Search Fail: %s ".formatted(guildId.toString()));
            logger.error(e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public Result update(Connection connection, UUID guildId, BigDecimal fund) {
        String query = builder.update().where("guild_id").set("fund").build();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBigDecimal(1, fund);
            statement.setString(2, guildId.toString());

            return statement.executeUpdate() > 0 ? Result.SUCCESS
                    : Result.ERROR("Guild Fund Update Fail: %s : %s".formatted(guildId.toString(), fund.doubleValue()));
        } catch (SQLException e) {
            logger.error("Guild Fund Update Fail: %s : %s".formatted(guildId.toString(), fund.doubleValue()));
            return Result.EXCEPTION(e);
        }
    }

    public List<GuildFund> getALLGuilds(Connection connection) {
        String query = builder.selectAll().build();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            List<GuildFund> funds = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    GuildFund fund = new GuildFund(
                            UUID.fromString(resultSet.getString("guild_id")),
                            resultSet.getString("guild_name"),
                            resultSet.getBigDecimal("fund"));
                    funds.add(fund);
                }
            }
            return funds;
        } catch (SQLException e) {
            logger.error("Guild Fund Search (All) Fail");
            logger.error(e.getMessage());
            return List.of();
        }

    }

    public Result deleteGuild(Connection connection, UUID guildId) {
        if (guildId == null) {
            return Result.ERROR("Id is Null");
        }

        String query = builder.delete().where("guild_id").build();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            return statement.executeUpdate() > 0 ? Result.SUCCESS
                    : Result.ERROR("Guild Fund Delete Fail: {%s}".formatted(guildId));
        } catch (SQLException e) {
            logger.error("Guild Fund Delete Fail: {%s}".formatted(guildId));
            return Result.EXCEPTION(e);
        }
    }
}
