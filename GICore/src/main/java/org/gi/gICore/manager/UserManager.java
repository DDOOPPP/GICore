package org.gi.gICore.manager;

import javafx.scene.control.Tab;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.OfflinePlayer;
import org.gi.gICore.GICore;
import org.gi.gICore.model.user.Userdata;
import org.gi.gICore.repository.user.UserRepository;
import org.gi.gICore.repository.user.WalletRepository;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.Result;
import org.gi.gICore.util.TaskUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class UserManager {
    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private ModuleLogger logger;

    public UserManager() {
        this.userRepository = new UserRepository();
        this.walletRepository = new WalletRepository();
        this.logger = new ModuleLogger(GICore.getInstance(),"User");

    }

    public Result create(OfflinePlayer player) {
        Userdata userdata = createUserdata(player);

        CompletableFuture<Result> db = TaskUtil.supplyAsync(() -> {
            
        })
    }

    private Userdata createUserdata(OfflinePlayer player) {
        PlayerData playerData = PlayerData.get(player);

        String profess = playerData.getProfess().getName();
        int level = playerData.getLevel();
        int farm_level = 1;
        int fish_level = 1;
        int mine_level = 1;
        boolean tutorial = false;
        Userdata userdata = new Userdata(
                player.getUniqueId(),
                player.getName(),
                "NONE",
                profess,
                level,
                farm_level,
                fish_level,
                mine_level,
                tutorial
                );

        return userdata;
    }
}
