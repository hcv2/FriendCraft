package com.friendcraft.init;

import com.friendcraft.config.ConfigManager;
import com.friendcraft.entity.AIPlayer;
import com.friendcraft.manager.BehaviorManager;
import com.friendcraft.manager.MemoryManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Command registration for FriendCraft mod.
 * Provides /aiplayer command with spawn, despawn, follow, stop, and mine subcommands.
 */
public class ModCommands {
    private static final Map<String, AIPlayer> activeAIPlayers = new ConcurrentHashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("aiplayer")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("spawn")
                .then(Commands.argument("bot_id", StringArgumentType.string())
                    .executes(ctx -> spawnAIPlayer(ctx, StringArgumentType.getString(ctx, "bot_id")))
                )
            )
            .then(Commands.literal("despawn")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes(ctx -> despawnAIPlayer(ctx, StringArgumentType.getString(ctx, "name")))
                )
            )
            .then(Commands.literal("follow")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes(ctx -> followPlayer(ctx, StringArgumentType.getString(ctx, "name")))
                )
            )
            .then(Commands.literal("stop")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes(ctx -> stopBehavior(ctx, StringArgumentType.getString(ctx, "name")))
                )
            )
            .then(Commands.literal("mine")
                .then(Commands.argument("name", StringArgumentType.string())
                    .then(Commands.argument("block", StringArgumentType.string())
                        .executes(ctx -> mineBlock(ctx, 
                            StringArgumentType.getString(ctx, "name"),
                            StringArgumentType.getString(ctx, "block")))
                    )
                )
            )
        );
    }

    private static int spawnAIPlayer(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, String botId) {
        try {
            ServerPlayer executor = ctx.getSource().getPlayerOrException();
            Level level = executor.level();
            
            // Get bot configuration
            ConfigManager.BotConfig botConfig = ConfigManager.getInstance().getBotConfig(botId);
            if (botConfig == null) {
                ctx.getSource().sendFailure(Component.literal("Bot configuration not found for: " + botId));
                return 0;
            }
            
            // Create AI player entity
            AIPlayer aiPlayer = new AIPlayer(ModEntities.AI_PLAYER.get(), level, botId, botConfig.getName());
            aiPlayer.setPos(executor.position().add(2, 0, 0));
            aiPlayer.setBehaviorState("idle");
            
            // Initialize memory for this bot
            MemoryManager.getInstance().initializeMemory(botId, botConfig.getMemoryPath());
            
            // Add to world and active players
            level.addFreshEntity(aiPlayer);
            activeAIPlayers.put(botConfig.getName(), aiPlayer);
            
            ctx.getSource().sendSuccess(
                () -> Component.literal("Spawned AI player '" + botConfig.getName() + "' (" + botId + ")"),
                true
            );
            
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Failed to spawn AI player: " + e.getMessage()));
            return 0;
        }
    }

    private static int despawnAIPlayer(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, String name) {
        AIPlayer aiPlayer = findAIPlayerByName(name);
        if (aiPlayer == null) {
            ctx.getSource().sendFailure(Component.literal("AI player not found: " + name));
            return 0;
        }
        
        aiPlayer.despawn();
        activeAIPlayers.remove(name);
        
        ctx.getSource().sendSuccess(() -> Component.literal("Despawned AI player '" + name + "'"), true);
        return 1;
    }

    private static int followPlayer(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, String name) {
        AIPlayer aiPlayer = findAIPlayerByName(name);
        if (aiPlayer == null) {
            ctx.getSource().sendFailure(Component.literal("AI player not found: " + name));
            return 0;
        }
        
        try {
            ServerPlayer executor = ctx.getSource().getPlayerOrException();
            aiPlayer.setBehaviorState("follow");
            aiPlayer.setFollowTarget(executor);
            
            ctx.getSource().sendSuccess(
                () -> Component.literal("AI player '" + name + "' is now following you"),
                true
            );
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Failed to set follow behavior: " + e.getMessage()));
            return 0;
        }
    }

    private static int stopBehavior(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, String name) {
        AIPlayer aiPlayer = findAIPlayerByName(name);
        if (aiPlayer == null) {
            ctx.getSource().sendFailure(Component.literal("AI player not found: " + name));
            return 0;
        }
        
        aiPlayer.setBehaviorState("idle");
        aiPlayer.setFollowTarget(null);
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("AI player '" + name + "' has stopped"),
            true
        );
        return 1;
    }

    private static int mineBlock(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, String name, String blockType) {
        AIPlayer aiPlayer = findAIPlayerByName(name);
        if (aiPlayer == null) {
            ctx.getSource().sendFailure(Component.literal("AI player not found: " + name));
            return 0;
        }
        
        aiPlayer.setBehaviorState("mine");
        // TODO: Set mine target based on blockType
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("AI player '" + name + "' is now mining " + blockType),
            true
        );
        return 1;
    }

    private static AIPlayer findAIPlayerByName(String name) {
        return activeAIPlayers.get(name);
    }

    public static Map<String, AIPlayer> getActiveAIPlayers() {
        return activeAIPlayers;
    }
}
