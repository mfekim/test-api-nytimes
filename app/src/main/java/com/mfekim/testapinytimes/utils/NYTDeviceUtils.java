package com.mfekim.testapinytimes.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Gives device information.
 */
public class NYTDeviceUtils {
    /** Tag for logs. */
    private static final String TAG = NYTDeviceUtils.class.getSimpleName();

    /**
     * @param context A context.
     * @return The screen dimension IN PIXELS.
     */
    public static Point getScreenDimension(Context context) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        return new Point(display.widthPixels, display.heightPixels);
    }

    /**
     * @param context A context.
     * @return The screen width IN PIXELS.
     */
    public static int getScreenWidth(Context context) {
        return getScreenDimension(context).x;
    }
}
