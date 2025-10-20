package org.gi.gICore.events;

import com.alessiodp.parties.api.events.PartiesEvent;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPostCreateEvent;
import com.alessiodp.parties.api.interfaces.Party;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gi.gICore.GICore;
import org.gi.gICore.util.ModuleLogger;

public class PartiesEvnets implements Listener {
    private ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"PartiesEvnets");

    @EventHandler
    public void onCreateEvent(BukkitPartiesPartyPostCreateEvent event) {
        Party party = event.getParty();
        logger.info("Party Create %s".formatted(party.getName()));
    }
}
