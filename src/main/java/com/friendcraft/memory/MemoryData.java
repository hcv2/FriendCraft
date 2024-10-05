package com.friendcraft.memory;

import com.friendcraft.FriendCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Memory data structure for AI players.
 * Stores learning data, experiences, and knowledge base entries.
 */
public class MemoryData {
    private String botId;
    private Map<String, Double> behaviorWeights;
    private Map<String, Object> experiences;
    private java.util.List<String> knowledge;
    
    public MemoryData() {
        this.behaviorWeights = new HashMap<>();
        this.experiences = new HashMap<>();
        this.knowledge = new java.util.ArrayList<>();
    }
    
    public String getBotId() {
        return botId;
    }
    
    public void setBotId(String botId) {
        this.botId = botId;
    }
    
    public Map<String, Double> getBehaviorWeights() {
        return behaviorWeights;
    }
    
    public void setBehaviorWeights(Map<String, Double> behaviorWeights) {
        this.behaviorWeights = behaviorWeights;
    }
    
    public void addBehaviorWeight(String behavior, double weight) {
        this.behaviorWeights.put(behavior, weight);
    }
    
    public Double getBehaviorWeight(String behavior) {
        return this.behaviorWeights.getOrDefault(behavior, 1.0);
    }
    
    public Map<String, Object> getExperiences() {
        return experiences;
    }
    
    public void setExperiences(Map<String, Object> experiences) {
        this.experiences = experiences;
    }
    
    public void addExperience(String key, Object value) {
        this.experiences.put(key, value);
    }
    
    public java.util.List<String> getKnowledge() {
        return knowledge;
    }
    
    public void setKnowledge(java.util.List<String> knowledge) {
        this.knowledge = knowledge;
    }
    
    public void addKnowledge(String entry) {
        this.knowledge.add(entry);
    }
}
