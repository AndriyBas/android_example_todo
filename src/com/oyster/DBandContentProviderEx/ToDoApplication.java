package com.oyster.DBandContentProviderEx;

import android.app.Application;
import android.content.SharedPreferences;
import com.oyster.DBandContentProviderEx.data.ToDo;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * @author bamboo
 * @since 3/30/14 12:56 PM
 */
public class ToDoApplication extends Application {

    public static final String SHARED_PREFS_USER_SETTINGS = "com.oyster.ToDoApplication";

    private static final String KEY_LAST_USER_SESSION_DATE = "last_user_session_date";
    private static final String KEY_LAST_SERVER_SYNCHRONIZE_DATE = "last_server_synchronize_date";

    private static SharedPreferences mSharedPreferencesUserSettings;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register _ToDo_ class to use with Parse functions
        ParseObject.registerSubclass(ToDo.class);

        // initializing Parse account with App ID and Client ID
        Parse.initialize(this, ParseKeys.APPLICATION_ID,
                ParseKeys.CLIENT_KEY);

        // initialize SharedPreferences
        mSharedPreferencesUserSettings = getSharedPreferences(SHARED_PREFS_USER_SETTINGS, MODE_MULTI_PROCESS);
    }

    public static String getCurrentUserId() {
        return ParseUser.getCurrentUser().getObjectId();
    }

    /**
     * @return the last time user was log in or zero if this is first session
     */
    public static long getLastUserSessionDate() {
        return mSharedPreferencesUserSettings
                .getLong(getCurrentUserKey(KEY_LAST_USER_SESSION_DATE), 0L);
    }

    /**
     * updates the time user last was log in (use just before the log out)
     *
     * @param lastDate time just before the log out
     */
    public static void setLastUserSessionDate(long lastDate) {
        mSharedPreferencesUserSettings
                .edit()
                .putLong(getCurrentUserKey(KEY_LAST_USER_SESSION_DATE), lastDate)
                .commit();
    }

    /**
     * @return the last time of user updated date with server
     */
    public static long getLastServerSynchronizeDate() {
        return mSharedPreferencesUserSettings
                .getLong(getCurrentUserKey(KEY_LAST_SERVER_SYNCHRONIZE_DATE), 0L);
    }

    /**
     * @param date time to update the last synchronization with server
     */
    public static void setLastServerSynchronizeDate(long date) {
        mSharedPreferencesUserSettings
                .edit()
                .putLong(getCurrentUserKey(KEY_LAST_SERVER_SYNCHRONIZE_DATE), date)
                .commit();
    }

    private static String getCurrentUserKey(String key) {
        return key + "_" + getCurrentUserId();
    }
}


