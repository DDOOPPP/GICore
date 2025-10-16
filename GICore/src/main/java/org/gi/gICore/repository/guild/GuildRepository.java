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
import org.gi.gICore.model.guild.Guild;
import org.gi.gICore.model.table.GuildTables;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;

public class GuildRepository {
    private ModuleLogger logger;
    private QueryBuilder queryBuilder;

    public GuildRepository() {
        if (logger == null) {
            logger = new ModuleLogger(GICore.getInstance(), "GuildRepository");
        }
        queryBuilder = new QueryBuilder(GuildTables.GUILD_TABLE);
    }

    public Result insertGuild(Guild guild, Connection connection) {
        String query = queryBuilder.insert(
                "guild_id", "guild_name",
                "owner_id", "level", "exp", "emblem");
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, guild.getGuildId().toString());
            statement.setString(2, guild.getGuildName());
            statement.setString(3, guild.getOwnerId().toString());
            statement.setInt(4, guild.getLevel());
            statement.setBigDecimal(5, guild.getExp());
            statement.setString(6, guild.getEmblem());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("Guild Insert Fail");
        } catch (SQLException e) {
            logger.error("Guild Insert Fail");
            logger.error("Insert Data: [%s]".formatted(guild.toString()));
            return Result.EXCEPTION(e);
        }
    }

    public Result updateGuild(Connection connection, UUID guildId, Object object, String set) {
        if (!List.of("level", "exp", "emblem").contains(set)) {
            return Result.ERROR("Invalid column name: " + set);
        }
        String query = queryBuilder.update().set(set).where("guild_id = ?").build();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (set.equals("level")) {
                int level = (object instanceof Number)
                        ? ((Number) object).intValue()
                        : Integer.parseInt(object.toString());
                statement.setInt(1, level);
            } else if (set.equals("exp")) {
                BigDecimal exp = (object instanceof BigDecimal)
                        ? (BigDecimal) object
                        : new BigDecimal(object.toString());
                statement.setBigDecimal(1, exp);
            } else if (set.equals("emblem")) {
                statement.setString(1, object.toString());
            }
            statement.setString(2, guildId.toString());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("Guild Update %s Fail".formatted(set));
        } catch (SQLException e) {
            logger.error("Guild Update %s Fail".formatted(set));
            return Result.EXCEPTION(e);
        }
    }

    public Result deleteGuild(Connection connection,UUID guildID){
        String query = queryBuilder.delete().where("guild_id").build();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, guildID.toString());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("Guild Delete Fail");
        } catch (SQLException e) {
            logger.error("Guild Delete Fail");
            return Result.EXCEPTION(e);
        }
    } 

    public Guild getGuildByID(Connection connection, UUID guildID){
        String query = queryBuilder.selectAll().where("guild_id = ?").build();
        Guild guild = null;
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, guildID.toString());

            try(ResultSet resultSet = statement.executeQuery()){
                guild = new Guild(
                    guildID,
                    resultSet.getString("guild_name"),
                    UUID.fromString(resultSet.getString("owner_id")),
                    resultSet.getInt("level"),
                    resultSet.getBigDecimal("exp"),
                    resultSet.getString("emblem")
                );
            }

            return guild;
        } catch (SQLException e) {
            logger.error("Guild Selcet Fail: [%s]".formatted(guildID.toString()));
            logger.error(e.getMessage());
            logger.warn(e.getLocalizedMessage());
            return null;
        }
    }

    public List<Guild> getALLGuilds(Connection connection){
        String query = queryBuilder.selectAll().build();

        List<Guild> guilds = new ArrayList();
        try(PreparedStatement statement = connection.prepareStatement(query)){
            Guild guild = null;

            try(ResultSet resultSet = statement.executeQuery()){
                guild = new Guild(
                    UUID.fromString(resultSet.getString("guild_id")),
                    resultSet.getString("guild_name"),
                    UUID.fromString(resultSet.getString("owner_id")),
                    resultSet.getInt("level"),
                    resultSet.getBigDecimal("exp"),
                    resultSet.getString("emblem")
                );

                guilds.add(guild);
            }

            return guilds;
        } catch (SQLException e) {
            logger.error("All Guild Selcet Fail");
            logger.error(e.getMessage());
            logger.warn(e.getLocalizedMessage());
            return List.of();
        }
    }
}
