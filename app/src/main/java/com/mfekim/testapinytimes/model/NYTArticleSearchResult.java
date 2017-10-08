package com.mfekim.testapinytimes.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Data structure of the result of the article search API call.
 */
public class NYTArticleSearchResult {
    /** Tag for logs. */
    private static final String TAG = NYTArticleSearchResult.class.getSimpleName();

    /** GSON. */
    private static final Gson GSON = new Gson();

    @SerializedName("response")
    private NYTArticleSearchResponse mResponse;

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
