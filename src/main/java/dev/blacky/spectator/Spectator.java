package dev.blacky.spectator;

import dev.blacky.spectator.command.SpectatorCommand;
import dev.blacky.spectator.listener.OtherPlayerListeners;
import dev.blacky.spectator.listener.PlayerJoinListener;
import dev.blacky.spectator.listener.PlayerQuitListener;
import dev.blacky.spectator.manager.CycleManager;
import dev.blacky.spectator.manager.PlayerManager;
import dev.blacky.spectator.setting.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class Spectator extends JavaPlugin {
    private PlayerManager playerManager;
    private CycleManager cycleManager;

    @Override
    public void onEnable() {
        new Config(this);

        this.playerManager = new PlayerManager(this);
        this.cycleManager = new CycleManager(this);

        Config.EXCLUDED_PLAYERS.forEach(stringUuid -> {
            playerManager.addExcludedPlayer(UUID.fromString(stringUuid));
        });

        getServer().getCommandMap().register("spectator", new SpectatorCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new OtherPlayerListeners(this), this);

        cycleManager.initGlobalCycle();
        cycleManager.initWorldChangeCycle();
    }

    @Override
    public void onDisable() {
        cycleManager.stopCycle();
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public CycleManager getCycleManager() {
        return cycleManager;
    }
}
