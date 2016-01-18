package com.aideus.tasky;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    // Constant for key of key-value boolean pair for splash_screen's visibility menu checkbox.
    public static final String SPLASH_IS_INVISIBLE = "splash_is_invisible";

    // Single instance of PreferenceHelper to work with sharedPreferences.
    private static PreferenceHelper instance;

    // Context for instance of PreferenceHelper and sharedPreferences.
    private Context context;

    // The SharedPreferences class provides a general framework that allows you to save and retrieve persistent key-value pairs of primitive data types.
    // You can use SharedPreferences to save any primitive data: booleans, floats, ints, longs, and strings.
    // This data will persist across user sessions (even if your application is killed).
    private SharedPreferences sharedPreferences;

    // Private constructor doesn't let to create many instances of PreferenceHelper.
    private PreferenceHelper() {
    }

    // Get existing instance of PreferenceHelper, if there isn't, create one.
    public static PreferenceHelper getInstance() {
        if (instance == null) {
            instance = new PreferenceHelper();
        }
        return instance;
    }

    // Initializing instance of sharedPreferences in private mode.
    public void init(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    // Save boolean using sharedPreferences via editor using string key and value.
    public void putBoolean(String key, Boolean value) {
        // Get instance of SharedPreferences Editor.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Save key-value boolean using string key.
        editor.putBoolean(key, value);
        // Commit preferences changes back from Editor to the SharedPreferences object it is editing.
        editor.apply();
    }

    // Get key-value boolean using string key from sharedPreferences object.
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}
