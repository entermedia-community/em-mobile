package org.entermediadb.chat2.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import org.entermediadb.chat2.R;

import javax.annotation.Nullable;


public class WebViewFragment extends Fragment {

    public WebView mWebView;
    public String fieldBaseUrl;
    public boolean loaded = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View theview = inflater.inflate(R.layout.fragment_chatlog, container, false);

        //https://medium.com/hootsuite-engineering/handling-orientation-changes-on-android-41a6b62cb43f
        setRetainInstance(true);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |     WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        mWebView = (WebView) theview.findViewById(R.id.chatlog_webview);  //chatlog_webview
        //mWebView.loadUrl("google.com");

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setTextZoom(20);
        //webSettings.setLo
        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());
        return theview;
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaded = true;
        //you can set the title for your toolbar here for different fragments different titles
        renderUrl();
    }
    public void setUrl(String inUrl)
    {
        fieldBaseUrl = inUrl;
        if( loaded )
        {
            renderUrl();
        }
    }
    protected void renderUrl()
    {
        mWebView.loadUrl(fieldBaseUrl);

    }
}