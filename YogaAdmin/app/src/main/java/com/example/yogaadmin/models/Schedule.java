package com.example.yogaadmin.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Schedule {
    private int id;
    private String date;        // Required - format: dd/MM/yyyy
    private String teacher;     // Required
    private String comments;    // Optional
    private int yogaCourseId;   // Foreign key to YogaCourse
    private int currentEnrollment; // Additional field: track current number of students
    private boolean isCancelled;   // Additional field: track if class is cancelled
    private long lastModified;     // For cloud sync
    private boolean isSynced;      // For cloud sync

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

    public Schedule() {
        this.lastModified = Calendar.getInstance().getTimeInMillis();
        this.isSynced = false;
        this.currentEnrollment = 0;
        this.isCancelled = false;
    }

    public Schedule(String date, String teacher, String comments, int yogaCourseId) {
        this();
        this.date = date;
        this.teacher = teacher;
        this.comments = comments;
        this.yogaCourseId = yogaCourseId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getDate() { return date; }
    public void setDate(String date) { 
        this.date = date;
        updateLastModified();
    }
    
    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { 
        this.teacher = teacher;
        updateLastModified();
    }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { 
        this.comments = comments;
        updateLastModified();
    }
    
    public int getYogaCourseId() { return yogaCourseId; }
    public void setYogaCourseId(int yogaCourseId) { 
        this.yogaCourseId = yogaCourseId;
        updateLastModified();
    }

    public int getCurrentEnrollment() { return currentEnrollment; }
    public void setCurrentEnrollment(int currentEnrollment) { 
        this.currentEnrollment = currentEnrollment;
        updateLastModified();
    }

    public boolean isCancelled() { return isCancelled; }
    public void setCancelled(boolean cancelled) { 
        isCancelled = cancelled;
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

    // Utility methods
    public String getDayOfWeek() {
        try {
            Date scheduleDate = DATE_FORMAT.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(scheduleDate);
            
            String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            return days[cal.get(Calendar.DAY_OF_WEEK)];
        } catch (ParseException e) {
            return null;
        }
    }

    public boolean isValidDate() {
        try {
            DATE_FORMAT.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String formatDate(int day, int month, int year) {
        return String.format(Locale.UK, "%02d/%02d/%04d", day, month, year);
    }
}