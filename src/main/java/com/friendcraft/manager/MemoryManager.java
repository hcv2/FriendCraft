package com.friendcraft.manager;

import com.friendcraft.FriendCraftMod;
import com.friendcraft.memory.MemoryData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages memory storage for AI players.
 * Supports both global (config) and save-bound memory locations.
 */
public class MemoryManager {
    private static final String MEMORY_SUBDIR = "FriendCraft/memory";
    private static MemoryManager instance;
    private final Gson gson;
    private final Map<String, MemoryData> activeMemories;
    private Path currentMemoryRoot;
    private boolean isGlobalMemory = true;
    
    private MemoryManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.activeMemories = new HashMap<>();
        this.currentMemoryRoot = FMLPaths.CONFIGDIR.get().resolve(MEMORY_SUBDIR);
    }
    
    public static MemoryManager getInstance() {
        if (instance == null) {
            instance = new MemoryManager();
        }
        return instance;
    }
    
    /**
     * Initializes memory path for a bot based on configuration.
     * @param botId The bot identifier
     * @param memoryPath Memory path from configuration
     */
    public void initializeMemory(String botId, String memoryPath) {
        if (memoryPath.startsWith("./memory/")) {
            // Global memory in config directory
            this.isGlobalMemory = true;
            this.currentMemoryRoot = FMLPaths.CONFIGDIR.get().resolve(MEMORY_SUBDIR);
        } else {
            // Save-bound memory
            this.isGlobalMemory = false;
            if (ServerLifecycleHooks.getCurrentServer() != null) {
                Path worldPath = ServerLifecycleHooks.getCurrentServer().getWorldPath(ResourceLocation.parse("friendcraft:memory"));
                this.currentMemoryRoot = worldPath;
            }
        }
        
        // Create memory directory if it doesn't exist
        Path botMemoryPath = this.currentMemoryRoot.resolve(botId);
        try {
            Files.createDirectories(botMemoryPath);
        } catch (IOException e) {
            FriendCraftMod.LOGGER.error("Failed to create memory directory for bot {}: {}", botId, e.getMessage());
        }
        
        // Load existing memory data
        loadMemoryData(botId);
    }
    
    /**
     * Loads memory data for a bot.
     */
    private void loadMemoryData(String botId) {
        Path memoryFile = currentMemoryRoot.resolve(botId).resolve("memory.json");
        if (Files.exists(memoryFile)) {
            try {
                String json = Files.readString(memoryFile);
                MemoryData data = gson.fromJson(json, MemoryData.class);
                if (data != null) {
                    activeMemories.put(botId, data);
                    FriendCraftMod.LOGGER.info("Loaded memory data for bot {}", botId);
                }
            } catch (IOException e) {
                FriendCraftMod.LOGGER.error("Failed to load memory data for bot {}: {}", botId, e.getMessage());
            }
        } else {
            // Create new memory data
            MemoryData data = new MemoryData();
            data.setBotId(botId);
            activeMemories.put(botId, data);
            saveMemoryData(botId);
        }
    }
    
    /**
     * Saves memory data for a bot.
     */
    public void saveMemoryData(String botId) {
        MemoryData data = activeMemories.get(botId);
        if (data == null) {
            return;
        }
        
        Path memoryFile = currentMemoryRoot.resolve(botId).resolve("memory.json");
        try {
            Files.createDirectories(memoryFile.getParent());
            String json = gson.toJson(data);
            Files.writeString(memoryFile, json);
            FriendCraftMod.LOGGER.debug("Saved memory data for bot {}", botId);
        } catch (IOException e) {
            FriendCraftMod.LOGGER.error("Failed to save memory data for bot {}: {}", botId, e.getMessage());
        }
    }
    
    /**
     * Gets memory data for a bot.
     */
    public MemoryData getMemoryData(String botId) {
        return activeMemories.get(botId);
    }
    
    /**
     * Saves all active memory data.
     * Called when server is stopping.
     */
    public void saveAllMemories() {
        for (String botId : activeMemories.keySet()) {
            saveMemoryData(botId);
        }
        FriendCraftMod.LOGGER.info("Saved all AI memory data");
    }
    
    /**
     * Initializes memory paths when a save is loaded.
     */
    public void onServerStarting() {
        FriendCraftMod.LOGGER.info("Initializing memory system on server start");
        // Reset to global memory by default
        this.currentMemoryRoot = FMLPaths.CONFIGDIR.get().resolve(MEMORY_SUBDIR);
    }
    
    /**
     * Saves all memory data when server is stopping.
     */
    public void onServerStopping() {
        saveAllMemories();
        FriendCraftMod.LOGGER.info("Memory system shut down");
    }
    
    /**
     * Gets the current memory root path.
     */
    public Path getCurrentMemoryRoot() {
        return currentMemoryRoot;
    }
    
    /**
     * Checks if using global memory storage.
     */
    public boolean isGlobalMemory() {
        return isGlobalMemory;
    }
}
