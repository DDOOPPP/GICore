package org.gi.gICore.repository.guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gi.gICore.GICore;
import org.gi.gICore.builder.QueryBuilder;
import org.gi.gICore.model.guild.GuildEnum;
import org.gi.gICore.model.guild.GuildMember;
import org.gi.gICore.model.table.GuildTables;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;

public class GuildMemberRepository {
    private ModuleLogger logger;
    private QueryBuilder builder;

    public GuildMemberRepository() {
        if (logger == null) {
            this.logger = new ModuleLogger(GICore.getInstance(), "GuildFund");
        }

        this.builder = new QueryBuilder(GuildTables.GUILD_MEMBERS);
    }

    public Result insert(Connection connection , GuildMember member){
        String query = builder.insert("member_id","member_name","guild_id","role");

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, member.getMemberId().toString());
            statement.setString(2, member.getMemberName());
            statement.setString(3, member.getGuildId().toString());
            statement.setString(4, member.getRole().name());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILURE;
        } catch (SQLException e) {
            logger.error("GuildMember Insert Fail CODE: [%s]".formatted(e.getErrorCode()));
            logger.error("INSERT DATA: [%s]".formatted(member.toString()));
            logger.error(e.getMessage());

            return Result.EXCEPTION(e);
        }
    }

    public Result delete(Connection connection , Object arg){
        String param = null;
        String query = "";
        if (arg instanceof UUID uuid){
            param = uuid.toString();
            query = builder.delete().where("member_id").build();
        }else if(arg instanceof String){
            param = arg.toString();
            query = builder.delete().where("member_name").build();
        }else{
            return Result.ERROR("Unsupported argument type: " + arg.getClass().getName());
        }

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, param);


            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILURE;
        } catch (SQLException e) {
            logger.error("GuildMember Delete Fail CODE: [%s]".formatted(e.getErrorCode()));
            logger.error(e.getMessage());
            return Result.EXCEPTION(e);
        }
    }

    public Result update(Connection connection , GuildMember member){
        String query = builder.update().set("role").where("member_id").build();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, member.getRole().name());
            statement.setString(2, member.getMemberId().toString());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILURE;
        } catch (SQLException e) {
            logger.error("GuildMember Update Fail CODE: [%s]".formatted(e.getErrorCode()));
            logger.error("UPDATE DATA: [%s]".formatted(member.toString()));
            logger.error(e.getMessage());
            return Result.EXCEPTION(e);
        }
    }

    public GuildMember getMemberById(Connection connection , UUID memberId){
        String query = builder.selectAll().where("member_id").build();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, memberId.toString());
            GuildMember member = null;
            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    member = new GuildMember(
                            memberId,
                            resultSet.getString("member_name"),
                            UUID.fromString(resultSet.getString("guild_id")),
                            GuildEnum.GuildRole.valueOf(resultSet.getString("role"))
                    );
                }
                return member;
            }
        } catch (SQLException e) {
            logger.error("GuildMember Select Fail CODE: [%s]".formatted(e.getErrorCode()));
            logger.error(e.getMessage());
            return null;
        }
    }

    public  List<GuildMember> getMemberAll(Connection connection , UUID guildId){
        String query = builder.selectAll().where("guild_id").build();
        List<GuildMember> members = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, guildId.toString());
            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    GuildMember member = new GuildMember(
                            UUID.fromString(resultSet.getString("member_id")),
                            resultSet.getString("member_name"),
                            guildId,
                            GuildEnum.GuildRole.valueOf(resultSet.getString("role"))
                    );
                    members.add(member);
                }
                return members;
            }
        } catch (SQLException e) {
            logger.error("GuildMember Select By All GuildMember Fail CODE: [%s]".formatted(e.getErrorCode()));
            logger.error(e.getMessage());
            return List.of();
        }
    }
}
