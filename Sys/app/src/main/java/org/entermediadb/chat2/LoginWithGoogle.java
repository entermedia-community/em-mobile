package org.entermediadb.chat2;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;

import org.entermediadb.firebase.quickstart.auth.java.GoogleSignInActivity;
import org.json.simple.JSONObject;

import java.io.IOException;


//https://stackoverflow.com/questions/40838154/retrieve-google-access-token-after-authenticated-using-firebase-authentication
    public class LoginWithGoogle extends AsyncTask<Void, Void, String> {
        private static final String TAG = "LoginWithGoogle";
        private final Activity activity;
        private final Context context;
        Intent fieldIntent;
        Account fieldAccount;
        public LoginWithGoogle(Activity inActivity, Context context, Account inAccount, Intent inIntent)
        {
            this.context = context;
            this.activity  = inActivity;
            this.fieldIntent = inIntent;
            this.fieldAccount = inAccount;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE;
                String googletoken = GoogleAuthUtil.getToken(context, fieldAccount, scope, new Bundle());

                JSONObject jsonreply = null;
                try {
                    JSONObject obj = new JSONObject();

                    obj.put("accesstoken", googletoken);

                    EnterMediaConnection connection = new EnterMediaConnection();
//                    connection.setTokenType("google");
//                    connection.setToken(googletoken);
                    jsonreply = connection.postJson(EnterMediaConnection.MEDIADB + "/services/authentication/googlelogin.json", obj);
                }
                catch( Throwable ex)
                {
                    //Log this somehow
                    Log.w(TAG, "network:failure", ex);
                    return null;
                }
                JSONObject results = (JSONObject) jsonreply.get("results");
                final String entermediakey = (String) results.get("entermediakey");
                final String userid = (String) results.get("userid");
                final String email = (String) results.get("email");


                activity.getSharedPreferences("app",Context.MODE_PRIVATE).edit().
                        putString("email", email).
                        putString("emuserid", userid).
                        putString("entermediakey",entermediakey).
                        commit();
                return entermediakey;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
            return null;
        }

    @Override
    protected void onPostExecute(String inEmToken) {

        if( inEmToken == null)
        {
            return;
        }
        Intent intent = new Intent(context, MainActivity.class);
        GoogleSignInAccount signedin = GoogleSignIn.getLastSignedInAccount(context);
        if(signedin !=null)
        {
            intent.putExtra("useremail", signedin.getEmail());
        }
        if( fieldIntent != null && fieldIntent.getExtras() != null)
        {
            for (String key : fieldIntent.getExtras().keySet() )
            {
                intent.putExtra(key, fieldIntent.getStringExtra(key));
            }
        }

        intent.putExtra("token", inEmToken);
        intent.putExtra("tokentype", "entermedia");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //Error?
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);


    }
}

