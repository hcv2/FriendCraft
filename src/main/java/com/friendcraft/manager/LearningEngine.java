package com.friendcraft.manager;

import com.friendcraft.FriendCraftMod;
import com.friendcraft.memory.MemoryData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Learning engine that adjusts AI behavior weights based on player feedback.
 * Tracks player interactions and adjusts AI strategy accordingly.
 */
public class LearningEngine {
    private static LearningEngine instance;
    private final Map<String, BehaviorFeedback> feedbackHistory;
    private static final double LEARNING_RATE = 0.1;
    private static final double MIN_WEIGHT = 0.1;
    private static final double MAX_WEIGHT = 2.0;
    
    /**
     * Records feedback for a specific behavior.
     */
    public static class BehaviorFeedback {
        private String behavior;
        private int positiveCount;
        private int negativeCount;
        private double currentWeight;
        
        public BehaviorFeedback(String behavior) {
            this.behavior = behavior;
            this.positiveCount = 0;
            this.negativeCount = 0;
            this.currentWeight = 1.0;
        }
        
        public String getBehavior() {
            return behavior;
        }
        
        public int getPositiveCount() {
            return positiveCount;
        }
        
        public int getNegativeCount() {
            return negativeCount;
        }
        
        public double getCurrentWeight() {
            return currentWeight;
        }
        
        public void addPositive() {
            this.positiveCount++;
        }
        
        public void addNegative() {
            this.negativeCount++;
        }
    }
    
    private LearningEngine() {
        this.feedbackHistory = new ConcurrentHashMap<>();
    }
    
    public static LearningEngine getInstance() {
        if (instance == null) {
            instance = new LearningEngine();
        }
        return instance;
    }
    
    /**
     * Records positive feedback for a behavior.
     */
    public void recordPositiveFeedback(String botId, String behavior) {
        String key = botId + ":" + behavior;
        BehaviorFeedback feedback = feedbackHistory.computeIfAbsent(key, k -> new BehaviorFeedback(behavior));
        feedback.addPositive();
        
        adjustWeight(botId, behavior, LEARNING_RATE);
        FriendCraftMod.LOGGER.debug("Positive feedback for {} behavior: {}", botId, behavior);
    }
    
    /**
     * Records negative feedback for a behavior.
     */
    public void recordNegativeFeedback(String botId, String behavior) {
        String key = botId + ":" + behavior;
        BehaviorFeedback feedback = feedbackHistory.computeIfAbsent(key, k -> new BehaviorFeedback(behavior));
        feedback.addNegative();
        
        adjustWeight(botId, behavior, -LEARNING_RATE);
        FriendCraftMod.LOGGER.debug("Negative feedback for {} behavior: {}", botId, behavior);
    }
    
    /**
     * Adjusts the weight of a behavior based on feedback.
     */
    private void adjustWeight(String botId, String behavior, double adjustment) {
        MemoryData memoryData = MemoryManager.getInstance().getMemoryData(botId);
        if (memoryData == null) {
            return;
        }
        
        double currentWeight = memoryData.getBehaviorWeight(behavior);
        double newWeight = Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, currentWeight + adjustment));
        memoryData.addBehaviorWeight(behavior, newWeight);
        
        FriendCraftMod.LOGGER.debug("Adjusted weight for {} behavior {}: {} -> {}", 
            botId, behavior, currentWeight, newWeight);
    }
    
    /**
     * Processes all pending feedback and saves memory data.
     */
    public void processAllFeedback() {
        for (Map.Entry<String, BehaviorFeedback> entry : feedbackHistory.entrySet()) {
            String[] parts = entry.getKey().split(":");
            String botId = parts[0];
            String behavior = parts[1];
            
            MemoryData memoryData = MemoryManager.getInstance().getMemoryData(botId);
            if (memoryData != null) {
                MemoryManager.getInstance().saveMemoryData(botId);
            }
        }
        FriendCraftMod.LOGGER.info("Processed all feedback");
    }
    
    /**
     * Gets the current weight for a behavior.
     */
    public double getBehaviorWeight(String botId, String behavior) {
        MemoryData memoryData = MemoryManager.getInstance().getMemoryData(botId);
        if (memoryData == null) {
            return 1.0;
        }
        return memoryData.getBehaviorWeight(behavior);
    }
    
    /**
     * Resets all feedback history.
     */
    public void resetFeedback() {
        feedbackHistory.clear();
    }
}
