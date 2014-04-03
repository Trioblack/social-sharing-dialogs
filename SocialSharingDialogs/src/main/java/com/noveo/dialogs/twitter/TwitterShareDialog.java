package com.noveo.dialogs.twitter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.noveo.dialogs.R;
import com.noveo.dialogs.utils.BundleUtils;
import com.noveo.dialogs.utils.MetadataUtils;
import com.noveo.dialogs.utils.PreferenceUtils;

import twitter4j.auth.AccessToken;

public class TwitterShareDialog extends DialogFragment {
    private static final String NAME_CONSUMER_KEY = "twitter_consumer_key";
    private static final String NAME_CONSUMER_SECRET_KEY = "twitter_consumer_secret_key";
    private static final String FRAGMENT_TAG = "twitter_share_dialog";

    private String consumerKey;
    private String consumerSecretKey;

    private Payload payload;
    public AccessToken accessToken4j;

    public TwitterShareDialog (){}

    public static TwitterShareDialog newInstance(Payload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("payload can't be null");
        }

        final TwitterShareDialog fragment = new TwitterShareDialog();
        final Bundle arguments = new Bundle();

        BundleUtils.putPayload(arguments, payload);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            payload = (Payload) BundleUtils.getPayload(arguments);
        }

        consumerKey = MetadataUtils.getMetaString(getActivity(), NAME_CONSUMER_KEY);
        consumerSecretKey = MetadataUtils.getMetaString(getActivity(), NAME_CONSUMER_SECRET_KEY);

        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecretKey)) {
            throw new RuntimeException("consumerKey and consumerSecretKey must be set in <meta-data> in AndroidManifest.xml");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View rootView = inflater.inflate(R.layout.twitter_share_dialog_layout, container, false);
        accessToken4j = PreferenceUtils.restoreTwitterAccessToken(getActivity());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            if (accessToken4j == null) {
                final Fragment fragment = WebViewFragment.newInstance(consumerKey, consumerSecretKey, payload);
                fragmentTransaction.add(R.id.fragment_container, fragment);
            } else {
                final Fragment fragment = UpdateStatusFragment.newInstance(consumerKey, consumerSecretKey, payload);
                fragmentTransaction.add(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public void show(final FragmentManager manager, final String tag) {
        final Fragment fragment = manager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            manager.beginTransaction()
                    .remove(fragment)
                    .commit();
            manager.executePendingTransactions();
        }
        super.show(manager, tag != null ? tag : FRAGMENT_TAG);
    }

    @Override
    public int show(final FragmentTransaction transaction, final String tag) {
        return super.show(transaction, tag != null ? tag : FRAGMENT_TAG);
    }

    public static void logout(final Context context) {
        PreferenceUtils.saveTwitterAccessToken(context, null);
    }

    public static final class Payload implements com.noveo.dialogs.models.Payload {
        private String message;
        private String link;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getStatus() {
            StringBuilder builder = new StringBuilder();
            builder.append(message);
            if (!TextUtils.isEmpty(message)) { builder.append(" "); }
            builder.append(link);

            return builder.toString();
        }
    }
}
