package com.mfekim.testapinytimes.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mfekim.testapinytimes.api.request.NYTGsonRequest;
import com.mfekim.testapinytimes.model.NYTArticleSearchResult;
import com.mfekim.testapinytimes.network.NYTNetworkClient;
import com.mfekim.testapinytimes.utils.NYTLogUtils;

/**
 * Client API.
 */
public class NYTClientAPI {
    /** Tag for logs. */
    private static final String TAG = NYTClientAPI.class.getSimpleName();

    /** Host. */
    private static final String HOST = "https://api.nytimes.com";

    /** API Key. */
    private static final String API_KEY = "a510ddc3d91f44a784f3ce7f20182bc8";

    /** The url of the article search API. */
    private static final String ARTICLE_SEARCH_API_URL = HOST + "/svc/search/v2/articlesearch.json";

    /** Holder. */
    private static class SingletonHolder {
        /** Unique instance. */
        private final static NYTClientAPI INSTANCE = new NYTClientAPI();
    }

    /** Unique entry point to get the instance. */
    public static NYTClientAPI getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /** Private constructor. */
    private NYTClientAPI() {
    }

    /**
     * Fetches articles.
     *
     * @param context       Context.
     * @param tag           Request tag.
     * @param listener      Listener for success.
     * @param errorListener Listener for error.
     */
    public void fetchArticles(Context context, String tag,
                              final Response.Listener<NYTArticleSearchResult> listener,
                              final Response.ErrorListener errorListener) {
        String url = NYTLogUtils.logUrl(getArticleSearchApiUrl());
        NYTGsonRequest<NYTArticleSearchResult> request =
                new NYTGsonRequest<>(url, NYTArticleSearchResult.class, null,
                        new Response.Listener<NYTArticleSearchResult>() {
                            @Override
                            public void onResponse(NYTArticleSearchResult result) {
                                Log.d(TAG, "Fetch articles SUCCESS\n" + result.toString());
                                if (listener != null) {
                                    listener.onResponse(result);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Fetch articles ERROR - " + error.getLocalizedMessage());
                                if (errorListener != null) {
                                    onErrorResponse(error);
                                }
                            }
                        });
        request.setTag(tag);
        NYTNetworkClient.getInstance().addToRequestQueue(context, request);
    }

    /**
     * Gets a ready to use article search api URL.
     *
     * @return The article search API url.
     */
    private String getArticleSearchApiUrl() {
        return ARTICLE_SEARCH_API_URL + "?api-key=" + API_KEY;
    }
}
