package org.gi.gICore.manager;

import org.gi.gICore.GICore;
import org.gi.gICore.util.JsonUtil;
import org.gi.gICore.util.ModuleLogger;

import java.io.File;
import java.util.*;

public class ComponentManager {
    private static ComponentManager instance;
    ModuleLogger logger;

    private Set<String> transferKeys = new HashSet<>();
    public ComponentManager(){
        registerTranslation(ConfigManager.getLangugeFile());
        logger = new ModuleLogger(GICore.getInstance(),"ComponentManager");
        instance = this;
    }


    public void registerTranslation(File translationFile) {
        Set<String> keys = JsonUtil.getKeys(translationFile);

        if (keys == null) {
            return;
        }
        transferKeys.addAll(keys);
    }


    public boolean hasKey(String key){
        return transferKeys.contains(key);
    }

    public void reload(){
        transferKeys.clear();
        registerTranslation(ConfigManager.getLangugeFile());
        logger.info("Reload Translation Keys");
        for (String key : transferKeys){
            logger.info(key);
        }
        logger.info("End Reload Translation");
    }

    public static ComponentManager getInstance(){
        if (instance == null){
            instance = new ComponentManager();
        }
        return instance;
    }
}
