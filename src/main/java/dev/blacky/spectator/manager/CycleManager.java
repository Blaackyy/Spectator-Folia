package dev.blacky.spectator.manager;

import dev.blacky.spectator.Spectator;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;

import java.util.*;

public final class CycleManager {
    private final Spectator spectator;
    private ScheduledTask globalCycle;
    private final Set<UUID> playersOnCycle;
    private final List<UUID> shuffledPlayers;
    private final Set<UUID> spectatedPlayers;

    public CycleManager(Spectator spectator) {
        this.spectator = spectator;
        this.playersOnCycle = new HashSet<>();
        this.shuffledPlayers = new LinkedList<>();
        this.spectatedPlayers = new HashSet<>();
    }

    public void initGlobalCycle() {
        //TODO make the scheduler repeat time configurable
        this.globalCycle = Bukkit.getGlobalRegionScheduler().runAtFixedRate(spectator, (task) -> {
            if (playersOnCycle.isEmpty()) return;
            if (spectator.getPlayerManager().getOnlinePlayers().isEmpty()) return;
            shufflePlayers();
            UUID selected = pickPlayer();




        }, 1, 20);
    }

    private void shufflePlayers() {
        Set<UUID> onlinePlayers = spectator.getPlayerManager().getOnlinePlayers(); //Excluded are not here
        shuffledPlayers.addAll(onlinePlayers);
        shuffledPlayers.removeAll(spectatedPlayers);

        if (shuffledPlayers.isEmpty()) {
            spectatedPlayers.clear();
            shufflePlayers();
        }

        Collections.shuffle(shuffledPlayers);
    }

    private UUID pickPlayer() {
        UUID selectedPlayer = shuffledPlayers.get(0);
        spectatedPlayers.add(selectedPlayer);
        return selectedPlayer;
    }

    public void addToCycle(UUID uuid) {
        playersOnCycle.add(uuid);
    }

    public void removeFromCycle(UUID uuid) {
        playersOnCycle.remove(uuid);
    }
}
