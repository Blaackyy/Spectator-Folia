package dev.blacky.spectator.command.subcommand;

import dev.blacky.spectator.Spectator;
import dev.blacky.spectator.util.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StartSubCommand extends SubCommand {
    public StartSubCommand(Spectator spectator) {
        super(spectator);
        this.setPermission("spectator.command.start");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (spectator.getCycleManager().isSpectating(player.getUniqueId())) {
            Message.toPlayer(player, "&cYou are already spectating!");
            return;
        }

        Message.toPlayer(player, "&aYou are now spectating!");
        spectator.getCycleManager().addToCycle(player.getUniqueId());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
