package com.mfekim.testapinytimes.utils;

import android.util.Log;

/**
 * Utils methods for log.
 */
public class NYTLogUtils {
    /** Tag for logs. */
    private static final String TAG = NYTLogUtils.class.getSimpleName();

    /**
     * @param url URL to log.
     * @return The URL passed as parameter.
     */
    public static String logUrl(String url) {
        Log.d(TAG, "URL = " + url);
        return url;
    }
}
