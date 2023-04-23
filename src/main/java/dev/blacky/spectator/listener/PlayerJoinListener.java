package dev.blacky.spectator.listener;

import dev.blacky.spectator.Spectator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public final class PlayerJoinListener implements Listener {
    private final Spectator spectator;

    public PlayerJoinListener(Spectator spectator) {
        this.spectator = spectator;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (spectator.getPlayerManager().getExcludedPlayers().contains(playerId)) return;
        spectator.getPlayerManager().addOnlinePlayer(playerId);
        spectator.getPlayerManager().hideSpectatorsTo(event.getPlayer());
    }
}
