package net.azurewebsites.ashittyscheduler.ass;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.Overview.OverviewFragment;
import net.azurewebsites.ashittyscheduler.ass.profile.ProfileActivity;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;
import net.azurewebsites.ashittyscheduler.ass.settings.SettingsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , AdapterView.OnItemClickListener {

    private Fragment fragmentToSet = null;
    //Our notification manager,
    private NotificationManager mNotificationManager;
    private Thread notificationThread;
    private Handler notificationThreadHandler;
    private DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener(){

        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            // If there is a fragment to set
            if (fragmentToSet != null) {
                // Replace the framelayout with the fragment to set
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, fragmentToSet)
                        .commit();

                // And reset the variable
                fragmentToSet = null;
            }
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };
    private ArrayList<String> messageIdNotificationsList;

    @Override
    protected void onResume() {
        super.onResume();

        // CHECK IF TOKEN IS VALID
        ApplicationConstants.tokenCheck(this, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("To Do");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(drawerListener);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SetUpNotificationManager();
        SetUpNotificationThread();
        UnreadMessageNotification();

        // START IN TODO SCREEN
        getSupportActionBar().setTitle("To Do");
        // Replace the framelayout with the overview fragment
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new OverviewFragment())
                .commit();

    }

    /**
     * This method sets up our notification thread.
     * If u want notifications to be gathered , supply them in the runnable here!
     */
    private void SetUpNotificationThread() {
        if(this.messageIdNotificationsList == null){
            this.messageIdNotificationsList = new ArrayList<>();
        }
        if(this.notificationThreadHandler == null){
            this.notificationThreadHandler = new Handler();
        }
        if(this.notificationThread == null){
            this.notificationThread = new Thread(){
                public void run(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RunTimeMessageChecker();
                            //Our loop time
                            Log.d("Current thread", "run: "+Thread.currentThread().getId() );
                            notificationThreadHandler.postDelayed(this, 5000);
                        }

                        private void RunTimeMessageChecker() {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().minusSeconds(5));
                                Pair[] parameters = new Pair[] {
                                        new Pair<>("date", date)
                                };
                                Log.d("DATE", "RunTimeMessageChecker: " + date);
                                HttpTask task = new HttpTask(MainMenu.this, HttpMethod.POST, "http://ashittyscheduler.azurewebsites.net/api/chat/getChatNotifications", new AsyncHttpListener() {
                                    @Override
                                    public void onBeforeExecute() {
                                        //Log.d("TESTES", "onBeforeExecute: EXECUTING");
                                    }

                                    @Override
                                    public void onResponse(HttpResponse httpResponse) {
                                        int code = httpResponse.getCode();
                                        if(code == HttpStatusCode.OK.getCode()){
                                            try {
                                                JSONArray messageList = new JSONArray(httpResponse.getMessage());
                                                Log.d("", "onResponse: " + messageList.length());
                                                for(int i =0; i<messageList.length(); ++i) {
                                                    JSONObject message = messageList.getJSONObject(i);
                                                    final String userId = message.getString("SenderId");
                                                    final String text = message.getString("Message");
                                                    String messageId = message.getString("MessageId");

                                                    if(!MainMenu.this.messageIdNotificationsList.contains(messageId)) {
                                                        Pair[] parameters = new Pair[] {
                                                                new Pair<>("userId", userId)
                                                        };
                                                        HttpTask task = new HttpTask(MainMenu.this, HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/users", new AsyncHttpListener() {
                                                            String responseString;
                                                            @Override
                                                            public void onBeforeExecute() {

                                                            }

                                                            @Override
                                                            public void onResponse(HttpResponse httpResponse) {
                                                                try {
                                                                    JSONObject user = new JSONObject(httpResponse.getMessage());
                                                                    responseString = user.getString("Username");
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                            @Override
                                                            public void onError() {

                                                            }

                                                            @Override
                                                            public void onFinishExecuting() {
                                                                Log.i("NOTIFICATION", "onFinishExecuting: CREATED A NOTIFICATION FROM" + responseString);
                                                                CreateMessageNotification(responseString,text);
                                                            }
                                                        });
                                                        task.setUriParameters(parameters);
                                                        task.execute();
                                                        MainMenu.this.messageIdNotificationsList.add(messageId);
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError() {

                                    }

                                    @Override
                                    public void onFinishExecuting() {

                                    }
                                });
                                task.setBodyParameters(parameters);
                                task.execute();
                            }
                        }
                    });
                }
            };
            //Set thread to not start
            //this.notificationThread.start();
        }
    }

    private void CreateMessageNotification(String from, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "High")
                .setSmallIcon(R.drawable.transparentbutt) // notification icon
                .setContentTitle("You received a message from : " + from) // title for notification
                .setContentText(text)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        mBuilder.setContentIntent(pi);
        //mNotificationManager.cancelAll();
        mNotificationManager.notify( ApplicationConstants.RUNTIMEMESSAGENOTIFICATIONID, mBuilder.build());
    }

    private void UnreadMessageNotification() {
        HttpTask task = new HttpTask(this, HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/chat/AmountNotReadMessages", new AsyncHttpListener() {
            @Override
            public void onBeforeExecute() {

            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                int code = httpResponse.getCode();

                if(code == HttpStatusCode.OK.getCode()){
                    int value = Integer.parseInt(httpResponse.getMessage());
                    if(value > 0){

                        String message = "You have " + Integer.toString(value) + " unread messages.";
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "High")
                                .setSmallIcon(R.drawable.transparentbutt) // notification icon
                                .setContentTitle("Unread messages") // title for notification
                                .setContentText(message)// message for notification
                                .setAutoCancel(true); // clear notification after click
                        Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                        mBuilder.setContentIntent(pi);
                        mNotificationManager.cancelAll();
                        mNotificationManager.notify( ApplicationConstants.UNREADMESSAGENOTIFICATIONID, mBuilder.build());
                    }
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinishExecuting() {
                //mNotificationManager.cancel(0);
            }
        });
        task.execute();
    }

    private void SetUpNotificationManager() {
        mNotificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("High",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Log out")
                    .setMessage("Are you sure you want to leave us?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LogoutTask();
                            MainMenu.this.notificationThreadHandler.removeCallbacksAndMessages(null);
                            MainMenu.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No!", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //TextView t = (TextView)findViewById(R.id.textViewIdForUsername);
        //t.setText("UsernameString");

        LinearLayout header = findViewById(R.id.headerMainMenu);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // use the logged in users' own user id to show their profile
                String userId = getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("UserId", null);

                Intent intent = new Intent();
                intent.setClass(MainMenu.this, ProfileActivity.class);
                intent.putExtra("UserId", userId);

                // make sure all the menu items are uncheckedd
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                int size = navigationView.getMenu().size();
                for(int i=0; i<size; ++i) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }

                // start the profile activity
                startActivity(intent);

                // and close the menu
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.LEFT, true);
            }
        });

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

            fragmentToSet = new OverviewFragment();

        } else if (id == R.id.nav_Friends) {
            //getSupportActionBar().setTitle("Friends");
            LoadNewPage(FriendsActivity.class);
        } else if (id == R.id.nav_Settings) {

            getSupportActionBar().setTitle("Settings");

            // set the settings fragment after closing the drawer
            fragmentToSet = new SettingsFragment();

        }
        else if (id == R.id.nav_Logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Log out")
                    .setMessage("Are you sure you want to leave us?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LogoutTask();
                        }
                    })
                    .setNegativeButton("No!", null)
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void LogoutTask(){
        String url = "http://ashittyscheduler.azurewebsites.net/api/users/logout";

        HttpTask logoutTask = new HttpTask(
                MainMenu.this,
                HttpMethod.PUT,
                url,
                new AsyncHttpListener() {

                    private ProgressDialog progressDialog;

                    @Override
                    public void onBeforeExecute() {
                        progressDialog = ProgressDialog.show(MainMenu.this,
                                "Logging out",
                                "Please wait");
                    }

                    @Override
                    public void onResponse(HttpResponse httpResponse) {
                        if (httpResponse.getCode() == HttpStatusCode.OK.getCode()) {
                            //200, we've successfully logged out
                            Toast.makeText(getApplicationContext(), "You have been logged out.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinishExecuting() {
                        progressDialog.dismiss();

                        // delete local token and user id
                        SharedPreferences.Editor editor = getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).edit();
                        editor.remove("Token");
                        editor.remove("UserId");
                        editor.commit();

                        // return to the login screen
                        Intent i = new Intent();
                        i.setClass(MainMenu.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainMenu.this.notificationThreadHandler.removeCallbacksAndMessages(null);
                        startActivity(i);
                        finish();
                    }
                });

        logoutTask.execute();

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
/*
       if(resultCode == Intent_Constants.INTENT_REQUEST_CODE){
           messageText = data.getStringExtra(Intent_Constants.INTENT_MESSAGE_FIELD);
           arrayList.add(messageText);
           arrayAdapter.notifyDataSetChanged();
       }*/
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


