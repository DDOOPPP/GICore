package org.gi.gICore.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.gi.gICore.GICore;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.config.DataBaseSetting;
import org.gi.gICore.model.table.LogTables;
import org.gi.gICore.model.table.UserTables;
import org.gi.gICore.util.ModuleLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static HikariDataSource dataSource;
    private static ModuleLogger logger;

    public static void initialize(ConfigCore config){
        logger = new ModuleLogger(GICore.getInstance(), "DatabaseManager");
        DataBaseSetting setting = new DataBaseSetting(config);

        HikariConfig hikariConfig = setting.toHikariConfig();
        dataSource = new HikariDataSource(hikariConfig);
        if (dataSource.isRunning()){
            logger.info("Database is running");
        }
        createTable();
    }

    public static Connection getconnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        dataSource.close();
    }

    public static void close(Connection connection) throws SQLException {
        connection.close();
    }

    public static void rollback(Connection connection) throws SQLException {
        connection.rollback();
    }

    public static <T> T transactionHelper(SQLFunction<Connection, T> work) {
        try (Connection conn = DatabaseManager.getconnection()) {
            conn.setAutoCommit(false);
            try {
                T out = work.apply(conn);
                conn.commit();
                return out;
            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw new RuntimeException(e);
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB connection error", e);
        }
    }

    private static void createTable(){
        try(Connection connection = getconnection();
        Statement statement = connection.createStatement()) {
            statement.execute(UserTables.CREATE_USER_TABLE);
            statement.execute(UserTables.CREATE_WALLET);

            statement.execute(LogTables.CREATE_TRANSACTION_LOG);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface SQLFunction<C, R> {
        R apply(C conn) throws Exception;
    }
}
