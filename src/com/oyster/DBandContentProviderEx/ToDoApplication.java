package com.oyster.DBandContentProviderEx;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * @author bamboo
 * @since 3/30/14 12:56 PM
 */
public class ToDoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register _ToDo_ class to use with Parse functions
        ParseObject.registerSubclass(ToDo.class);

        // initializing Parse account with App ID and Client ID
        Parse.initialize(this, "rlLpWKmTrWC1QaHVhBD5YGypYeYmR0rxGWGsi8LD",
                "zEv4ijaZMoOy0mBW2sShcziCEJyKmqcXd05T59fY");
    }

    public static String getCurrentUserId() {
        return ParseUser.getCurrentUser().getObjectId();
    }
}
