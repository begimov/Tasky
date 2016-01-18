package com.aideus.tasky.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.aideus.tasky.database.DBHelper;
import com.aideus.tasky.model.ModelTask;
import java.util.ArrayList;
import java.util.List;

public class AlarmSetter extends BroadcastReceiver{

    // On BOOT_COMPLETED receive intent for respective action and restore alarms after reboot.
    @Override
    public void onReceive(Context context, Intent intent) {

        // Init DBHelper and AlarmHelper
        DBHelper dbHelper = new DBHelper(context);
        AlarmHelper.getInstance().init(context);
        AlarmHelper alarmHelper = AlarmHelper.getInstance();

        // Init ArrayList and fill it with current/overdue tasks from DB sorted by date.
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(dbHelper.query().getTasks(DBHelper.SELECTION_STATUS + " OR "
                + DBHelper.SELECTION_STATUS, new String[]{Integer.toString(ModelTask.STATUS_CURRENT),
                Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));

        // Set alarm for every current task (ModelTask) in tasks (ArrayList).
        for (ModelTask task : tasks) {
            if (task.getDate() != 0) {
                alarmHelper.setAlarm(task);
            }
        }
    }
}
