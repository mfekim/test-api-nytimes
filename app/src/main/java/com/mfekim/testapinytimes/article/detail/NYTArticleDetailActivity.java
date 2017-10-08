package com.mfekim.testapinytimes.article.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.mfekim.testapinytimes.R;
import com.mfekim.testapinytimes.base.NYTBaseActivity;

/**
 * Shows the detail of an article.
 */
public class NYTArticleDetailActivity extends NYTBaseActivity {
    /** Extras. */
    public static final String EXTRA_ARTICLE_ID = "extra_article_id";

    /** Tag for logs. */
    private static final String TAG = NYTArticleDetailActivity.class.getSimpleName();

    /** Fragment Tag. */
    private static final String FRAGMENT_TAG = "article_detail_fragment_tag";

    /**
     * Launches the activity.
     *
     * @param context   Context.
     * @param articleId Article id.
     */
    public static void launchActivity(Context context, String articleId) {
        Intent intent = new Intent(context, NYTArticleDetailActivity.class);
        intent.putExtra(EXTRA_ARTICLE_ID, articleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nyt_activity_detail_article);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        String articleId = null;
        if (getIntent().hasExtra(EXTRA_ARTICLE_ID)) {
            articleId = getIntent().getStringExtra(EXTRA_ARTICLE_ID);
        } else {
            Log.e(TAG, "No article id found");
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nyt_activity_detail_article_fragment_container,
                            NYTArticleDetailFragment.newInstance(articleId), FRAGMENT_TAG)
                    .commit();
        }
    }
}
