package org.gi.gICore.builder;

import com.sun.jdi.request.StepRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.gi.gICore.GICore;
import org.gi.gICore.manager.ComponentManager;
import org.gi.gICore.util.ModuleLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.CheckedInputStream;

public class ComponentBuilder {
    private ComponentManager componentManager;
    private ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"ComponentBuilder");
    private MiniMessage miniMessage = MiniMessage.miniMessage();

    public ComponentBuilder(){
        this.componentManager = ComponentManager.getInstance();
    }

    public Component translate(String key){
        if (!componentManager.hasKey(key)){
            logger.error("Key not found : %s",key);

            return Component.translatable(key);
        }

        return Component.translatable(key);
    }

    public Component translate(String key, Object... args){
        if (!componentManager.hasKey(key)){
            logger.error("Key not found : %s",key);

            return Component.text(key);
        }

        Component[] components = new Component[args.length];

        for (int i = 0; i < args.length; i++){
            if (args[i] instanceof Component){
                components[i] = (Component) args[i];
            } else {
                components[i] = Component.text(args[i].toString());
            }
        }

        return Component.translatable(key,components);
    }

    public Component translateNamed(String key, Map<String, Object> placeholders){
        if (!componentManager.hasKey(key)){
            logger.error("Key not found : %s",key);
            return Component.text(key);
        }

        String template = componentManager.getText(key);
        String replaced = replacePlaceholders(template, placeholders);

        return miniMessage.deserialize(replaced);
    }

    private String replacePlaceholders(String template, Map<String, Object> placeholders){
        String result = template;
        for (Map.Entry<String, Object> entry : placeholders.entrySet()){
            String placeholder = "{" + entry.getKey() + "}";
            String value;
            if (entry.getValue() instanceof Component){
                value = miniMessage.serialize((Component) entry.getValue());
            } else {
                value = entry.getValue().toString();
            }
            result = result.replace(placeholder, value);
        }
        return result;
    }

    public StyleBuilder style(String key){
        return new StyleBuilder(this,key);
    }

    public static class StyleBuilder{
        private final String key;
        private final ComponentBuilder parent;
        private final List<Object> args = new ArrayList<>();
        private TextColor color;
        private boolean bold;
        private boolean italic;
        private boolean underLine;
        private boolean strikeThrough;
        private boolean obfuscated;

        private StyleBuilder(ComponentBuilder parent, String key){
            this.parent = parent;
            this.key = key;
        }

        public StyleBuilder with(Object arg){
            args.add(arg);
            return this;
        }

        public StyleBuilder color(TextColor color){
            this.color = color;
            return this;
        }


        public StyleBuilder gold(){
            return color(NamedTextColor.GOLD);
        }

        public StyleBuilder darkGray(){
            return color(NamedTextColor.DARK_GRAY);
        }

        public StyleBuilder yellow(){
            return color(NamedTextColor.YELLOW);
        }

        public StyleBuilder blue(){
            return color(NamedTextColor.BLUE);
        }

        public StyleBuilder aqua(){
            return color(NamedTextColor.AQUA);
        }

        public StyleBuilder purple(){
            return color(NamedTextColor.LIGHT_PURPLE);
        }

        public StyleBuilder green(){
            return color(NamedTextColor.GREEN);
        }

        public StyleBuilder red(){
            return color(NamedTextColor.RED);
        }

        public StyleBuilder white(){
            return color(NamedTextColor.WHITE);
        }

        public StyleBuilder gray(){
            return color(NamedTextColor.GRAY);
        }

        public StyleBuilder darkGreen(){
            return color(NamedTextColor.DARK_GREEN);
        }

        public StyleBuilder darkRed(){
            return color(NamedTextColor.DARK_RED);
        }

        public StyleBuilder darkAqua(){
            return color(NamedTextColor.DARK_AQUA);
        }

        public StyleBuilder darkBlue(){
            return color(NamedTextColor.DARK_BLUE);
        }

        public StyleBuilder darkPurple(){
            return color(NamedTextColor.DARK_PURPLE);
        }

        public StyleBuilder strikeThrough(){
            this.strikeThrough = true;
            return this;
        }

        public StyleBuilder underLine(){
            this.underLine = true;
            return this;
        }

        public StyleBuilder bold(){
            this.bold = true;
            return this;
        }

        public StyleBuilder italic(){
            this.italic = true;
            return this;
        }

        public StyleBuilder obfuscated(){
            this.obfuscated = true;
            return this;
        }

        public Component build(){
            Component component;
            if (args.isEmpty()){
                component = parent.translate(key);
            }

            component = parent.translate(key,args.toArray());

            if (color != null){
                component = component.color(color);
            }

            if (bold){
                component = component.decorate(TextDecoration.BOLD);
            }
            if (italic){
                component = component.decorate(TextDecoration.ITALIC);
            }
            if (underLine){
                component = component.decorate(TextDecoration.UNDERLINED);
            }
            if (strikeThrough){
                component = component.decorate(TextDecoration.STRIKETHROUGH);
            }
            if (obfuscated){
                component = component.decorate(TextDecoration.OBFUSCATED);
            }
            return component;
        }
    }
}
