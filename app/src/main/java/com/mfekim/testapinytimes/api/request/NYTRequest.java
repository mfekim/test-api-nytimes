package com.mfekim.testapinytimes.api.request;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;

/**
 * Base request.
 */
public abstract class NYTRequest<T> extends Request<T> {
    /** Tag for logs. */
    private static final String TAG = NYTRequest.class.getSimpleName();

    /** Timeout value in seconds. */
    private static final int TIME_OUT_SECONDS = 30;

    /**
     * Gets a custom retry policy.
     * Allows to set the timeout.
     *
     * @return A {@link RetryPolicy} instance.
     */
    public static RetryPolicy getCustomRetryPolicy() {
        return new DefaultRetryPolicy(TIME_OUT_SECONDS * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    /** {@inheritDoc} */
    protected NYTRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
        setRetryPolicy(getCustomRetryPolicy());
    }
}
