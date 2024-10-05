package com.friendcraft;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(FriendCraftMod.MOD_ID)
public class FriendCraftMod {
    public static final String MOD_ID = "friendcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(FriendCraftMod.class);

    public FriendCraftMod(IEventBus modEventBus) {
        LOGGER.info("FriendCraft mod initialized!");
        
        // Register mod components to the event bus
        // Entity types, commands, and other systems will be registered here
    }
}
