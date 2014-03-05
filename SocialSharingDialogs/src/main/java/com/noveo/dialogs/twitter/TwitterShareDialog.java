package com.noveo.dialogs.twitter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.noveo.dialogs.R;
import com.noveo.dialogs.utils.PreferenceUtils;

import twitter4j.auth.AccessToken;

public class TwitterShareDialog extends DialogFragment {
    private static final String CONSUMER_KEY = "C5Lqexj9p0yHaJuCUoeuQ";
    private static final String CONSUMER_SECRET= "mO2NZMvVpsU33NbnKiPcrlfFqghjm7sN0kKUjpW2k";
    private static final String FRAGMENT_TAG = TwitterShareDialog.class.getName();
    public AccessToken accessToken4j;

    public static TwitterShareDialog newInstance() {
        final TwitterShareDialog fragment = new TwitterShareDialog();
        return fragment;
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
                fragmentTransaction.add(R.id.fragment_container, WebViewFragment.newInstance(CONSUMER_KEY, CONSUMER_SECRET));
            } else {
                fragmentTransaction.add(R.id.fragment_container, UpdateStatusFragment.newInstance(CONSUMER_KEY, CONSUMER_SECRET));
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
}
