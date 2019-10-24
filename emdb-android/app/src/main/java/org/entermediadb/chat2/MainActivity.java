package org.entermediadb.chat2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements OnChatSelectedListener
{
       // MenuItem.OnMenuItemClickListener {

    //TODO: get from fire
    public static final String CONFIG_SERVER = "http://192.168.0.108:8080/assets";

    EnterMediaConnection connection = new EnterMediaConnection();
    List<JSONObject> menudata;
    //private AppBarConfiguration mAppBarConfiguration;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        //See if im loged in already?

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        drawerToggle =  new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);


        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        Intent intent = getIntent();
        if( intent != null) {
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
            //TODO: Handle notification messages
            String idtoken = intent.getStringExtra("idtoken");
            if( idtoken != null) {
                //String email = intent.getStringExtra("useremail");
               // Toast.makeText(MainActivity.this, idtoken, Toast.LENGTH_SHORT).show();
                reloadMenu(idtoken);
                //https://developer.android.com/guide/webapps/webview
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void reloadMenu(final String inIdToken)
    {

            UpdateActivity handler = new UpdateActivity(this)
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

                    JSONObject all = connection.postJson(CONFIG_SERVER + "/mediadb/services/lists/search/librarycollection?googleaccesskey=" + inIdToken,
                            obj);
                    setJsonData(all);
                }

                public void runUiUpdate()
                {
                    //TODO?? getJsonData();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);

                //https://blog.teamtreehouse.com/add-navigation-drawer-android
                //https://stackoverflow.com/questions/31722566/dynamic-adding-item-to-navigationview-in-android

                NavigationView navigationView = findViewById(R.id.nav_view);

                //https://stackoverflow.com/questions/31722566/dynamic-adding-item-to-navigationview-in-android
                Menu menu = navigationView.getMenu();

                SubMenu topChannelMenu = menu.addSubMenu("Chat Channels");

                //TODO: Go to EnterMedia REST API and get projects your a team on

                //Collection all = connection.getAsList("https://entermediadb.org/");

                //topChannelMenu.add(0, 2, 1, "EnterMedia / General");

//                TypefaceSpan span = new TypefaceSpan("serif");
//                SpannableStringBuilder title = new SpannableStringBuilder("My Menu Item Title");
//                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
//                title.setSpan(bss, 0, title.length(), 0);
                //menu.add(0, 0, 1, title);

                    try {

                        JSONObject data = getJsonData();
                        menudata = new ArrayList((Collection)data.get("results"));
                        int index = 0;
                        for(JSONObject objct : menudata)
                        {
                            String name = (String)objct.get("name");
                            MenuItem item = topChannelMenu.add(666, index++, Menu.NONE, name);
                           // item.setOnMenuItemClickListener(MainActivity.this);
                        }
                        //TODO Subscribe to each of these topics

                    } catch (Throwable ex)
                    {
                        //log
                        Toast.makeText(MainActivity.this, "Could not get menu " + ex, Toast.LENGTH_SHORT).show();
                    }


                //item.setIcon()
                //        item.setChecked(true);
                //MenuItem item = menu.findItem(R.id.group2);


//        public boolean onNavigationItemSelected(MenuItem item){
//            // Handle navigation view item clicks here.
//            int id = item.getItemId();
//        }
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.

//                Set<Integer> topLevelDestinations = new HashSet<>();
//
//                topLevelDestinations.add(R.id.nav_home);
//                topLevelDestinations.add(R.id.nav_gallery);
//                topLevelDestinations.add(R.id.nav_tools);
//                mAppBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
//                        .setDrawerLayout(drawer)
//                        .build();
//
//
//                //mAppBarConfiguration
//                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
//                NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, mAppBarConfiguration);
//                NavigationUI.setupWithNavController(navigationView, navController);
              //  navigationView.setNavigationItemSelectedListener(MainActivity.this);

            }
        };
        connection.process(handler);


    }

    public void loginComplete(String inUserName, String inToken)
    {
        /*
        mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
        mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

        findViewById(R.id.signInButton).setVisibility(View.GONE);
        findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);



        String channelId  = "fcm_default_channel";
        String channelName = getString(R.string.default_notification_channel_name);
        NotificationManager notificationManager =
                getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW));

        mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()) + " channel" + channelId );

        Log.d(TAG, "Channel:9 registered");

        FirebaseMessaging.getInstance().subscribeToTopic("9")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg =  "ok";
                        if (!task.isSuccessful()) {
                            msg = "error";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(GoogleSignInActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    */

    }

//    @Override
//    public boolean onMenuItemClick(MenuItem menuItem) {
//
//        menuItem.setChecked(true);
//        return false;
//    }

    //https://www.youtube.com/watch?v=ZyJeyZpIhFA
    //https://github.com/umangburman/Navigation-Drawer-With-Navigation-Component
    //https://guides.codepath.com/android/fragment-navigation-drawer
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        menuItem.setChecked(true);
//        return true;
//    }
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


            org.entermediadb.chat2.ui.chat.WebViewFragment browser = (org.entermediadb.chat2.ui.chat.WebViewFragment)
                    getSupportFragmentManager().findFragmentByTag("navtest_chatlog");

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            String collectionid = (String) data.get("id");
            String url = "https://em9dev.entermediadb.org?collectionid=" + collectionid;

            if( browser == null)
            {
                //https://developer.android.com/guide/components/fragments.html#Adding
                browser = new org.entermediadb.chat2.ui.chat.WebViewFragment();
                browser.setUrl(url);
                ft.add(R.id.nav_host_fragment,browser,"navtest_chatlog");
                ft.replace(R.id.nav_host_fragment, browser);
            }
            else
            {
                browser.setUrl(url);
                ft.replace(R.id.nav_host_fragment, browser);
            }
            ft.addToBackStack(null);
            ft.commit();

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
                case R.id.nav_gallery:
                    fragmentClass = org.entermediadb.chat2.ui.gallery.GalleryFragment.class;
                    break;
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


    public void onCollectionSelected(int position)
    {

    }

}
