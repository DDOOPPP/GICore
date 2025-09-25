package org.gi.gICore.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.gi.gICore.GICore;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.config.DataBaseSetting;
import org.gi.gICore.util.ModuleLogger;

import java.sql.Connection;

public class DatabaseManager {
    private static DatabaseManager instance;
    private ConfigCore config;
    private HikariDataSource dataSource;
    private ModuleLogger logger;
    public DatabaseManager(ConfigCore config) {
        this.config = config;
        this.logger = new ModuleLogger(GICore.getInstance(), "DatabaseManager");
        DataBaseSetting setting = new DataBaseSetting(config);

        initialize(setting);
    }

    public void initialize(DataBaseSetting setting){
        HikariConfig config = setting.toHikariConfig();
        dataSource = new HikariDataSource(config);
    }

    public Connection connection() throws Exception {
        return dataSource.getConnection();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private DatabaseManager() {

    }
}
