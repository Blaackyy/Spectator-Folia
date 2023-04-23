package dev.blacky.spectator.manager;

import dev.blacky.spectator.Spectator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerManager {
    private final Spectator spectator;
    private final Set<UUID> onlinePlayers;
    private final Set<UUID> excludedPlayers;

    public PlayerManager(Spectator spectator) {
        this.spectator = spectator;
        this.onlinePlayers = new HashSet<>();
        this.excludedPlayers = new HashSet<>();
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

    public void removeExcludedPlayer(UUID uuid) {
        excludedPlayers.remove(uuid);
    }

    public Set<UUID> getOnlinePlayers() {
        return onlinePlayers;
    }

    public Set<UUID> getExcludedPlayers() {
        return excludedPlayers;
    }
}
