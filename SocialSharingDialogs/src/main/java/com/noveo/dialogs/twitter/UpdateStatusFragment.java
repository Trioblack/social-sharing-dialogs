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
import com.noveo.dialogs.utils.BundleUtils;
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

    private String consumerKey;
    private String consumerSecretKey;
    private TwitterShareDialog.Payload payload;

    public UpdateStatusFragment () {}

    public static UpdateStatusFragment newInstance(final String consumerKey, final String consumerSecretKey, TwitterShareDialog.Payload payload) {
        UpdateStatusFragment fragment = new UpdateStatusFragment();
        Bundle arguments = new Bundle();

        BundleUtils.putCustomerToken(arguments, consumerKey);
        BundleUtils.putCustomerSecretToken(arguments, consumerSecretKey);
        BundleUtils.putPayload(arguments, payload);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            payload = BundleUtils.getPayload(arguments);
            consumerKey = BundleUtils.getCustomerToken(arguments);
            consumerSecretKey = BundleUtils.getCustomerSecretToken(arguments);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.update_status_layout, container, false);
        sendButton = (Button) rootView.findViewById(R.id.send_button);
        statusEditText = (EditText) rootView.findViewById(R.id.status_text);
        accessToken4j = PreferenceUtils.restoreTwitterAccessToken(getActivity());

        statusEditText.setText(payload.getStatus());

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