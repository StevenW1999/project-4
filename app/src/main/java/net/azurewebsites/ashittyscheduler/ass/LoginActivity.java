package net.azurewebsites.ashittyscheduler.ass;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.spark.submitbutton.SubmitButton;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    //Setting internet acces policy
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    //
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String URLString = "https://ashittyscheduler.azurewebsites.net/api/users/login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set main thread policy to acces internet
        StrictMode.setThreadPolicy(policy);
        //Default starting acitvity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Button signIn = (Button) findViewById(R.id.signIn);
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
        final Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LoadNewPage(RegisterActivity.class);
            }
        });
    }

    private void ShowFail() {
        Toast.makeText(this,"Something went wrong...",Toast.LENGTH_SHORT).show();
    }

    private void signIn() throws IOException {
        EditText usernameBox = (EditText)findViewById(R.id.Username);
        EditText passwordBox = (EditText)findViewById(R.id.Password);
        StringBuilder tokenUri=new StringBuilder("username=");
        tokenUri.append(URLEncoder.encode(usernameBox.getText().toString(),"UTF-8"));
        tokenUri.append("&password=");
        tokenUri.append(URLEncoder.encode(passwordBox.getText().toString(),"UTF-8"));
        URL url = new URL(URLString);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("User-Agent", USER_AGENT);
        httpConnection.setRequestProperty("Accept-Language", "UTF-8");
        httpConnection.setDoOutput(true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpConnection.getOutputStream());
        outputStreamWriter.write(tokenUri.toString());
        outputStreamWriter.flush();
        int code  = httpConnection.getResponseCode();
        if(code == 200){
            Toast.makeText(this,"Logged in" , Toast.LENGTH_LONG).show();
            LoadNewPage(MainMenu.class);
            finish();
        } else if (code==403){
            Log.d("Internet connection", "Response = Doing toast , failed auth ");
            Toast.makeText(this,"Wrong username or password" , Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Something went wrong.....", Toast.LENGTH_SHORT).show();
            throw new IOException(Integer.toString(code));
        }
        Log.d("Internet connection", "Response code = " + code);
    }

    private void LoadNewPage(Class ActivityName) {
        Intent loadPage = new Intent(this,ActivityName);
        startActivity(loadPage);
    }
}
