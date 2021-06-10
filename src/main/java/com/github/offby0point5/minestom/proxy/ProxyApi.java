package com.github.offby0point5.minestom.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import com.github.offby0point5.minestom.config.Config;
import com.github.offby0point5.minestom.proxy.data.*;
import unirest.ObjectMapper;
import unirest.Unirest;

import java.util.Set;
import java.util.stream.Collectors;

public class ProxyApi {
    static {
        Unirest.config().addDefaultHeader("Content-Type", "application/json");
        Unirest.config().setObjectMapper(new ObjectMapper() {
            final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper()
                    .registerModule(new ParanamerModule());

            public String writeValue(Object value) {
                try {
                    return mapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return mapper.readValue(value, valueType);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    public static void shutdown() {
        Unirest.shutDown();
    }

    // GET requests ================================================
    public static Set<String> getServers() {
        return Unirest.get(Config.getProxyHttpBaseUrl() +ResourceUrls.UNIREST_SERVERS).asJson().getBody().getArray().toList()
                .stream().map(element -> (String) element).collect(Collectors.toSet());
    }

    public static MenuData getMenuMain() {
        return Unirest.get(Config.getProxyHttpBaseUrl()+ResourceUrls.UNIREST_MENU_MAIN)
                .asObject(MenuData.class).getBody();
    }

    public static MenuData getMenuGroup(String groupName) {
        return Unirest.get(Config.getProxyHttpBaseUrl()+ResourceUrls.UNIREST_MENU_GROUP)
                .routeParam("id", groupName)
                .asObject(MenuData.class).getBody();
    }

    // POST requests ================================================
    public static void postSendPlayerServer(String serverName, String playerName) {
        Unirest.post(Config.getProxyHttpBaseUrl()+ResourceUrls.UNIREST_SEND_PLAYER_TO_SERVER)
                .routeParam("player", playerName)
                .routeParam("id", serverName).asEmpty();
    }

    public static void postSendPlayerGroup(String groupName, String playerName) {
        Unirest.post(Config.getProxyHttpBaseUrl()+ResourceUrls.UNIREST_SEND_PLAYER_TO_GROUP)
                .routeParam("player", playerName)
                .routeParam("id", groupName).asEmpty();
    }

    // PUT requests ================================================

    /**
     * Requests initial registration on the proxy. Should only be sent once.
     * @param serverName The name this server should have on the proxy
     * @param ports      The ServerPorts object describing all ports this server provides
     */
    public static void putServerPorts(String serverName, ServerPorts ports) {
        Unirest.put(Config.getProxyHttpBaseUrl()+ResourceUrls.UNIREST_PORTS)
                .routeParam("id", serverName)
                .body(ports).asEmpty();
    }

    public static void putServerGroups(String serverName, ServerGroups groups) {
        Unirest.put(Config.getProxyHttpBaseUrl()+ResourceUrls.UNIREST_GROUPS)
                .routeParam("id", serverName)
                .body(groups).asEmpty();
    }

    public static void putServerFlags(String serverName, ServerFlags flags) {
        Unirest.put(Config.getProxyHttpBaseUrl()+ResourceUrls.UNIREST_FLAGS)
                .routeParam("id", serverName)
                .body(flags).asEmpty();
    }

    // DELETE requests ================================================
    public static void deleteServer(String serverName) {
        Unirest.delete(Config.getProxyHttpBaseUrl()+ResourceUrls.UNIREST_DELETE)
                .routeParam("id", serverName).asEmpty();
    }
}
