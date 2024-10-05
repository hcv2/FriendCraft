package com.friendcraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.friendcraft.FriendCraftMod;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages multi-AI role configuration for FriendCraft mod.
 * Configuration is stored in config/friendcraft.json and supports manual editing by players.
 */
public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "friendcraft.json";
    private static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get();
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve(CONFIG_FILE_NAME);
    
    private static ConfigManager instance;
    private final Gson gson;
    private Map<String, BotConfig> botConfigs;
    
    /**
     * Configuration for a single AI bot.
     */
    public static class BotConfig {
        private String name;
        private String memoryPath;
        private String personality;
        
        public BotConfig() {
            // Default constructor for Gson
        }
        
        public BotConfig(String name, String memoryPath, String personality) {
            this.name = name;
            this.memoryPath = memoryPath;
            this.personality = personality;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getMemoryPath() {
            return memoryPath;
        }
        
        public void setMemoryPath(String memoryPath) {
            this.memoryPath = memoryPath;
        }
        
        public String getPersonality() {
            return personality;
        }
        
        public void setPersonality(String personality) {
            this.personality = personality;
        }
    }
    
    private ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.botConfigs = new HashMap<>();
    }
    
    /**
     * Gets the singleton instance of ConfigManager.
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Loads configuration from config/friendcraft.json.
     * If the file doesn't exist, creates a default configuration.
     */
    public void loadConfig() {
        if (!Files.exists(CONFIG_FILE)) {
            createDefaultConfig();
            return;
        }
        
        try {
            String json = Files.readString(CONFIG_FILE);
            Type type = new TypeToken<Map<String, BotConfig>>(){}.getType();
            this.botConfigs = gson.fromJson(json, type);
            if (this.botConfigs == null) {
                this.botConfigs = new HashMap<>();
            }
            FriendCraftMod.LOGGER.info("Loaded {} bot configurations", this.botConfigs.size());
        } catch (IOException e) {
            FriendCraftMod.LOGGER.error("Failed to load configuration: {}", e.getMessage());
            this.botConfigs = new HashMap<>();
        }
    }
    
    /**
     * Saves configuration to config/friendcraft.json.
     */
    public void saveConfig() {
        try {
            Files.createDirectories(CONFIG_DIR);
            String json = gson.toJson(this.botConfigs);
            Files.writeString(CONFIG_FILE, json);
            FriendCraftMod.LOGGER.info("Saved {} bot configurations", this.botConfigs.size());
        } catch (IOException e) {
            FriendCraftMod.LOGGER.error("Failed to save configuration: {}", e.getMessage());
        }
    }
    
    /**
     * Creates a default configuration file with example bot.
     */
    private void createDefaultConfig() {
        this.botConfigs = new HashMap<>();
        this.botConfigs.put("bot1", new BotConfig("Assistant", "./memory/bot1/", "assistant"));
        saveConfig();
        FriendCraftMod.LOGGER.info("Created default configuration");
    }
    
    /**
     * Gets the configuration for a specific bot by bot_id.
     */
    public BotConfig getBotConfig(String botId) {
        return this.botConfigs.get(botId);
    }
    
    /**
     * Gets all bot configurations.
     */
    public Map<String, BotConfig> getAllConfigs() {
        return new HashMap<>(this.botConfigs);
    }
    
    /**
     * Adds or updates a bot configuration.
     */
    public void setBotConfig(String botId, BotConfig config) {
        this.botConfigs.put(botId, config);
        saveConfig();
    }
    
    /**
     * Removes a bot configuration.
     */
    public void removeBotConfig(String botId) {
        this.botConfigs.remove(botId);
        saveConfig();
    }
    
    /**
     * Checks if a bot configuration exists.
     */
    public boolean hasBotConfig(String botId) {
        return this.botConfigs.containsKey(botId);
    }
}
