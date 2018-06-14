package net.azurewebsites.ashittyscheduler.ass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.service.autofill.RegexValidator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.IOException;

/**
 *
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String URLString = "https://ashittyscheduler.azurewebsites.net/api/users/register";

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final Button register = (Button) findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                registerAccount();
            }
        });
        final Button cancel = (Button) findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LoadNewPage(LoginActivity.class);
                finish();
            }
        });
    }

    /**
     *
     */
    private void showFailed() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    private void registerAccount() {

        EditText usernameField = findViewById(R.id.usernameField);
        EditText passwordField = findViewById(R.id.passwordField);
        EditText passwordField2 = findViewById(R.id.passwordField2);

        if (!usernameField.getText().toString().matches("^[a-zA-Z0-9]+$")) {
            usernameField.setError("Username may only contain letters and numbers.");
            usernameField.requestFocus();
            return;
        }

        // Username must be between 3 and 20 characters
        if (usernameField.getText().toString().length() < 3 ||
                usernameField.getText().toString().length() > 20) {
            usernameField.setError("Username must be between 3 and 20 characters long.");
            usernameField.requestFocus();
            return;
        }

        // password must be >=5 characters
        if (passwordField.getText().toString().length() < 5) {
            passwordField.setError("Password must contain at least 5 characters.");
            passwordField.requestFocus();
        }

        // password cant be the same as the username
        if (passwordField.getText().toString().equals(usernameField.getText().toString())) {
            passwordField.setError("Password cannot be equal to the username.");
            passwordField.requestFocus();
        }

        // passwords have to match
        if(!passwordField.getText().toString().equals(passwordField2.getText().toString()) ){
            passwordField2.setError("Passwords dont match");
            passwordField2.requestFocus();
            Toast.makeText(this,"Passwords dont match",Toast.LENGTH_SHORT).show();
            return;
        }

        // parameters
        Pair[] parameters = new Pair[]{
                new Pair<>("username", usernameField.getText().toString()),
                new Pair<>("password", passwordField.getText().toString())
        };

        HttpTask task = new HttpTask(this, HttpMethod.POST,
                "https://ashittyscheduler.azurewebsites.net/api/users/register",
                new AsyncHttpListener() {

            private ProgressDialog progressDialog;

            @Override
            public void onBeforeExecute() {
                // show a progress dialog (duh)
                progressDialog = ProgressDialog.show(RegisterActivity.this,
                        "Creating account ",
                        "Please wait");
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                // obtain code
                int code = httpResponse.getCode();

                if (code == HttpStatusCode.OK.getCode()) {
                    Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();

                    // TODO: Perform a login here? instead of returning to the login screen
                    LoadNewPage(LoginActivity.class);
                    finish();
                } else if (code == HttpStatusCode.BAD_REQUEST.getCode()) {
                    Toast.makeText(getApplicationContext(), httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError() {
                // TODO: Handle error
                Toast.makeText(getApplicationContext(), "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinishExecuting() {
                progressDialog.dismiss();
            }
        });

        task.setBodyParameters(parameters);

        task.execute();
    }

    /**
     *
     * @param ActivityName
     */
    private void LoadNewPage(Class ActivityName) {
        Intent loadPage = new Intent(this,ActivityName);
        startActivity(loadPage);
    }
}
