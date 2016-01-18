package com.aideus.tasky.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.aideus.tasky.model.ModelTask;

// Extends from SQLiteOpenHelper helper class which manages database creation and version management.
public class DBHelper extends SQLiteOpenHelper {

    // Database related constants, version, name, table name, column names.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tasky_database";
    public static final String TASKS_TABLE = "tasks_table";
    public static final String TASK_TITLE_COLUMN = "task_title";
    public static final String TASK_DATE_COLUMN = "task_date";
    public static final String TASK_PRIORITY_COLUMN = "task_priority";
    public static final String TASK_STATUS_COLUMN = "task_status";
    public static final String TASK_TIME_STAMP_COLUMN = "task_time_stamp";

    // Script to create table and columns.
    // Implementing the BaseColumns interface, class can inherit a primary key field called _ID.
    // That some Android classes such as cursor adaptors expect it to have.
    private static final String TASKS_TABLE_CREATE_SCRIPT = "CREATE TABLE "
            + TASKS_TABLE + " (" + BaseColumns._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_TITLE_COLUMN + " TEXT NOT NULL, "
            + TASK_DATE_COLUMN + " LONG, " + TASK_PRIORITY_COLUMN + " INTEGER, "
            + TASK_STATUS_COLUMN + " INTEGER, " + TASK_TIME_STAMP_COLUMN + " LONG);";

    // Select rows from database by TASK_STATUS_COLUMN.
    public static final String SELECTION_STATUS = DBHelper.TASK_STATUS_COLUMN + " = ?";

    // Select rows from database by TASK_TIME_STAMP_COLUMN.
    public static final String SELECTION_TIME_STAMP = DBHelper.TASK_TIME_STAMP_COLUMN + " = ?";

    // Select rows from database by TASK_TITLE_COLUMN.
    public static final String SELECTION_LIKE_TITLE = TASK_TITLE_COLUMN + " LIKE ?";

    // Getting instance of my DBQueryManager class to get data from database.
    private DBQueryManager dbQueryManager;

    // Getting instance of my DBUpdateManager class to update data in database.
    private DBUpdateManager dbUpdateManager;

    // Constructor initializes new instances of DBQueryManager and DBUpdateManager classes.
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbQueryManager = new DBQueryManager(getReadableDatabase());
        // getWritableDatabase creates and/or open a database that will be used for reading and writing. The first time this is called,
        // the database will be opened and onCreate(SQLiteDatabase), onUpgrade(SQLiteDatabase, int, int) and/or onOpen(SQLiteDatabase) will be called.
        // Once opened successfully, the database is cached, so you can call this method every time you need to write to the database.
        dbUpdateManager = new DBUpdateManager(getWritableDatabase());
    }

    // Called when the database is created for the first time.
    // This is where the creation of tables and the initial population of the tables should happen.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
        db.execSQL(TASKS_TABLE_CREATE_SCRIPT);
    }

    // Called when the database needs to be upgraded.
    // The implementation should use this method to drop tables, add tables, or do anything else it needs to upgrade to the new schema version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TASKS_TABLE);
        onCreate(db);
    }

    // Save new task to database.
    public void saveTask(ModelTask task) {
        // Create a new map of values, where column names are the keys.
        ContentValues newValues = new ContentValues();
        newValues.put(TASK_TITLE_COLUMN, task.getTitle());
        newValues.put(TASK_DATE_COLUMN, task.getDate());
        newValues.put(TASK_PRIORITY_COLUMN, task.getPriority());
        newValues.put(TASK_STATUS_COLUMN, task.getStatus());
        newValues.put(TASK_TIME_STAMP_COLUMN, task.getTimeStamp());

        // // Insert the new row.
        getWritableDatabase().insert(TASKS_TABLE, null, newValues);
    }

    // Return instance of DBQueryManager.
    public DBQueryManager query() {
        return dbQueryManager;
    }

    // Return instance of DBUpdateManager.
    public  DBUpdateManager update() {
        return dbUpdateManager;
    }

    // Method to remove task from database by its timeStamp.
    public void removeTask(long timeStamp) {
        getWritableDatabase().delete(TASKS_TABLE, SELECTION_TIME_STAMP, new String[]{Long.toString(timeStamp)});
    }
}
