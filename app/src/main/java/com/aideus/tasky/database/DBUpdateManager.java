package com.aideus.tasky.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.aideus.tasky.model.ModelTask;

public class DBUpdateManager {

    // SQLiteDatabase exposes methods to manage a SQLite database.
    SQLiteDatabase database;

    // Get database using constructor.
    DBUpdateManager(SQLiteDatabase database) {
        this.database = database;
    }

    // Update title in TASK_TITLE_COLUMN using its unique timeStamp and new title value.
    public void title(long timeStamp, String title) {
        update(DBHelper.TASK_TITLE_COLUMN, timeStamp, title);
    }

    // Update date in TASK_DATE_COLUMN using its unique timeStamp and new date value.
    public void date(long timeStamp, long date) {
        update(DBHelper.TASK_DATE_COLUMN, timeStamp, date);
    }

    // Update priority in TASK_PRIORITY_COLUMN using its unique timeStamp and new priority value.
    public void priority(long timeStamp, int priority) {
        update(DBHelper.TASK_PRIORITY_COLUMN, timeStamp, priority);
    }

    // Update status in TASK_STATUS_COLUMN using its unique timeStamp and new status value.
    public void status(long timeStamp, int status) {
        update(DBHelper.TASK_STATUS_COLUMN, timeStamp, status);
    }

    // Update whole row in db with new data from task (ModelTask).
    public void task(ModelTask task) {
        title(task.getTimeStamp(), task.getTitle());
        date(task.getTimeStamp(), task.getDate());
        priority(task.getTimeStamp(), task.getPriority());
        status(task.getTimeStamp(), task.getStatus());
    }

    // Method to update with String value.
    private void update(String column, long key, String value) {
        // Create new ContentValues object and put String column-value.
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        // Update database 'column' with cv 'value' in the row with unique timeStamp.
        database.update(DBHelper.TASKS_TABLE, cv, DBHelper.TASK_TIME_STAMP_COLUMN + " = " + key, null);
    }

    // Method to update with long value.
    private  void update(String column, long key, long value) {
        // Create new ContentValues object and put String column-value.
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        // Update database 'column' with cv 'value' in the row with unique timeStamp.
        database.update(DBHelper.TASKS_TABLE, cv, DBHelper.TASK_TIME_STAMP_COLUMN + " = " + key, null);
    }

}
