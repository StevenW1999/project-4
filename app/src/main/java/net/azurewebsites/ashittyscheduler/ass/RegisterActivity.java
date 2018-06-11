package net.azurewebsites.ashittyscheduler.ass;

import android.app.ProgressDialog;
import android.content.Intent;
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
                try {
                    registerAccount();
                } catch (IOException e) {
                    showFailed();
                    e.printStackTrace();
                }
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

    /**
     *
     * @throws IOException
     */
    private void registerAccount() throws IOException{

        EditText usernameField = (EditText)findViewById(R.id.usernameField);
        EditText passwordField = (EditText)findViewById(R.id.passwordField);
        EditText passwordField2 = (EditText)findViewById(R.id.passwordField2);

        if(!passwordField.getText().toString().equals(passwordField2.getText().toString()) ){
            passwordField2.setError("Passwords dont match");
            Toast.makeText(this,"Passwords dont match",Toast.LENGTH_SHORT).show();
        }else {
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
