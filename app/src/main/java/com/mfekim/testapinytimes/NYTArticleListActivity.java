package com.mfekim.testapinytimes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mfekim.testapinytimes.api.NYTClientAPI;

public class NYTArticleListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nyt_activity_main);

        NYTClientAPI.getInstance().fetchArticles(this, null, null, null);
    }
}
