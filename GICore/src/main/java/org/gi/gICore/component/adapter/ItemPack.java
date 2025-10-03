package org.gi.gICore.component.adapter;

import io.lumine.mythic.bukkit.utils.lib.jooq.BindingGetSQLInputContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.GICore;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ItemPack {
    private static GICore plugin;

    public static void initialize(){
        plugin = GICore.getInstance();


    }

//    public List<File> getFiles(){
//        File file = new File(plugin.getDataFolder(),"item");
//
//        if (!file.exists()){
//            file.mkdirs();
//
//
//        }
//    }


}
