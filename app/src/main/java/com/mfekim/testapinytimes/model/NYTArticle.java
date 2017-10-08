package com.mfekim.testapinytimes.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data structure of a NYT article.
 */
public class NYTArticle {
    /** Tag for logs. */
    private static final String TAG = NYTArticle.class.getSimpleName();

    @SerializedName("_id")
    private String mId;

    @SerializedName("multimedia")
    private List<NYTMultimedia> mMultimediaList;
}
