package dev.tr7zw.disguiseheads;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.tr7zw.disguiseheads.config.Config;
import dev.tr7zw.disguiseheads.config.ConfigScreenProvider;
import dev.tr7zw.util.ModLoaderUtil;

public class DisguiseHeadsShared {

    public static final Logger LOGGER = LogManager.getLogger("DisguiseHeads");
    public static DisguiseHeadsShared instance;
    public Config config;
    private static final File settingsFile = new File("config", "disguiseheads.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void init() {
        instance = this;
        LOGGER.info("Loading DisguiseHeads!");
        if (settingsFile.exists()) {
            try {
                config = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        Config.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                ex.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            writeConfig();
        }
        ModLoaderUtil.disableDisplayTest();
        ModLoaderUtil.registerConfigScreen(ConfigScreenProvider::createConfigScreen);
    }
    
    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.write(settingsFile.toPath(), gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


}
