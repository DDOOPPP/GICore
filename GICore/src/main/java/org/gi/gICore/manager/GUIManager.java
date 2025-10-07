package org.gi.gICore.manager;

import org.gi.gICore.model.gui.MainMenu;

import java.util.Map;

public class GUIManager {
    private static ConfigManager configManager = new ConfigManager();

    public static MainMenu getMainMenu(){
        return new MainMenu(configManager.getGUIConfig("mainmenu"));
    }
}
