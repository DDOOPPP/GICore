package org.gi.gICore.model.guild;


public class GuildEnum {
    
    public enum GuildRole {
        NORMAL ("gi.guild.role.normal"),
        SUB_OWNER ("gi.guild.role.sub_owner"),
        OWNER ("gi.guild.role.owner");
        String display;

        GuildRole(String display){
            this.display = display;
        }
    }

    public enum LogAction{
        JOIN, KICK, QUIT, DEPOSIT, WITHDRAW, LEVEL_UP
    }
}


