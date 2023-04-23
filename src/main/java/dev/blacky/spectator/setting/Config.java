package dev.blacky.spectator.setting;

import dev.blacky.spectator.Spectator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Config {
    private static Spectator spectator;
    public static String PREFIX = "§8[§6Spectator§8] §7";
    public static Integer CYCLE_TIME = 10;
    public static List<String> EXCLUDED_PLAYERS = new ArrayList<>();

    public Config(Spectator plugin) {
        spectator = plugin;
        this.load();
    }

    public void load() {
        spectator.saveDefaultConfig();
        PREFIX = spectator.getConfig().getString("prefix");
        CYCLE_TIME = spectator.getConfig().getInt("cycle-time");
        EXCLUDED_PLAYERS = spectator.getConfig().getStringList("excluded-players");
    }

    public static void exclude(UUID playerId) {
        EXCLUDED_PLAYERS.add(playerId.toString());
        spectator.getConfig().set("excluded-players", EXCLUDED_PLAYERS);
        spectator.saveConfig();
    }

    public static void reAdd(UUID uuid) {
        EXCLUDED_PLAYERS.add(uuid.toString());
        spectator.getConfig().set("excluded-players", EXCLUDED_PLAYERS);
        spectator.saveConfig();
    }
}
