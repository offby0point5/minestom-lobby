package com.github.offby0point5.minestom.config;

import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.Position;

import java.io.File;

public final class Config {
    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_HOST = "server.host";
    public static final String SERVER_ANY_PORT = "server.any-port";
    public static final String PROXY_BUNGEE_ENABLE = "proxy.bungee.enable";
    public static final String PROXY_VELOCITY_ENABLE = "proxy.velocity.enable";
    public static final String PROXY_VELOCITY_SECRET = "proxy.velocity.secret";
    public static final String LOBBY_SPAWN = "lobby.spawn";

    public static void load(String fileName) { load(new File(fileName)); }
    public static void load(File file) {
        MinecraftServer.LOGGER.debug(String.format("Config corrected %d entries.%n", LoadConfig.load(file)));
    }

    public static String getServerHost() {
        return LoadConfig.config.getOrElse(SERVER_HOST, "0.0.0.0");
    }

    @SuppressWarnings("UnstableApiUsage") public static int getServerPort() {
        if (MinecraftServer.isStarted())
            return MinecraftServer.getNettyServer().getServerChannel().localAddress().getPort();
        else if (LoadConfig.config.getOrElse(SERVER_ANY_PORT, false))
            return 0;
        else
            return LoadConfig.config.getIntOrElse(SERVER_PORT, 25570);
    }

    public static boolean isBungeeEnabled() {
        return LoadConfig.config.getOrElse(PROXY_BUNGEE_ENABLE, false);
    }

    public static boolean isVelocityEnabled() {
        return LoadConfig.config.getOrElse(PROXY_VELOCITY_ENABLE, false);
    }

    public static String getVelocitySecret() {
        return LoadConfig.config.get(PROXY_VELOCITY_SECRET);
    }

    public static Position getLobbySpawn() {
        String locString = LoadConfig.config.getOrElse(LOBBY_SPAWN, "0,256,0");
        locString = locString.replace(" ", "");
        String[] coords = locString.split(",");
        switch (coords.length) {
            case 3:
                return new Position(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
            case 5:
                return new Position(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]),
                        Float.parseFloat(coords[3]), Float.parseFloat(coords[4]));
            default:
                throw new IllegalArgumentException(String.format(
                        "Configuration value %s needs 3 or 5 values separated by \",\"", LOBBY_SPAWN));
        }
    }
}
