package com.github.offby0point5.minestom.server.lobby;

import com.github.offby0point5.minestom.command.StopCommand;
import com.github.offby0point5.minestom.config.Config;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
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
        defaultInstance.setTime(-6000);
        // ===================================================

        server.start(Config.getServerHost(), Config.getServerPort());
        @SuppressWarnings("UnstableApiUsage") InetSocketAddress socketAddress =
                MinecraftServer.getNettyServer().getServerChannel().localAddress();
        MinecraftServer.LOGGER.info(String.format("Server listens to %s:%d.",
                socketAddress.getHostName(), socketAddress.getPort()));
    }

    public static void stoppedByCommand() {
        MinecraftServer.LOGGER.info("Server shutdown.");
    }

    private static void registerEvents() { // todo use the new tree based events
        EventNode<Event> manager = MinecraftServer.getGlobalEventHandler();
        EventNode<PlayerEvent> playerEventNode = EventNode.type("player", EventFilter.PLAYER);
        EventNode<ItemEvent> itemEventNode = EventNode.type("item", EventFilter.ITEM);
        EventNode<EntityEvent> entityEventNode = EventNode.type("entity", EventFilter.ENTITY);
        manager.addChild(playerEventNode);
        manager.addChild(itemEventNode);
        manager.addChild(entityEventNode);

        playerEventNode.addListener(PlayerLoginEvent.class, event -> {
            event.setSpawningInstance(defaultInstance);
            Player player = event.getPlayer();
            player.setRespawnPoint(Config.getLobbySpawn());
            player.setGameMode(GameMode.ADVENTURE);
        });

        playerEventNode.addListener(PlayerDeathEvent.class, event -> {
            if (event.getPlayer() instanceof FakePlayer)
                event.setChatMessage((Component) null);
        });

        playerEventNode.addListener(PlayerPreEatEvent.class, event -> event.setCancelled(true));
        playerEventNode.addListener(PlayerBlockBreakEvent.class, event -> event.setCancelled(true));
        playerEventNode.addListener(PlayerBlockPlaceEvent.class, event -> event.setCancelled(true));
        playerEventNode.addListener(PlayerBlockInteractEvent.class, event -> event.setCancelled(true));
        playerEventNode.addListener(PlayerSwapItemEvent.class, event -> event.setCancelled(true));
        playerEventNode.addListener(PlayerItemAnimationEvent.class, event -> event.setCancelled(true));
        itemEventNode.addListener(ItemDropEvent.class, event -> event.setCancelled(true));
        playerEventNode.addListener(PlayerUseItemEvent.class, event -> event.setCancelled(true));

        // Reset players to spawn if they fall into the void
        entityEventNode.addListener(EntityDamageEvent.class, event -> {
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
