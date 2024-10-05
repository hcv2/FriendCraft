package com.friendcraft.behavior;

/**
 * Enumeration of all possible behavior states for AI players.
 */
public enum BehaviorState {
    IDLE("idle"),
    FOLLOWING("follow"),
    EXPLORING("explore"),
    FIGHTING("fight"),
    MINING("mine");
    
    private final String name;
    
    BehaviorState(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public static BehaviorState fromString(String name) {
        for (BehaviorState state : values()) {
            if (state.name.equals(name)) {
                return state;
            }
        }
        return IDLE;
    }
}
