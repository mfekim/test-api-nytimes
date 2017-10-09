package com.mfekim.testapinytimes.article.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mfekim.testapinytimes.R;
import com.mfekim.testapinytimes.api.NYTClientAPI;
import com.mfekim.testapinytimes.article.detail.NYTArticleDetailActivity;
import com.mfekim.testapinytimes.base.NYTBaseFragment;
import com.mfekim.testapinytimes.model.NYTArticle;
import com.mfekim.testapinytimes.model.NYTArticleSearchResult;
import com.mfekim.testapinytimes.network.NYTNetworkClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
    private RecyclerView.Adapter<NYTArticleListAdapter.NYTViewHolder> mAdapter;

    /** Layout Manager. */
    private RecyclerView.LayoutManager mLayoutManager;

    /** Articles. */
    private List<NYTArticle> mArticles = new ArrayList<>();

    /** Page. */
    private int mCurrentPage = 0;

    /** Flags. */
    private boolean mIsFetching;
    private boolean mFetchNextPage;

    /**
     * How many entries earlier to start loading more
     * Example:
     * <t/>Total item count: 20
     * <t/>{@link #mVisibleThreshold}: 7
     * <t/>We will load more item when the item 13 will be visible.
     */
    private int mVisibleThreshold = 4;

    /** Needed fields to have into the API response. */
    private List<String> mFields;

    /**
     * @return A new instance of {@link NYTArticleListFragment}.
     */
    public static NYTArticleListFragment newInstance() {
        return new NYTArticleListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mFields = new ArrayList<>();
        mFields.add(NYTClientAPI.FIELD_ID);
        mFields.add(NYTClientAPI.FIELD_HEADLINE);
        mFields.add(NYTClientAPI.FIELD_MULTIMEDIA);
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

        // No data msg
        mTvNoDataMsg = view.findViewById(R.id.nyt_fragment_list_article_no_data_msg);

        // Recycler view
        mRvList = view.findViewById(R.id.nyt_fragment_list_article_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRvList.setLayoutManager(mLayoutManager);
        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager instanceof LinearLayoutManager) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int firstVisibleItemPosition = ((LinearLayoutManager) mLayoutManager)
                            .findFirstVisibleItemPosition();

                    if (!mIsFetching && mFetchNextPage && visibleItemCount != 0 &&
                            (totalItemCount - visibleItemCount) <=
                                    (firstVisibleItemPosition + mVisibleThreshold)) {
                        fetchMore();
                    }
                }
            }
        });

        if (mArticles.isEmpty()) {
            fetch(0);
        } else {
            mRvList.setAdapter(mAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        NYTNetworkClient.getInstance().cancelAllRequest(getContext(), REQUEST_TAG);
        super.onDestroyView();
    }

    //region Fetching

    /**
     * Fetches articles.
     *
     * @param page Page index.
     */
    private void fetch(final int page) {
        onFetchStarting(page);
        NYTClientAPI.getInstance().fetchArticles(getContext(), page, mFields,
                new Response.Listener<NYTArticleSearchResult>() {
                    @Override
                    public void onResponse(NYTArticleSearchResult result) {
                        onFetchSucceeded(page, result.getArticles());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onFetchError(page);
                    }
                }, REQUEST_TAG);
    }

    /**
     * Called when fetching articles is starting.
     *
     * @param page Page index.
     */
    private void onFetchStarting(int page) {
        Log.d(TAG, "Fetch articles page " + page + " is starting");

        mIsFetching = true;
        mVLoader.setVisibility(mArticles != null && !mArticles.isEmpty() ?
                View.INVISIBLE : View.VISIBLE);
        mTvNoDataMsg.setVisibility(View.INVISIBLE);
    }

    /**
     * Called when fetching articles succeeded.
     *
     * @param page Page index.
     */
    private void onFetchSucceeded(int page, List<NYTArticle> articles) {
        Log.d(TAG, "Fetch articles page " + page + " SUCCESS");

        mCurrentPage = page;

        if (mCurrentPage == 0) {
            mArticles.addAll(articles);
            mAdapter = new NYTArticleListAdapter();
            mRvList.setAdapter(mAdapter);
        } else {
            // Remove load more item
            mArticles.remove(mArticles.size() - 1);
            mAdapter.notifyItemRemoved(mArticles.size());

            // Add new articles
            int oldArticlesSize = mArticles.size();
            mArticles.addAll(articles);
            mAdapter.notifyItemRangeInserted(oldArticlesSize, articles.size());
        }

        mFetchNextPage = !articles.isEmpty();

        onFetchFinished(page);
    }

    /**
     * Called when fetching articles failed.
     *
     * @param page Page index.
     */
    private void onFetchError(int page) {
        Log.d(TAG, "Fetch articles page " + page + " ERROR");

        onFetchFinished(page);
    }

    /**
     * Called when fetching articles finished.
     *
     * @param page Page index.
     */
    private void onFetchFinished(int page) {
        mVLoader.setVisibility(View.INVISIBLE);
        mTvNoDataMsg.setVisibility(mArticles != null && !mArticles.isEmpty() ?
                View.INVISIBLE : View.VISIBLE);
        mIsFetching = false;
    }

    /**
     * Fetches more articles.
     */
    private void fetchMore() {
        // Add load more item
        mArticles.add(null);
        mAdapter.notifyItemInserted(mArticles.size() - 1);

        fetch(mCurrentPage + 1);
    }
    //endregion

    /**
     * Manages article list.
     */
    /*package*/ class NYTArticleListAdapter extends
            RecyclerView.Adapter<NYTArticleListAdapter.NYTViewHolder> {
        /** Types. */
        private static final int VIEW_TYPE_ITEM_DEFAULT = 0;
        private static final int VIEW_TYPE_ITEM_LOAD_MORE = 1;

        @Override
        public NYTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM_LOAD_MORE) {
                return new NYTViewHolderLoadMore(LayoutInflater
                        .from(getActivity())
                        .inflate(R.layout.nyt_item_list_article_load_more, null));
            } else {
                return new NYTViewHolderDefault(LayoutInflater
                        .from(getActivity())
                        .inflate(R.layout.nyt_item_list_article_default, null));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mArticles.get(position) != null ?
                    VIEW_TYPE_ITEM_DEFAULT : VIEW_TYPE_ITEM_LOAD_MORE;
        }

        @Override
        public void onBindViewHolder(NYTViewHolder holder, int position) {
            int itemViewType = getItemViewType(position);

            if (itemViewType == VIEW_TYPE_ITEM_DEFAULT) {
                NYTViewHolderDefault viewHolderDefault = (NYTViewHolderDefault) holder;
                NYTArticle article = mArticles.get(position);

                // Image
                String thumbnailUrl = article.getThumbnailUrl();
                if (!TextUtils.isEmpty(thumbnailUrl)) {
                    Picasso.with(getContext())
                           .load(NYTClientAPI.getInstance().buildImageUrl(thumbnailUrl))
                           .into(viewHolderDefault.imgImage, new Callback() {
                               @Override
                               public void onSuccess() {
                                   Log.d(TAG, "Image loading SUCCESS");
                               }

                               @Override
                               public void onError() {
                                   Log.e(TAG, "Image loading ERROR");
                               }
                           });
                    viewHolderDefault.imgImage.setVisibility(View.VISIBLE);
                } else {
                    viewHolderDefault.imgImage.setVisibility(View.GONE);
                }

                // Headline
                String headline = article.optMainHeadline(null);
                viewHolderDefault.tvHeadline.setText(headline);
                viewHolderDefault.tvHeadline.setVisibility(TextUtils.isEmpty(headline) ?
                        View.GONE : View.VISIBLE);

                // Divider
                viewHolderDefault.vDivider.setVisibility(position == getItemCount() - 1 ?
                        View.GONE : View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mArticles.size();
        }

        /**
         * View holder.
         */
        /*package*/ abstract class NYTViewHolder extends RecyclerView.ViewHolder {
            /** {@inheritDoc} */
            public NYTViewHolder(View itemView) {
                super(itemView);
            }
        }

        /**
         * View holder for default item.
         */
        /*package*/ class NYTViewHolderDefault extends NYTViewHolder {
            /* Views. */
            ImageView imgImage;
            TextView tvHeadline;
            View vDivider;

            /** {@inheritDoc} */
            public NYTViewHolderDefault(View itemView) {
                super(itemView);

                // Click
                itemView.findViewById(R.id.nyt_item_list_article_default_main)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                NYTArticleDetailActivity.launchActivity(getActivity(),
                                        mArticles.get(getAdapterPosition()).getId());
                            }
                        });

                // Image
                imgImage = itemView.findViewById(R.id.nyt_item_list_article_default_image);

                // Headline
                tvHeadline = itemView.findViewById(R.id.nyt_item_list_article_default_headline);

                // Divider
                vDivider = itemView.findViewById(R.id.nyt_item_list_article_default_divider);
            }
        }

        /**
         * View holder for load more item.
         */
        /*package*/ class NYTViewHolderLoadMore extends NYTViewHolder {
            /** {@inheritDoc} */
            public NYTViewHolderLoadMore(View itemView) {
                super(itemView);
            }
        }
    }
}
