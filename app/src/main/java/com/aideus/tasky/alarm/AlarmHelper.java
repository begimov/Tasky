package com.aideus.tasky.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.aideus.tasky.model.ModelTask;

public class AlarmHelper {

    private static AlarmHelper instance;
    private Context context;
    private AlarmManager alarmManager;

    private AlarmHelper() {
    }

    // Return instance of AlarmHelper, to make sure there is only one.
    public static AlarmHelper getInstance() {
        if (instance == null) {
            instance = new AlarmHelper();
        }
        return instance;
    }

    // Init AlarmManager which provides access to the system alarm services.
    // These allow you to schedule your application to be run at some point in the future.
    // When an alarm goes off, the {@link Intent} that had been registered for it
    // is broadcast by the system, automatically starting the target application
    // if it is not already running. Registered alarms are retained while the
    // device is asleep (and can optionally wake the device up if they go off
    // during that time), but will be cleared if it is turned off and rebooted.
    public void init (Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    // Set alarm for task on its date. Create and register intent for it which AlarmReceiver will get.
    public void setAlarm(ModelTask task) {

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", task.getTitle());
        intent.putExtra("time_stamp", task.getTimeStamp());
        intent.putExtra("color", task.getPriorityColor());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                (int) task.getTimeStamp(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDate(), pendingIntent);
    }

    // Remove alarm by its pendingIntent and taskTimeStamp.
    public void removeAlarm(long taskTimeStamp) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) taskTimeStamp,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Remove any alarms with a matching pendingIntent.
        alarmManager.cancel(pendingIntent);
    }
}
