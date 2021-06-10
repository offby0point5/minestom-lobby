package com.github.offby0point5.minestom.config;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.io.File;
import java.net.URL;

public class LoadConfig {
    protected static CommentedFileConfig config;
    protected static ConfigSpec configSpec;

    static {
        configSpec = new ConfigSpec();

        // Server networking
        configSpec.defineInRange(Config.SERVER_PORT, 25570, 0, 65535);
        configSpec.defineOfClass(Config.SERVER_HOST, "0.0.0.0", String.class);

        // Proxy related
        configSpec.defineOfClass(Config.PROXY_HTTP_HOST, "localhost", String.class);
        configSpec.defineInRange(Config.PROXY_HTTP_PORT, 25564, 1000, 65535);
        configSpec.defineOfClass(Config.PROXY_BUNGEE_ENABLE, false, Boolean.class);
        configSpec.defineOfClass(Config.PROXY_VELOCITY_ENABLE, false, Boolean.class);
        configSpec.defineOfClass(Config.PROXY_VELOCITY_SECRET, "", String.class);

        configSpec.defineOfClass(Config.LOBBY_SPAWN, "0,2,0", String.class); // todo make validator for positions
    }

    /**
     * Loads and validates the config file
     * @param file the config file
     * @return number of corrected errors in config
     */
    protected static int load(File file) {
        URL defaultConfigLocation = LoadConfig.class.getClassLoader().getResource("lobby.toml");
        if (defaultConfigLocation == null) {
            throw new RuntimeException("Default configuration file does not exist.");
        }

        config = CommentedFileConfig.builder(file)
                .defaultData(defaultConfigLocation)
                .preserveInsertionOrder()
                .sync()
                .build();
        config.load();

        if (configSpec.isCorrect(config)) return -1;
        return configSpec.correct(config, (action, path, incorrectValue, correctedValue)
                -> System.out.printf("%s %s %s %s%n", action, path, incorrectValue, correctedValue));
    }
}
