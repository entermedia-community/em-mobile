package org.entermediadb.chat2.ui.chat;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
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
    public String fieldOpenCollection;
    private static WebViewFragment fieldInstance;
    public static WebViewFragment getInstance()
    {
        return fieldInstance;
    }
    public void setInstance(WebViewFragment inThis)
    {
        fieldInstance = inThis;
    }

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
        if (Build.VERSION.SDK_INT > 17) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        //webSettings.setTextZoom(20);
        //webSettings.setLo
        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());


        mWebView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    WebView webView = (WebView) v;

                    switch(keyCode)
                    {
                        case KeyEvent.KEYCODE_BACK:
                            if(webView.canGoBack())
                            {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }

                return false;
            }
        });

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
    public String getOpenCollection()
    {
        return fieldOpenCollection;
    }
    public void setOpenCollection(String inCollectionId)
    {
        fieldOpenCollection = inCollectionId;
    }
    protected void renderUrl()
    {
        mWebView.loadUrl(fieldBaseUrl);

    }
}