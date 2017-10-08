package com.mfekim.testapinytimes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mfekim.testapinytimes.api.NYTClientAPI;
import com.mfekim.testapinytimes.base.NYTBaseFragment;
import com.mfekim.testapinytimes.model.NYTArticle;
import com.mfekim.testapinytimes.model.NYTArticleSearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a list of articles.
 */
public class NYTArticleListFragment extends NYTBaseFragment {
    /** Tag for logs. */
    private static final String TAG = NYTArticleListFragment.class.getSimpleName();

    /** Request tag. */
    private static final String REQUEST_TAG = "article_list_request_tag";

    /** Views. */
    private RecyclerView mRvList;
    private View mVLoader;
    private View mTvNoDataMsg;

    /** Adapter. */
    private RecyclerView.Adapter<NYTArticleListAdapter.ViewHolder> mAdapter;

    /** Items. */
    private List<NYTArticle> mArticles = new ArrayList<>();

    /** Page. */
    private int mCurrentPage = 0;

    /**
     * @return A new instance of {@link NYTArticleListFragment}.
     */
    public static NYTArticleListFragment newInstance() {
        return new NYTArticleListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nyt_fragment_list_article, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Loader
        mVLoader = view.findViewById(R.id.nyt_fragment_list_article_loader);
        mVLoader.setVisibility(View.VISIBLE);

        // No data msg
        mTvNoDataMsg = view.findViewById(R.id.nyt_fragment_list_article_no_data_msg);

        // Recycler view
        mRvList = view.findViewById(R.id.nyt_fragment_list_article_recycler_view);
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchArticles();
    }

    /**
     * Fetches articles.
     */
    private void fetchArticles() {
        onFetchStarting();
        NYTClientAPI.getInstance().fetchArticles(getContext(), REQUEST_TAG,
                new Response.Listener<NYTArticleSearchResult>() {
                    @Override
                    public void onResponse(NYTArticleSearchResult result) {
                        List<NYTArticle> articles = result.getArticles();
                        if (articles != null && !articles.isEmpty()) {
                            mArticles.addAll(articles);
                            if (mCurrentPage == 0) {
                                mAdapter = new NYTArticleListAdapter();
                                mRvList.setAdapter(mAdapter);
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }
                            onFetchSucceeded();
                        } else {
                            onFetchFailed();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO
                    }
                });
    }

    /**
     * Called when fetching articles is starting.
     */
    private void onFetchStarting() {
        mVLoader.setVisibility(mArticles != null && !mArticles.isEmpty() ?
                View.INVISIBLE : View.VISIBLE);
    }

    /**
     * Called when fetching articles succeeded.
     */
    private void onFetchSucceeded() {
        onFetchFinished();
    }

    /**
     * Called when fetching articles failed.
     */
    private void onFetchFailed() {
        onFetchFinished();
    }

    /**
     * Called when fetching articles finished.
     */
    private void onFetchFinished() {
        mVLoader.setVisibility(View.INVISIBLE);
        mTvNoDataMsg.setVisibility(mArticles != null && !mArticles.isEmpty() ?
                View.INVISIBLE : View.VISIBLE);
    }

    /**
     * Manages article list.
     */
    /*package*/ class NYTArticleListAdapter extends
            RecyclerView.Adapter<NYTArticleListAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity())
                                          .inflate(R.layout.nyt_item_list_article, null);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            NYTArticle article = mArticles.get(position);

            // Image TODO

            // Title
            String title = article.optMainHeadline(null);
            holder.tvTitle.setText(title);
            holder.tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);

            // Divider
            holder.vDivider.setVisibility(position == getItemCount() - 1 ?
                    View.GONE : View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return mArticles.size();
        }

        /**
         * View holder.
         */
        /*package*/ class ViewHolder extends RecyclerView.ViewHolder {
            /* Views. */
            ImageView image;
            TextView tvTitle;
            View vDivider;

            /** {@inheritDoc} */
            public ViewHolder(View itemView) {
                super(itemView);

                // Image
                image = itemView.findViewById(R.id.nyt_item_list_article_image);

                // Title
                tvTitle = itemView.findViewById(R.id.nyt_item_list_article_title);

                // Divider
                vDivider = itemView.findViewById(R.id.nyt_item_list_article_divider);
            }
        }
    }
}
