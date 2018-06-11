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

public class LoginActivity extends AppCompatActivity {

    //Setting internet acces policy
    // Test
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    //
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String URLString = "https://ashittyscheduler.azurewebsites.net/api/users/login";
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
                try {
                    signIn();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("Failed connection", ""+ e.getMessage());
                    ShowFail();
                }
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

    private void ShowFail() {
        Toast.makeText(this,"Something went wrong...", Toast.LENGTH_SHORT).show();
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

    private void signIn() throws IOException {

        EditText usernameBox = (EditText)findViewById(R.id.Username);
        EditText passwordBox = (EditText)findViewById(R.id.Password);

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

                        // Save token to sharedpreferences
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

    private void LoadNewPage(Class ActivityName) {
        Intent loadPage = new Intent(this,ActivityName);
        startActivity(loadPage);
    }
}
