package org.gi.gICore.util;

import net.kyori.adventure.text.Component;

public class MessageUtil {
    public Component message(String message){
        return Component.translatable(message);
    }
}
