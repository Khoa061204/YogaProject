package com.example.yogaadmin.models;

import java.util.Calendar;

public class YogaCourse {
    private int id;
    private String dayOfWeek;  // Required
    private String time;       // Required
    private float price;       // Required
    private int capacity;      // Required
    private int duration;      // Required
    private String type;       // Required
    private String description;// Optional
    private boolean isActive;  // Additional field: track if course is currently active
    private String difficulty; // Additional field: beginner, intermediate, advanced
    private String equipment;  // Additional field: required equipment for the class
    private long lastModified; // For cloud sync
    private boolean isSynced;  // For cloud sync

    public YogaCourse() {
        this.lastModified = Calendar.getInstance().getTimeInMillis();
        this.isSynced = false;
        this.isActive = true;
    }

    public YogaCourse(String dayOfWeek, String time, float price, int capacity, 
                     int duration, String type, String description, 
                     String difficulty, String equipment) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.price = price;
        this.capacity = capacity;
        this.duration = duration;
        this.type = type;
        this.description = description;
        this.difficulty = difficulty;
        this.equipment = equipment;
        this.isActive = true;
        this.lastModified = Calendar.getInstance().getTimeInMillis();
        this.isSynced = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { 
        this.dayOfWeek = dayOfWeek;
        updateLastModified();
    }
    
    public String getTime() { return time; }
    public void setTime(String time) { 
        this.time = time;
        updateLastModified();
    }
    
    public float getPrice() { return price; }
    public void setPrice(float price) { 
        this.price = price;
        updateLastModified();
    }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { 
        this.capacity = capacity;
        updateLastModified();
    }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { 
        this.duration = duration;
        updateLastModified();
    }
    
    public String getType() { return type; }
    public void setType(String type) { 
        this.type = type;
        updateLastModified();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description;
        updateLastModified();
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { 
        isActive = active;
        updateLastModified();
    }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { 
        this.difficulty = difficulty;
        updateLastModified();
    }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { 
        this.equipment = equipment;
        updateLastModified();
    }

    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { this.isSynced = synced; }

    private void updateLastModified() {
        this.lastModified = Calendar.getInstance().getTimeInMillis();
        this.isSynced = false;
    }
}