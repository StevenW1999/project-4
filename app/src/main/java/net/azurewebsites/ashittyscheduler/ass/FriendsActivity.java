package net.azurewebsites.ashittyscheduler.ass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;
import net.azurewebsites.ashittyscheduler.ass.profile.ProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.friendsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friends");
        AddFriend();
        FriendRequestPage();
        final ArrayList<User> users = new ArrayList<>();
        //usernames.add("Bot");
        this.adapter = new CustomAdapter(this, users);
        final ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(this.adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                User user = ((User) listView.getItemAtPosition(position));
                Log.d("USERNAME:", "onItemClick: " + user);
                Toast.makeText(getApplicationContext(), user.getUsername(), Toast.LENGTH_SHORT).show();

                // Load the chat for this friend (DISABLED!!!)
                //LoadFriendChat(FriendChatActivity.class , o);

                // Load the profile for this friend
                LoadFriendProfile(FriendChatActivity.class , user.getId());
            }
        });
        LoadFriendList();
    }

    public void LoadFriendList() {
        Pair[] parameters = new Pair[]{
                new Pair<>("tokenId",getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("Token" , null))
        };
        final CustomAdapter adapter = this.adapter;
        final ArrayList<User> users = new ArrayList<>();

        HttpTask task = new HttpTask(FriendsActivity.this, HttpMethod.POST, "http://ashittyscheduler.azurewebsites.net/api/friend/GetFriends", new AsyncHttpListener() {
            private ProgressDialog progressDialog;
            @Override
            public void onBeforeExecute() {
                progressDialog = ProgressDialog.show(FriendsActivity.this,
                        "Getting friends",
                        "Please wait");
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                try {
                    JSONArray userList = new JSONArray(httpResponse.getMessage());

                    for(int i =0; i<userList.length(); ++i) {
                        JSONObject user = userList.getJSONObject(i);
                        User userObject = new User();
                        userObject.setId(user.getString("Id"));
                        userObject.setUsername(user.getString("Username"));
                        userObject.setName(user.getString("DisplayName"));
                        String username = user.getString("Username");
                        Log.d("USERS TO FRIEND", username);
                        users.add(userObject);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), "An error occured. Could not load friends. Please try again later ☹", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinishExecuting() {
                progressDialog.dismiss();
                adapter.updateData(users);
            }
        });
        task.setBodyParameters(parameters);
        task.execute();
    }

    private void FriendRequestPage() {
        FloatingActionButton requestsButton = (FloatingActionButton)findViewById(R.id.FriendRequestPage);
        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                //View DialogView = getLayoutInflater().inflate(R.layout.friend_requests,null);
                //builder.setView(DialogView);
                //ListView ls = (ListView)DialogView.findViewById(R.id.requests_list);
                GetRequests();
                /*
                FriendRequestAdapter friendRequestAdapter = new FriendRequestAdapter(FriendsActivity.this,GetRequests());
                ls.setAdapter(friendRequestAdapter);
                final AlertDialog dialog = builder.create();
                dialog.show();*/
            }
        });
    }

    private void GetRequests(){
        final ArrayList<String>list = new ArrayList<>();
        Pair[] parameters = new Pair[]{
                new Pair<>("tokenId",getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("Token" , null))
        };
        HttpTask task = new HttpTask(FriendsActivity.this, HttpMethod.POST, "http://ashittyscheduler.azurewebsites.net/api/friend/getFriendRequests", new AsyncHttpListener() {
            private ProgressDialog progressDialog;
            @Override
            public void onBeforeExecute() {
                progressDialog = ProgressDialog.show(FriendsActivity.this,
                        "Getting requests",
                        "Please wait");
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                try {
                    JSONArray userList = new JSONArray(httpResponse.getMessage());

                    for(int i =0; i<userList.length(); ++i) {
                        JSONObject user = userList.getJSONObject(i);
                        String username = user.getString("Username");
                        Log.d("USERS TO REQUEST", username);
                        list.add(username);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinishExecuting() {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                View DialogView = getLayoutInflater().inflate(R.layout.friend_requests,null);
                builder.setView(DialogView);
                ListView ls = (ListView)DialogView.findViewById(R.id.requests_list);
                FriendRequestAdapter friendRequestAdapter = new FriendRequestAdapter(FriendsActivity.this , list);
                ls.setAdapter(friendRequestAdapter);
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        task.setBodyParameters(parameters);
        task.execute();
    }

    private void AddFriend() {
        FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.AddFriendButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                View DialogView = getLayoutInflater().inflate(R.layout.search_friends,null);
                Button friendAddButton = (Button)DialogView.findViewById(R.id.AddAfriend);
                final EditText usernameGiven = (EditText)DialogView.findViewById(R.id.givenUsername);

                builder.setView(DialogView);
                final AlertDialog dialog = builder.create();
                dialog.show();
                friendAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Send request to : " + usernameGiven.getText(),Toast.LENGTH_SHORT).show();

                        Pair[] parameters = new Pair[] {
                                new Pair<>("UserId", getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("Token" , null)),
                                new Pair<>("UsernameFriend", usernameGiven.getText().toString())
                        };

                        HttpTask task = new HttpTask(getApplicationContext(), HttpMethod.POST ,"https://ashittyscheduler.azurewebsites.net/api/friend/friendRequest",new AsyncHttpListener(){
                            private ProgressDialog progressDialog;
                            @Override
                            public void onBeforeExecute() {
                                //Log.d("TEST SUBMIT Friend", "onBeforeExecute: TESTING ");
                                progressDialog = ProgressDialog.show(FriendsActivity.this,
                                        "Adding friend",
                                        "Please wait");
                            }

                            @Override
                            public void onResponse(HttpResponse httpResponse) {
                                int code = httpResponse.getCode();
                                httpResponse.getMessage();
                                if(code == HttpStatusCode.OK.getCode()){
                                    Toast.makeText(getApplicationContext(),"Request send", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),"Could not submit friend request", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(), "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFinishExecuting() {
                                progressDialog.dismiss();
                            }
                        });
                        task.setBodyParameters(parameters);
                        task.execute();

                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void LoadFriendChat(Class ActivityName , User o) {
        Intent loadPage = new Intent(this,ActivityName);
        loadPage.putExtra("User" , o);
        startActivity(loadPage);
    }

    private void LoadFriendProfile(Class ActivityName, String userId) {
        Intent intent = new Intent();
        intent.setClass(FriendsActivity.this, ProfileActivity.class);
        intent.putExtra("UserId", userId);
        startActivity(intent);
    }
}

class FriendRequestAdapter extends ArrayAdapter<String>{
    private final Context context;
    private ArrayList<String> usernames;

    public  FriendRequestAdapter(Context context , ArrayList<String> usernames){
        super(context, R.layout.customlistviewfriends , usernames);
        this.context = context;
        this.usernames = usernames;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.friend_requests_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.request_username);
        ImageButton accept = rowView.findViewById(R.id.request_accept);
        ImageButton decline = rowView.findViewById(R.id.request_decline);
        textView.setText(this.usernames.get(position));
        final String s = this.usernames.get(position);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ACCEPT REQUEST BUTTON", "onClick: accept: " + s);
                HandleRequest(s,true );
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DECLINE REQUEST BUTTON", "onClick: decline: " + s);
                HandleRequest(s,false);
            }
        });
        return rowView;
    }

    private void loadData(){
        //TO DO
        //REFRESH THE DATA IN THIS LIST VIEW
        final Context context = this.context;
        final FriendRequestAdapter friendRequestAdapter = this;
        Pair[] parameters = new Pair[]{
                new Pair<>("tokenId",context.getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("Token" , null))
        };
        final ArrayList<String> usernames = new ArrayList<>();
        HttpTask task = new HttpTask(context, HttpMethod.POST, "http://ashittyscheduler.azurewebsites.net/api/friend/getFriendRequests", new AsyncHttpListener() {
            private ProgressDialog progressDialog;
            @Override
            public void onBeforeExecute() {
                progressDialog = ProgressDialog.show(context,
                        "Responding to request",
                        "Reloading, please wait");
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                int code = httpResponse.getCode();
                httpResponse.getMessage();
                if(code == HttpStatusCode.OK.getCode()){
                    Toast.makeText(context,"Loaded requests", Toast.LENGTH_SHORT).show();
                    try {
                        JSONArray userList = new JSONArray(httpResponse.getMessage());

                        for(int i =0; i<userList.length(); ++i) {
                            JSONObject user = userList.getJSONObject(i);
                            String username = user.getString("Username");
                            Log.d("USERS TO REQUEST", username);
                            usernames.add(username);
                            //usernames.add("Tester Try");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context,"Could not load requests...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(context, "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinishExecuting() {
                friendRequestAdapter.setData(usernames);
                //friendRequestAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
                Log.d("TESTING RELOAD REQUESTS", "onFinishExecuting: RELOADED DATA");
            }
        });
        task.setBodyParameters(parameters);
        task.execute();
    }


    private void HandleRequest(final String username , boolean accepted){
        final Context context = this.context;
        final FriendRequestAdapter ap = this;
        String bool;
        if(accepted){bool = "true";}
        else{bool = "false";}
        Pair[] parameters = new Pair[]{
          new Pair<>("tokenId" , this.context.getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("Token" , null)),
          new Pair<>("UsernameFriend" , username),
          new Pair<>("Accepted" , bool)
        };
        HttpTask task = new HttpTask(context, HttpMethod.POST, "https://ashittyscheduler.azurewebsites.net/api/friend/FriendRequestResponse", new AsyncHttpListener() {
        private ProgressDialog progressDialog;
        @Override
        public void onBeforeExecute() {
            progressDialog = ProgressDialog.show(context,
                    "Responding to request",
                    "Please wait");
        }

        @Override
        public void onResponse(HttpResponse httpResponse) {
            int code = httpResponse.getCode();
            httpResponse.getMessage();
            if(code == HttpStatusCode.OK.getCode()){
                Toast.makeText(context,"Response send", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,"Could not send response..", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError() {
            Toast.makeText(context, "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinishExecuting() {
            progressDialog.dismiss();
            ap.loadData();
        }
    });
        task.setBodyParameters(parameters);
        task.execute();
    }

    public void setData(ArrayList<String> usernames){
        this.usernames.clear();
        for(String item : usernames){
            this.usernames.add(item);
        }
        notifyDataSetChanged();
    }
}

class CustomAdapter extends ArrayAdapter<User>{

    private final Context context;
    private ArrayList<User> users;

    public CustomAdapter(Context context, ArrayList<User> users) {
        super(context, R.layout.customlistviewfriends, users);
        this.context = context;
        this.users = users;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.customlistviewfriends, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.FriendsUsername);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.FriendsUserImage);
        ImageView hasText = (ImageView)rowView.findViewById(R.id.MessageNotificatie);
        textView.setText(this.users.get(position).getUsername());
        // Change the icon for Windows and iPhone
        String s = this.users.get(position).getUsername();
        /*if (s.startsWith("Windows7") || s.startsWith("iPhone")
                || s.startsWith("Solaris")) {
            imageView.setImageResource(R.drawable.no);
        } else {
            imageView.setImageResource(R.drawable.ok);
        }*/

        return rowView;
    }

    public void updateData(ArrayList<User> users){
        this.users.clear();
        for(User item : users){
            this.users.add(item);
        }
        Collections.sort(this.users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                String s1 = o1.getUsername();
                String s2 = o2.getUsername();
                return s1.compareToIgnoreCase(s2);
            }
        });
        notifyDataSetChanged();
    }
}
