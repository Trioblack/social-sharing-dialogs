package com.noveo.dialogs.twitter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.noveo.dialogs.R;
import com.noveo.dialogs.utils.PreferenceUtils;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class UpdateStatusFragment extends Fragment {
    public EditText statusEditText;
    public Button sendButton;
    public AccessToken accessToken4j;

    private static final String CONSUMER_KEY_TAG = "consumer_key_tag";
    private static final String CONSUMER_SECRET_KEY_TAG = "consumer_secret_key_tag";

    public String consumerKey;
    public String consumerSecretKey;

    public static UpdateStatusFragment newInstance(final String consumerKey, final String consumerSecretKey) {
        UpdateStatusFragment fragment = new UpdateStatusFragment();
        Bundle args = new Bundle();
        args.putString(CONSUMER_KEY_TAG, consumerKey);
        args.putString(CONSUMER_SECRET_KEY_TAG, consumerSecretKey);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.update_status_layout, container, false);
        sendButton = (Button) rootView.findViewById(R.id.send_button);
        statusEditText = (EditText) rootView.findViewById(R.id.status_text);
        accessToken4j = PreferenceUtils.restoreTwitterAccessToken(getActivity());

        if (getArguments() != null) { // TODO : Add check exists
            consumerKey = getArguments().getString(CONSUMER_KEY_TAG);
            consumerSecretKey = getArguments().getString(CONSUMER_SECRET_KEY_TAG);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        updateStatus();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (getParentFragment() instanceof  TwitterShareDialog) {
                            ((TwitterShareDialog) getParentFragment()).dismiss();
                        }
                    }
                }.execute();
            }
        });
    }

    private void updateStatus() {
        String message = null;
        if (statusEditText != null && !TextUtils.isEmpty(statusEditText.getText()) ) {
            message = statusEditText.getText().toString();
        }

        if (!TextUtils.isEmpty(message) && accessToken4j != null) {
            Twitter twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(consumerKey, consumerSecretKey);
            twitter.setOAuthAccessToken(accessToken4j);

            try {
                Status status = twitter.updateStatus(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }

        }
    }
}