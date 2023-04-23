package dev.blacky.spectator.command.subcommand;

import dev.blacky.spectator.Spectator;
import dev.blacky.spectator.util.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StopSubCommand extends SubCommand {
    public StopSubCommand(Spectator spectator) {
        super(spectator);
        this.setPermission("spectator.command.stop");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (!spectator.getCycleManager().isSpectating(player.getUniqueId())) {
            Message.toPlayer(player, "&cYou are not spectating!");
            return;
        }

        Message.toPlayer(player, "&aYou are no longer spectating!");
        spectator.getCycleManager().removeFromCycle(player.getUniqueId());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
