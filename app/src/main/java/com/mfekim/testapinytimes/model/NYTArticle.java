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

    @SerializedName("headline")
    private NYTArticleHeadline mHeadline;

    @SerializedName("multimedia")
    private List<NYTMultimedia> mMultimediaList;

    /**
     * @param defaultValue Default value if the data is null or empty.
     * @return The main headline, default value passed as parameter otherwise.
     */
    public String optMainHeadline(String defaultValue) {
        return mHeadline != null ? mHeadline.optMain(defaultValue) : defaultValue;
    }
}
