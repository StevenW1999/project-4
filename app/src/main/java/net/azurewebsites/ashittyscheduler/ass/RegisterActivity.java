package net.azurewebsites.ashittyscheduler.ass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String URLString = "https://ashittyscheduler.azurewebsites.net/api/users/register";
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

    private void showFailed() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    private void registerAccount() throws IOException{
        EditText usernameField = (EditText)findViewById(R.id.usernameField);
        EditText passwordField = (EditText)findViewById(R.id.passwordField);
        EditText passwordField2 = (EditText)findViewById(R.id.passwordField2);

        if(!passwordField.getText().toString().equals(passwordField2.getText().toString()) ){
            passwordField2.setError("Passwords dont match");
            Toast.makeText(this,"Passwords dont match",Toast.LENGTH_SHORT).show();
        }else{
            StringBuilder tokenUri=new StringBuilder("username=");
            tokenUri.append(URLEncoder.encode(usernameField.getText().toString(),"UTF-8"));
            tokenUri.append("&password=");
            tokenUri.append(URLEncoder.encode(passwordField.getText().toString(),"UTF-8"));
            URL url = new URL(URLString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent",USER_AGENT);
            conn.setRequestProperty("Accept-Language", "UTF-8");
            conn.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
            outputStreamWriter.write(tokenUri.toString());
            outputStreamWriter.flush();
            int code = conn.getResponseCode();
            String content = conn.getContent().toString();
            if(code == 200){
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
                LoadNewPage(LoginActivity.class);
                finish();
            }else if(code == 400){
                //READ RESPONSE BODY
                Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d("Webservice", ""+code);
                throw new IOException();
            }
        }



    }
    private void LoadNewPage(Class ActivityName) {
        Intent loadPage = new Intent(this,ActivityName);
        startActivity(loadPage);
    }
}
