package dev.blacky.spectator.listener;

import dev.blacky.spectator.Spectator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PlayerQuitListener implements Listener {
    private final Spectator spectator;

    public PlayerQuitListener(Spectator spectator) {
        this.spectator = spectator;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (spectator.getPlayerManager().getExcludedPlayers().contains(playerId)) return;
        spectator.getPlayerManager().removeOnlinePlayer(playerId);
    }
}
