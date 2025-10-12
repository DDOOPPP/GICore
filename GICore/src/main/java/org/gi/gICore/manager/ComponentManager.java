package org.gi.gICore.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.gi.gICore.GICore;
import org.gi.gICore.util.JsonUtil;
import org.gi.gICore.util.ModuleLogger;

import java.io.File;
import java.util.*;

public class ComponentManager {
    private static ComponentManager instance;
    ModuleLogger logger;

    private Set<String> transferKeys = new HashSet<>();
    private Map<String, Map<String,String>> translationMap = new HashMap<>();
    private List<String> supportedLangs = List.of("en_us", "ko_kr", "ja_jp");
    public ComponentManager(){
        logger = new ModuleLogger(GICore.getInstance(),"ComponentManager");
        registerTranslation(ConfigManager.getLangugeFile());
        loadTemplate();
        instance = this;
    }

    private void loadTemplate(){
        File outputDir = new File(GICore.getInstance().getDataFolder(),"messages/output/assets/minecraft/lang");

        File[] files = outputDir.listFiles();
        for (File file : files){
            if (file.isDirectory()){
                continue;
            }
            if (!file.getName().endsWith(".json")){
                continue;
            }

            Map<String,String> keyValueMap = JsonUtil.getKeyValueMap(file);
            if (keyValueMap == null){
                logger.error("Failed to load translation file: " + file.getName());
                logger.error("Please check the file format.");
                continue;
            }
            translationMap.put(file.getName().replace(".json",""),keyValueMap);
        }
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

    public String getText(OfflinePlayer player, String key){
        String local = null;
        if (player.isOnline()){
            local =  player.getPlayer().getLocale();
        }else{
            local = "ko_kr";
        }

        Map<String,String> langMap = translationMap.get(local);
        if (langMap == null){
            logger.error("Failed to find translation file for " + local);
            return "";
        }

        if (!langMap.containsKey(key)){
            logger.error("Failed to find translation key: " + key);
            return key;
        }

        return langMap.get(key);
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
        loadTemplate();
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
