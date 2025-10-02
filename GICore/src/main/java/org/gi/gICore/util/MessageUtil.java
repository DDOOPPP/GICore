package org.gi.gICore.util;

import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageUtil {
    public static List<Component> list (String message) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable(message).decorate(TextDecoration.ITALIC));
        return list;
    }

    public static Component message(String message){
        return Component.translatable(message).decorate(TextDecoration.ITALIC);
    }

    public static String parse(String message, OfflinePlayer player, Map<String,String> values){
        if (player != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        if (values != null) {
            message = StringUtil.replacePlaceholders(message, values);
        }
        return message;
    }
}
