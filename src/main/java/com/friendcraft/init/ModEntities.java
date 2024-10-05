package com.friendcraft.init;

import com.friendcraft.FriendCraftMod;
import com.friendcraft.entity.AIPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registry for all entity types in the FriendCraft mod.
 */
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
        DeferredRegister.create(Registries.ENTITY_TYPE, FriendCraftMod.MOD_ID);

    public static final Supplier<EntityType<AIPlayer>> AI_PLAYER = 
        ENTITY_TYPES.register("ai_player", () -> EntityType.Builder.of(AIPlayer::new, MobCategory.MISC)
            .sized(0.6f, 1.8f)
            .build(new ResourceLocation(FriendCraftMod.MOD_ID, "ai_player").toString()));

    /**
     * Registers all entity types to the mod event bus.
     */
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
