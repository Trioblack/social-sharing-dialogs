package com.noveo.dialogs.twitter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.noveo.dialogs.R;
import com.noveo.dialogs.utils.ApiLevelChooser;
import com.noveo.dialogs.utils.BundleUtils;
import com.noveo.dialogs.utils.PreferenceUtils;
import com.twitter.Extractor;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class UpdateStatusFragment extends Fragment {
    private static final int SHORT_URL_LENGTH = 22;
    private static final int MAX_TWITT_LENGTH = 140;

    public LinearLayout inputFields;
    public TextView twittLength;
    public ProgressBar progressBar;
    public EditText statusEditText;
    public Button sendButton;
    public AccessToken accessToken4j;

    private String consumerKey;
    private String consumerSecretKey;
    private TwitterShareDialog.Payload payload;
    private Twitter twitter;
    private Extractor extractor = new Extractor();

    public UpdateStatusFragment() {
    }

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
            payload = (TwitterShareDialog.Payload) BundleUtils.getPayload(arguments);
            consumerKey = BundleUtils.getCustomerToken(arguments);
            consumerSecretKey = BundleUtils.getCustomerSecretToken(arguments);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.update_status_layout, container, false);

        final WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final ViewGroup.LayoutParams params = rootView.getLayoutParams();
        params.width = (int) (0.8 * display.getWidth());
        params.height = (int) (0.8 * display.getHeight());
        rootView.setLayoutParams(params);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        twittLength = (TextView) rootView.findViewById(R.id.twitt_length);
        inputFields = (LinearLayout) rootView.findViewById(R.id.input_field);
        sendButton = (Button) rootView.findViewById(R.id.send_button);
        statusEditText = (EditText) rootView.findViewById(R.id.status_text);
        accessToken4j = PreferenceUtils.restoreTwitterAccessToken(getActivity());

        statusEditText.setText(payload.getStatus());
        updateTwittLength();
        //setupProgressStat();
        setupWorkState();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        statusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTwittLength();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendButton();
            }
        });
    }

    private void updateTwittLength() {
        try {
            int remainingNumber = getRemainingNumberCharts();
            int color = 0;

            if (remainingNumber >= 0) {
                color = getResources().getColor(android.R.color.black);
            } else {
                color = getResources().getColor(R.color.red);
            }

            twittLength.setTextColor(color);
            twittLength.setText("" + remainingNumber);
        } catch (IllegalStateException ignore) {
        }
    }

    private void onClickSendButton() {
        int remainingNumber = getRemainingNumberCharts();
        if (remainingNumber < 0) {
            return;
        }

        AsyncTask<Object, Void, Boolean> task = new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                setupProgressStat();
            }

            @Override
            protected Boolean doInBackground(Object... params) {
                return updateStatus();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    if (getParentFragment() instanceof TwitterShareDialog) {
                        ((TwitterShareDialog) getParentFragment()).dismiss();
                    } else {
                        throw new RuntimeException("Must be called from Twitter Share Dialog");
                    }
                } else {
                    setupWorkState();
                }
            }
        };
        ApiLevelChooser.<Object, Void, Boolean>startAsyncTask(task);
    }

    private Boolean updateStatus() {
        String message = null;

        if (statusEditText != null && !TextUtils.isEmpty(statusEditText.getText())) {
            message = statusEditText.getText().toString();
        }

        if (!TextUtils.isEmpty(message) && accessToken4j != null) {
            try {
                if (twitter == null) {
                    twitter = TwitterFactory.getSingleton();
                }

                boolean isEmptyKeys = TextUtils.isEmpty(twitter.getConfiguration().getOAuthConsumerKey())
                        || TextUtils.isEmpty(twitter.getConfiguration().getOAuthConsumerSecret());

                if (isEmptyKeys) {
                    try {
                        twitter.setOAuthConsumer(consumerKey, consumerSecretKey);
                        twitter.setOAuthAccessToken(accessToken4j);
                    } catch (Exception ignore) {}
                }

                twitter.updateStatus(message);

                return true;

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void setupProgressStat() {
        progressBar.setVisibility(View.VISIBLE);
        inputFields.setVisibility(View.INVISIBLE);
    }

    private void setupWorkState() {
        progressBar.setVisibility(View.INVISIBLE);
        inputFields.setVisibility(View.VISIBLE);
    }

    private int getTwittLength() {
        String message;
        if (statusEditText != null && !TextUtils.isEmpty(statusEditText.getText())) {
            message = statusEditText.getText().toString();
            int result = message.length();
            List<String> urls = extractor.extractURLs(message);
            for (String url : urls) {
                result -= url.length() - SHORT_URL_LENGTH;
            }
            return result;

        } else {
            return 0;
        }
    }

    private int getRemainingNumberCharts(int length) {
        return (MAX_TWITT_LENGTH - length);
    }

    private int getRemainingNumberCharts() {
        int length = getTwittLength();
        return getRemainingNumberCharts(length);
    }
}