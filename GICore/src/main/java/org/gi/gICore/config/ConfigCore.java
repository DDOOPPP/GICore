package org.gi.gICore.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Set;

public class ConfigCore {
    private final File file;
    private final FileConfiguration config;

    public  ConfigCore(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public List<String> getStringList(String path) {
        return  config.getStringList(path);
    }

    public List<Integer> getIntList(String path) {
        return config.getIntegerList(path);
    }

    public Set<String> getKeys() {
        return  config.getKeys(false);
    }

    public ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }
}
