package org.gi.gICore.component.adapter;

import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.GICore;
import org.gi.gICore.config.ConfigCore;
import org.gi.gICore.manager.ConfigManager;
import org.gi.gICore.util.ModuleLogger;

import java.io.File;
import java.util.*;

public class MessagePack {
    public static final List<String> SUPPORTED_LANGS = List.of("en_us", "ko_kr", "ja_jp");
    public static final List<String> DEFAULT_FILES = List.of("common.yml", "economy.yml");

    // ✅ 나중에 초기화
    private static ModuleLogger logger;

    // Map<언어, Map<키, 메시지>> 구조
    private static final Map<String, Map<String, String>> messagePacks = new HashMap<>();

    public static void loadPack(JavaPlugin plugin) {
        // ✅ 로거 초기화
        if (logger == null) {
            logger = new ModuleLogger(plugin, "MessagePack");
        }

        logger.info("Loading Message Pack");
        File langDir = new File(plugin.getDataFolder(), "messages");

        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        for (String lang : SUPPORTED_LANGS) {
            logger.info("Loading Language: " + lang);
            File langFile = new File(langDir, lang);

            if (!langFile.exists()) {
                logger.info("Language directory not found, creating: " + lang);
                langFile.mkdirs();
            }

            // 언어별 맵 초기화
            messagePacks.putIfAbsent(lang, new HashMap<>());

            // 기본 파일 로드
            for (String file : DEFAULT_FILES) {
                File defaultFile = new File(langFile, file);

                if (!defaultFile.exists()) {
                    plugin.saveResource("messages/" + lang + "/" + file, false);
                }

                loadMessages(lang, file);
            }

            // ✅ 커스텀 파일 로드 (NPE 안전 처리)
            loadCustomFiles(langFile, lang);
        }

        logger.info("Message Pack Loading Complete - Total Languages: " + messagePacks.size());
    }

    // ✅ 커스텀 파일 로딩 분리
    private static void loadCustomFiles(File langDir, String lang) {
        File[] files = langDir.listFiles();

        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            // 기본 파일은 이미 로드했으므로 제외
            if (DEFAULT_FILES.contains(file.getName())) {
                continue;
            }

            // yml 파일만 처리
            if (file.isFile() && file.getName().endsWith(".yml")) {
                logger.info("Loading custom message file: " + lang + "/" + file.getName());
                loadMessages(lang, file.getName());
            }
        }
    }

    // 메시지 로딩 로직
    private static void loadMessages(String lang, String file) {
        try {
            String path = "messages/" + lang + "/" + file;
            ConfigCore config = ConfigManager.getConfig(path);

            if (config == null) {
                logger.error("Failed to load config: " + path);
                return;
            }

            Map<String, String> langPack = messagePacks.get(lang);
            int loadedCount = 0;

            for (String key : config.getKeys()) {
                if (key == null || key.isEmpty()) {
                    continue;
                }

                String value = config.getString(key);
                if (value != null) {
                    langPack.put(key, value);
                    loadedCount++;
                }
            }

            logger.info("Loaded %d messages from %s/%s".formatted(loadedCount, lang, file));

        } catch (Exception e) {
            logger.error("Error loading messages from " + lang + "/" + file, e);
        }
    }

    // ✅ 메서드명 통일: get → getMessage로 변경
    public static String getMessage(String lang, String key) {
        return messagePacks.getOrDefault(lang, Collections.emptyMap())
                .getOrDefault(key, key);
    }

    // 편의 메서드들
    public static String getKorean(String key) {
        return getMessage("ko_kr", key);
    }

    public static String getJapanese(String key) {
        return getMessage("ja_jp", key);
    }

    public static String getEnglish(String key) {
        return getMessage("en_us", key);
    }

    // 플레이어 언어에 따라 메시지 반환
    public static String getPlayerMessage(String key, String playerLang) {
        // 지원하지 않는 언어면 영어로 fallback
        if (!SUPPORTED_LANGS.contains(playerLang)) {
            playerLang = "en_us";
        }
        return getMessage(playerLang, key);
    }

    // ✅ 포매팅 지원
    public static String getFormatted(String lang, String key, Object... args) {
        String message = getMessage(lang, key);
        return String.format(message, args);
    }

    // ✅ 디버그용: 로드된 메시지 수 확인
    public static int getMessageCount(String lang) {
        return messagePacks.getOrDefault(lang, Collections.emptyMap()).size();
    }

    // ✅ 리로드 기능
    public static void reload(JavaPlugin plugin) {
        messagePacks.clear();
        loadPack(plugin);
    }
}