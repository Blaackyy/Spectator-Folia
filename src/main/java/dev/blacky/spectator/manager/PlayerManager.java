package dev.blacky.spectator.manager;

import dev.blacky.spectator.Spectator;
import dev.blacky.spectator.setting.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerManager {
    private final Spectator spectator;
    private final Set<UUID> hiddenPlayers;
    private final Set<UUID> onlinePlayers;
    private final Set<UUID> excludedPlayers;

    public PlayerManager(Spectator spectator) {
        this.spectator = spectator;
        this.hiddenPlayers = new HashSet<>();
        this.onlinePlayers = new HashSet<>();
        this.excludedPlayers = ConcurrentHashMap.newKeySet();
    }

    public void addOnlinePlayer(UUID uuid) {
        onlinePlayers.add(uuid);
    }

    public void removeOnlinePlayer(UUID uuid) {
        onlinePlayers.remove(uuid);
    }

    public void addExcludedPlayer(UUID uuid) {
        excludedPlayers.add(uuid);
    }

    public void exclude(UUID uuid) {
        Config.add(uuid);
        excludedPlayers.add(uuid);
        onlinePlayers.remove(uuid); // remove the player from the online list
    }

    public boolean isExcluded(UUID uuid) {
        return excludedPlayers.contains(uuid);
    }

    public void removeExcludedPlayer(UUID uuid) {
        excludedPlayers.remove(uuid);
        onlinePlayers.add(uuid); // add the player to the online list
        Config.remove(uuid);
    }

    public void hideFromTab(Player player, boolean hide) {
        if (hide) {
            this.hiddenPlayers.add(player.getUniqueId());
            player.setMetadata("vanished", new FixedMetadataValue(spectator, true));
        } else {
            this.hiddenPlayers.remove(player.getUniqueId());
            player.removeMetadata("vanished", spectator);
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getUniqueId().equals(player.getUniqueId())) continue;
            if (hide) {
                target.hidePlayer(spectator, player);
            } else {
                target.showPlayer(spectator, player);
            }
        }
    }

    public void hideSpectatorsTo(Player player) {
        if (hiddenPlayers.isEmpty()) return;
        for (UUID uuid : hiddenPlayers) {
            Player target = Bukkit.getPlayer(uuid);
            if (target == null) continue;
            player.hidePlayer(spectator, target);
        }
    }

    public Set<UUID> getOnlinePlayers() {
        return onlinePlayers;
    }

    public Set<UUID> getExcludedPlayers() {
        return excludedPlayers;
    }
}
