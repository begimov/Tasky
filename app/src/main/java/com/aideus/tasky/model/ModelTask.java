package com.aideus.tasky.model;

import com.aideus.tasky.R;

import java.util.Date;

// Model for task.
public class ModelTask implements Item {

    // Constants for task's priority.
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HIGH = 2;
    public static final String[] PRIORITY_LEVELS = {"Low Priority", "Normal Priority", "High Priority"};

    // Constants for task's status.
    public static final int STATUS_OVERDUE = 0;
    public static final int STATUS_CURRENT = 1;
    public static final int STATUS_DONE = 2;

    // Task property fields.
    private String title;
    private long date;
    private int priority;
    private int status;
    private long timeStamp;
    private int dateStatus;

    // Constructor for task (ModelTask) with no parameters and set its timeStamp tu current date/time.
    public ModelTask() {
        this.status = -1;
        this.timeStamp = new Date().getTime();
    }

    // Constructor for task (ModelTask) with known parameters.
    public ModelTask(String title, long date, int priority, int status, long timeStamp) {
        this.title = title;
        this.date = date;
        this.priority = priority;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    // Get color for priority CircleImageView.
    public int getPriorityColor() {
        switch (getPriority()) {

            // If priority in high and status in STATUS_CURRENT or STATUS_OVERDUE then max return most saturated color for PRIORITY_HIGH.
            // Else (STATUS_DONE) return least saturated color for PRIORITY_HIGH.
            // Same logic for all priority levels.
            case PRIORITY_HIGH:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_high;
                } else {
                    return R.color.priority_high_selected;
                }
            case PRIORITY_NORMAL:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_normal;
                } else {
                    return R.color.priority_normal_selected;
                }
            case PRIORITY_LOW:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_low;
                } else {
                    return R.color.priority_low_selected;
                }
            default:
                return 0;
        }
    }

    // Return if object is task.
    @Override
    public boolean isTask() {
        return true;
    }

    // Getters and setters for all field properties.
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(int dateStatus) {
        this.dateStatus = dateStatus;
    }
}
