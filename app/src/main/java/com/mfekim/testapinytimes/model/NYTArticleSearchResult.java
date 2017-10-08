package com.mfekim.testapinytimes.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data structure of the result of the Article Search API call.
 */
public class NYTArticleSearchResult {
    /** Tag for logs. */
    private static final String TAG = NYTArticleSearchResult.class.getSimpleName();

    /** GSON. */
    private static final Gson GSON = new Gson();

    @SerializedName("response")
    private NYTArticleSearchResponse mResponse;

    /**
     * @return A list of {@link NYTArticle}, null otherwise.
     */
    public List<NYTArticle> getArticles() {
        return mResponse != null ? mResponse.getArticles() : null;
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
