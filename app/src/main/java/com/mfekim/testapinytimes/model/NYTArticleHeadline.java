package com.mfekim.testapinytimes.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Data structure of an article headline.
 */
public class NYTArticleHeadline {
    /** Tag for logs. */
    private static final String TAG = NYTArticleHeadline.class.getSimpleName();

    @SerializedName("main")
    private String mMain;

    /**
     * @param defaultValue Default value if the data is null or empty.
     * @return The main value, default value passed as parameter otherwise.
     */
    public String optMain(String defaultValue) {
        return TextUtils.isEmpty(mMain) ? defaultValue : mMain;
    }
}
