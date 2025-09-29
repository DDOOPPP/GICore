package org.gi.gICore.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.checkerframework.checker.units.qual.C;
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
    private static DatabaseManager instance;
    private ConfigCore config;
    private static HikariDataSource dataSource;
    private ModuleLogger logger;
    public DatabaseManager(ConfigCore config) {
        this.config = config;
        this.logger = new ModuleLogger(GICore.getInstance(), "DatabaseManager");
        DataBaseSetting setting = new DataBaseSetting(config);

        initialize(setting);
    }

    public static void initialize(DataBaseSetting setting){
        HikariConfig config = setting.toHikariConfig();
        dataSource = new HikariDataSource(config);
        createTable();
    }

    public static Connection getconnection() throws Exception {
        return dataSource.getConnection();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager(ConfigManager.getConfig("database.yml"));
        }
        return instance;
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

    private static void createTable(){
        try(Connection connection = getconnection();
        Statement statement = connection.createStatement()) {
            statement.execute(UserTables.USERS_TABLE);
            statement.execute(UserTables.CREATE_WALLET);

            statement.execute(LogTables.CREATE_TRANSACTION_LOG);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
