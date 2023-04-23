package dev.blacky.spectator.manager;

import dev.blacky.spectator.Spectator;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;

import java.util.*;

public final class CycleManager {
    private final Spectator spectator;
    private ScheduledTask globalCycle;
    private final Set<UUID> playersOnCycle;
    private final Queue<UUID> playersQueue;

    public CycleManager(Spectator spectator) {
        this.spectator = spectator;
        this.playersOnCycle = new HashSet<>();
        this.playersQueue = new ArrayDeque<>();
    }

    public void initGlobalCycle() {
        //TODO make the scheduler repeat time configurable
        this.globalCycle = Bukkit.getGlobalRegionScheduler().runAtFixedRate(spectator, (task) -> {
            if (playersOnCycle.isEmpty()) return;

        }, 1, 20);

    }

    public void addToCycle(UUID uuid) {
        playersOnCycle.add(uuid);
    }

    public void removeFromCycle(UUID uuid) {
        playersOnCycle.remove(uuid);
    }
}
