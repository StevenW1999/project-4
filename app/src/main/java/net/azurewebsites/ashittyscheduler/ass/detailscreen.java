package net.azurewebsites.ashittyscheduler.ass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
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

public class detailscreen extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    private String todoId;
    private TextView tv_title;
    private TextView tv_description;
    private TextView tv_date;
    private TextView tv_time;
    private TextView tv_Rdate;
    private TextView tv_Rtime;
    private Switch statusSwitch;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailscreen);

        tv_title = findViewById(R.id.DTitle);
        tv_description = findViewById(R.id.DDetails);
        tv_date = findViewById(R.id.DDate);
        tv_time = findViewById(R.id.DTime);
        tv_Rdate = findViewById(R.id.DreminderDate);
        tv_Rtime = findViewById(R.id.DreminderTime);
        statusSwitch = findViewById(R.id.StatusSwitch);
        statusSwitch.setOnCheckedChangeListener(this);

        Intent intent = getIntent();
        this.todoId = intent.getStringExtra("todoId");



        Pair[] parameters = new Pair[]{
                new Pair("todoId", todoId)
        };

        final HttpTask httpTask = new HttpTask(this.getApplicationContext(),
                HttpMethod.GET,
                "http://ashittyscheduler.azurewebsites.net/api/todo/get",

                new AsyncHttpListener()

                {
                    private ProgressDialog progressDialog;

                    @Override
                    public void onBeforeExecute() {
                        // show a progress dialog (duh)
                        progressDialog = ProgressDialog.show(detailscreen.this,
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
                                String[] RdateTime = todo.getString("DateReminder").split("T");

                                tv_Rdate.setText(RdateTime[0]);

                                // if there is a time
                                if (RdateTime.length > 1) {
                                    // ignore last three characters
                                    tv_Rtime.setText(RdateTime[1].substring(0, RdateTime[1].length() - 3));
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                        else{
                            Toast.makeText(getApplicationContext(), "FAILED TO GET TODO" + httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
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


    }

    public void EditButtonClicked(View view) {
        Intent intent = new Intent();
        intent.setClass(detailscreen.this, edittodo.class);

        intent.putExtra("todoId", this.todoId);
        startActivity(intent);

}

public void DeleteButtonClicked(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(detailscreen.this);
        mBuilder.setTitle(R.string.dialog_title);
        mBuilder.setMessage(R.string.dialog_message);
        mBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            Pair[] parameters = new Pair[]{
                    new Pair("todoId", todoId)};

            @Override
            public void onClick(DialogInterface dialog, int i) {

                HttpTask httpTask = new HttpTask(getApplicationContext(),
                        HttpMethod.DELETE,
                        "http://ashittyscheduler.azurewebsites.net/api/todo/delete",

                        new AsyncHttpListener() {


                            @Override
                            public void onBeforeExecute() {

                            }

                            @Override
                            public void onResponse(HttpResponse httpResponse) {
                                int code = httpResponse.getCode();

                                if (code == HttpStatusCode.OK.getCode()){
                                    Toast.makeText(getApplicationContext(), "TODO DELETED", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "FAILED TO DELETE TODO" + httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("DELETETODO", httpResponse.getMessage());
                                }

                            }

                            @Override
                            public void onError() {


                            }

                            @Override
                            public void onFinishExecuting() {
                                finish();

                            }


                        }

                );

                httpTask.setUriParameters(parameters);
                httpTask.execute();

            }

        });
        mBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (statusSwitch.isChecked()){
            Toast.makeText(getApplicationContext(), "HI ", Toast.LENGTH_SHORT).show();
            statusSwitch.setText("DONE");
//            try {
//
//
//                // create body parameters
//                Pair[] parameters = new Pair[]{
//                        new Pair("Id", todoId),
//                        new Pair("Todo_Status", statusSwitch)
//
//                };
//
//                HttpTask task = new HttpTask(this.getApplicationContext(),
//                        HttpMethod.PUT,
//                        "http://ashittyscheduler.azurewebsites.net/api/todo/update",
//                        new AsyncHttpListener() {
//                            private ProgressDialog progressDialog;
//
//                            @Override
//                            public void onBeforeExecute() {
//                                // show a progress dialog (duh)
//                                progressDialog = android.app.ProgressDialog.show(detailscreen.this,
//                                        "Updating todo",
//                                        "Please wait");
//                            }
//
//                            @Override
//                            public void onResponse(HttpResponse httpResponse) {
//
//                                int code = httpResponse.getCode();
//
//                                if (code == HttpStatusCode.OK.getCode()){
//                                    Toast.makeText(getApplicationContext(), "TODO UPDATED", Toast.LENGTH_SHORT).show();
//                                }
//                                else{
//                                    Toast.makeText(getApplicationContext(), "FAILED TO update TODO" + httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//
//
//                            }
//
//                            @Override
//                            public void onError() {
//
//
//                            }
//
//                            @Override
//                            public void onFinishExecuting() {
//                                // dismiss the progress dialog (duh)
//                                progressDialog.dismiss();
//                            }
//                        }
//                );
//
//                // set body parameters
//                task.setBodyParameters(parameters);
//                task.execute();
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//
//            finish();
//        }
        }
    }}


