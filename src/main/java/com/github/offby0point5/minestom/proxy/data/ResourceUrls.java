package com.github.offby0point5.minestom.proxy.data;

import com.github.offby0point5.minestom.config.Config;

public class ResourceUrls {
    public static final String SERVERS = "/servers";  // get all registered server IDs
    public static final String MENU_MAIN = "/menu";  // get main page server menu entries
    public static final String MENU_GROUP = "/menu/:id";  // get group page server menu entries for group with this ID
    public static final String FLAGS = "/server/:id/flags";  // set flags for server with this ID
    public static final String GROUPS = "/server/:id/groups";  // set groups for server with this ID
    public static final String PORTS = "/server/:id/ports";  // set ports for server with this ID
    public static final String DELETE = "/server/:id";  // unregister server with this ID

    public static final String SEND_PLAYER_TO_GROUP = "/send/group/:player/:id";  // send player to group with this ID
    public static final String SEND_PLAYER_TO_SERVER = "/send/server/:player/:id";  // send player to server with this ID

    public static final String UNIREST_SERVERS = SERVERS;
    public static final String UNIREST_MENU_MAIN = MENU_MAIN;
    public static final String UNIREST_MENU_GROUP = MENU_GROUP.replace(":id", "{id}");
    public static final String UNIREST_FLAGS = FLAGS.replace(":id", "{id}");
    public static final String UNIREST_GROUPS = GROUPS.replace(":id", "{id}");
    public static final String UNIREST_PORTS = PORTS.replace(":id", "{id}");
    public static final String UNIREST_DELETE = DELETE.replace(":id", "{id}");

    public static final String UNIREST_SEND_PLAYER_TO_GROUP = SEND_PLAYER_TO_GROUP
            .replace(":id", "{id}").replace(":player", "{player}");
    public static final String UNIREST_SEND_PLAYER_TO_SERVER = SEND_PLAYER_TO_SERVER
            .replace(":id", "{id}").replace(":player", "{player}");
}
