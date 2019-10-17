package org.entermediadb.entermediaslide;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        //https://blog.teamtreehouse.com/add-navigation-drawer-android
        //https://stackoverflow.com/questions/31722566/dynamic-adding-item-to-navigationview-in-android

        NavigationView navigationView = findViewById(R.id.nav_view);

        //https://stackoverflow.com/questions/31722566/dynamic-adding-item-to-navigationview-in-android
        Menu menu = navigationView.getMenu();

        SubMenu topChannelMenu = menu.addSubMenu("Chat Channels");


        //TODO: Go to EnterMedia REST API and get projects your a team on

        topChannelMenu.add("EnterMedia / General");
        topChannelMenu.add("CBC / General");
        topChannelMenu.add("PRN / General");
        MenuItem item = topChannelMenu.add("PRN / Emergency");
        item.setChecked(true);


        TypefaceSpan span = new TypefaceSpan("serif");

        SpannableStringBuilder title = new SpannableStringBuilder("My Menu Item Title");

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        title.setSpan(bss, 0, title.length(), 0);
        //menu.add(0, 0, 1, title);
        topChannelMenu.add(0, 0, 1, title);

        //item.setIcon()
        //item.set
        //MenuItem item = menu.findItem(R.id.group2);


//        public boolean onNavigationItemSelected(MenuItem item){
//            // Handle navigation view item clicks here.
//            int id = item.getItemId();
//        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        Set<Integer> topLevelDestinations = new HashSet<>();

        topLevelDestinations.add(R.id.nav_home);
        topLevelDestinations.add(R.id.nav_gallery);
        topLevelDestinations.add(R.id.nav_tools);
        topLevelDestinations.add(R.id.nav_share);
        topLevelDestinations.add(R.id.nav_send);

        mAppBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .setDrawerLayout(drawer)
                .build();


        //mAppBarConfiguration
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



    }

    //https://www.youtube.com/watch?v=ZyJeyZpIhFA
    //https://github.com/umangburman/Navigation-Drawer-With-Navigation-Component
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawers();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        int id = menuItem.getItemId();

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
        return true;

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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
