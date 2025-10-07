package org.gi.gICore.manager;

import org.gi.gICore.GICore;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.model.gui.MainMenu;
import org.gi.gICore.util.ModuleLogger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    public static Map<String ,ConfigCore> configCoreMap = new HashMap<>();
    private static ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"ConfigManager");
    public static ConfigCore getConfig(String fileName){
        File file = new File(GICore.getInstance().getDataFolder(),fileName);
        if(!file.exists()){
            GICore.getInstance().saveResource(fileName,false);
        }
        return new ConfigCore(file);
    }

    public static File getLangugeFile(){
        String path = "messages/output/assets/minecraft/lang/ko_kr.json";
        File file = new File(GICore.getInstance().getDataFolder(),path);
        if(!file.exists()){
            GICore.getInstance().saveResource(path,false);
        }
        return file;
    }

    public static void loadGUIConfig (){
        File guiFolder = new File(GICore.getInstance().getDataFolder(),"gui");
        if (!guiFolder.exists()){
            GICore.getInstance().copyResourceFolder("gui");
        }

        for (File f : guiFolder.listFiles()){
            if (f.isDirectory()){
                continue;
            }
            if (!f.getName().endsWith(".yml")){
                continue;
            }

            File configFile = new File(f.getParentFile(),f.getName());
            ConfigCore config = new ConfigCore(configFile);
            String title = config.getString("title");
            if (title == null || title.isEmpty()){
                logger.error("Invalid title: " + f.getName());
                return;
            }
            logger.info("Loaded GUI Config: " + title);
            logger.info(f.getName().replace(".yml",""));
            configCoreMap.put(f.getName().replace(".yml",""),config);
        }

        return;
    }

    public ConfigCore getGUIConfig(String key){
        return configCoreMap.get(key);
    }
}
