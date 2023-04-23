package dev.blacky.spectator.manager;

import dev.blacky.spectator.Spectator;
import dev.blacky.spectator.model.PlayerData;
import dev.blacky.spectator.setting.Config;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;

import java.util.*;

public final class CycleManager {
    private final Spectator spectator;
    private ScheduledTask globalCycle;
    private ScheduledTask worldChangeCycle;
    private final Set<UUID> playersOnCycle;
    private final Map<UUID, BossBar> spectatorBossBars;
    private final Map<UUID, UUID> currentlySpectating;
    private final Map<UUID, PlayerData> spectatorData;
    private final Map<UUID, List<UUID>> spectatedPlayers;

    public CycleManager(Spectator spectator) {
        this.spectator = spectator;
        this.playersOnCycle = new HashSet<>();
        this.spectatorBossBars = new HashMap<>();
        this.currentlySpectating = new HashMap<>();
        this.spectatorData = new HashMap<>();
        this.spectatedPlayers = new HashMap<>();
    }

    public void initGlobalCycle() {
        this.globalCycle = Bukkit.getGlobalRegionScheduler().runAtFixedRate(spectator, (task) -> {
            if (playersOnCycle.isEmpty()) return;
            if (spectator.getPlayerManager().getOnlinePlayers().isEmpty()) return;

            for (UUID uuid : playersOnCycle) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) continue; //Removed on quit if player is not online

                UUID selected = shuffleAndGet(uuid); //get some player to spectate
                if (selected == null) { //if there is no player to spectate, come back to player camera
                    selfSpectate(player);
                    continue;
                }

                Player selectedPlayer = Bukkit.getPlayer(selected); //get player entity to spectate
                if (selectedPlayer == null || !selectedPlayer.isOnline() || selectedPlayer.isDead()) { //if player is not online or dead, pick another player
                    if (!fastPick(player)) selfSpectate(player);
                    continue;
                }

                currentlySpectating.put(uuid, selected); //set player to spectate
                spectate(player, selectedPlayer); //spectate player
            }

        }, 1, 20L * Config.CYCLE_TIME);
    }

    public void initWorldChangeCycle() {
        this.worldChangeCycle = Bukkit.getGlobalRegionScheduler().runAtFixedRate(spectator, (task) -> {
            if (currentlySpectating.isEmpty()) return;
            currentlySpectating.forEach((spectator, spectated) -> {
                Player spectatorPlayer = Bukkit.getPlayer(spectator);
                if (spectatorPlayer == null || !spectatorPlayer.isOnline()) return;
                Player spectatedPlayer = Bukkit.getPlayer(spectated);
                if (spectatedPlayer == null || !spectatedPlayer.isOnline()) return;

                if (!spectatorPlayer.getWorld().equals(spectatedPlayer.getWorld())) {
                    spectate(spectatorPlayer, spectatedPlayer);
                }
            });
        }, 1, 10);
    }

    public void selfSpectate(Player player) {
        if (!player.isOnline()) return;
        player.setSpectatorTarget(null);
        ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetCameraPacket(((CraftPlayer) player).getHandle()));
        displayBossbar(player, false);
        currentlySpectating.remove(player.getUniqueId());
    }

    public void spectate(Player origin, Player target) {
        origin.setSpectatorTarget(null);
        origin.teleportAsync(target.getLocation())
                .thenAccept(aBoolean -> {
                    Bukkit.getRegionScheduler().execute(spectator, origin.getLocation(), () -> {
                        ((CraftPlayer) origin).getHandle().connection.send(new ClientboundSetCameraPacket(((CraftPlayer) target).getHandle()));
                        origin.setSpectatorTarget(target);
                        displayBossbar(origin, true);
                    });
                });
    }

    private void displayBossbar(Player player, boolean status) {
        BossBar bossBar = spectatorBossBars.get(player.getUniqueId());

        if (status) {
            Player spectated = Bukkit.getPlayer(currentlySpectating.get(player.getUniqueId()));
            if (spectated == null || !spectated.isOnline()) return;
            if (bossBar == null) {
                bossBar = BossBar.bossBar(
                        Component.text("Spectating: ", NamedTextColor.GOLD).append(Component.text(spectated.getName(), NamedTextColor.YELLOW)),
                        1f,
                        BossBar.Color.BLUE,
                        BossBar.Overlay.PROGRESS
                );
                spectatorBossBars.put(player.getUniqueId(), bossBar);
            }
            bossBar.name(Component.text("Spectating: ", NamedTextColor.GOLD).append(Component.text(spectated.getName(), NamedTextColor.YELLOW)));
            player.showBossBar(bossBar);
            return;
        }

        if (bossBar == null) {
            bossBar = BossBar.bossBar(
                    Component.text("Waiting for players...", NamedTextColor.GOLD),
                    1f,
                    BossBar.Color.BLUE,
                    BossBar.Overlay.PROGRESS
            );
            spectatorBossBars.put(player.getUniqueId(), bossBar);
        }

        bossBar.name(Component.text("Waiting for players...", NamedTextColor.GOLD));
        player.showBossBar(bossBar);
    }

    public void removeBossBar(UUID uuid) {
        BossBar bossBar = spectatorBossBars.remove(uuid);
        if (bossBar == null) return;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) return;
        player.hideBossBar(bossBar);
    }

    public boolean fastPick(Player player) {
        UUID selected = shuffleAndGet(player.getUniqueId());
        if (selected == null) return false;

        Player selectedPlayer = Bukkit.getPlayer(selected);
        if (selectedPlayer == null || !selectedPlayer.isOnline() || selectedPlayer.isDead()) return fastPick(player);

        spectate(player, selectedPlayer);
        return true;
    }

    private UUID shuffleAndGet(UUID uuid) {
        Set<UUID> onlinePlayers = spectator.getPlayerManager().getOnlinePlayers(); //Excluded are not here
        onlinePlayers.removeAll(playersOnCycle); //Remove players on cycle
        if (onlinePlayers.isEmpty()) return null; //If there is no player to spectate, return null

        List<UUID> shuffledPlayers = new ArrayList<>(onlinePlayers);
        List<UUID> spectatedPlayers = this.spectatedPlayers.getOrDefault(uuid, new ArrayList<>());

        shuffledPlayers.removeAll(spectatedPlayers); //Remove players that are already spectated

        if (shuffledPlayers.isEmpty()) { //If all players are spectated, reset the list
            spectatedPlayers.clear();
            shuffledPlayers = new ArrayList<>(onlinePlayers);
        }

        Collections.shuffle(shuffledPlayers);

        UUID selected = shuffledPlayers.get(0);
        spectatedPlayers.add(selected);
        this.spectatedPlayers.put(uuid, spectatedPlayers);
        return selected;
    }

    public void addToCycle(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            spectatorData.put(uuid, new PlayerData(player.getGameMode(), player.getLocation()));
            spectator.getPlayerManager().hideFromTab(player, true);
            player.getScheduler().execute(spectator, () -> {
                player.setGameMode(GameMode.SPECTATOR);
                playersOnCycle.add(uuid);
            }, () -> {
                spectator.getLogger().info("Could not add player " + player.getName() + " to the spectator cycle.");
            }, 1);
        }
    }

    public void removeFromCycle(UUID uuid) {
        playersOnCycle.remove(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            player.getScheduler().execute(spectator, () -> {
                player.setSpectatorTarget(null);
                player.setGameMode(spectatorData.get(uuid).getGameMode());
                player.teleportAsync(spectatorData.get(uuid).getLocation());
                ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetCameraPacket(((CraftPlayer) player).getHandle()));
                spectator.getPlayerManager().hideFromTab(player, false);
                playersOnCycle.remove(uuid);
                spectatorData.remove(uuid);
                spectatedPlayers.remove(uuid);
                currentlySpectating.remove(uuid);
                removeBossBar(uuid);
            }, () -> {
                spectatorData.remove(uuid);
                spectatedPlayers.remove(uuid);
                playersOnCycle.remove(uuid);
                currentlySpectating.remove(uuid);
                removeBossBar(uuid);
            }, 1);
    }

    public boolean isSpectating(UUID uuid) {
        return playersOnCycle.contains(uuid);
    }

    public void stopCycle() {
        if (globalCycle != null) globalCycle.cancel();
        if (worldChangeCycle != null) worldChangeCycle.cancel();
    }

    public boolean isBeingSpectated(UUID spectated) {
        return currentlySpectating.containsValue(spectated);
    }

    public UUID getSpectator(UUID spectated) {
        return currentlySpectating.entrySet().stream().filter(entry -> entry.getValue().equals(spectated)).findFirst().map(Map.Entry::getKey).orElse(null);
    }
}
