package org.gi.gICore.model.table;

public class UserTables {
    private static String USERS_TABLE = "user_table";
    private static String USER_WALLTES = "user_wallets";
    private static String USER_MAILBOXS = "user_mailbox";

    public static String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE + " (" +
            "player_id VARCHAR(36) UNIQUE NOT NULL," +
            "player_name VARCHAR(40) UNIQUE NOT NULL," +
            "guild_name VARCHAR(40) NOT NULL DEFAULT 'NONE'," +
            "profession VARCHAR(50) NOT NULL," +
            "level INTEGER NOT NULL DEFAULT 1," +
            "farm_level INTEGER NOT NULL DEFAULT 1," +
            "mine_level INTEGER NOT NULL DEFAULT 1," +
            "fishing_level INTEGER NOT NULL DEFAULT 1," +
            "tutorial_done BOOLEAN NOT NULL DEFAULT FALSE," +
            "update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "PRIMARY KEY (player_id))";

}
