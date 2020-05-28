package org.entermediadb.chat2.ui.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import org.entermediadb.chat2.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class WebViewFragment extends Fragment {

    public WebView mWebView;
    public String fieldBaseUrl;
    public String fieldOpenCollection;
    public String fieldOpenGoal;
    protected Map<String,String> fieldExtraHeaders;


    public final static int FILECHOOSER_RESULTCODE = 1;
    public ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> mUploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;


    public void setFieldExtraHeaders(Map<String,String> inMap)
    {
        fieldExtraHeaders = inMap;
    }

//    private static WebViewFragment fieldInstance;
//    public static WebViewFragment getInstance()
//    {
//        return fieldInstance;
//    }
//    public void setInstance(WebViewFragment inThis)
//    {
//        fieldInstance = inThis;
//    }

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
        mWebView.setWebViewClient(new WebViewClient() );


        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView.javascript", consoleMessage.message());
                return true;
            }
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                Log.d("alert", message);
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                result.confirm();
                return true;
            };


            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;
//                Toast.makeText(getApplicationContext(), "A", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i,"File Chooser"), REQUEST_SELECT_FILE);
                return true;
            }

        });



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
    public String getOpenGoal()
    {
        return fieldOpenGoal;
    }
    public void setOpenGoal(String inGoalId)
    {
        fieldOpenCollection = inGoalId;
    }
    protected void renderUrl()
    {
        if( fieldExtraHeaders != null)
        {
            mWebView.loadUrl(fieldBaseUrl,fieldExtraHeaders);
        }
        else {
            mWebView.loadUrl(fieldBaseUrl);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode==REQUEST_SELECT_FILE){
            Uri result = intent == null || resultCode != RESULT_OK ?null:Uri.parse(intent.getDataString());
            if(result==null) return;
            if(uploadMessage!=null){
                uploadMessage.onReceiveValue(new Uri[]{result});
                uploadMessage = null;
            }else{
                uploadMessage.onReceiveValue(null);
                uploadMessage= null;
            }
        }
    }


}