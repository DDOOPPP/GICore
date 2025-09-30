package org.gi.gICore.model.table;

import static org.gi.gICore.model.table.UserTables.USERS_TABLE;

public class LogTables {
    public static final String TRANSACTION_LOG = "transaction_log";

    public static final String CREATE_TRANSACTION_LOG ="CREATE TABLE IF NOT EXISTS " + TRANSACTION_LOG + " ("+
            "player_id VARCHAR(36) NOT NULL," +
            "type VARCHAR(30) NOT NULL DEFAULT 'NEW'," +
            "amount DECIMAL(10,0) NOT NULL DEFAULT 0," +
            "previous DECIMAL(10,0) NOT NULL DEFAULT 0," +
            "current DECIMAL(10,0) NOT NULL DEFAULT 0," +
            "create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (player_id) REFERENCES "+ USERS_TABLE + "(player_id) ON DELETE CASCADE)";

}
