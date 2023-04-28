package dev.blacky.spectator.command.subcommand;

import dev.blacky.spectator.Spectator;
import dev.blacky.spectator.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ExcludeSubCommand extends SubCommand {
    public ExcludeSubCommand(Spectator spectator) {
        super(spectator);
        this.setPermission("spectator.command.exclude");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player playerSender = (Player) sender;
        Player player = playerSender;

        if (args.length != 0 && playerSender.hasPermission("spectator.command.exclude.others")) {
            Player other = Bukkit.getPlayer(args[0]);
            if (other != null && other.isOnline()) player = other;
        }

        if (spectator.getPlayerManager().isExcluded(player.getUniqueId())) {
            Message.toPlayer(playerSender, "&cYou are no longer excluded from the cycle.");
            spectator.getPlayerManager().removeExcludedPlayer(player.getUniqueId());
            return;
        }

        Message.toPlayer(playerSender, "&aYou are now excluded from the cycle.");
        spectator.getPlayerManager().exclude(player.getUniqueId());

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 0 && sender.hasPermission("spectator.command.exclude.others"))
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();

        return List.of();
    }
}
