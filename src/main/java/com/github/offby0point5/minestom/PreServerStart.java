package com.github.offby0point5.minestom;

import com.github.offby0point5.minestom.config.Config;
import com.github.offby0point5.minestom.server.lobby.Server;

public class PreServerStart {
    /**
     * Program entry point. Executes before starting the server.
     * @param args the arguments given by the command line
     */
    public static void main(String[] args) {
        // todo read command line arguments

        Config.load("lobby.toml");

        // Start the server
        Server.start();
    }
}
