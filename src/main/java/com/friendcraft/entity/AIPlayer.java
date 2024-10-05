package com.friendcraft.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.friendcraft.manager.BehaviorManager;
import com.friendcraft.manager.MemoryManager;
import com.friendcraft.memory.MemoryData;

/**
 * AI Player entity that extends ServerPlayer.
 * This entity can execute behaviors, follow players, fight, and mine blocks.
 */
public class AIPlayer extends ServerPlayer {
    private String botId;
    private String behaviorState = "idle";
    private ServerPlayer followTarget;
    private boolean isDespawning = false;
    private MemoryData memoryData;

    public AIPlayer(EntityType<? extends ServerPlayer> entityType, Level level) {
        super(level.getServer().getPlayerList().getServer(), level, level.getGameProfile(), null);
        this.botId = "default";
    }

    public AIPlayer(EntityType<? extends ServerPlayer> entityType, Level level, String botId, String name) {
        super(level.getServer().getPlayerList().getServer(), level, level.getGameProfile(), null);
        this.botId = botId;
        this.setCustomNameVisible(false);
        this.memoryData = MemoryManager.getInstance().getMemoryData(botId);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (isDespawning) {
            return;
        }
        
        // Execute current behavior
        BehaviorManager.getInstance().executeBehavior(this);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("botId", this.botId);
        compound.putString("behaviorState", this.behaviorState);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("botId")) {
            this.botId = compound.getString("botId");
        }
        if (compound.contains("behaviorState")) {
            this.behaviorState = compound.getString("behaviorState");
        }
    }

    // Getters and Setters
    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    public String getBehaviorState() {
        return behaviorState;
    }

    public void setBehaviorState(String behaviorState) {
        this.behaviorState = behaviorState;
    }

    public ServerPlayer getFollowTarget() {
        return followTarget;
    }

    public void setFollowTarget(ServerPlayer followTarget) {
        this.followTarget = followTarget;
    }

    public MemoryData getMemoryData() {
        return memoryData;
    }

    public void despawn() {
        this.isDespawning = true;
        this.discard();
    }

    /**
     * Creates default attributes for AI player entities.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return ServerPlayer.createAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.7)
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.ATTACK_DAMAGE, 1.0)
            .add(Attributes.FOLLOW_RANGE, 16.0);
    }
}
