package org.entermediadb.chat2;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.entermediadb.firebase.quickstart.auth.java.ChooserActivity;
import org.entermediadb.firebase.quickstart.auth.java.EmailPasswordActivity;
import org.entermediadb.firebase.quickstart.auth.java.EnterMediaLoginActivity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;


//https://stackoverflow.com/questions/40838154/retrieve-google-access-token-after-authenticated-using-firebase-authentication
    public class GetEmToken extends AsyncTask<Void, Void,String> {

        private static final String TAG = "GetEmToken";
        private final Activity activity;
        Intent fieldIntent;
        String fieldEmail;
        String fieldPassword;
        private FirebaseAuth mAuth;

        public GetEmToken(Activity inActivity, FirebaseAuth inAuth, String inEmail, String inPassword, Intent inIntent)
        {
            this.activity = inActivity;
            mAuth = inAuth;
            fieldEmail = inEmail;
            fieldPassword = inPassword;
            fieldIntent = inIntent;
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            JSONObject jsonreply = null;
            try {
                JSONObject obj = new JSONObject();

                obj.put("email", fieldEmail);
                obj.put("password", fieldPassword);

                EnterMediaConnection connection = new EnterMediaConnection();
                jsonreply = connection.postJson(EnterMediaConnection.MEDIADB + "/services/authentication/firebaselogin.json", obj);
            }
            catch( Throwable ex)
            {
                //Log this somehow
                Log.w(TAG, "network:failure", ex);
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("error", "network:failure " +  ex.getMessage());
                activity.startActivity(intent);
                return null;
            }

            JSONObject results = (JSONObject) jsonreply.get("results");
            final String entermediakey = (String) results.get("entermediakey");
            final String userid = (String) results.get("userid");

            activity.getPreferences(Context.MODE_PRIVATE).edit().
                    putString("email", fieldEmail).
                    putString("emuserid", userid).
                    putString("password", fieldPassword).
                    commit();


            //Login with firebase API
            mAuth.signInWithEmailAndPassword(fieldEmail, fieldPassword)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(activity, MainActivity.class);

                                intent.putExtra("useremail", fieldEmail);

                                intent.putExtra("token", entermediakey);
                                intent.putExtra("tokentype", "entermedia");

                                if( fieldIntent != null && fieldIntent.getExtras() != null)
                                {
                                    for (String key : fieldIntent.getExtras().keySet() )
                                    {
                                        intent.putExtra(key, fieldIntent.getStringExtra(key));
                                    }
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //Error?
                                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                activity.startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                               // Toast.makeText(EnterMediaLoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            return entermediakey;
            //save key in context
        }

}

