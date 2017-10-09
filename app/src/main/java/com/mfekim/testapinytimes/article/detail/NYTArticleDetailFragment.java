package com.mfekim.testapinytimes.article.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mfekim.testapinytimes.article.NYTShareArticle;
import com.mfekim.testapinytimes.base.NYTBaseFragment;
import com.mfekim.testapinytimes.model.NYTArticle;
import com.mfekim.testapinytimes.model.NYTArticleMultimedia;
import com.mfekim.testapinytimes.model.NYTArticleSearchResult;
import com.mfekim.testapinytimes.network.NYTNetworkClient;
import com.mfekim.testapinytimes.utils.NYTDeviceUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Show the detail of an article.
 */
public class NYTArticleDetailFragment extends NYTBaseFragment {
    /** Arguments. */
    public static final String ARG_ARTICLE_ID = "arg_article_id";

    /** Tag for logs. */
    private static final String TAG = NYTArticleDetailFragment.class.getSimpleName();

    /** Request tag. */
    private static final String REQUEST_TAG = "article_list_request_tag";

    /** Views. */
    private View mVLoader;
    private View mTvErrorMsg;
    private ViewGroup mVgMain;
    private ImageView mImgImage;
    private TextView mTvHeadline;
    private TextView mTvSnippet;
    private View mVShare;

    /** Article Id. */
    private String mArticleId;

    /** Article. */
    private NYTArticle mArticle;

    /** Needed fields to have into the API response. */
    private List<String> mFields;

    /**
     * @param articleId Article id.
     * @return A new instance of {@link NYTArticleDetailFragment}.
     */
    public static NYTArticleDetailFragment newInstance(String articleId) {
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, articleId);

        NYTArticleDetailFragment fragment = new NYTArticleDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mFields = new ArrayList<>();
        mFields.add(NYTClientAPI.FIELD_ID);
        mFields.add(NYTClientAPI.FIELD_HEADLINE);
        mFields.add(NYTClientAPI.FIELD_MULTIMEDIA);
        mFields.add(NYTClientAPI.FIELD_SNIPPET);
        mFields.add(NYTClientAPI.FIELD_WEB_URL);

        Bundle args = getArguments();
        if (args.containsKey(ARG_ARTICLE_ID)) {
            mArticleId = args.getString(ARG_ARTICLE_ID);
        } else {
            Log.e(TAG, "No article id found");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nyt_fragment_detail_article, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Views
        mVgMain = view.findViewById(R.id.nyt_fragment_detail_article_main);
        mImgImage = view.findViewById(R.id.nyt_fragment_detail_article_image);
        mTvHeadline = view.findViewById(R.id.nyt_fragment_detail_article_headline);
        mTvSnippet = view.findViewById(R.id.nyt_fragment_detail_article_snippet);
        mVLoader = view.findViewById(R.id.nyt_fragment_detail_article_loader);
        mTvErrorMsg = view.findViewById(R.id.nyt_fragment_detail_article_error_msg);
        mVShare = view.findViewById(R.id.nyt_fragment_detail_article_share_button);

        // Share
        mVShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mArticle != null) {
                    NYTShareArticle.getInstance().shareArticle(getActivity(), mArticle);
                } else {
                    // TODO
                }
            }
        });

        if (mArticle != null) {
            bindData(mArticle);
        } else {
            fetch();
        }
    }


    @Override
    public void onDestroyView() {
        NYTNetworkClient.getInstance().cancelAllRequest(getContext(), REQUEST_TAG);
        super.onDestroyView();
    }

    //region Fetching

    /**
     * Fetches article.
     */
    private void fetch() {
        onFetchStarting();
        NYTClientAPI.getInstance().fetchArticle(getContext(), mArticleId, mFields,
                new Response.Listener<NYTArticleSearchResult>() {
                    @Override
                    public void onResponse(NYTArticleSearchResult result) {
                        List<NYTArticle> articles = result.getArticles();
                        onFetchSucceeded(articles != null && !articles.isEmpty() ?
                                articles.get(0) : null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onFetchError();
                    }
                }, REQUEST_TAG);
    }

    /**
     * Called when fetching article is starting.
     */
    private void onFetchStarting() {
        Log.d(TAG, "Fetch article " + mArticleId + " is starting");

        mVLoader.setVisibility(View.VISIBLE);
        mTvErrorMsg.setVisibility(View.INVISIBLE);
    }

    /**
     * Called when fetching article succeeded.
     */
    private void onFetchSucceeded(NYTArticle article) {
        Log.d(TAG, "Fetch article " + mArticleId + "SUCCESS");

        bindData(article);
        onFetchFinished();
    }

    /**
     * Called when fetching article failed.
     */
    private void onFetchError() {
        Log.d(TAG, "Fetch article " + mArticleId + " ERROR");

        mTvErrorMsg.setVisibility(View.VISIBLE);

        onFetchFinished();
    }

    /**
     * Called when fetching article finished.
     */
    private void onFetchFinished() {
        mVLoader.setVisibility(View.INVISIBLE);
    }
    //endregion

    /**
     * Binds data.
     *
     * @param article Article.
     */
    private void bindData(NYTArticle article) {
        mArticle = article;

        if (mArticle != null) {
            // Image
            NYTArticleMultimedia image = article.getImageXLarge();
            if (image != null) {
                mImgImage.setVisibility(View.VISIBLE);

                int deviceWidth = NYTDeviceUtils.getScreenWidth(getContext());
                mImgImage.getLayoutParams().width = deviceWidth;
                mImgImage.getLayoutParams().height = (int) (deviceWidth / image.getRatio());

                Picasso.with(getContext())
                       .load(NYTClientAPI.getInstance().buildImageUrl(image.getUrl()))
                       .into(mImgImage, new Callback() {
                           @Override
                           public void onSuccess() {
                               Log.d(TAG, "Load image SUCCESS");
                           }

                           @Override
                           public void onError() {
                               Log.e(TAG, "Load image ERROR");
                           }
                       });
            } else {
                mImgImage.setVisibility(View.GONE);
            }

            // Headline
            String headline = article.optMainHeadline(null);
            mTvHeadline.setText(headline);
            mTvHeadline.setVisibility(TextUtils.isEmpty(headline) ?
                    View.GONE : View.VISIBLE);

            // Snippet
            mTvSnippet.setText(article.optSnippet(""));

            mVgMain.setVisibility(View.VISIBLE);
            mVShare.setVisibility(View.VISIBLE);
        } else {
            mVgMain.setVisibility(View.INVISIBLE);
            mVShare.setVisibility(View.INVISIBLE);
            mTvErrorMsg.setVisibility(View.VISIBLE);
        }
    }
}
