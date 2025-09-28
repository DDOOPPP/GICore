package org.gi.gICore.model.table;

public class LogTables {
    public static final String TRANSACTION_LOG = "transaction_log";

    public static final String CREATE_TRANSACTION_LOG ="CREATE TABLE IF NOT EXISTS " + TRANSACTION_LOG + " ("+
            "player_id VARCHAR(36) NOT NULL," +
            "type VARCHAR (30) NOT NULL DEFAULT 'NEW'" +
            "amount DECIMAL (10,0) NOT NULL DEFAULT 0," +
            "amount DECIMAL (10,0) NOT NULL DEFAULT 0," +
            "amount DECIMAL (10,0) NOT NULL DEFAULT 0,";

}
