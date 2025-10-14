package org.gi.gICore.value;

import java.util.Map;

public class Color {
    public static final Map<String, String> LEGACY_TO_MM = Map.ofEntries(
            Map.entry("§0", "<black>"),
            Map.entry("§1", "<dark_blue>"),
            Map.entry("§2", "<dark_green>"),
            Map.entry("§3", "<dark_aqua>"),
            Map.entry("§4", "<dark_red>"),
            Map.entry("§5", "<dark_purple>"),
            Map.entry("§6", "<gold>"),
            Map.entry("§7", "<gray>"),
            Map.entry("§8", "<dark_gray>"),
            Map.entry("§9", "<blue>"),
            Map.entry("§a", "<green>"),
            Map.entry("§b", "<aqua>"),
            Map.entry("§c", "<red>"),
            Map.entry("§d", "<light_purple>"),
            Map.entry("§e", "<yellow>"),
            Map.entry("§f", "<white>"),
            Map.entry("§l", "<bold>"),
            Map.entry("§o", "<italic>"),
            Map.entry("§n", "<underlined>"),
            Map.entry("§m", "<strikethrough>"),
            Map.entry("§r", "<reset>")
    );

    public static String convertLegacyToMiniMessage(String text) {
        if (text == null) return null;
        for (Map.Entry<String, String> e : LEGACY_TO_MM.entrySet()) {
            text = text.replace(e.getKey(), e.getValue());
        }
        return text;
    }
}
