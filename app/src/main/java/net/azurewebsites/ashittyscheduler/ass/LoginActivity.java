package net.azurewebsites.ashittyscheduler.ass;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;
import net.azurewebsites.ashittyscheduler.ass.notifications.NotificationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This is class is the activity for the login screen.
 * This loads the home screen in which you come when you start the application.
 * @see AppCompatActivity for base class.
 * @author Beau
 * @author Robin
 */

public class LoginActivity extends AppCompatActivity {

    //Setting internet acces policy
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    //Class variables. These are static cause they remain the same for all within objects being created.
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String URLString = "https://ashittyscheduler.azurewebsites.net/api/users/login";

    /**
     * @param savedInstanceState needs a bundle instance on creation of the layout activity.
     * @return an object activity containing the layout.
     * Once this has been loaded the program will start to run.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Set main thread policy to acces internet
        StrictMode.setThreadPolicy(policy);
        //Default starting activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Start the notification service
        startNotificationService();

        final Button signIn = findViewById(R.id.signIn);

        final EditText usernameField = findViewById(R.id.Username);
        final EditText passwordField = findViewById(R.id.Password);

        // Sign in button
        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Username and password must contain values
                if (usernameField.getText().toString().length() < 1) {
                    usernameField.setError("Please enter a username.");
                    return;
                }
                if (passwordField.getText().toString().length() < 1) {
                    passwordField.setError("Please enter a password.");
                    return;
                }

                // Save remember me to shared preferences
                CheckBox rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
                SharedPreferences sharedPreferences = getSharedPreferences(ApplicationConstants.PREFERENCES ,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("RememberMe", rememberMeCheckbox.isChecked());
                editor.apply();

                signIn(usernameField.getText().toString(), passwordField.getText().toString());
            }
        });

        // Register new acc button
        final Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LoadNewPage(RegisterActivity.class);
            }
        });

        SharedPreferences sp = getSharedPreferences(ApplicationConstants.PREFERENCES,Context.MODE_PRIVATE);

        // If there's an active token, attempt a login with the token
        String token = sp.getString("Token", null);

        if (token != null) {
            // token is available... try to sign in using the token if remember me is enabled
            boolean rememberMe = sp.getBoolean("RememberMe", false);

            if (rememberMe) {
                automaticSignIn();
            }
        }
    }

    private void startNotificationService() {
        // use this to start and trigger a service
        Intent i= new Intent(this, NotificationService.class);
        // potentially add data to the intent
        //i.putExtra("KEY1", "Value to be used by the service");
        Toast.makeText(this, "Starting notification service (i hope)", Toast.LENGTH_LONG);
        this.startService(i);
    }

    private void automaticSignIn() {

        final ConstraintLayout layout = findViewById(R.id.Login_Layout);

        HttpTask task = new HttpTask(this, HttpMethod.PUT,
                "https://ashittyscheduler.azurewebsites.net/api/users/autologin",
                new AsyncHttpListener() {

                    private ProgressDialog progressDialog;

                    @Override
                    public void onBeforeExecute() {

                        // make layout invisible
                        layout.setVisibility(View.INVISIBLE);

                        // show a progress dialog (duh)
                        progressDialog = ProgressDialog.show(LoginActivity.this,
                                "Logging in",
                                "Please wait");
                    }

                    @Override
                    public void onResponse(HttpResponse httpResponse) {

                        // obtain code
                        int code = httpResponse.getCode();

                        if(code == HttpStatusCode.OK.getCode()){

                            // we have been automatically logged back in. good.
                            String welcomeMessage = httpResponse.getMessage().replace("\"", "");
                            Toast.makeText(getApplicationContext(), welcomeMessage, Toast.LENGTH_LONG).show();

                            LoadNewPage(MainMenu.class);
                            finish();

                        }
                        else {
                            // make layout visible again
                            layout.setVisibility(View.VISIBLE);
                        }

                        if (code == HttpStatusCode.UNAUTHORIZED.getCode()){
                            Toast.makeText(getApplicationContext(), "Please log in.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), "An error occurred. Please try again later ☹", Toast.LENGTH_SHORT).show();

                        // make layout visible again
                        layout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinishExecuting() {
                        progressDialog.dismiss();
                    }
                });

        task.execute();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Is this farewell?")
                .setMessage("Are you sure you wish to exit the application?")
                .setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Nope", null)
                .show();
    }

    /**
     * Takes input from the fields from out layout.
     * When this method is called the login sequence is being started.
     * {@link HttpTask} for connection with the web api to login.
     */
    private void signIn(String username, String password) {

        // login parameters
        Pair[] parameters = new Pair[] {
                new Pair<>("username", username),
                new Pair<>("password", password)
        };

        HttpTask task = new HttpTask(this, HttpMethod.POST,
                "https://ashittyscheduler.azurewebsites.net/api/users/login",
                new AsyncHttpListener() {

            private ProgressDialog progressDialog;

            @Override
            public void onBeforeExecute() {
                // show a progress dialog (duh)
                progressDialog = ProgressDialog.show(LoginActivity.this,
                        "Logging in",
                        "Please wait");
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {

                // obtain code
                int code = httpResponse.getCode();

                if(code == HttpStatusCode.OK.getCode()){

                    // obtain response message (our token in this case)
                    try {
                        JSONObject tokenObj = new JSONObject(httpResponse.getMessage());

                        String token = tokenObj.get("TokenId").toString();
                        String userId = tokenObj.get("UserId").toString();

                        // Save token to shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences(ApplicationConstants.PREFERENCES ,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Token", token);
                        editor.putString("UserId", userId);
                        editor.apply();
                        Log.d("TOKEN ID", token);
                        Log.d("USER ID", userId);
                        Toast.makeText(getApplicationContext(),"Login successful.", Toast.LENGTH_SHORT).show();

                        LoadNewPage(MainMenu.class);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (code == HttpStatusCode.UNAUTHORIZED.getCode()){
                    Toast.makeText(getApplicationContext(), httpResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), "An error occurred. Please try again later ☹", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinishExecuting() {
                // dismiss the progress dialog (duh)
                progressDialog.dismiss();
            }
        });

        task.setBodyParameters(parameters);

        task.execute();

    }

    /**
     * @param ActivityName : Needs a class name and class specified as a parameter.
     * Once this method is called with a correct parameter it will start a new activity.
     * @return Void: but starts a new activity.
     */
    private void LoadNewPage(Class ActivityName) {
        Intent loadPage = new Intent(this,ActivityName);
        startActivity(loadPage);
    }
}
