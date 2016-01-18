package com.aideus.tasky.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aideus.tasky.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

public class DBQueryManager {

    // SQLiteDatabase exposes methods to manage a SQLite database.
    private SQLiteDatabase database;

    // Get database using constructor.
    DBQueryManager (SQLiteDatabase database) {
        this.database = database;
    }

    // Get data and form task (ModelTask) by its timeStamp from database.
    public ModelTask getTask(long timeStamp) {
        ModelTask modelTask = null;
        Cursor c = database.query(DBHelper.TASKS_TABLE, null, DBHelper.SELECTION_TIME_STAMP,
                new String[]{Long.toString(timeStamp)}, null, null, null);
        if (c.moveToFirst()) {
            String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
            long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
            int priority = c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
            int status = c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));

            modelTask = new ModelTask(title, date, priority, status, timeStamp);
        }
        c.close();
        return modelTask;
    }

    // Method to get all existing tasks from database and return them in ArrayList.
    public List<ModelTask> getTasks(String selection, String[] selectionArgs, String orderBy) {

        // New ArrayList to store tasks (ModelTask).
        List<ModelTask> tasks = new ArrayList<>();

        // Get TASKS_TABLE from database and put it in cursor to get random read-write access to the result set returned by database query.
        Cursor c = database.query(DBHelper.TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);

        // moveToFirst() places the "read position" on the first entry in the results. Will return false if the cursor is empty.
        if (c.moveToFirst()) {

            // Read data from cursor, create task (ModelTask) using this data, and add tak into tasks (ArrayList).
            do {
                String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
                long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
                int priority = c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
                int status = c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));
                long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASK_TIME_STAMP_COLUMN));
                ModelTask modelTask = new ModelTask(title, date, priority, status, timeStamp);
                tasks.add(modelTask);
                // Do while there is next row in cursor. moveToNext will return false if the cursor is already past the last entry in the result set.
            } while (c.moveToNext());
        }
        // Close cursor, releasing all of its resources and making it completely invalid.
        c.close();

        // Return tasks (ArrayList) of (ModelTasks)
        return tasks;
    }

}
