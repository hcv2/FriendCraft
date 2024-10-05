package com.friendcraft.behavior;

import com.friendcraft.entity.AIPlayer;

/**
 * Idle behavior - AI player stops movement and occasionally looks around.
 */
public class IdleBehavior extends AIBehavior {
    private int randomLookTimer = 0;
    
    public IdleBehavior() {
        super(BehaviorState.IDLE, "idle");
    }
    
    @Override
    public void execute(AIPlayer aiPlayer) {
        // Stop horizontal movement
        aiPlayer.setDeltaMovement(0, aiPlayer.getDeltaMovement().y, 0);
        
        // Random head turning every ~40 ticks
        randomLookTimer++;
        if (randomLookTimer >= 40) {
            randomLookTimer = 0;
            aiPlayer.yHeadRotO = aiPlayer.yHeadRot;
            aiPlayer.yHeadRot += (aiPlayer.getRandom().nextFloat() - 0.5f) * 20.0f;
        }
    }
}
