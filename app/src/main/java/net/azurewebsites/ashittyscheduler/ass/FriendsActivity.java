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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.friendsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friends");
        AddFriend();
        FriendRequestPage();

        LoadFriendList();
        final ArrayList<String> usernames = new ArrayList<>();
        usernames.add("Test");
        usernames.add("Test2");
        CustomAdapter adapter = new CustomAdapter(this, usernames);
        final ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                String o = listView.getItemAtPosition(position).toString();
                Log.d("USERNAME:", "onItemClick: " + o);
                Toast.makeText(getApplicationContext(), o, Toast.LENGTH_SHORT).show();
                LoadFriendChat(FriendChatActivity.class , o);
            }
        });
    }

    public void LoadFriendList() {
        //...
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
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not get friend requests", Toast.LENGTH_SHORT).show();
        }
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
                        try {
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
                            //task.setBodyParameters(parameters);
                            //task.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void LoadFriendChat(Class ActivityName , String o) {
        Intent loadPage = new Intent(this,ActivityName);
        loadPage.putExtra("Username" , o);
        startActivity(loadPage);
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
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(ArrayList<String> usernames){
        this.usernames.clear();
        for(String item : usernames){
            this.usernames.add(item);
        }
        notifyDataSetChanged();
    }
}

class CustomAdapter extends ArrayAdapter<String>{

    private final Context context;
    private ArrayList<String> usernames;

    public CustomAdapter(Context context, ArrayList<String> usernames) {
        super(context, R.layout.customlistviewfriends, usernames);
        this.context = context;
        this.usernames = usernames;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.customlistviewfriends, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.FriendsUsername);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.FriendsUserImage);
        ImageView hasText = (ImageView)rowView.findViewById(R.id.MessageNotificatie);
        textView.setText(this.usernames.get(position));
        // Change the icon for Windows and iPhone
        String s = this.usernames.get(position);
        /*if (s.startsWith("Windows7") || s.startsWith("iPhone")
                || s.startsWith("Solaris")) {
            imageView.setImageResource(R.drawable.no);
        } else {
            imageView.setImageResource(R.drawable.ok);
        }*/

        return rowView;
    }
}
