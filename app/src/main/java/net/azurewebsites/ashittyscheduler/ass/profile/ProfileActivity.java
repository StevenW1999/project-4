package net.azurewebsites.ashittyscheduler.ass.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.ApplicationConstants;
import net.azurewebsites.ashittyscheduler.ass.FriendChatActivity;
import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.User;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends Activity {

    private ImageView
            iv_avatar,
            iv_status;

    private TextView
            tv_displayname,
            tv_username,
            tv_description,
            tv_status,
            tv_location;

    private Button
            btn_friend,
            btn_chat;

    private ConstraintLayout cs_layout;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Layout
        cs_layout = findViewById(R.id.profile_layout);
        cs_layout.setVisibility(View.INVISIBLE);

        // Image views
        iv_avatar = findViewById(R.id.avatar);
        iv_status = findViewById(R.id.statusIndicator);

        // Text fields
        tv_displayname = findViewById(R.id.profile_displayname);
        tv_username = findViewById(R.id.profile_username);
        tv_description = findViewById(R.id.profile_description);
        tv_status = findViewById(R.id.statusText);
        tv_location = findViewById(R.id.profile_location);

        // Buttons
        btn_friend = findViewById(R.id.friendButton);
        btn_chat = findViewById(R.id.chatButton);

        Intent intent = getIntent();

        // save the user id that was passed to this activity (the user to show the profile of)
        this.userId = intent.getExtras().getString("UserId", "");

        // and, load the profile!
        loadProfile();
    }

    public void loadProfile() {

        // parameters
        Pair[] parameters = new Pair[] {
                new Pair<>("userId", userId),
        };

        HttpTask task = new HttpTask(this,
                HttpMethod.GET,
                "http://ashittyscheduler.azurewebsites.net/api/users",
                new AsyncHttpListener() {

                    private ProgressDialog progressDialog;

                    @Override
                    public void onBeforeExecute() {
                        cs_layout.setVisibility(View.INVISIBLE);
                        progressDialog = ProgressDialog.show(ProfileActivity.this,"Loading profile","Please wait");
                    }

                    @Override
                    public void onResponse(HttpResponse httpResponse) {
                        int code = httpResponse.getCode();

                        if (code == HttpStatusCode.OK.getCode()) {
                            try {
                                JSONObject userObj = new JSONObject(httpResponse.getMessage());

                                final User user = User.fromJson(userObj);

                                tv_displayname.setText(user.getName());
                                tv_username.setText("@" + user.getUsername());

                                tv_description.setText(user.getDescription());

                                // TODO: Set avatar
                                iv_avatar.setImageResource(R.drawable.winking_face);

                                // Is this my own profile?
                                boolean isSelf = (user.getId().equals(getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("UserId", null)));

                                // Set status indicator
                                if (user.isOnline()) {
                                    // ONLINE
                                    iv_status.setImageResource(android.R.drawable.presence_online);
                                    tv_status.setText("Online");
                                }
                                else {
                                    // OFFLINE
                                    iv_status.setImageResource(android.R.drawable.presence_offline);
                                    tv_status.setText("Offline");
                                }

                                if (isSelf) {
                                    // This is my own profile... no need to chat with myself or add myself as a friend
                                    btn_friend.setVisibility(View.INVISIBLE);
                                    btn_chat.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    if (user.isFriend()) {
                                        // User is already my friend, change to remove button
                                        SetFriendButton(FriendButtonState.REMOVE);
                                    }
                                    else {
                                        // User is not a friend yet, 'send friend request' button
                                        SetFriendButton(FriendButtonState.ADD);
                                    }

                                    // Chat button action
                                    btn_chat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent loadPage = new Intent();
                                            loadPage.setClass(ProfileActivity.this, FriendChatActivity.class);
                                            loadPage.putExtra("User" , user);
                                            startActivity(loadPage);
                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(ProfileActivity.this, "Could not load profile. Please try again later ☹", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFinishExecuting() {
                        cs_layout.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });

        task.setUriParameters(parameters);
        task.execute();
    }

    private void SetFriendButton(FriendButtonState state) {
        switch(state) {
            case ADD:
                btn_friend.setText("Send friend request");
                btn_friend.setBackgroundColor(Color.GREEN);
                btn_friend.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_input_add,0, 0, 0);
                btn_friend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddFriendAction(userId);
                    }
                });
                break;
            case PENDING:
                btn_friend.setText("Cancel friend request");
                btn_friend.setBackgroundColor(Color.rgb(255, 102, 0));
                btn_friend.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_input_delete,0, 0, 0);
                btn_friend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: Cancel friend request
                    }
                });
                break;
            case REMOVE:
                btn_friend.setText("Remove friend");
                btn_friend.setBackgroundColor(Color.RED);
                btn_friend.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete,0, 0, 0);
                btn_friend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RemoveFriendAction(userId);
                    }
                });
                break;
        }
    }


    /**
     * This function removes a friend who is specified as input parameter.
     * @param userId is the ID of the friend to delete.
     */
    private void RemoveFriendAction(String userId) {
        // parameters
        Pair[] parameters = new Pair[] {
                new Pair<>("friendId", userId),
        };

        HttpTask task = new HttpTask(this, HttpMethod.POST, "http://ashittyscheduler.azurewebsites.net/api/friend/RemoveFriend", new AsyncHttpListener() {
            private ProgressDialog progressDialog;
            @Override
            public void onBeforeExecute() {
                progressDialog = ProgressDialog.show(ProfileActivity.this,"Removing friend","Please wait");
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                int code = httpResponse.getCode();

                if(code == HttpStatusCode.OK.getCode()){
                    Toast.makeText(ProfileActivity.this,"Removed friend.", Toast.LENGTH_SHORT).show();
                    SetFriendButton(FriendButtonState.ADD);
                } else {
                    Toast.makeText(ProfileActivity.this,"Could not remove friend.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(ProfileActivity.this, "An error occurred. Please try again later ☹", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinishExecuting() {
                progressDialog.dismiss();
            }
        });
        task.setBodyParameters(parameters);
        task.execute();
    }

    private void AddFriendAction(String userId) {
        // parameters
        Pair[] parameters = new Pair[] {
                new Pair<>("friendId", userId),
        };

        HttpTask task = new HttpTask(this, HttpMethod.POST, "http://ashittyscheduler.azurewebsites.net/api/friend/friendRequestById", new AsyncHttpListener() {
            private ProgressDialog progressDialog;
            @Override
            public void onBeforeExecute() {
                progressDialog = ProgressDialog.show(ProfileActivity.this,"Sending friend request","Please wait");
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                int code = httpResponse.getCode();

                if(code == HttpStatusCode.OK.getCode()){
                    Toast.makeText(ProfileActivity.this,"Sent friend request.", Toast.LENGTH_SHORT).show();
                    SetFriendButton(FriendButtonState.PENDING);
                } else {
                    Toast.makeText(ProfileActivity.this,"Could not submit friend request.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(ProfileActivity.this, "An error occurred. Please try again later ☹", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinishExecuting() {
                progressDialog.dismiss();
            }
        });
        task.setBodyParameters(parameters);
        task.execute();
    }

}
