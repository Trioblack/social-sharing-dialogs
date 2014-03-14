package com.noveo.dialogs.twitter;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.noveo.dialogs.R;
import com.noveo.dialogs.utils.ApiLevelChooser;
import com.noveo.dialogs.utils.BundleUtils;
import com.noveo.dialogs.utils.PreferenceUtils;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.auth.AccessToken;

public class WebViewFragment extends Fragment {
    private String consumerKey;
    private String consumerSecretKey;

    public ProgressBar progressBar;
    public WebView webView;

    private OAuthService service;
    private String verifyCode = null;
    private Token requestToken;
    private Token accessToken;
    private AccessToken accessToken4j;
    private TwitterShareDialog.Payload payload;

    public WebViewFragment() {}

    public static WebViewFragment newInstance(final String consumerKey, final String consumerSecretKey, TwitterShareDialog.Payload payload) {
        WebViewFragment fragment = new WebViewFragment();
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
        final View rootView = inflater.inflate(R.layout.webview_fragment_layout, container, false);

        final WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final ViewGroup.LayoutParams params = rootView.getLayoutParams();
        params.width = (int) (0.8*display.getWidth());
        params.height =(int) (0.8*display.getHeight());

        rootView.setLayoutParams(params);

        webView = (WebView) rootView.findViewById(R.id.webview);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        service = new ServiceBuilder()
                .provider(TwitterApi.SSL.class)
                .apiKey(consumerKey)
                .apiSecret(consumerSecretKey)
                .build();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        registration();
    }

    private void registration() {
        AsyncTask<Object, Void, String> task = new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                return getAuthorizationUrl();
            }

            @Override
            protected void onPostExecute(final String startUrl) {
                setupWebView(startUrl);
            }
        };

        ApiLevelChooser.<Object, Void, String>startAsyncTask(task);
    }

    private String getAuthorizationUrl() {
        if (service != null) {
            requestToken = service.getRequestToken();
            service.getAuthorizationUrl(requestToken);
            return service.getAuthorizationUrl(requestToken);
        } else {
            return null;
        }
    }

    private void setupWebView(final String startUrl) {
        if (webView != null && !TextUtils.isEmpty(startUrl)) {
            webView.getSettings().setJavaScriptEnabled(true);

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    final Pattern pattern = Pattern.compile("<code>.*</code>");
                    final String html = consoleMessage.message().substring(5);
                    final Matcher matcher = pattern.matcher(html);
                    if (matcher.find()) {
                        setupProgressState();
                        verifyCode = matcher.group(0).replace("<code>", "").replace("</code>", "");

                        AsyncTask<Object, Void, Void> task = new AsyncTask<Object, Void, Void>() {
                            @Override
                            protected Void doInBackground(Object... params) {
                                Verifier verifier = new Verifier(verifyCode);
                                accessToken = service.getAccessToken(requestToken, verifier);
                                accessToken4j = new AccessToken(accessToken.getToken(), accessToken.getSecret());
                                PreferenceUtils.saveTwitterAccessToken(getActivity(), accessToken4j);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                openSendMessageFragment();
                            }
                        };

                        ApiLevelChooser.<Object, Void, Void>startAsyncTask(task);

                    } else {
                        setupWorkState();
                    }

                    return true;
                }
            });

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (!startUrl.equals(url)) {
                        view.loadUrl("javascript:console.log('HTMLOUT'+document.getElementsByTagName('html')[0].innerHTML);");
                    } else {
                        setupWorkState();
                    }
                }
            });


            webView.loadUrl(startUrl);

        }
    }

    private void openSendMessageFragment() {
        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        final Fragment fragment = UpdateStatusFragment.newInstance(consumerKey, consumerSecretKey, payload);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void setupProgressState() {
        webView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setupWorkState() {
        webView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}