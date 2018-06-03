package net.azurewebsites.ashittyscheduler.ass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class detailscreen extends AppCompatActivity {


    private TextView tv_title;
    private TextView tv_description;
    private TextView tv_date;
    private TextView tv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailscreen);

        tv_title = findViewById(R.id.detailTitle);
        tv_description = findViewById(R.id.detailDescription);
        tv_date = findViewById(R.id.detailDate);
        tv_time = findViewById(R.id.detailTime);

        Intent intent = getIntent();
        String todoId = intent.getStringExtra("todoId");

        Pair[] parameters = new Pair[]{
                new Pair("todoId", todoId)
        };

        try {
            HttpTask httpTask = new HttpTask(this.getApplicationContext(),
                    HttpMethod.GET,
                    "http://ashittyscheduler.azurewebsites.net/api/todo/get",

                    new AsyncHttpListener()

                    {
                        private ProgressDialog progressDialog;

                        @Override
                        public void onBeforeExecute() {
                            // show a progress dialog (duh)
                            progressDialog = android.app.ProgressDialog.show(detailscreen.this,
                                    "Getting todo",
                                    "Please wait");
                        }

                        @Override
                        public void onResponse(HttpResponse httpResponse) {
                            int code = httpResponse.getCode();
                            if (code == HttpStatusCode.OK.getCode()) {

                                try {
                                    JSONObject todo = new JSONObject(httpResponse.getMessage());

                                    tv_title.setText(todo.getString("Title"));
                                    tv_description.setText(todo.getString("Description"));

                                    String[] dateTime = todo.getString("Date").split("T");

                                    tv_date.setText(dateTime[0]);

                                    // if there is a time
                                    if (dateTime.length > 1) {
                                        // ignore last three characters
                                        tv_time.setText(dateTime[1].substring(0, dateTime[1].length() - 3));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        }

                        @Override
                        public void onError() {
                            //TODO: Handle error
                        }

                        @Override
                        public void onFinishExecuting() {
                            progressDialog.dismiss();
                        }
                    });
             httpTask.setUriParameters(parameters);
             httpTask.execute();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void EditButtonClicked(View view) {



}
}
