package org.gi.gICore.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.GICore;

public class QueryBuilder {
    private final String tableName;
    public QueryBuilder(String tableName) {
        ValidationUtil.requireNonEmpty(tableName, "tableName cannot be null or empty");
        
        this.tableName = tableName;
    }

    public String selectAll() {
        return "SELECT * FROM " + tableName;
    }
}
