package com.noveo.dialogs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import twitter4j.auth.AccessToken;

public class PreferenceUtils {
    private static final String PREFERENCE_TAG = "twitter_preference";
    private static final String KEY_TAG = "twitter_key";
    private static final String SECRET_KEY_TAG = "twitter_secret_key";

    private PreferenceUtils() {
        throw new UnsupportedOperationException();
    }

    public static void saveTwitterAccessToken(final Context context, final AccessToken token) {
        Log.d("TEST_PREFERENCE", "save");
        if (context != null) {
            final SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_TAG, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            if (token != null) {
                editor.putString(KEY_TAG, token.getToken());
                editor.putString(SECRET_KEY_TAG, token.getTokenSecret());
            } else {
                editor.putString(KEY_TAG, null);
                editor.putString(SECRET_KEY_TAG, null);
            }

            editor.commit();
        }
    }

    public static AccessToken restoreTwitterAccessToken(final Context context) {
        Log.d("TEST_PREFERENCE", "restore");
        if  (context != null) {
            final SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_TAG, Context.MODE_PRIVATE);
            final String token = preferences.getString(KEY_TAG, null);
            final String secretToken = preferences.getString(SECRET_KEY_TAG, null);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(secretToken)) {
                return new AccessToken(token, secretToken);
            }
        }
        return null;
    }
}
