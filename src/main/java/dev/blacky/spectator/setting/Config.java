package dev.blacky.spectator.setting;

import dev.blacky.spectator.Spectator;

public final class Config {
    private final Spectator spectator;

    public Config(Spectator spectator) {
        this.spectator = spectator;
    }

    public void load() {
        spectator.saveDefaultConfig();
    }
}
