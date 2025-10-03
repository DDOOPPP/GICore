package org.gi.gICore.component;

import org.bukkit.inventory.ItemStack;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.manager.ComponentManager;

public interface LangModule {
    String getModuleName();
    void Enable(ComponentBuilder builder);
    void Disable();
    ItemStack createItem();
}
