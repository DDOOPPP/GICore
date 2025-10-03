package org.gi.gICore.manager;

import org.gi.gICore.GICore;
import org.gi.gICore.config.ConfigCore;

import java.io.File;

public class ConfigManager {
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
}
