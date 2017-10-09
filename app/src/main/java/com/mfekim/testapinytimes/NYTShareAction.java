package com.mfekim.testapinytimes;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.util.Log;

import com.mfekim.testapinytimes.model.NYTArticle;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages article sharing.
 * <p>
 * You can share data with different texts on a few apps.
 */
public class NYTShareAction {
    /**
     * The managed share apps.
     * APP_NAME => APP_PACKAGE_NAME
     */
    private static final String EMAIL = "com.android.email";
    private static final String GMAIL = "com.google.android.gm";
    private static final String FACEBOOK = "com.facebook.katana";
    private static final String TWITTER = "com.twitter.android";
    private static final String SMS = "com.android.mms";
    private static final String LINKED_IN = "com.linkedin.android";
    private static final String GOOGLE_PLUS = "com.google.android.apps.plus";
    private static final String WHATS_APP = "com.whatsapp";

    /** Tag for logs. */
    private static final String TAG = NYTShareAction.class.getSimpleName();

    /** The share apps. */
    private List<String> mShareApps;

    /** Holder. */
    private static class SingletonHolder {
        /** Unique instance. */
        private final static NYTShareAction INSTANCE = new NYTShareAction();
    }

    /** Unique entry point to get the instance. */
    public static NYTShareAction getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Default constructor.
     */
    private NYTShareAction() {
        mShareApps = new ArrayList<>();
        mShareApps.add(EMAIL);
        mShareApps.add(GMAIL);
        mShareApps.add(FACEBOOK);
        mShareApps.add(TWITTER);
        mShareApps.add(SMS);
        mShareApps.add(LINKED_IN);
        mShareApps.add(GOOGLE_PLUS);
        mShareApps.add(WHATS_APP);
    }

    /**
     * Shows a list of share apps.
     *
     * @param activity Activity.
     * @param article  Data to share.
     */
    public void shareArticle(Activity activity, NYTArticle article) {
        if (article != null && activity != null && !activity.isFinishing()) {
            PackageManager packageManager = activity.getPackageManager();
            Resources resources = activity.getResources();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            List<LabeledIntent> shareIntentList = new ArrayList<>();
            for (ResolveInfo ri : packageManager.queryIntentActivities(intent, 0)) {
                String packageName = ri.activityInfo.packageName;

                Intent shareIntent = new Intent();
                shareIntent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                if (mShareApps.contains(packageName)) {
                    // EMAIL
                    if (packageName.contains(EMAIL)) {
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                                getExtraSubject(activity, EMAIL, article));
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                getExtraText(activity, EMAIL, article));
                    }
                    // TWITTER
                    if (packageName.contains(TWITTER)) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                getExtraText(activity, TWITTER, article));
                    }
                    // FACEBOOK
                    else if (packageName.contains(FACEBOOK)) {
                        // Warning: Facebook IGNORES our text. They say "These fields are intended
                        // for users to express themselves. Pre-filling these fields erodes the
                        // authenticity of the user voice."
                        // One workaround is to use the Facebook SDK to post, but that doesn't allow
                        // the user to choose how they want to share. We can also make a custom
                        // landing page, and the link will show the <meta content ="..."> text from
                        // that page with our link in Facebook.
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                getExtraText(activity, FACEBOOK, article));
                    }
                    // SMM
                    else if (packageName.contains(SMS)) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                getExtraText(activity, SMS, article));
                    }
                    // LINKED IN
                    else if (packageName.contains(LINKED_IN)) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                getExtraText(activity, LINKED_IN, article));
                    }
                    // GOOGLE PLUS
                    else if (packageName.equals(GOOGLE_PLUS)) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                getExtraText(activity, GOOGLE_PLUS, article));
                    }
                    // WHATS APP
                    else if (packageName.equals(WHATS_APP)) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                getExtraText(activity, WHATS_APP, article));
                    }
                    // GMAIL
                    else if (packageName.contains(GMAIL)) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                getExtraText(activity, GMAIL, article));
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                                getExtraSubject(activity, GMAIL, article));
                    }
                } else {
                    // Default text
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getExtraText(activity, null, article));
                }

                // Add share intent
                shareIntentList.add(new LabeledIntent(shareIntent, packageName,
                        ri.loadLabel(packageManager), ri.icon));
            }

            String shareAppChooserDialogTitle =
                    activity.getString(R.string.nyt_share_action_dialog_title);
            Intent shareAppChooser =
                    Intent.createChooser(shareIntentList.remove(0), shareAppChooserDialogTitle);
            shareAppChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    shareIntentList.toArray(new LabeledIntent[shareIntentList.size()]));
            activity.startActivity(shareAppChooser);
        } else {
            Log.d(TAG, "Failed to show the list of share apps");
        }
    }

    /**
     * Gets the subject to add if the app has a subject field such as Gmail.
     *
     * @param context  A context.
     * @param shareApp The app where the article will be shared.
     * @param article  The article to share.
     * @return A subject in string format.
     */
    protected String getExtraSubject(Context context, String shareApp, NYTArticle article) {
        if (article != null) {
            return context.getString(R.string.nyt_app_name) + " - " + article.optMainHeadline("");
        }

        return "";
    }

    /**
     * Gets the text to share.
     *
     * @param context  A context.
     * @param shareApp The app where the article will be shared.
     * @param article  The article to share.
     * @return A text.
     */
    protected String getExtraText(Context context, String shareApp, NYTArticle article) {
        return article != null ? article.getWebUrl() : "";
    }
}
