package org.gi.gICore.model.table;

public class GuildTables {

    public static final String GUILD_TABLE = "guild_table";
    public static final String GUILD_MEMBERS = "guild_members";
    public static final String GUILD_FUNDS = "guild_funds";
    public static final String GUILD_FUND_LOG = "guild_fund_log";
    public static final String GUILD_LOG = "guild_log";

    public static final String CREATE_GUILD_TABLE = String.format(
        "CREATE TABLE IF NOT EXISTS %s (" +
        "guild_id CHAR(36) PRIMARY KEY, " +
        "guild_name VARCHAR(50) UNIQUE NOT NULL, " +
        "owner_id CHAR(36) NOT NULL, " +
        "level INT DEFAULT 1, " +
        "exp BIGINT DEFAULT 0, " +
        "emblem VARCHAR(64) DEFAULT 'NONE', " +
        "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "FOREIGN KEY (owner_id) REFERENCES users(player_id)" +
        ")", GUILD_TABLE
    );

    public static final String CREATE_GUILD_FUNDS_TABLE = String.format(
        "CREATE TABLE IF NOT EXISTS %s (" +
        "guild_id CHAR(36) PRIMARY KEY, " +
        "guild_name VARCHAR(50) UNIQUE NOT NULL, " +
        "fund BIGINT DEFAULT 100000, " +
        "updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "FOREIGN KEY (guild_id) REFERENCES %s(guild_id) ON DELETE CASCADE" +
        ")", GUILD_FUNDS, GUILD_TABLE
    );

    public static final String CREATE_GUILD_MEMBER_TABLE = String.format(
        "CREATE TABLE IF NOT EXISTS %s (" +
        "member_id CHAR(36) PRIMARY KEY, " +
        "member_name VARCHAR(50) NOT NULL, " +
        "guild_id CHAR(36) NOT NULL, " +
        "role VARCHAR(20) NOT NULL, " +
        "join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "FOREIGN KEY (guild_id) REFERENCES %s(guild_id) ON DELETE CASCADE, " +
        "FOREIGN KEY (member_id) REFERENCES users(player_id) ON DELETE CASCADE, " +
        "INDEX idx_guild_id (guild_id)" +
        ")", GUILD_MEMBERS, GUILD_TABLE
    );

    public static final String CREATE_GUILD_FUND_LOG_TABLE = String.format(
        "CREATE TABLE IF NOT EXISTS %s (" +
        "log_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
        "guild_id CHAR(36) NOT NULL, " +
        "member_id CHAR(36), " +
        "member_name VARCHAR(50), " +
        "action_type VARCHAR(32), " +
        "amount BIGINT DEFAULT 0, " +
        "balance_after BIGINT DEFAULT 0, " +
        "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "FOREIGN KEY (guild_id) REFERENCES %s(guild_id) ON DELETE CASCADE, " +
        "FOREIGN KEY (member_id) REFERENCES %s(member_id) ON DELETE SET NULL, " +
        "INDEX idx_fund_guild (guild_id, create_date)" +
        ")", GUILD_FUND_LOG, GUILD_TABLE, GUILD_MEMBERS
    );
    
    public static final String CREATE_GUILD_LOG_TABLE = String.format(
        "CREATE TABLE IF NOT EXISTS %s (" +
        "log_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
        "guild_id CHAR(36) NOT NULL, " +
        "actor_uuid CHAR(36), " +
        "actor_name VARCHAR(50), " +
        "target_uuid CHAR(36), " +
        "target_name VARCHAR(50), " +
        "type VARCHAR(32), " +
        "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "FOREIGN KEY (guild_id) REFERENCES %s(guild_id) ON DELETE CASCADE, " +
        "INDEX idx_log_guild (guild_id, create_date)" +
        ")", GUILD_LOG, GUILD_TABLE
    );
}
