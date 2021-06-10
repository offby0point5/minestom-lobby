package com.github.offby0point5.minestom.proxy;

import com.github.offby0point5.minestom.config.Config;
import com.github.offby0point5.minestom.proxy.data.ServerGroups;
import com.github.offby0point5.minestom.proxy.data.ServerPorts;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.ServerListPingType;
import net.minestom.server.timer.Task;
import net.minestom.server.utils.time.TimeUnit;
import org.apache.http.conn.HttpHostConnectException;
import unirest.UnirestException;

import java.util.Collections;

public class ProxyConnector {
    private static Task task;
    private static long nextPing = 0;
    private static final long pingDistance = 1000*10;

    public static void setup() {
        // Pings the proxy
        task = MinecraftServer.getSchedulerManager().buildTask(() -> {
            if (nextPing > System.currentTimeMillis()) return;
            try {
                ProxyApi.putServerPorts(Config.getServerName(),
                        new ServerPorts(Config.getServerPort(), null, null));
                ProxyApi.putServerGroups(Config.getServerName(),
                        new ServerGroups("lobby", Collections.emptySet()));  // todo add other groups by config
            } catch (UnirestException e) {
                if (!(e.getCause() instanceof HttpHostConnectException))
                    MinecraftServer.LOGGER.warn("", e);
            }
        }).delay(3L, TimeUnit.SECOND)
                .repeat(1L, TimeUnit.SECOND).schedule();

        MinecraftServer.getGlobalEventHandler().addEventCallback(ServerListPingEvent.class, event -> {
            if (event.getPingType().equals(ServerListPingType.OPEN_TO_LAN)) return;
            nextPing = System.currentTimeMillis()+pingDistance;
        });
    }

    public static void stop() {
        ProxyApi.deleteServer(Config.getServerName());
        task.cancel();
    }
}
