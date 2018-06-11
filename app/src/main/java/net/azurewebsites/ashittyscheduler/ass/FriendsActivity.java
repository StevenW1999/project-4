package net.azurewebsites.ashittyscheduler.ass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 *Friends activity class
 * @see AppCompatActivity for base constructor
 * @see android.view.LayoutInflater.Factory for the inflater used in our list view adapter.
 * @author Robin
 */
public class FriendsActivity extends AppCompatActivity {
    private CustomAdapter adapter;
    private Thread getFriendsThread;
    private Handler aHandler = new Handler();

    /**
    Method that is called when this activity is created.
    Within this method we define certain buttons and set up our initializer.
    Also we define our class variables within this method.
     */
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

                User o = ((User) listView.getItemAtPosition(position));
                Log.d("USERNAME:", "onItemClick: " + o);
                Toast.makeText(getApplicationContext(), o.getUsername(), Toast.LENGTH_SHORT).show();
                LoadFriendChat(FriendChatActivity.class , o);
            }
        });
        //LoadFriendList();
        StartGetFriendsThread();
    }
    /**
    This method defines what happens when the back button is pressed.
    In our case it accesses the handler that controls the thread.
    When within the handler its stops the thread from executing.
     */
    @Override
    public void onBackPressed() {
        //To stop the thread
        aHandler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }

    /**
    This method is called when the back button is pressed and the result is coming back on this activity.
    This method restarts our thread through accessing the handler.
    Uses the getFriendsThread that is defined as a class variable.
    Uses the aHandler to acces our thread.
     */
    @Override
    protected void onResume() {
        super.onResume();
        StartGetFriendsThread();
        //Code to refresh listview
    }

    /**
    Method that connects to our web api to check if u received any new messages.
    This method is called from our getFriendsThread.
    Within this method we use the HTTPTASK to receive information from our web api.
     */
    public void LoadMessageChecker(){
        final CustomAdapter adapter = this.adapter;
        final ArrayList<User> users = new ArrayList<>();
        try {
            HttpTask task = new HttpTask(FriendsActivity.this, HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/chat/checkMessage", new AsyncHttpListener() {
                @Override
                public void onBeforeExecute() {
                    //Do nothing
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
                            Log.d("Messages from :", username);
                            users.add(userObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError() {

                }

                @Override
                public void onFinishExecuting() {
                    adapter.updateMessageChecker(users);
                }
            });
            task.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    Method that connects to our web api to load ur friends to the application.
    This method is called from our getFriendsThread.
    Within this method we use the HTTPTASK to receive information from our web api.
    This method is responsible for loading ur friends.
     */
    public void LoadFriendList() {
        Pair[] parameters = new Pair[]{
                new Pair<>("tokenId",getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("Token" , null))
        };
        final CustomAdapter adapter = this.adapter;
        final ArrayList<User> users = new ArrayList<>();
        try {
            HttpTask task = new HttpTask(FriendsActivity.this, HttpMethod.POST, "http://ashittyscheduler.azurewebsites.net/api/friend/GetFriends", new AsyncHttpListener() {
                //private ProgressDialog progressDialog;
                @Override
                public void onBeforeExecute() {
                    //progressDialog = ProgressDialog.show(FriendsActivity.this,
                    //        "Getting friends",
                    //        "Please wait");
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
                    //progressDialog.dismiss();
                    adapter.updateData(users);
                }
            });
            task.setBodyParameters(parameters);
            task.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    This method is called from our UI thread.
    When this method is called it sets up our button action to get requests from other users.
    After completion of this method you can click on the floating action button to see ur requests.
     */
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

    /**
    This method is called after u click the get request button.
    This load the current requests u have from other users.
    This uses the HTTP Task to get information from our web api.
    After the completion of this method it allows you to see open request to accept or decline.
     */
    private void GetRequests(){
        //New arraylist of strings to store the usernames of the requests here.
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

    /**
    This method defines the action for the add a friend button.
    This is executed on the main thread.
    This will load a new dialog in which you can send a request to another user to become friends.
    After sending a request it will use the web api through a http task action.
     */
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

    /**
    Sets up the method for loading a new activity.
    When this method is called it loads a chat with the specified user.
    This user is specified through clicking on a user in the listview.
     @param ActivityName : takes an specified activity class.
     @param o : takes a user object.
     */
    private void LoadFriendChat(Class ActivityName , User o) {
        Intent loadPage = new Intent(this,ActivityName);
        loadPage.putExtra("User" , o);
        aHandler.removeCallbacksAndMessages(null);
        startActivity(loadPage);

    }

    /**
    Assign thread to handler, otherwise no way to properly stop the thread from running
    Every 5 seconds gets the friends from our webservice
    Calls the "LoadFriendList" method.
    Uses a different thread to perform its designated tasks
     */
    private void StartGetFriendsThread(){
        this.getFriendsThread = new Thread(){
            public void run(){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadFriendList();
                                LoadMessageChecker();
                                aHandler.postDelayed(this, 5000);
                            }
                        });
                }
            };
        this.getFriendsThread.start();
    }
}

/**
 * Adapter for dialog that shows you open requests from other users
 * {@link ArrayAdapter} for base class.
 * Contains class variables.
 * Also handles requests once you clicked on it.
 */
class FriendRequestAdapter extends ArrayAdapter<String>{
    private final Context context;
    private ArrayList<String> usernames;

    /**
     * Initializes the Friendrequest adapter.
     * @param context takes an context as input parameter.
     * @param usernames takes an arraylist of String containing usernames.
     * @return a friendRequestAdapter.
     */
    public  FriendRequestAdapter(Context context , ArrayList<String> usernames){
        super(context, R.layout.customlistviewfriends , usernames);
        this.context = context;
        this.usernames = usernames;
    }

    /**
     * @param position takes a position of the specified object.
     * @param convertView takes a view as input.
     * @param parent takes a ViewGroup as input.
     * @return A view that is showed in the dialog after executing this method.
     */
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

    /**
     * This method performs a {@link HttpTask} to get friendrequests.
     * After executing this method it will add the data it got in the form of showing the usernames of users u have gotten requests from.
     * It also shows a alert dialog while getting information of new friendrequests.
     * It connects to out web api through the {@link HttpTask}.
     */
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

    /**
     * @param username : takes an username as input. This specifies the user u responded to with the request.
     * @param accepted : takes a boolean accept that specifies whether u accepted the request or not.
     * @return void : handles the request that is specified by the input given.
     * It will execute a {@link HttpTask} to complete the request with the specified action.
     * This action can be "Decline" or "Accept".
     */
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

    /**
     * @param usernames takes an Arraylist of usernames that defines the requests you have gotten from.
     * This method will reset the arraylist that contains the username of who you have gotten requests from.
     * After executing this method the notifyChanged is called from th superclas to reload the view.
     * This way it shows the new requests after executing this method.
     */
    public void setData(ArrayList<String> usernames){
        this.usernames.clear();
        for(String item : usernames){
            this.usernames.add(item);
        }
        notifyDataSetChanged();
    }
}

/**
 * Custom adapter for the list of friends that is being shown
 *  {@link ArrayAdapter} for base class
 *  Contains class variables that have dependency on the input parameter of the object.
 */
class CustomAdapter extends ArrayAdapter<User>{

    private final Context context;
    private ArrayList<User> users;
    private ArrayList<User> messagesFrom = new ArrayList<>();

    /**
     * Initializes the CustomAdapter adapter.
     * @param context takes an context as input parameter.
     * @param users takes an arraylist of user objects containing other users that are grabbed from our database..
     * @return a Custom_adapter for the listview of friends in the application.
     */
    public CustomAdapter(Context context, ArrayList<User> users) {
        super(context, R.layout.customlistviewfriends, users);
        this.context = context;
        this.users = users;
    }

    /**
     * @param position takes a position of the specified object.
     * @param convertView takes a view as input.
     * @param parent takes a ViewGroup as input.
     * @return A view that is showed in the dialog after executing this method.*
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("TESTING", "getView: CALLED VIEW");
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.customlistviewfriends, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.FriendsUsername);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.FriendsUserImage);
        ImageView hasMessage = (ImageView)rowView.findViewById(R.id.MessageNotificatie);
        if(this.messagesFrom != null && this.messagesFrom.size() != 0){
            for(User item : this.messagesFrom){
                if(item.getId().equals( this.users.get(position).getId())){
                    hasMessage.setVisibility(View.VISIBLE);
                }
                else {
                    hasMessage.setVisibility(View.INVISIBLE);
                }
            }
        }else{
            hasMessage.setVisibility(View.INVISIBLE);
        }
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

    /**
     * @param users takes a arraylist of user objects.
     * Updates the data of users in ur friendlist.
     * Also sorts ur friends on alphabetical order.
     */
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
    }

    /**
     * @param users takes an arraylist of users as input.
     * Checks whether u have gotten any new messages from these users.
     * Clears the existing list of users and replaces it with the newly generated one that has been passed as a parameter.
     */
    public void updateMessageChecker(ArrayList<User>users){
        this.messagesFrom.clear();
        for(User item : users){
            this.messagesFrom.add(item);
        }
        updateView();
    }

    /**
     * Notifies that the data has changed.
     * This calls the getview of this object.
     */
    private void updateView(){
        notifyDataSetChanged();
    }
}
