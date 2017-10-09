package com.mfekim.testapinytimes.model;

import android.text.TextUtils;

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
    private List<NYTArticleMultimedia> mMultimediaList;

    @SerializedName("snippet")
    private String mSnippet;

    @SerializedName("web_url")
    private String mWebUrl;

    /**
     * @param defaultValue Default value if the data is null or empty.
     * @return The main headline, default value passed as parameter otherwise.
     */
    public String optMainHeadline(String defaultValue) {
        return mHeadline != null ? mHeadline.optMain(defaultValue) : defaultValue;
    }

    /**
     * @param defaultValue Default value if the data is null or empty.
     * @return The snippet, default value passed as parameter otherwise.
     */
    public String optSnippet(String defaultValue) {
        return TextUtils.isEmpty(mSnippet) ? defaultValue : mSnippet;
    }

    /**
     * @return The Id.
     */
    public String getId() {
        return mId;
    }

    /**
     * @return The web url.
     */
    public String getWebUrl() {
        return mWebUrl;
    }

    /**
     * @return The article thumbnail URL, null otherwise.
     */
    public String getThumbnailUrl() {
        if (mMultimediaList != null) {
            for (NYTArticleMultimedia multimedia : mMultimediaList) {
                if (multimedia.isThumbnail()) {
                    return multimedia.getUrl();
                }
            }
        }

        return null;
    }

    /**
     * @return An image xlarge, null otherwise.
     */
    public NYTArticleMultimedia getImageXLarge() {
        if (mMultimediaList != null) {
            for (NYTArticleMultimedia multimedia : mMultimediaList) {
                if (multimedia.isXLarge()) {
                    return multimedia;
                }
            }
        }

        return null;
    }
}
