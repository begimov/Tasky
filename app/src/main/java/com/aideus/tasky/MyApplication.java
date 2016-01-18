package com.aideus.tasky;

import android.app.Application;

public class MyApplication extends Application {

    // Support class to set and return activityVisible state.

    private static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}
