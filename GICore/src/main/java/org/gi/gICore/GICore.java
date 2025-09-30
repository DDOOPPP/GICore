package org.gi.gICore;

import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.loader.CommandLoader;
import org.gi.gICore.loader.EventLoader;
import org.gi.gICore.loader.PlaceHolderLoader;
import org.gi.gICore.loader.VaultLoader;
import org.gi.gICore.manager.ConfigManager;
import org.gi.gICore.manager.DatabaseManager;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.TaskUtil;
import org.gi.gICore.util.ValidationUtil;

public final class GICore extends JavaPlugin {
    private static GICore instance;
    @Override
    public void onEnable(){
        instance = this;

        ConfigCore dbConfig = ConfigManager.getConfig("database.yml");

        DatabaseManager.initialize(dbConfig);

        TaskUtil.init(this);

        if (!VaultLoader.loadVault(this)) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!PlaceHolderLoader.PlaceHolderLoad(this)) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        MessagePack.loadPack(this);
        EventLoader.loadEvent(this);
        CommandLoader.loadCommand(this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static GICore getInstance() {
        return instance;
    }
}
