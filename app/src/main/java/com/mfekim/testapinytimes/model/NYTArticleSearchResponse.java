package com.mfekim.testapinytimes.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data structure of a response of the Article Search API.
 */
public class NYTArticleSearchResponse {
    /** Tag for logs. */
    private static final String TAG = NYTArticleSearchResponse.class.getSimpleName();

    @SerializedName("docs")
    private List<NYTArticle> mArticles;
}
