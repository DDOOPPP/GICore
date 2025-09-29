package org.gi.gICore.repository.user;

import io.lumine.mythic.bukkit.utils.lib.jooq.User;
import org.gi.gICore.GICore;
import org.gi.gICore.model.table.UserTables;
import org.gi.gICore.model.user.Userdata;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository {
    private QueryBuilder queryBuilder;
    private ModuleLogger logger;

    public UserRepository() {
        this.queryBuilder = new QueryBuilder(UserTables.USERS_TABLE);
        this.logger = new ModuleLogger(GICore.getInstance(),"UserRepository");
    }

    public Result insertUser(Userdata userdata, Connection connection) {
        String query = queryBuilder.insert(
                "player_id",
                "player_name",
                "guild_name",
                "profession",
                "level",
                "farm_level",
                "mine_level",
                "fishing_level",
                "tutorial_done"
                );
        logger.info(query);
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,userdata.getPlayerId().toString());
            statement.setString(2,userdata.getPlayerName());
            statement.setString(3,userdata.getGuildName());
            statement.setString(4,userdata.getProfession());
            statement.setInt(5,userdata.getLevel());
            statement.setInt(6,userdata.getFarmLevel());
            statement.setInt(7,userdata.getMineLevel());
            statement.setInt(8,userdata.getFishLevel());
            statement.setBoolean(9,userdata.isTutorial());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("User Insert Failed");
        } catch (SQLException e) {
            logger.error("User Insert Failed");
            return Result.EXCEPTION(e);
        }
    }

    public Result deleteUser(UUID player_id, Connection connection) {
        String query = queryBuilder.delete().where("player_id").build();
        logger.info(query);
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,player_id.toString());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("User Delete Failed");
        } catch (SQLException e) {
            logger.error("User Delete Failed");
            return Result.EXCEPTION(e);
        }
    }

    public Result updateLevel(UUID player_id, int level, Connection connection,String type) {
        String query = queryBuilder.update().set(type).where("player_id").build();
        logger.info(query);

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1,level);
            statement.setString(2,player_id.toString());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("User %s Update Failed".formatted(type));
        } catch (SQLException e) {
            return Result.EXCEPTION(e);
        }
    }

    public Result updateProfession(UUID player_id, String profession, Connection connection) {
        String query = queryBuilder.update().set("profession").where("player_id").build();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,profession);
            statement.setString(2,player_id.toString());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("User profession Update Failed");
        } catch (SQLException e) {
            return Result.EXCEPTION(e);
        }
    }

    public Result updateGuildName(UUID player_id, String guild_name, Connection connection) {
        String query = queryBuilder.update().set("guild_name").where("player_id").build();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,guild_name);
            statement.setString(2,player_id.toString());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR("User GuildName Update Failed");
        } catch (SQLException e) {
            return Result.EXCEPTION(e);
        }
    }

    public Userdata getUser(UUID player_id, Connection connection) {
        String query = queryBuilder.selectAll().where("player_id").build();
        Userdata userdata = null;
        logger.info(query);

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1,player_id.toString());

            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userdata = new Userdata(
                            player_id,
                            resultSet.getString("player_name"),
                            resultSet.getString("guild_name"),
                            resultSet.getString("profession"),
                            resultSet.getInt("level"),
                            resultSet.getInt("farm_level"),
                            resultSet.getInt("mine_level"),
                            resultSet.getInt("fishing_level"),
                            resultSet.getBoolean("tutorial_done")
                    );
                }
            }
            return userdata;
        } catch (SQLException e) {
            logger.error("User Search Failed");
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<Userdata> getAllUsers(Connection connection) {
        String query = queryBuilder.selectAll().build();
        logger.info(query);

        List<Userdata> userdataList = new ArrayList<>();
        Userdata data = null;
        try(PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()) {

            while(resultSet.next()) {
                data = new Userdata(
                        UUID.fromString(resultSet.getString("player_id")),
                        resultSet.getString("player_name"),
                        resultSet.getString("guild_name"),
                        resultSet.getString("profession"),
                        resultSet.getInt("level"),
                        resultSet.getInt("farm_level"),
                        resultSet.getInt("mine_level"),
                        resultSet.getInt("fishing_level"),
                        resultSet.getBoolean("tutorial_done")
                );
                userdataList.add(data);
            }
            return userdataList;
        } catch (SQLException e) {
            logger.error("UserList(ALL) Search Failed");
            logger.error(e.getMessage());
            return List.of();
        }
    }
}
