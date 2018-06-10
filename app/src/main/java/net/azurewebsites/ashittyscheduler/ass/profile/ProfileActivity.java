package net.azurewebsites.ashittyscheduler.ass.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends Activity {

    private ImageView iv_avatar;

    private TextView
            tv_displayname,
            tv_username,
            tv_description,
            tv_location;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        iv_avatar = findViewById(R.id.avatar);

        tv_displayname = findViewById(R.id.profile_displayname);
        tv_username = findViewById(R.id.profile_username);
        tv_description = findViewById(R.id.profile_description);
        tv_location = findViewById(R.id.profile_location);

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
                        progressDialog = ProgressDialog.show(ProfileActivity.this,"Loading profile","Please wait");
                    }

                    @Override
                    public void onResponse(HttpResponse httpResponse) {
                        int code = httpResponse.getCode();

                        if (code == HttpStatusCode.OK.getCode()) {
                            try {
                                JSONObject userObj = new JSONObject(httpResponse.getMessage());

                                String username = userObj.getString("Username");
                                String displayName = userObj.getString("DisplayName");
                                String description = userObj.getString("Description");
                                String email = userObj.getString("Email");
                                boolean isOnline = userObj.getBoolean("IsOnline");

                                tv_displayname.setText(displayName);
                                tv_username.setText(username);
                                tv_description.setText(description);

                                // TODO: Set avatar
                                iv_avatar.setImageResource(R.drawable.transparentbutt);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(ProfileActivity.this, "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinishExecuting() {
                        progressDialog.dismiss();
                    }
                });

        task.setUriParameters(parameters);
        task.execute();
    }
}
