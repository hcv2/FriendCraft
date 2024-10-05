package com.friendcraft;

import com.friendcraft.config.ConfigManager;
import com.friendcraft.init.ModEntities;
import com.friendcraft.init.ModCommands;
import com.friendcraft.manager.BehaviorManager;
import com.friendcraft.manager.MemoryManager;
import com.friendcraft.manager.LearningEngine;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(FriendCraftMod.MOD_ID)
public class FriendCraftMod {
    public static final String MOD_ID = "friendcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(FriendCraftMod.class);

    public FriendCraftMod(IEventBus modEventBus) {
        LOGGER.info("FriendCraft mod initializing...");
        
        // Register deferred registers
        ModEntities.register(modEventBus);
        
        // Register mod lifecycle events
        modEventBus.addListener(this::commonSetup);
        
        // Register game events
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        
        LOGGER.info("FriendCraft mod initialized!");
    }
    
    private void commonSetup(FMLCommonSetupEvent event) {
        // Initialize managers
        BehaviorManager.getInstance();
        MemoryManager.getInstance();
        LearningEngine.getInstance();
        LOGGER.info("FriendCraft common setup complete");
    }
    
    private void onServerStarting(ServerStartingEvent event) {
        // Load configuration when server starts
        ConfigManager.getInstance().loadConfig();
        MemoryManager.getInstance().onServerStarting();
        LOGGER.info("Configuration loaded");
    }
    
    private void onServerStopping(ServerStoppingEvent event) {
        // Save configuration and memory when server stops
        ConfigManager.getInstance().saveConfig();
        MemoryManager.getInstance().onServerStopping();
        LearningEngine.getInstance().processAllFeedback();
        LOGGER.info("Configuration and memory saved");
    }
    
    private void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
