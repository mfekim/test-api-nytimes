package com.mfekim.testapinytimes.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data structure of a multimedia (i.e photo, video, etc).
 */
public class NYTArticleMultimedia {
    /** Tag for logs. */
    private static final String TAG = NYTArticleMultimedia.class.getSimpleName();

    /** Multimedia Types. */
    private static final String TYPE_IMAGE = "image";

    /** Image Types. */
    private static final String TYPE_IMAGE_THUMBNAIL = "thumbnail";

    @SerializedName("type")
    private String mType;

    @SerializedName("subtype")
    private String mSubtype;

    @SerializedName("url")
    private String mUrl;

    /**
     * @return True if it is a thumbnail, false otherwise.
     */
    public boolean isThumbnail() {
        return isImage() && mSubtype != null && mSubtype.equals(TYPE_IMAGE_THUMBNAIL);
    }

    /**
     * @return True if it is an image, false otherwise.
     */
    public boolean isImage() {
        return mType != null && mType.equals(TYPE_IMAGE);
    }

    /**
     * @return Thr URL.
     */
    public String getUrl() {
        return mUrl;
    }
}
