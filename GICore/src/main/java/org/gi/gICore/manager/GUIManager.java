package org.gi.gICore.manager;

import org.gi.gICore.model.gui.MainMenu;
import org.gi.gICore.model.gui.SkillGUI;
import org.gi.gICore.model.gui.StatusGUI;


public class GUIManager {
    private static ConfigManager configManager = new ConfigManager();

    public static MainMenu getMainMenu(){
        return new MainMenu(configManager.getGUIConfig("mainmenu"));
    }

    public static StatusGUI getStatusGUI(){
        return new StatusGUI(configManager.getGUIConfig("status"));
    }

    public static SkillGUI getSkillGUI(){ return new SkillGUI(configManager.getGUIConfig("skill")); }
}
