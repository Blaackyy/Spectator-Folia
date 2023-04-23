package dev.blacky.spectator.command;

import dev.blacky.spectator.Spectator;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SpectatorCommand extends BukkitCommand {
    private final Spectator spectator;
    public SpectatorCommand(Spectator spectator) {
        super("Spectator", "Main Spectator Command", "/spectator help", List.of("spec"));
        this.spectator = spectator;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command!");
            return true;
        }

        Player player = (Player) sender;

        if (spectator.getCycleManager().isSpectating(player.getUniqueId())) {
            spectator.getCycleManager().removeFromCycle(player.getUniqueId());
            return true;
        }

        spectator.getCycleManager().addToCycle(player.getUniqueId());
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return List.of();
    }
}
