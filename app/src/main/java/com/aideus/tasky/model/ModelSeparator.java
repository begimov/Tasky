package com.aideus.tasky.model;

import com.aideus.tasky.R;

// Model of separator.
public class ModelSeparator implements Item {

    // Constants for separator types.
    public static final int TYPE_OVERDUE = R.string.separator_overdue;
    public static final int TYPE_TODAY = R.string.separator_today;
    public static final int TYPE_TOMORROW = R.string.separator_tomorrow;
    public static final int TYPE_FUTURE = R.string.separator_future;

    private int type;

    public ModelSeparator (int type) {
        this.type = type;
    }

    // Return if object is task.
    @Override
    public boolean isTask() {
        return false;
    }

    // Getter and setter for separator type.
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
