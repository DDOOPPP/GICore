package org.gi.gICore.model.item;

import org.bukkit.inventory.ItemStack;
import org.gi.gICore.GICore;
import org.gi.gICore.builder.ComponentBuilder;
import org.gi.gICore.component.LangModule;
import org.gi.gICore.util.ModuleLogger;

public class MoneyItem implements LangModule{
    private ComponentBuilder builder;
    private ModuleLogger logger;
    @Override
    public String getModuleName() {
        return "MoneyItem";
    }

    @Override
    public void Enable(ComponentBuilder builder) {
        this.logger = new ModuleLogger(GICore.getInstance(),"MoneyItem");
        this.builder = builder;

        logger.info("MoneyItem Enabled");
    }

    @Override
    public void Disable() {
        this.builder = null;
        logger.info("MoneyItem Disabled");
    }

    @Override
    public ItemStack createItem() {

        return null;
    }
}
