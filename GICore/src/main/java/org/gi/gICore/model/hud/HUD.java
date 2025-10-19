package org.gi.gICore.model.hud;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.gi.gICore.util.TaskUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public class HUD {
    private boolean running = false;

    public void start(){
        if (running) return;
        running = true;

        TaskUtil.runSyncTimer(this::tick, 0L, 3);
    }

    private void tick(){
        for(Player player : Bukkit.getOnlinePlayers()){
            if (!player.isOnline()) continue;

            CompletableFuture<HUDData> future = TaskUtil.supplyAsync(() -> collect(player));

            TaskUtil.thenSync(future, data -> {
                Component hud = Component.text(data.getHp());

                player.sendActionBar(hud);
            });
        }
    }

    private HUDData collect(Player player){
        return new HUDData(player);
    }

    public static class HUDData {
        @Getter
        Double hp;

        public HUDData(Player player){
            this.hp = player.getHealth();
        }
    }
}
