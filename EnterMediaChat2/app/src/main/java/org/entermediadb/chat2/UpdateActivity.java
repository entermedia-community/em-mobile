package org.entermediadb.chat2;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.entermediadb.firebase.quickstart.auth.java.EnterMediaLoginActivity;
import org.json.simple.JSONObject;


public abstract class UpdateActivity implements Runnable
{
    private static final String TAG = "UpdateActivity";
    private String ACTIVITYTAG;

    Activity fieldActivity;
    JSONObject jsonData;
    Throwable fieldError;

    public UpdateActivity(Activity inActivity,String inACTIVITYTAG)
    {
        fieldActivity = inActivity;
        ACTIVITYTAG = inACTIVITYTAG;
    }
    protected String fieldUrl;

    public String getUrl() {
        return fieldUrl;
    }

    public void setUrl(String fieldUrl) {
        this.fieldUrl = fieldUrl;
    }

    public JSONObject getJsonData() {
        return jsonData;
    }
    public void setJsonData(org.json.simple.JSONObject inData)
    {
        jsonData = inData;
    }

    public Throwable getError()
    {
        return fieldError;
    }
    public void setError(Throwable ex)
    {
        fieldError = ex;
    }
    public abstract void runNetwork();
    //params
    public abstract void runUiUpdate();

    public void runUiFinally()
    {

    }

    @Override
    public void run() {
        try {
            runNetwork();
        }
        catch ( Throwable ex)
        {
             Log.w(TAG, "Network failed " + getUrl(), ex);
             setError(ex);
        }
            //https://stackoverflow.com/questions/15136199/when-to-use-handler-post-when-to-new-thread
            //https://stackoverflow.com/questions/12618038/why-to-use-handlers-while-runonuithread-does-the-same
          fieldActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if( getError() != null) {
                        Toast.makeText(fieldActivity, "UI error " + getError(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        runUiUpdate();
                    }
                    catch( Throwable ex)
                    {
                        Log.w(TAG, "UI Update failed " + getUrl(), ex);
                        Toast.makeText(fieldActivity, "UI error " + getError(), Toast.LENGTH_LONG).show();
                        setError(ex);
                    }
                    finally {
                        try {
                            runUiFinally();
                        }
                        catch( Throwable ex2)
                        {
                            Log.w(TAG, "Finally failed " + getUrl(), ex2);
                        }
                    }
                }
            });
        }
    }
