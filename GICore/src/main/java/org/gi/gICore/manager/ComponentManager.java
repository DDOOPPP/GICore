package org.gi.gICore.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.gi.gICore.GICore;
import org.gi.gICore.util.JsonUtil;
import org.gi.gICore.util.ModuleLogger;

import java.io.File;
import java.util.*;

public class ComponentManager {
    private static ComponentManager instance;
    ModuleLogger logger;

    private Set<String> transferKeys = new HashSet<>();
    private Map<String, String> translationMap = new HashMap<>();

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

        Map<String, String> keyValueMap = JsonUtil.getKeyValueMap(translationFile);
        if (keyValueMap != null) {
            translationMap.putAll(keyValueMap);
        }
    }


    public boolean hasKey(String key){
        return transferKeys.contains(key);
    }

    public String getText(String key){
        return translationMap.getOrDefault(key, key);
    }

    public String getText(Component component, Locale locale){
        String translate = PlainTextComponentSerializer.plainText().serialize(
                GlobalTranslator.render(component, locale)
        );
        return translate;
    }

    public void reload(){
        transferKeys.clear();
        translationMap.clear();
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
