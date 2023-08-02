package dev.blacky.spectator.listener;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import dev.blacky.spectator.Spectator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public final class OtherPlayerListeners implements Listener {
    private final Spectator spectator;

    public OtherPlayerListeners(Spectator spectator) {
        this.spectator = spectator;
    }

    @EventHandler
    public void onAdvancementGrant(PlayerAdvancementCriterionGrantEvent event) {
        Player player = event.getPlayer();
        if (!spectator.getCycleManager().isSpectating(player.getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void spectatorChangesGameMode(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (spectator.getCycleManager().isSpectating(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void kickCyclingPlayer(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (spectator.getCycleManager().isSpectating(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void dismountTarget(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (spectator.getCycleManager().isSpectating(player.getUniqueId())) event.setCancelled(true);
    }
}
