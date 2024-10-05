package com.friendcraft.behavior;

import com.friendcraft.entity.AIPlayer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Follow behavior - makes AI player follow a target player.
 */
public class FollowBehavior extends AIBehavior {
    private static final double FOLLOW_DISTANCE = 3.0;
    private static final double MAX_FOLLOW_DISTANCE = 16.0;
    
    public FollowBehavior() {
        super(BehaviorState.FOLLOWING, "follow");
    }
    
    @Override
    public void execute(AIPlayer aiPlayer) {
        ServerPlayer followTarget = aiPlayer.getFollowTarget();
        if (followTarget == null) {
            aiPlayer.setBehaviorState("idle");
            return;
        }
        
        double distance = aiPlayer.distanceTo(followTarget);
        if (distance > MAX_FOLLOW_DISTANCE) {
            // Target too far, switch to idle
            aiPlayer.setBehaviorState("idle");
            aiPlayer.setFollowTarget(null);
            return;
        }
        
        if (distance > FOLLOW_DISTANCE) {
            // Move towards follow target
            double dx = followTarget.getX() - aiPlayer.getX();
            double dy = followTarget.getY() - aiPlayer.getY();
            double dz = followTarget.getZ() - aiPlayer.getZ();
            
            double speed = 0.5;
            aiPlayer.setDeltaMovement(
                dx / distance * speed,
                dy > 0 ? 0.5 : 0,
                dz / distance * speed
            );
        } else {
            // Stop when close enough
            aiPlayer.setDeltaMovement(0, aiPlayer.getDeltaMovement().y, 0);
        }
    }
}
