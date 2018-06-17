package net.azurewebsites.ashittyscheduler.ass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
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

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Map;

public class detailscreen extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    //declare variables
    private String todoId;
    private TextView tv_title;
    private TextView tv_description;
    private TextView tv_date;
    private TextView tv_time;
    private TextView tv_Rdate;
    private TextView tv_Rtime;
    private TextView tv_location;
    private boolean Status;
    private Button statusButton;
    private Switch NotificationSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailscreen);




        //declare variables
        NotificationSwitch = findViewById(R.id.detailNotificationSwitch);
        tv_title = findViewById(R.id.DTitle);
        tv_description = findViewById(R.id.DDetails);
        tv_date = findViewById(R.id.DDate);
        tv_time = findViewById(R.id.DTime);
        tv_Rdate = findViewById(R.id.DreminderDate);
        tv_Rtime = findViewById(R.id.DreminderTime);
        tv_location = findViewById(R.id.DLocation);





        //declare buttons
        statusButton = findViewById(R.id.StatusButton);
        //create a getIntent
        Intent intent = getIntent();
        //gets todoId sent from overview
        this.todoId = intent.getStringExtra("todoId");


        //specify to get todoId  from database
        Pair[] parameters = new Pair[]{
                new Pair("todoId", todoId)
        };
        // set the web api task
        final HttpTask httpTask = new HttpTask(this.getApplicationContext(),
                //declare the task that will be used
                HttpMethod.GET,
                //give the link for the task
                "http://ashittyscheduler.azurewebsites.net/api/todo/get",

                new AsyncHttpListener()

                {
                    //create a pop-up progressbar for the task
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
                                //get the todo from the database matching the todoId
                                JSONObject todo = new JSONObject(httpResponse.getMessage());

                                //give the title of the detailscreen the title of the todo
                                tv_title.setText(todo.getString("Title"));
                                //give the description of the detailscreen the description of the todo
                                tv_description.setText(todo.getString("Description"));

                                //give the location of the detailscreen the location of the todo
                                tv_location.setText(todo.getString("Location"));
                                //if there is no location
                                if (tv_location.getText().equals("")){
                                    //set the location as No Location Given
                                    tv_location.setText("No Location Given.");
                                }
                                //give the location of the detailscreen the location of the todo
                                else {
                                    tv_location.setText(todo.getString("Location"));
                                }

                                //split the time recieved from the todo (date and time are one in the database)
                                String[] dateTime = todo.getString("Date").split("T");

                                tv_date.setText(dateTime[0]);
                                //checks the status of the todo
                                if (todo.getString("Todo_Status") == "false" ){
                                    statusButton.setText("SET AS DONE");
                                    Status = false;


                                }
                                else {
                                    statusButton.setText("SET AS UNDONE");
                                    Status = true;

                                }

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
                            //if an error occurs show the response message
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
        //give URI parameter to the link to get the todo
        httpTask.setUriParameters(parameters);
        httpTask.execute();


    }

    public void EditButtonClicked(View view) {
        //create intent to give the todoId
        Intent intent = new Intent();
        //specify to which class you want to give the intent
        intent.setClass(detailscreen.this, edittodo.class);
        //give the todoId to the specified class
        intent.putExtra("todoId", this.todoId);
        startActivity(intent);

}

public void DeleteButtonClicked(View view){
        //create pop-up
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(detailscreen.this);
        mBuilder.setTitle(R.string.dialog_title);
        mBuilder.setMessage(R.string.dialog_message);
        //set a YES and NO button
        //if the YES button is clicked
        mBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            //specify what paramameter to get from the database
            Pair[] parameters = new Pair[]{
                    new Pair("todoId", todoId)};

            @Override
            public void onClick(DialogInterface dialog, int i) {
                // set the web api task
                HttpTask httpTask = new HttpTask(getApplicationContext(),
                        //declare the task that will be used
                        HttpMethod.DELETE,
                        //give the link for the task
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
                                //finish activity when todo is deleted
                                finish();

                            }


                        }

                );

                httpTask.setUriParameters(parameters);
                httpTask.execute();

            }

        });

        //if NO button is clicked
        mBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss the pop-up
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();

    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



    }

    public void StatusButtonClicked(View view) {

        //specify which parameters you want to use
        Pair[] parameters = new Pair[]{
                new Pair("Id", todoId),
                //change the status to the opposite of the current status
                //False becomes True/ True becomes False
                new Pair("Todo_Status",!Status)

        };
        // set the web api task
        HttpTask task = new HttpTask(this.getApplicationContext(),
                //declare the task that will be used
                HttpMethod.PUT,
                //give the link for the task
                "http://ashittyscheduler.azurewebsites.net/api/todo/updatestatus",
                new AsyncHttpListener() {
                    private ProgressDialog progressDialog;

                    @Override
                    public void onBeforeExecute() {
                        // show a progress dialog (duh)
                        progressDialog = ProgressDialog.show(detailscreen.this,
                                "Updating todo",
                                "Please wait");
                    }

                    @Override
                    public void onResponse(HttpResponse httpResponse) {

                        int code = httpResponse.getCode();

                        if (code == HttpStatusCode.OK.getCode()){
                            Toast.makeText(getApplicationContext(), "TODO UPDATED", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "FAILED TO update TODO" + httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onError() {


                    }

                    @Override
                    public void onFinishExecuting() {
                        // dismiss the progress dialog (duh)
                        progressDialog.dismiss();
                    }
                }
        );

        // set body parameters
        task.setBodyParameters(parameters);
        task.execute();
        finish();


    }
    }




