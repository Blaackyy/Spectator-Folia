package dev.blacky.spectator.command.subcommand;

import dev.blacky.spectator.Spectator;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {
    public final Spectator spectator;
    private String permission;

    public SubCommand(Spectator spectator, String permission) {
        this.spectator = spectator;
        this.permission = permission;
    }

    public SubCommand(Spectator spectator) {
        this.spectator = spectator;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
