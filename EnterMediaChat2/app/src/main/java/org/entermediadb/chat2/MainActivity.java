package org.entermediadb.chat2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;


import org.entermediadb.chat2.ui.home.HomeFragment;
import org.entermediadb.firebase.quickstart.auth.java.ChooserActivity;
import org.entermediadb.firebase.quickstart.auth.java.EnterMediaLoginActivity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
{
       // MenuItem.OnMenuItemClickListener {
    private static final String TAG = "MainActivity";

    //TODO: get from fire

    EnterMediaConnection connection = new EnterMediaConnection();
    List<JSONObject> menudata;
    //private AppBarConfiguration mAppBarConfiguration;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    AppUpdateManager mAppUpdateManager;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    //https://medium.com/hootsuite-engineering/handling-orientation-changes-on-android-41a6b62cb43f

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initUpdateCheck();

        //See if im loged in already?

        setContentView(R.layout.navigation_drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


/*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

 */
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);


        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, new HomeFragment());
        ft.commit();

        //Auto login code begin.

        Intent intent = getIntent();
        if (intent != null) {

            if(intent.getExtras() != null) {
                Log.d(TAG, String.valueOf(intent.getExtras().keySet()));
            }
            /*String appLinkAction = appLinkIntent.getAction();*/
            Uri appLinkData = intent.getData();

            if(appLinkData != null) {

                String urlKey = String.valueOf(appLinkData.getQueryParameters("entermedia.key"));
                //Log.d(TAG, appLinkAction);
                Log.d(TAG, String.valueOf(urlKey));
            }
//            Uri data = intent.getData();
//            if( data != null) {
//                // Figure out what to do based on the intent type
//                if (intent.getType().indexOf("image/") != -1) {
//                    // Handle intents with image data ...
//                } else if (intent.getType().equals("text/plain")) {
//                    // Handle intents with text ...
//                    Toast.makeText(ChooserActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }
            String error = intent.getStringExtra("error");  //Regular login from chooser
            if (error != null) {
                //TODO: Show error fragment
                Toast.makeText(MainActivity.this, "Could login " + error, Toast.LENGTH_SHORT).show();
                return;
            }
            String token = intent.getStringExtra("token");  //Regular login from chooser
            if (token != null) {
                connection.setToken(token);
                String tokentype = intent.getStringExtra("tokentype");  //Regular login from chooser
                connection.setTokenType(tokentype);

                try {
                    PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                    int version = pInfo.versionCode;
                    connection.setVersion(String.valueOf(version));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                //TODO: Add excellent error handling

                //String email = intent.getStringExtra("useremail");
                // Toast.makeText(MainActivity.this, idtoken, Toast.LENGTH_SHORT).show();
                String collectionid = intent.getStringExtra("collectionid");  //Chooser notification
                String collectionlabel = intent.getStringExtra("collectionlabel");
                String projectgoalid = intent.getStringExtra("projectgoalid");  //Chooser notification
                String projectgoallabel = intent.getStringExtra("projectgoallabel");
                String collectivetopicid = intent.getStringExtra("collectivetopicid");

                reloadMenu(collectionid, collectionlabel, projectgoalid, projectgoallabel, collectivetopicid);
                //https://developer.android.com/guide/webapps/webview


                String useremail = intent.getStringExtra("useremail");
                if (useremail != null) {
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View header = navigationView.getHeaderView(0);
                    TextView text = (TextView) header.findViewById(R.id.subtitle);
                    text.setText(useremail);
                }
                if (projectgoalid != null) {
                    //Show goal details
                    ///demoall/webapp/WEB-INF /base/eminstitute/app/coll ective/goals/editgoalpanel_app.html
                    String url = EnterMediaConnection.EMINSTITUTE + "/collective/goals/editgoalpanel_app.html?collectionid=" + collectionid + "&goalid=" + projectgoalid;
                    org.entermediadb.chat2.ui.web.WebViewFragment browser = showBrowser("Ticket: " + collectionlabel, url);
                    browser.setOpenCollection(null);
                    return;
                } else if (collectionid != null) {
                    String url = null;
                    String label = null;
                    if (collectionid.endsWith("-messages")) {
                        label = "Direct Message";
                        url = EnterMediaConnection.EMINSTITUTE + "/messages/index.html?topic=" + collectivetopicid;
                    } else {
                        label = "Chat:" + collectionlabel;
                        url = EnterMediaConnection.EMINSTITUTE + "/collective/community/index.html?goaltrackerstaff=*&collectionid=" + collectionid + "&topic=" + collectivetopicid;
                    }
                    org.entermediadb.chat2.ui.web.WebViewFragment browser = showBrowser(label, url);
                    browser.setOpenCollection(collectionid);
                    return;
                }
            }
        } else {
            reloadMenu(null, null, null, null, null);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_logout:
                //GoogleSignIn.getLastSignedInAccount(
                connection.setToken(null);
                getSharedPreferences("app",Context.MODE_PRIVATE).edit().
                        remove("email").
                        remove("emuserid").
                        remove("password").
                        remove("entermediakey").
                        commit();
                //mGoogleSignInClient.signOut()
//                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestEmail()
//                        .build();
//                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
//                mGoogleSignInClient.signOut();

                UpdateActivity handler = new UpdateActivity(this, TAG) {
                    public void runNetwork()
                    {
                        try
                        {
                            FirebaseAuth.getInstance().signOut();
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        //FirebaseInstanceId.getInstance().deleteToken(FirebaseInstanceId.getInstance().getToken(), INSTANCE_ID_SCOPE);
                    }

                    @Override
                    public void runUiUpdate() {
                        String url = EnterMediaConnection.EMINSTITUTE + "/authentication/logout.html";
                        showBrowser("Logout", url); //This should clear the cookies and record the event

                        Intent intent = new Intent(getApplicationContext(), EnterMediaLoginActivity.class);
                        intent.putExtra("logout", "true");

                        fieldActivity.startActivity(intent);
                    }
                };
                connection.process(handler);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void reloadMenu(String openCollectionId,String collectionlabel, String projectgoalid, String projectgoallabel, String collectivetopicid)
    {
            UpdateActivity handler = new UpdateActivity(this, TAG)
            {
                @Override
                public void runNetwork()
                {
                    JSONObject obj = new JSONObject();

                    obj.put("page","1");
                    obj.put("hitsperpage","100");
                    JSONObject query = new JSONObject();
                    obj.put("query",query);

                    JSONArray terms = new JSONArray();
                    query.put("terms",terms);

                    JSONObject term = new JSONObject();
                    term.put("field","id");
                    term.put("operator","matches");
                    term.put("value","*");
                    terms.add(term);

                    JSONObject all = connection.postJson(EnterMediaConnection.MEDIADB+ "/services/module/librarycollection/viewprojects.json",
                            obj);
                    setJsonData(all);

//                    try {
//                        FirebaseInstanceId.getInstance().deleteInstanceId();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    if (Build.VERSION.SDK_INT > 25) {
                        String channelId = "fcm_default_channel";
                        String channelName = getString(R.string.default_notification_channel_name);
                        NotificationManager notificationManager =
                                getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                                channelName, NotificationManager.IMPORTANCE_LOW));
                    }

                    JSONObject data = getJsonData();
                    JSONObject response = (JSONObject) data.get("response");
                    Collection<JSONObject> results = new ArrayList((Collection)data.get("results"));

                    String userid = (String)response.get("userid");
                    connection.setUserId(userid);

//                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MyActivity.this,  new OnSuccessListener<InstanceIdResult>() {
//                        @Override
//                        public void onSuccess(InstanceIdResult instanceIdResult) {
//                            String mToken = instanceIdResult.getToken();
//                            Log.e("Token",mToken);
//                        }
//                    });
//                    String token = FirebaseInstanceId.getInstance().getToken();
//                    // Used to get firebase token until its null so it will save you from null pointer exeption
//                    while(token == null) {
//                        token = FirebaseInstanceId.getInstance().getToken();
//                    }

                    String messagecollectionid = userid + "-messages";
                    FirebaseMessaging.getInstance().subscribeToTopic(messagecollectionid).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "Could not subscribe to " +messagecollectionid);
                            }
                        }
                    });


                    int index = 0;
                    for(JSONObject objct : results)
                    {
                        final String collectionid = (String)objct.get("id");
                        //String name = (String)objct.get("name");

                        FirebaseMessaging.getInstance().subscribeToTopic(collectionid).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Log.d(TAG, "Could not subscribe to " + collectionid);
                                }
                            }
                        });
                    }

                }

                public void runUiUpdate()
                {

                    DrawerLayout drawer = findViewById(R.id.drawer_layout);

                    //https://blog.teamtreehouse.com/add-navigation-drawer-android
                    //https://stackoverflow.com/questions/31722566/dynamic-adding-item-to-navigationview-in-android

                    NavigationView navigationView = findViewById(R.id.nav_view);

                    //https://stackoverflow.com/questions/31722566/dynamic-adding-item-to-navigationview-in-android
                    Menu menu = navigationView.getMenu();

                    SubMenu topChannelMenu = menu.addSubMenu("Projects");

                    //TODO: Go to EnterMedia REST API and get projects your a team on

                    //Collection all = connection.getAsList("https://entermediadb.org/");

                    //topChannelMenu.add(0, 2, 1, "EnterMedia / General");

    //                TypefaceSpan span = new TypefaceSpan("serif");
    //                SpannableStringBuilder title = new SpannableStringBuilder("My Menu Item Title");
    //                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
    //                title.setSpan(bss, 0, title.length(), 0);
                    //menu.add(0, 0, 1, title);

                    JSONObject data = getJsonData();
                    JSONObject response = (JSONObject) data.get("response");
                    menudata = new ArrayList((Collection)data.get("results"));

                    int index = 0;
                    for(JSONObject objct : menudata)
                    {
                        final String collectionid = (String)objct.get("id");
                        String name = (String)objct.get("name");

                        SpannableStringBuilder title = new SpannableStringBuilder(name);
                        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);  //Depending on unread
                        title.setSpan(bss, 0, title.length(), 0);

                        MenuItem item = topChannelMenu.add(666, index++, Menu.NONE, title);

                    }
            }
        };
        connection.process(handler);

    }

    //https://www.youtube.com/watch?v=ZyJeyZpIhFA
    //https://github.com/umangburman/Navigation-Drawer-With-Navigation-Component
    //https://guides.codepath.com/android/fragment-navigation-drawer
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        menuItem.setChecked(true);
//        return true;
//    }


    @Override
    protected void onPause()
    {
        org.entermediadb.chat2.ui.web.WebViewFragment browser = (org.entermediadb.chat2.ui.web.WebViewFragment)
                getSupportFragmentManager().findFragmentByTag("navtest_chatlog");
        if( browser != null)
        {
            String collectionid = browser.getOpenCollection();
            if( collectionid != null)
            {
                    FirebaseMessaging.getInstance().subscribeToTopic(collectionid);
                    Log.d(TAG, "re-subscribe complete " + collectionid);
            }
        }
        super.onPause();

    }

    @Override
    protected void onResume(){
        super.onResume();
        //TODO: Create menu again? Pick last menu item?

    }

    public void selectDrawerItem(MenuItem menuItem) {

        menuItem.setChecked(true);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawers();

        //BUG: https://stackoverflow.com/questions/5293850/fragment-duplication-on-fragment-transaction
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        if( menuItem.getGroupId() == 666)
        {
            int id = menuItem.getItemId();

            JSONObject data = menudata.get(id);

            String collectionid = (String) data.get("id");

            String url = EnterMediaConnection.EMINSTITUTE + "/collective/community/index.html?goaltrackerstaff=*&collectionid=" + collectionid;
            String title = (String)data.get("name");

            org.entermediadb.chat2.ui.web.WebViewFragment browser = showBrowser("Chat " + title,url);
            String previouscollection = browser.getOpenCollection();
            if( previouscollection != null && previouscollection != collectionid)
            {
                FirebaseMessaging.getInstance().subscribeToTopic(previouscollection);
            }
            browser.setOpenCollection(collectionid);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(collectionid); //While visable



            //
//            String collectionid = (String) data.get("id");
//            browser.setUrl("https://em9dev.entermediadb.org?collectionid=" + collectionid);


            //https://stackoverflow.com/questions/33606431/add-fragment-to-navigationdrawer-activity
            //https://www.simplifiedcoding.net/android-navigation-drawer-example-using-fragments/


                    //getSupportFragmentManager().findFragmentById(R.id.navtest_chatlog);
//



           // navController.navigate(R.id.nav_gallery); //Switch statment


        }
        else
        {
            //navController.navigate(R.id.nav_gallery); //Switch statment
            //super? Natural?
            Fragment fragment = null;
            Class fragmentClass;
            switch(menuItem.getItemId()) {
                case R.id.nav_tools:
                    String url = EnterMediaConnection.EMINSTITUTE + "/users/activity/recent.html";
                    String title = "Recent Tickets";
                    org.entermediadb.chat2.ui.web.WebViewFragment browser = showBrowser(title,url);
                    return;
                case R.id.nav_gallery:
                    fragmentClass = org.entermediadb.chat2.ui.gallery.GalleryFragment.class;
                    break;
                case R.id.nav_messages:
                    url = EnterMediaConnection.EMINSTITUTE + "/messages/index.html";
                    title = "Direct Messages";
                    browser = showBrowser(title,url);
                    return;
                default:
                    fragmentClass = org.entermediadb.chat2.ui.home.HomeFragment.class;
            }

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the navigation drawer
        }

        /*
        switch (id) {

            case R.id.first:
                navController.navigate(R.id.firstFragment);
                break;

            case R.id.second:
                navController.navigate(R.id.secondFragment);
                break;

            case R.id.third:
                navController.navigate(R.id.thirdFragment);
                break;

        }
        */
        //return true;

    }

    public org.entermediadb.chat2.ui.web.WebViewFragment showBrowser(String inTitle, String inUrl)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        org.entermediadb.chat2.ui.web.WebViewFragment browser = (org.entermediadb.chat2.ui.web.WebViewFragment)
                getSupportFragmentManager().findFragmentByTag("navtest_chatlog");

        if( browser == null)
        {
            //https://developer.android.com/guide/components/fragments.html#Adding
            browser = new org.entermediadb.chat2.ui.web.WebViewFragment();
            ft.add(R.id.nav_host_fragment,browser,"navtest_chatlog").commit();
            ft = getSupportFragmentManager().beginTransaction();
        }
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("X-token",connection.getToken());
        headers.put("X-tokentype",connection.getTokenType());
        headers.put("X-emappversion",connection.getVersion());


        setTitle(inTitle);
        ft.replace(R.id.nav_host_fragment, browser);
        ft.addToBackStack("navtest_chatlog");
        ft.commit();
        browser.setFieldExtraHeaders(headers);
        browser.setUrl(inUrl);


        return browser;
    }

    public void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment

        // fragmentTransaction.replace(R.id.home_container, fragment);

        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }


    private void popupSnackbarForCompleteUpdate() {

        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar =
                Snackbar.make(
                        parentLayout,
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null){
                mAppUpdateManager.completeUpdate();
            }
        });


        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    protected void initUpdateCheck()
    {
        InstallStateUpdatedListener installStateUpdatedListener = new
                InstallStateUpdatedListener() {
                    @Override
                    public void onStateUpdate(InstallState state) {
                        if (state.installStatus() == InstallStatus.DOWNLOADED){
                            popupSnackbarForCompleteUpdate();
                        } else if (state.installStatus() == InstallStatus.INSTALLED){
                            if (mAppUpdateManager != null){
                                mAppUpdateManager.unregisterListener(this);
                            }
                        } else {
                            Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                        }
                    }
                };


        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        int RC_APP_UPDATE_CODE = 666; //Custom code to track with

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo ->
        {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
            {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE, MainActivity.this, RC_APP_UPDATE_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
            else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                popupSnackbarForCompleteUpdate();
            } else {
                Log.e(TAG, "checkForAppUpdateAvailability: nada");
            }
        });
    }

}
