package com.mfekim.testapinytimes.article.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.mfekim.testapinytimes.R;
import com.mfekim.testapinytimes.base.NYTBaseActivity;

/**
 * Shows a list of articles.
 */
public class NYTArticleListActivity extends NYTBaseActivity {
    /** Tag for logs. */
    private static final String TAG = NYTArticleListActivity.class.getSimpleName();

    /** Fragment Tag. */
    private static final String FRAGMENT_TAG = "article_list_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nyt_activity_list_article);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nyt_activity_list_article_fragment_container,
                            NYTArticleListFragment.newInstance(), FRAGMENT_TAG)
                    .commit();
        }
    }
}
