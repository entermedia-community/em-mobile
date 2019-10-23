package org.entermediadb.chat2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;


public class WebViewFragment extends Fragment {

    public WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View theview = inflater.inflate(R.layout.fragment_chatlog, container, false);

        mWebView = (WebView) theview.findViewById(R.id.chatlog_webview);  //chatlog_webview
        //mWebView.loadUrl("google.com");

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());
        return theview;
    }

    public void setUrl(String inUrl)
    {
        mWebView.loadUrl(inUrl);
    }
}