package com.github.offby0point5.minestom.server.lobby;

import com.github.offby0point5.minestom.command.StopCommand;
import com.github.offby0point5.minestom.config.Config;
import com.github.offby0point5.minestom.proxy.ProxyConnector;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.time.UpdateOption;
import net.minestom.server.world.Difficulty;

import java.net.InetSocketAddress;

public class Server {
    private static InstanceContainer defaultInstance;

    /**
     * Actually starts the Minestom server
     */
    public static void start() {
        if (Config.isVelocityEnabled())
            VelocityProxy.enable(Config.getVelocitySecret());
        else if (Config.isBungeeEnabled())
            BungeeCordProxy.enable();

        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.setBrandName("lobby");
        MinecraftServer.setChunkViewDistance(7);
        MinecraftServer.setEntityViewDistance(7);
        MinecraftServer.setCompressionThreshold(256);
        MinecraftServer.setDifficulty(Difficulty.PEACEFUL);
        MinecraftServer.getExceptionManager().setExceptionHandler(
                (exception) -> MinecraftServer.LOGGER.warn("", exception));

        registerCommands();
        registerEvents();
        registerSchedulers();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        defaultInstance = instanceManager.createInstanceContainer();
        // subject to change =================================
        defaultInstance.setChunkGenerator(new WorldGen());
        defaultInstance.enableAutoChunkLoad(true);
        defaultInstance.setTimeRate(0);
        defaultInstance.setTime(6000);
        defaultInstance.setTimeUpdate(new UpdateOption(1, TimeUnit.TICK));  // todo what is the doDaylightCycle=false ?
        // ===================================================

        server.start(Config.getServerHost(), Config.getServerPort());
        @SuppressWarnings("UnstableApiUsage") InetSocketAddress socketAddress =
                MinecraftServer.getNettyServer().getServerChannel().localAddress();
        MinecraftServer.LOGGER.info(String.format("Server listens to %s:%d.",
                socketAddress.getHostName(), socketAddress.getPort()));

        ProxyConnector.setup();  // Assures that the proxy knows about this server
    }

    public static void stop() {
        ProxyConnector.stop();
    }

    private static void registerEvents() { // todo use the new tree based events
        GlobalEventHandler manager = MinecraftServer.getGlobalEventHandler();
        manager.addEventCallback(PlayerLoginEvent.class, event -> {
            event.setSpawningInstance(defaultInstance);
            Player player = event.getPlayer();
            player.setRespawnPoint(Config.getLobbySpawn());
            player.setGameMode(GameMode.ADVENTURE);
        });

        manager.addEventCallback(PlayerDeathEvent.class, event -> {
            if (event.getPlayer() instanceof FakePlayer)
                event.setChatMessage((Component) null);
        });

        manager.addEventCallback(PlayerPreEatEvent.class, event -> event.setCancelled(true));
        manager.addEventCallback(PlayerBlockBreakEvent.class, event -> event.setCancelled(true));
        manager.addEventCallback(PlayerBlockPlaceEvent.class, event -> event.setCancelled(true));
        manager.addEventCallback(PlayerBlockInteractEvent.class, event -> event.setCancelled(true));
        manager.addEventCallback(PlayerSwapItemEvent.class, event -> event.setCancelled(true));
        manager.addEventCallback(PlayerItemAnimationEvent.class, event -> event.setCancelled(true));
        manager.addEventCallback(ItemDropEvent.class, event -> event.setCancelled(true));
        manager.addEventCallback(PlayerUseItemEvent.class, event -> event.setCancelled(true));

        // Reset players to spawn if the fall into the void
        manager.addEventCallback(EntityDamageEvent.class, event -> {
            if (event.getDamageType() == DamageType.VOID && event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                player.teleport(player.getRespawnPoint());
                event.setCancelled(true);
            }
        });
    }

    private static void registerCommands() {
        MinecraftServer.getCommandManager().register(new StopCommand());
    }

    private static void registerSchedulers() {

    }
}
