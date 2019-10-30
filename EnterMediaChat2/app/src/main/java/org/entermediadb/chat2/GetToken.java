package org.entermediadb.chat2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;

import org.entermediadb.firebase.quickstart.auth.java.GoogleSignInActivity;

import java.io.IOException;


//https://stackoverflow.com/questions/40838154/retrieve-google-access-token-after-authenticated-using-firebase-authentication
    public class GetToken extends AsyncTask<Void, Void, String> {

        private final Context context;
        Intent fieldIntent;
        public GetToken(Context context,Intent inIntent)
        {
            this.context = context;

            this.fieldIntent = inIntent;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE;
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                    return GoogleAuthUtil.getToken(context, account.getAccount(), scope, new Bundle());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
            return null;
        }

    @Override
    protected void onPostExecute(String inToken) {

        Intent intent = new Intent(context, MainActivity.class);
      //  intent.putExtra("useremail", user.getEmail());

        intent.putExtra("idtoken", inToken);
        if( fieldIntent != null)
        {
            for (String key : fieldIntent.getExtras().keySet() )
            {
                intent.putExtra(key, fieldIntent.getStringExtra(key));
            }
        }
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //Error?
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);


    }
}

