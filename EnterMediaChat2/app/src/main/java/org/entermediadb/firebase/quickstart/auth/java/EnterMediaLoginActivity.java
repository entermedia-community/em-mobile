/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.entermediadb.firebase.quickstart.auth.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.entermediadb.chat2.EnterMediaConnection;
import org.entermediadb.chat2.MainActivity;
import org.entermediadb.chat2.R;
import org.entermediadb.chat2.UpdateActivity;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class EnterMediaLoginActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EnterMediaLoginActivity";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entermedialogin);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        //findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.verifyEmailButton).setOnClickListener(this);
        // Magic link button
        findViewById(R.id.forgotPasswordButton).setOnClickListener(this);


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        String email = getSharedPreferences("app", Context.MODE_PRIVATE).getString("email", null);
        mEmailField.setText(email);
        String password = getSharedPreferences("app", Context.MODE_PRIVATE).getString("password", null);
        mPasswordField.setText(password);

        // [END initialize_auth]

        if (email != null && password != null) {
            String logoutaction = getIntent().getStringExtra("logout");
            if (!Boolean.parseBoolean(logoutaction)) {
                signIn(email, password,null);
            }
        }


        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();

        if (appLinkIntent != null) {

            if(appLinkIntent.getExtras() != null) {
                Log.d(TAG, String.valueOf(appLinkIntent.getExtras().keySet()));
            }
            /*String appLinkAction = appLinkIntent.getAction();*/
            Uri appLinkData = appLinkIntent.getData();
            if(appLinkData != null) {
                String urlKey = String.valueOf(appLinkData.getQueryParameter("entermedia.key"));
                signIn(null,null, urlKey);
                //Log.d(TAG, appLinkAction);
                Log.d(TAG, String.valueOf(urlKey));
            }
        }
    }

    public void sendMagicLink(String email){

        EnterMediaConnection connection = new EnterMediaConnection();
        Log.d(TAG, "signIn:" + email);

        UpdateActivity handler = new UpdateActivity(this,TAG)
        {
            @Override
            public void runNetwork() {

                showProgressDialog();
                JSONObject obj = new JSONObject();

                obj.put("to", email);
                //      curl https://entermediadb.org/mediadb//services/authentication/firebaselogin.json?entermedia.key=426md5423a2a7c0c40a50970c656693f5975093002d4c
                String url = EnterMediaConnection.MEDIADB + "/services/authentication/sendmagiclink.json";

                //todo replace with mediadb api
                JSONObject jsonreply = connection.postJson(url, obj);
                setJsonData(jsonreply);
            }

            public void runUiUpdate()
            {
                getSharedPreferences("app",Context.MODE_PRIVATE).edit().
                        putString("email", email).
                        commit();
            }

            public void runUiFinally()
            {
                hideProgressDialog();
            }
        };
        connection.process(handler);


    }
    private void signIn(String email, String password, String entermediakey) {
        Log.d(TAG, "signIn:" + email);

        EnterMediaConnection connection = new EnterMediaConnection();
        UpdateActivity handler = new UpdateActivity(this,TAG)
       {
           @Override
           public void runNetwork() {
               showProgressDialog();
               JSONObject obj = new JSONObject();

               if(entermediakey != null) {
                   obj.put("entermedia.key",entermediakey);
               }
               else {
                   obj.put("password", password);
                   obj.put("email", email);
               }
          //      curl https://entermediadb.org/mediadb//services/authentication/firebaselogin.json?entermedia.key=426md5423a2a7c0c40a50970c656693f5975093002d4c
               JSONObject jsonreply = connection.postJson(EnterMediaConnection.MEDIADB + "/services/authentication/firebaselogin.json", obj);
               setJsonData(jsonreply);
           }

           public void runUiUpdate()
           {
               JSONObject response = (JSONObject) getJsonData().get("response");
               JSONObject results = (JSONObject) getJsonData().get("results");
               //TODO Cheeck for ok

               String ok = (String) response.get("status");
               if ("invalidlogin".equals(ok)) {
                   String error = "Could not login";
                   Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                   return;
               }

               final String entermediakey = (String) results.get("entermediakey");
               final String userid = (String) results.get("userid");
               final String email = (String) results.get("email");
               final String firebasepassword = (String) results.get("firebasepassword");

               getSharedPreferences("app",Context.MODE_PRIVATE).edit().
                       putString("email", email).
                       putString("emuserid", userid).
  //                     putString("firebasepassword", firebasepassword).
                       putString("entermediakey", entermediakey).
                       commit();

               //Login with firebase API
                mAuth.signInWithEmailAndPassword(email, firebasepassword)
                        .addOnCompleteListener(EnterMediaLoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent gotopage = new Intent(getApplicationContext(), MainActivity.class);

                                    Intent intent = EnterMediaLoginActivity.this.getIntent();

                                    if (intent != null && intent.getExtras() != null) {
                                        for (String key : intent.getExtras().keySet()) {
                                            intent.putExtra(key, intent.getStringExtra(key));
                                        }
                                    }

                                    gotopage.putExtra("useremail", email);
                                    gotopage.putExtra("token", entermediakey);
                                    gotopage.putExtra("tokentype", "entermedia");

                                    gotopage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //Error?
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    startActivity(gotopage);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    getSharedPreferences("app",Context.MODE_PRIVATE).edit().remove("email").
                                            commit();
                                    // Toast.makeText(EnterMediaLoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
           }
           public void runUiFinally()
           {
               hideProgressDialog();
           }
       };
       connection.process(handler);

    }



    private void sentEmailReminder() {
        // Disable button
            JSONObject obj = new JSONObject();

            String email = mEmailField.getText().toString();
            if( email == null )
            {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_LONG).show();
                return;
            }

            obj.put("to", email);

            EnterMediaConnection connection = new EnterMediaConnection();
            UpdateActivity handler = new UpdateActivity(this, TAG) {
                @Override
                public void runNetwork()
                {
                    JSONObject jsonreply = connection.postJson(EnterMediaConnection.MEDIADB + "/services/authentication/sendpassword.json", obj);
                    setJsonData(jsonreply);
                }

                public void runUiUpdate()
                {
                    if( getError() != null) {
                        Toast.makeText(EnterMediaLoginActivity.this, "network:failure" + getError(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    String status = (String)((Map)getJsonData().get("response")).get("status");
                    if( status.equals("invalidlogin") )
                    {
                        Toast.makeText(EnterMediaLoginActivity.this, "No such email", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // [END send_email_verification]
                    Toast.makeText(EnterMediaLoginActivity.this,"Email sent to "  + email, Toast.LENGTH_LONG).show();

                }
            };
            connection.process(handler);

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private boolean validateMagicLink() {
        // TODO: 5/20/20 make background of email yellow for alert.
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            mEmailField.setHighlightColor(android.R.attr.colorAccent);
            valid = false;
        } else {
            mEmailField.setError("Email sent");
        }

        return valid;
    }

//    private void updateUI(FirebaseUser user) {
//        hideProgressDialog();
//        if (user != null) {
//            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
//                    user.getEmail(), user.isEmailVerified()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
//            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
//
//            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
//
//            String email = mEmailField.getText().toString();
//            String password = mPasswordField.getText().toString();
//            //Send to EnterMedia and get back entermedia.key
//            //Pass along the key to all requests
//            LoginWithPassword gett = new LoginWithPassword(getApplicationContext(),null,email,password);
//            gett.execute();
//
//        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
//            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
//        }
//    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            //createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            sentEmailReminder();
        } else if (i == R.id.emailSignInButton) {
            if (!validateForm()) {
                return;
            }
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString(),null);
        }else if (i == R.id.forgotPasswordButton){
            if (!validateMagicLink()){
                return;
            }
            Toast.makeText(this, "Sent! Please check your e-mail.", Toast.LENGTH_LONG).show();
            sendMagicLink(mEmailField.getText().toString());

        }
    }
}
