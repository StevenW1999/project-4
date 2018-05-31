package net.azurewebsites.ashittyscheduler.ass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import net.azurewebsites.ashittyscheduler.ass.settings.SettingsFragment;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , AdapterView.OnItemClickListener {
ListView listView;
ArrayList<String> arrayList;
ArrayAdapter<String> arrayAdapter;
String messageText;

    private Fragment fragmentToSet = null;

    private DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener(){

        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frameLayout, fragmentToSet);
            ft.commit();
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("To Do");

        listView = (ListView) findViewById(R.id.textView);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.setClass(MainMenu.this, edittodo.class);
                intent.putExtra(Intent_Constants.INTENT_MESSAGE_DATA,arrayList.get(position).toString());
                intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION, position);

////                startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE_TWO);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(drawerListener);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        TextView t = (TextView)findViewById(R.id.textViewIdForUsername);
        t.setText("UsernameString");
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Agenda) {
            // Handle the camera action
            //getSupportActionBar().setTitle("Agenda");
            LoadNewPage(Agenda.class);
        } else if (id == R.id.nav_ToDo) {
            getSupportActionBar().setTitle("To Do");
        } else if (id == R.id.nav_Notes) {
            getSupportActionBar().setTitle("Notes");
        } else if (id == R.id.nav_Friends) {
            //getSupportActionBar().setTitle("Friends");
            LoadNewPage(FriendsActivity.class);
        } else if (id == R.id.nav_Settings) {

            // fragment to set = settings
            fragmentToSet = new SettingsFragment();

        } else if (id == R.id.nav_Rateus) {
            getSupportActionBar().setTitle("Rate us");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This method is used to load a new activity/screen.
     * To use this method provide it with the class name of the activity
     * you would like to start.
     * @param ActivityName
     */
    private void LoadNewPage(Class ActivityName) {
        Intent loadPage = new Intent(this,ActivityName);
        startActivity(loadPage);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = (TextView)view;
        Toast.makeText(this, "You click on " + tv.getText()+ position, Toast.LENGTH_SHORT).show();
    }
    public void onClick(View v){
        Intent intent = new Intent();
        intent.setClass(MainMenu.this, addtodo.class);
        startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       if(resultCode == Intent_Constants.INTENT_REQUEST_CODE){
           messageText = data.getStringExtra(Intent_Constants.INTENT_MESSAGE_FIELD);
           arrayList.add(messageText);
           arrayAdapter.notifyDataSetChanged();
       }
//       else if(resultCode==Intent_Constants.INTENT_RESULT_CODE_TWO){
//           messageText = data.getStringExtra(Intent_Constants.INTENT_CHANGED_MESSAGE);
//           position = data.getIntExtra(Intent_Constants.INTENT_ITEM_POSITION,-1);
//           arrayList.remove(position);
//           arrayList.add(position,messageText);
//           arrayAdapter.notifyDataSetChanged();
//
//       }
    }
}


