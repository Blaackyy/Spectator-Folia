package dev.blacky.spectator.model;

import org.bukkit.GameMode;
import org.bukkit.Location;

public final class PlayerData {
    private GameMode gameMode;
    private Location location;

    public PlayerData(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public PlayerData(GameMode gameMode, Location location) {
        this.gameMode = gameMode;
        this.location = location;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
