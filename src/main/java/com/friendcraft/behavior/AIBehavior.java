package com.friendcraft.behavior;

import com.friendcraft.entity.AIPlayer;

/**
 * Base class for all AI behaviors.
 * Each behavior implements the execute method to define its logic.
 */
public abstract class AIBehavior {
    protected final BehaviorState state;
    protected final String name;
    
    protected AIBehavior(BehaviorState state, String name) {
        this.state = state;
        this.name = name;
    }
    
    public BehaviorState getState() {
        return state;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Executes the behavior logic for one tick.
     * @param aiPlayer The AI player entity to execute behavior on
     */
    public abstract void execute(AIPlayer aiPlayer);
    
    /**
     * Called when this behavior becomes active.
     * @param aiPlayer The AI player entity
     */
    public void onStart(AIPlayer aiPlayer) {
        // Default implementation does nothing
    }
    
    /**
     * Called when this behavior is deactivated.
     * @param aiPlayer The AI player entity
     */
    public void onStop(AIPlayer aiPlayer) {
        // Default implementation does nothing
    }
}
