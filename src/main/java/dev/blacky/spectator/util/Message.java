package dev.blacky.spectator.util;

import dev.blacky.spectator.setting.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class Message {
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', Config.PREFIX + message);
    }

    public static void toPlayer(Player player, String message) {
        player.sendMessage(colorize(message));
    }

    public static void toConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(colorize(message));
    }
}
