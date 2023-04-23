package dev.blacky.spectator.command;

import dev.blacky.spectator.Spectator;
import dev.blacky.spectator.command.subcommand.ExcludeSubCommand;
import dev.blacky.spectator.command.subcommand.StartSubCommand;
import dev.blacky.spectator.command.subcommand.StopSubCommand;
import dev.blacky.spectator.command.subcommand.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpectatorCommand extends BukkitCommand {
    private final Spectator spectator;
    private final Map<String, SubCommand> subCommandMap;

    public SpectatorCommand(Spectator plugin) {
        super("spectator", "Spectator Commands", "/spectator", List.of("spec"));
        this.spectator = plugin;
        this.subCommandMap = new HashMap<>();
        this.loadSubCommands();
    }

    private void loadSubCommands() {
        this.subCommandMap.put("exclude", new ExcludeSubCommand(spectator));
        this.subCommandMap.put("start", new StartSubCommand(spectator));
        this.subCommandMap.put("stop", new StopSubCommand(spectator));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Please specify an operation!");
            return true;
        }

        String operation = args[0];
        if (!subCommandMap.containsKey(operation)) {
            player.sendMessage(ChatColor.RED + "Unknown operation!");
            return true;
        }

        SubCommand subCommand = this.subCommandMap.get(operation);

        if (!player.hasPermission(subCommand.getPermission())) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);

        subCommand.execute(player, newArgs);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 0 || args.length == 1) {
            return getCompletion(this.subCommandMap.keySet().stream().toList(), args);
        }

        String subCommand = args[0];

        if (this.subCommandMap.containsKey(subCommand)) {
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);

            return getCompletion(this.subCommandMap.get(subCommand).onTabComplete(sender, newArgs), args);
        }

        return List.of();
    }

    private List<String> getCompletion(List<String> arguments, String[] args) {
        List<String> results = new ArrayList<>();
        for (String argument : arguments) {
            if (!argument.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) continue;
            results.add(argument);
        }
        return results;
    }
}
