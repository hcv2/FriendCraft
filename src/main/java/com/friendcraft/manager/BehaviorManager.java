package com.friendcraft.manager;

import com.friendcraft.behavior.*;
import com.friendcraft.entity.AIPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages behavior states for AI players.
 * Handles behavior switching and execution.
 */
public class BehaviorManager {
    private static BehaviorManager instance;
    private final Map<BehaviorState, AIBehavior> behaviors;
    
    private BehaviorManager() {
        behaviors = new HashMap<>();
        registerDefaultBehaviors();
    }
    
    public static BehaviorManager getInstance() {
        if (instance == null) {
            instance = new BehaviorManager();
        }
        return instance;
    }
    
    private void registerDefaultBehaviors() {
        behaviors.put(BehaviorState.IDLE, new IdleBehavior());
        behaviors.put(BehaviorState.FOLLOWING, new FollowBehavior());
        behaviors.put(BehaviorState.MINING, new MiningBehavior());
        // Add more behaviors as needed
    }
    
    /**
     * Registers a new behavior.
     */
    public void registerBehavior(AIBehavior behavior) {
        behaviors.put(behavior.getState(), behavior);
    }
    
    /**
     * Gets the behavior for a given state.
     */
    public AIBehavior getBehavior(BehaviorState state) {
        return behaviors.get(state);
    }
    
    /**
     * Executes the current behavior for an AI player.
     */
    public void executeBehavior(AIPlayer aiPlayer) {
        BehaviorState currentState = BehaviorState.fromString(aiPlayer.getBehaviorState());
        AIBehavior behavior = behaviors.get(currentState);
        
        if (behavior != null) {
            behavior.execute(aiPlayer);
        }
    }
}
