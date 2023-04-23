package dev.blacky.spectator;

import dev.blacky.spectator.command.SpectatorCommand;
import dev.blacky.spectator.listener.PlayerJoinListener;
import dev.blacky.spectator.listener.PlayerQuitListener;
import dev.blacky.spectator.manager.CycleManager;
import dev.blacky.spectator.manager.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spectator extends JavaPlugin {
    private PlayerManager playerManager;
    private CycleManager cycleManager;

    @Override
    public void onEnable() {
        this.playerManager = new PlayerManager(this);
        this.cycleManager = new CycleManager(this);

        getServer().getCommandMap().register("spectator", new SpectatorCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public CycleManager getCycleManager() {
        return cycleManager;
    }
}
