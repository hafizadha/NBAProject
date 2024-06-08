package com.example.NBAProject;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NBAProject.Journey.MapFragment;
import com.example.NBAProject.MarketPlace.MarketPage;
import com.example.NBAProject.Sidebar.DrawerAdapter;
import com.example.NBAProject.Sidebar.DrawerItem;
import com.example.NBAProject.Sidebar.SimpleItem;
import com.example.NBAProject.TeamRoster.Roster;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

//This Class is acts as the Main Activity, which is the Side bar that can transition into different pages for decoration
//None of the basic features can be found here
public class NavigationBar extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private final int POS_Team = 0;
    private final int POS_Market = 1;
    private final int POS_Journey = 2;
    private String[] screenTitles;// Collection of sidebar titles
    private Drawable[] screenIcons; //Collection of sidebar icons
    private SlidingRootNav slidingRootNav; //External dependency that gives the sliding animation for the sidebar

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        //This is the container where fragment will be displayed by replacing this layout
        setContentView(R.layout.activity_main);

        //Setting the top bar of phone to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Generate the custom sliding sidebar
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        //Load screen icons and titles for sidebar by calling these methods
        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        //Creating the sidebar recyclerview adapter (display the icons) by sending List of DrawerItem (icons and titles)
        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_Team).setChecked(true),
                createItemFor(POS_Market),
                createItemFor(POS_Journey)
                ));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list); //Ignore error
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        //Initial
        adapter.setSelected(POS_Team);
    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_Team) {
            //Create a new Roster fragment and call the showFragment to replace the container view
            Roster fragment = new Roster();
            TextView PageTitle = findViewById(R.id.pagetitle);

            // Set the text value of the TextView
            PageTitle.setText("Team Roster");
            showFragment(fragment);

        }
        else if(position == POS_Market){
            //Create a new MarketPage and call the showFragment to replace the container view with the fragment
            MarketPage fragment = new MarketPage();
            TextView PageTitle = findViewById(R.id.pagetitle);
            // Set the text value of the TextView
            PageTitle.setText("MARKET");
            showFragment(fragment);
        }
        else if(position == POS_Journey){
            //Create a new MapFragment and call the showFragment to replace the container view
            MapFragment fragment = new MapFragment();
            TextView PageTitle = findViewById(R.id.pagetitle);

            // Set the text value of the TextView
            PageTitle.setText("Journey");
            showFragment(fragment);
        }
        slidingRootNav.closeMenu();

    }


    //Method to switch the container layout of main activity into a wanted fragment
    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        //Create Icons and their colors ( when idle and clicked)
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.NBAblue))
                .withSelectedTextTint(color(R.color.NBAblue));
    }

    private String[] loadScreenTitles() {
        //Retrieve array of String titles in values -> strings.xml and generate an array of String
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        //Retrieve array of String icons in values -> strings.xml
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {

            //get the resource id of the current index from the id
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id); //Get specific drawable based on their resource ID and load into array
            }
        }
        ta.recycle();
        return icons; //Return Drawables (icon images)
    }

    //Get the color resource id given the resource ID
    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
