package com.noveo.dialogs.utils;

import android.os.Bundle;

import com.noveo.dialogs.models.Payload;

public class BundleUtils {
    private static final String TOKEN_CUSTOMER_KEY = "auth_customer_token";
    private static final String TOKEN_CUSTOMER_SECRET_KEY = "auth_customer_secret_token";
    private BundleUtils() {
        throw new UnsupportedOperationException();
    }

    public static void putPayload(final Bundle bundle, final Payload payload) {
        if (bundle != null && payload != null) {
            bundle.putSerializable(Payload.class.getName(), payload);
        }
    }

    public static Payload getPayload(final Bundle bundle) {
        if (bundle != null && bundle.containsKey(Payload.class.getName())) {
            return (Payload) bundle.getSerializable(Payload.class.getName());
        }
        return null;
    }

    public static void putCustomerToken(final Bundle bundle, final String token) {
        putString(bundle, token, TOKEN_CUSTOMER_KEY);
    }

    public static String getCustomerToken(final Bundle bundle) {
        return getString(bundle, TOKEN_CUSTOMER_KEY);
    }

    public static void putCustomerSecretToken(final Bundle bundle, final String token) {
        putString(bundle, token, TOKEN_CUSTOMER_SECRET_KEY);
    }

    public static String getCustomerSecretToken(final Bundle bundle) {
        return getString(bundle, TOKEN_CUSTOMER_SECRET_KEY);
    }

    private static void putString(final Bundle bundle, final String token, final String key) {
        if (bundle != null) {
            bundle.putString(key, token);
        }
    }

    private static String getString(final Bundle bundle, final String key) {
        if (bundle != null) {
            return bundle.getString(key, null);
        }

        return null;
    }
}
