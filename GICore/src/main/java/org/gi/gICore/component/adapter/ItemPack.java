package org.gi.gICore.component.adapter;

import org.bukkit.configuration.ConfigurationSection;
import org.gi.gICore.GICore;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.model.item.CustomItem;
import org.gi.gICore.model.item.GUIITem;
import org.gi.gICore.model.item.MoneyItem;
import org.gi.gICore.model.item.SkillItem;
import org.gi.gICore.model.item.StatusItem;
import org.gi.gICore.util.ModuleLogger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemPack {
    private static final Map<String, CustomItem> ITEM_MAP = new HashMap<>();
    private static final Map<String, String> SKILL_ITEM = new HashMap<>();

    private static ModuleLogger logger;

    public static void initializer(){
        if (logger == null){
            logger = new ModuleLogger(GICore.getInstance(),"ItemPack");
        }
        register();
        loadSKILLItem();
    }

    private static void register(){
        File base = new File(GICore.getInstance().getDataFolder(),"item");

        if (!base.exists()){
            base.mkdirs();
            logger.info("Item folder not found. Create folder.");

            GICore.getInstance().copyResourceFolder("item");
        }

        for (File f : base.listFiles()){
            if (f.isDirectory()){
                continue;
            }
            if (!f.getName().endsWith(".yml")){
                continue;
            }

            ConfigCore config = new ConfigCore(f);

            int i = 0;
            for (String key : config.getKeys()){
                if (key == null || key.isEmpty()){
                    logger.error("Invalid key: " + key);
                    continue;
                }

                ConfigurationSection section = config.getSection(key);
                if (section == null){
                    logger.error("Invalid section: " + key);
                    continue;
                }
                String action = section.getString("action");
                if (action == null || action.isEmpty()){
                    logger.error("Invalid action: " + key);
                    continue;
                }
                CustomItem item = switch (action){
                    case "MONEY" -> new MoneyItem(section);
                    case "NONE"-> new GUIITem(section);
                    case "STATUS" -> new StatusItem(section);
                    case "SKILL" -> new SkillItem(section);
                    default -> null;
                };

                ITEM_MAP.put(key, item);
                i ++;
            }
            logger.info("Loaded %d items from %s".formatted(i,f.getName()));
        }
    }

    private static void loadSKILLItem(){
        File file = new File(GICore.getInstance().getDataFolder(), "skill/skill.yml");
        if (!file.exists()) {
            GICore.getInstance().saveResource("skill/skill.yml", false);
        }
        ConfigCore configCore = new ConfigCore(file);

        for(String key : configCore.getKeys()){
              if (key == null || key.isEmpty()) {
                    continue;
                }
                
                String value = configCore.getString(key);
                logger.info(key+":"+value);
                SKILL_ITEM.put(key, value);
        }
    }

    public static String getTexture(String key){
        return SKILL_ITEM.get(key);
    }

    public static CustomItem getItem(String key){
        return ITEM_MAP.getOrDefault(key,null);
    }

    public static boolean hasKey(String key){
        return ITEM_MAP.containsKey(key);
    }

    public static void reload(){
        ITEM_MAP.clear();
        SKILL_ITEM.clear();
        register();
        loadSKILLItem();
    }
}
