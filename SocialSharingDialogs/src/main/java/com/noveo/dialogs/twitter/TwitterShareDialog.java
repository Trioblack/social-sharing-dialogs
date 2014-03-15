package com.noveo.dialogs.twitter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.noveo.dialogs.R;
import com.noveo.dialogs.utils.BundleUtils;
import com.noveo.dialogs.utils.PreferenceUtils;

import twitter4j.auth.AccessToken;

public class TwitterShareDialog extends DialogFragment {
    private static final String CONSUMER_KEY = "C5Lqexj9p0yHaJuCUoeuQ";
    private static final String CONSUMER_SECRET = "mO2NZMvVpsU33NbnKiPcrlfFqghjm7sN0kKUjpW2k";
    private static final String FRAGMENT_TAG = TwitterShareDialog.class.getName();
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
                final Fragment fragment = WebViewFragment.newInstance(CONSUMER_KEY, CONSUMER_SECRET, payload);
                fragmentTransaction.add(R.id.fragment_container, fragment);
            } else {
                final Fragment fragment = UpdateStatusFragment.newInstance(CONSUMER_KEY, CONSUMER_SECRET, payload);
                fragmentTransaction.add(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        }
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
