package org.gi.gICore;

import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.component.adapter.ItemPack;
import org.gi.gICore.component.adapter.MessagePack;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.loader.CommandLoader;
import org.gi.gICore.loader.EventLoader;
import org.gi.gICore.loader.PlaceHolderLoader;
import org.gi.gICore.loader.VaultLoader;
import org.gi.gICore.manager.ConfigManager;
import org.gi.gICore.manager.DatabaseManager;
import org.gi.gICore.manager.ResourcePackManager;
import org.gi.gICore.util.TaskUtil;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        ItemPack.initializer();
        ConfigManager.loadGUIConfig();
        ResourcePackManager.initialize();
    }

    public static void copyResourceFolder(String folderName) {
        File folder = new File(instance.getDataFolder(), folderName);
        folder.mkdirs();

        try(JarFile jar = new JarFile(instance.getFile())) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                // 해당 폴더의 파일인지 확인
                if (name.startsWith(folderName + "/") && !entry.isDirectory()) {
                    String fileName = name.substring(folderName.length() + 1);

                    try (InputStream input = jar.getInputStream(entry)) {
                        File outFile = new File(folder, fileName);
                        outFile.getParentFile().mkdirs();
                        Files.copy(input, outFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            instance.getLogger().info("Copied resource folder: " + folderName);
        } catch (Exception e) {
            instance.getLogger().severe("Failed to copy resource folder: " + folderName);
        }
    }
    @Override
    public void onDisable() {
        instance = null;
    }

    public static GICore getInstance() {
        return instance;
    }
}
