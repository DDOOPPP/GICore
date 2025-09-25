package org.gi.gICore.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class DataBaseSetting {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int maxLifetime;
    private final int connectionTimeout;
    private final int idleTimeout;
    private final int leakDetectionThreshold;
    private final int maxPoolSize;

    public DataBaseSetting(ConfigCore config) {
        this.host = config.getString("database.host");
        this.port = config.getInt("database.port");
        this.database = config.getString("database.database");
        this.username = config.getString("database.username");
        this.password = config.getString("database.password");
        this.maxPoolSize = config.getInt("database.maxPoolSize");
        this.maxLifetime = config.getInt("database.maxLifetime");
        this.connectionTimeout = config.getInt("database.connectionTimeout");
        this.idleTimeout = config.getInt("database.idleTimeout");
        this.leakDetectionThreshold = config.getInt("database.leakDetectionThreshold");
    }

    public String getURL(){
        return String.format("jdbc:mysql://%s:%d/%s", host, port, database);
    }

    public HikariConfig toHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format(getURL()));
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMaxLifetime(maxLifetime);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setLeakDetectionThreshold(leakDetectionThreshold);
        return config;
    }
}
