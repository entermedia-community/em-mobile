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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.entermediadb.chat2.MainActivity;
import org.entermediadb.chat2.R;


/**
 * Simple list-based Activity to redirect to one of the other Activities. This Activity does not
 * contain any useful code related to Firebase Authentication. You may want to start with
 * one of the following Files:
 *     {@link GoogleSignInActivity}
 *     {@link EmailPasswordActivity}
 *     {@link PasswordlessActivity}
 *     {@link PhoneAuthActivity}
 *     {@link AnonymousAuthActivity}
 *     {@link CustomAuthActivity}
 *     {@link GenericIdpActivity}
 */
public class ChooserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "ChooserActivity";

    private static final Class[] CLASSES = new Class[]{
            GoogleSignInActivity.class,
            EnterMediaLoginActivity.class,
    };
    private FirebaseAuth mAuth;

    private static final int[] DESCRIPTION_IDS = new int[] {
            R.string.desc_google_sign_in,
            R.string.desc_entermedia_sign_in,
    };

                /*
            FacebookLoginActivity.class,
            TwitterLoginActivity.class,
            EmailPasswordActivity.class,
            PasswordlessActivity.class,
            PhoneAuthActivity.class,
            AnonymousAuthActivity.class,
            FirebaseUIActivity.class,
            CustomAuthActivity.class,
            GenericIdpActivity.class,
            R.string.desc_facebook_login,
            R.string.desc_twitter_login,
            R.string.desc_emailpassword,
            R.string.desc_passwordless,
            R.string.desc_phone_auth,
            R.string.desc_anonymous_auth,
            R.string.desc_firebase_ui,
            R.string.desc_custom_auth,
            R.string.desc_generic_idp,
            */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_chooser);

//        // Set up ListView and Adapter
//        ListView listView = findViewById(R.id.listView);
//
//        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
//        adapter.setDescriptionIds(DESCRIPTION_IDS);
//
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(this);
//
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();

        if (appLinkIntent != null) {

            if(appLinkIntent.getExtras() != null) {
                Log.d(TAG, String.valueOf(appLinkIntent.getExtras().keySet()));
            }
            /*String appLinkAction = appLinkIntent.getAction();*/
            Uri appLinkData = appLinkIntent.getData();
            if(appLinkData != null) {
                String urlKey = String.valueOf(appLinkData.getQueryParameters("entermedia.key"));
                //Log.d(TAG, appLinkAction);
                Log.d(TAG, String.valueOf(urlKey));
            }
        }

        loginCheck();

        //https://developer.android.com/training/basics/intents/filters#java
        // Get the intent that started this activity


    }

    protected void loginCheck()
    {
        boolean autologin = true;

        Intent intent = getIntent();
        if( intent != null)
        {
            String logoutaction = intent.getStringExtra("logout");
            if( Boolean.parseBoolean(logoutaction)) {
                autologin = false;
            }
        }
        if( autologin)
        {
            mAuth = FirebaseAuth.getInstance(); //Have to run this first. If I am already logged into Firebase then just skip ahead?
            //FirebaseAuth.getInstance().addAuthStateListener(mAuthState());
//            mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
//                @Override
//                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                    //Nada?
//                }
//            });
            if( mAuth.getCurrentUser() != null )
            {
                String entermediakey = getSharedPreferences("app",Context.MODE_PRIVATE).getString("entermediakey", null);
                String emuserid = getSharedPreferences("app",Context.MODE_PRIVATE).getString("emuserid", null);
                if( entermediakey != null && emuserid != null)
                {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent gopage = new Intent(this, MainActivity.class);

                    if (intent != null && intent.getExtras() != null) {
                        for (String key : intent.getExtras().keySet()) {
                            gopage.putExtra(key, intent.getStringExtra(key));
                        }
                    }
                    gopage.putExtra("useremail", user.getEmail());
                    gopage.putExtra("emuserid", emuserid);

                    gopage.putExtra("token", entermediakey);
                    gopage.putExtra("tokentype", "entermedia");
                    startActivity(gopage);
                    return;
                }
            }
        }
        Intent loginpage = new Intent(this, EnterMediaLoginActivity.class);
        if (intent != null && intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                loginpage.putExtra(key, intent.getStringExtra(key));
            }
        }
        startActivity(loginpage);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
    }

    public static class MyArrayAdapter extends ArrayAdapter<Class> {

        private Context mContext;
        private Class[] mClasses;
        private int[] mDescriptionIds;

        public MyArrayAdapter(Context context, int resource, Class[] objects) {
            super(context, resource, objects);

            mContext = context;
            mClasses = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }

           // ((TextView) view.findViewById(android.R.id.text1)).setText(mClasses[position].getSimpleName());
            ((TextView) view.findViewById(android.R.id.text2)).setText(mDescriptionIds[position]);

            return view;
        }

        public void setDescriptionIds(int[] descriptionIds) {
            mDescriptionIds = descriptionIds;
        }
    }
}
