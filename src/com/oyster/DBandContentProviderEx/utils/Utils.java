package com.oyster.DBandContentProviderEx.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author bamboo
 * @since 4/5/14 7:53 PM
 */
public class Utils {

    /**
     * Check if the network is connected
     *
     * @param c Context of the caller
     * @return true - network is connected, false - else
     */

    private static Context sAppContext;

    public static boolean isNetworkConnected(Context c) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            return true;
        }

        return false;
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static void setAppContext(Context appContext) {
        sAppContext = appContext;
    }
}
