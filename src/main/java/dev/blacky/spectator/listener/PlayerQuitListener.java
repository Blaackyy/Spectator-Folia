package dev.blacky.spectator.listener;

import dev.blacky.spectator.Spectator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerQuitListener implements Listener {
    private final Spectator spectator;

    public PlayerQuitListener(Spectator spectator) {
        this.spectator = spectator;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }
}
