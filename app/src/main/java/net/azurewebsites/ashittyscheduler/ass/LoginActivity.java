package net.azurewebsites.ashittyscheduler.ass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

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
        final Button signIn = (Button) findViewById(R.id.signIn);

        // Sign in button
        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                signIn();
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

        // If there's an active token, attempt a login with the token
        String token = getSharedPreferences(ApplicationConstants.PREFERENCES,Context.MODE_PRIVATE).getString("Token", null);

        if (token != null) {
            // token is available... try to sign in using the token

            //TODO: We should only do this if 'remember me' was checked!!! ok
           automaticSignIn();
        }
    }

    private void automaticSignIn() {

        HttpTask task = new HttpTask(this, HttpMethod.PUT,
                "https://ashittyscheduler.azurewebsites.net/api/users/autologin",
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

                            // we have been automatically logged back in. good.
                            String welcomeMessage = httpResponse.getMessage().replace("\"", "");
                            Toast.makeText(getApplicationContext(), welcomeMessage, Toast.LENGTH_LONG).show();

                            LoadNewPage(MainMenu.class);
                            finish();

                        } else if (code == HttpStatusCode.UNAUTHORIZED.getCode()){
                            Toast.makeText(getApplicationContext(), "Please log in.", Toast.LENGTH_LONG).show();
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

        task.execute();
    }

    /**
     * Takes input from the fields from out layout.
     * When this method is called the login sequence is being started.
     * {@link HttpTask} for connection with the web api to login.
     */
    private void signIn() {

        EditText usernameBox = findViewById(R.id.Username);
        EditText passwordBox = findViewById(R.id.Password);

        // login parameters
        Pair[] parameters = new Pair[] {
                new Pair<>("username", usernameBox.getText().toString()),
                new Pair<>("password", passwordBox.getText().toString())
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
                Toast.makeText(getApplicationContext(), "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
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
