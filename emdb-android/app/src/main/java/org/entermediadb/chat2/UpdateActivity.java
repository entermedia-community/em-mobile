package org.entermediadb.chat2;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;

import org.json.simple.JSONObject;


public abstract class UpdateActivity implements Runnable
{
    private static final String TAG = "UpdateActivity";

    Activity fieldActivity;
    JSONObject jsonData;

    public UpdateActivity(Activity inActivity)
    {
        fieldActivity = inActivity;
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

    public abstract void runNetwork();
    //params
    public abstract void runUiUpdate();

    @Override
    public void run() {
        try {
            runNetwork();

            //https://stackoverflow.com/questions/15136199/when-to-use-handler-post-when-to-new-thread
            //https://stackoverflow.com/questions/12618038/why-to-use-handlers-while-runonuithread-does-the-same
            fieldActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runUiUpdate();
                }
            });
        }
        catch ( Throwable ex)
        {
            Log.w(TAG, "UI Update failed " + getUrl(), ex);
          /*
            fieldActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Snackbar.make(fieldActivity.findViewById(R.id.nav_home), " network failed " + ex + " "  +getUrl(), Snackbar.LENGTH_SHORT).show();
                }
            });

           */
        }

    }





}
