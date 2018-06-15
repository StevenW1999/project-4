package net.azurewebsites.ashittyscheduler.ass;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import net.azurewebsites.ashittyscheduler.ass.Overview.OverviewFragment;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class edittodo extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    //declare Textviews etc
    private TimePickerDialog timePickerDialog;
    private Calendar Eremindercalendar;
    private Calendar calendar;
    private TextView EAccesTime;
    private TextView EDisplayTime;
    private TextView datepickerdialogbutton;
    private TextView Edatepickerdialogbutton;
    private TextView selecteddate;
    private TextView EreminderTime;
    private TextView EreminderDisplayTime;
    private TextView ETitle;
    private TextView EDescription;
    private TextView EEditDate;
    private TextView EReminderDate;
    private TextView EditRepeatText, EditRepeatTypeText,EditmRepeatText, EditNotificationText;
    private Switch EditRepeatSwitch, EditNotificationSwitch;

    // declare variables
    private boolean EditRepeat;
    private String EditRepeatType;
    private boolean Status;
    private String todoId;
    private int CalendarHour;
    private  int CalendarMinute;
    private int EReminderCalendarHour;
    private  int EReminderCalendarMinute;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittodo);

        //set title and description
        ETitle = (TextView)findViewById(R.id.EditTitle) ;
        EDescription = (TextView)findViewById(R.id.EditDescription);

        //set date and reminder date
        EEditDate = (TextView)findViewById(R.id.EditDate);
        EReminderDate = (TextView) findViewById(R.id.EditRDate) ;

        //Set Time Picker
        EAccesTime = (TextView) findViewById(R.id.EditTime);
        EDisplayTime = (TextView) findViewById(R.id.EditTime);

        //Set reminder Time picker
        EreminderTime = (TextView) findViewById(R.id.EditRTime);
        EreminderDisplayTime = (TextView) findViewById(R.id.EditRTime);

        //set repeat text and repeat types
        EditRepeatText = (TextView) findViewById(R.id.ERepeatText) ;
        EditRepeatTypeText = (TextView)findViewById(R.id.EditRepeatType);
        EditmRepeatText = (TextView) findViewById(R.id.EditRepeatType);

        //set notification text
        EditNotificationText = (TextView)findViewById(R.id.ENotificationsTextView) ;

        //set switches
        EditRepeatSwitch = (Switch) findViewById(R.id.EditRepeatSwitch);
        EditNotificationSwitch = (Switch)findViewById(R.id.ENotificationsSwitch) ;

        //refer switches to this class
        EditRepeatSwitch.setOnCheckedChangeListener(this);
        EditNotificationSwitch.setOnCheckedChangeListener(this);

        // get todoId from detailscreen
        Intent intent = getIntent();
        this.todoId = intent.getStringExtra("todoId");


        //get todoId from database
        Pair[] parameters = new Pair[]{
                new Pair("todoId", todoId)
        };
        //web api task
        final HttpTask httpTask = new HttpTask(this.getApplicationContext(),
                HttpMethod.GET,
                "http://ashittyscheduler.azurewebsites.net/api/todo/get",

                new AsyncHttpListener()

                {
                    private ProgressDialog progressDialog;

                    // perform this action right before doing the api task
                    @Override
                    public void onBeforeExecute() {
                        // show a progress dialog (duh)
                        progressDialog = ProgressDialog.show(edittodo.this,
                                "Getting todo",
                                "Please wait");
                    }
                    // perform this action right before if the api task is completed
                    @Override
                    public void onResponse(HttpResponse httpResponse) {
                        int code = httpResponse.getCode();
                        if (code == HttpStatusCode.OK.getCode()) {

                            try {
                                JSONObject todo = new JSONObject(httpResponse.getMessage());

                                //set the hints of the textviews
                                ETitle.setHint(todo.getString("Title"));
                                EDescription.setHint(todo.getString("Description"));
                                EditRepeatType = todo.getString("Repeat_Interval");
                                EditRepeatTypeText.setHint(todo.getString("Repeat_Interval"));
                                Status = todo.getBoolean("Todo_Status");

                                String[] dateTime = todo.getString("Date").split("T");

                                EEditDate.setHint(dateTime[0]);

                                // if there is a time
                                if (dateTime.length > 1) {
                                    // ignore last three characters
                                    EDisplayTime.setHint(dateTime[1].substring(0, dateTime[1].length() - 3));
                                }
                               String[] RdateTime = todo.getString("DateReminder").split("T");


                                EReminderDate.setHint(RdateTime[0]);

                                // if there is a time
                                if (RdateTime.length > 1) {
                                    // ignore last three characters
                                    EreminderDisplayTime.setHint(RdateTime[1].substring(0, RdateTime[1].length() - 3));
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


        //Clock
        EAccesTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delclare clock
                calendar = Calendar.getInstance();
                //declare clock variables
                CalendarHour= calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);

                //create clock pop-up
                timePickerDialog = new TimePickerDialog(edittodo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeStamp = String.format("%02d:%02d", hourOfDay, minute);
                        EDisplayTime.setText(timeStamp);
                    }
                },
                        //show pop-up
                        CalendarHour,CalendarMinute, false);
                timePickerDialog.show();

            }
        });
        EreminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delclare clock
                Eremindercalendar = Calendar.getInstance();
                //declare clock variables
                EReminderCalendarHour= Eremindercalendar.get(Calendar.HOUR_OF_DAY);
                EReminderCalendarMinute = Eremindercalendar.get(Calendar.MINUTE);
                //create clock pop-up
                timePickerDialog = new TimePickerDialog(edittodo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDayr, int minuter) {
                        String timeStamp = String.format("%02d:%02d", hourOfDayr, minuter);
                        EreminderDisplayTime.setText(timeStamp);
                    }
                },
                        //show pop-up
                        EReminderCalendarHour,EReminderCalendarMinute, false);
                timePickerDialog.show();

            }
        });




        datepickerdialogbutton = (TextView) findViewById(R.id.EditDate);
        selecteddate = (TextView)findViewById(R.id.EditDate);

        Edatepickerdialogbutton = (TextView) findViewById(R.id.EditRDate);
        TextView Eselecteddate = (TextView)findViewById(R.id.EditRDate);

        datepickerdialogbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                DialogFragment dialogfragment = new EditDatePickerDialogClass();

                dialogfragment.show(getFragmentManager(), "Date Picker Dialog");

            }
        });
        Edatepickerdialogbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogfragment = new EditReminderDatePickerDialogClass();

                dialogfragment.show(getFragmentManager(), "Date Picker Dialog");
            }
        });
    }

    //finish activity when cancelbutton is clicked
    public void CancelButtonClicked(View view) {

        finish();
    }


    //switches
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        //if repeat switch is checked
        if (EditRepeatSwitch.isChecked()){
            EditRepeatText.setText("Repeat ON");
            //set the Repeat as true
            EditRepeat = true;
            // make the repeat text visible
            EditmRepeatText.setAlpha(1.0f);
            EditRepeatTypeText.setAlpha(1.0f);
            //make repeat text clickable
            EditRepeatTypeText.setEnabled(true);

        }
        else {
            //if repeat switch is unchecked
            //set the text as OFF
            EditRepeatText.setText("Repeat OFF");
            //set the repeat as false
            EditRepeat = false;
            //make repeattype empty
            EditRepeatType = "";
            //make text invisible
            EditmRepeatText.setAlpha(0.0f);
            EditRepeatTypeText.setAlpha(0.0f);

            EditRepeatTypeText.setEnabled(false);

        }
        if (EditNotificationSwitch.isChecked()){
            //if notification switch is checked
            //set the text of the switch
            EditNotificationText.setText("Notifications ON");
            //make the textviews visible and clickable
            EReminderDate.setAlpha(1.0f);
            EreminderTime.setAlpha(1.0f);
            EReminderDate.setClickable(true);
            EreminderTime.setClickable(true);
            EReminderDate.setEnabled(true);
            EreminderTime.setEnabled(true);

        }
        else {
            //if notification switch is checked
            //set the text of the switch
            EditNotificationText.setText("Notifications OFF");
            //make the textviews invisible and unclickable
            EReminderDate.setAlpha(0.0f);
            EreminderTime.setAlpha(0.0f);
            EReminderDate.setClickable(false);
            EreminderTime.setClickable(false);
            EReminderDate.setEnabled(false);
            EreminderTime.setEnabled(false);

        }

    }
    public void selectRepeatType(View v){
        //create array for the intervals
        final String[] items = new String[3];
        items[0] = "Daily";
        items[1] = "Weekly";
        items[2] = "Monthly";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //create pop-up
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                //set the array items in the pop-up
                //set the repeat type on the clicked item
                EditRepeatType = items[item];
                EditRepeatTypeText.setText(EditRepeatType);
                EditmRepeatText.setText(EditRepeatType);

            }

        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    //Calendar
    public static class EditDatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            //create calendar
            final Calendar Editcalendar = Calendar.getInstance();
            //declare calendar variables
            int Eyear = Editcalendar.get(Calendar.YEAR);
            int Emonth = Editcalendar.get(Calendar.MONTH);
            int Eday = Editcalendar.get(Calendar.DAY_OF_MONTH);
            //create calendar pop-up
            DatePickerDialog Editdatepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,Eyear,Emonth,Eday);
            //return calendar pop-up
            return Editdatepickerdialog;

        }
        //function for selecting a date
        public void onDateSet(DatePicker view, int Eyear, int Emonth, int Eday){

            TextView textview = (TextView)getActivity().findViewById(R.id.EditDate);
            //set the text for the selected date
            textview.setText(Eyear + "-" +(Emonth+1)  + "-" +Eday );
        }
    }


    //Reminder Calendar
    public static class EditReminderDatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //create reminder calendar
            final Calendar Eremindercalendar = Calendar.getInstance();
            //declare reminder calendar variables
            int Eryear = Eremindercalendar.get(Calendar.YEAR);
            int Ermonth = Eremindercalendar.get(Calendar.MONTH);
            int Erday = Eremindercalendar.get(Calendar.DAY_OF_MONTH);
            //create reminder calendar pop-up
            DatePickerDialog Editreminderdatepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, Eryear, Ermonth, Erday);
            //return reminder calendar pop-up
            return Editreminderdatepickerdialog;

        }
        //function for selecting a date
        public void onDateSet(DatePicker view, int Eryear, int Ermonth, int Erday) {

            TextView textview = (TextView) getActivity().findViewById(R.id.EditRDate);
            //set the text for the selected date
            textview.setText(Eryear + "-" + (Ermonth + 1) + "-" + Erday);
        }
    }



    //add todo to the listview
    public void EditButtonClicked (View v){
        //declare variables you want to update in the database
        String messageText = ((EditText)findViewById(R.id.EditTitle)).getText().toString();
        String DescText = ((EditText)findViewById(R.id.EditDescription)).getText().toString();
        String dateText = ((TextView)findViewById(R.id.EditDate)).getText().toString();
        String timeText = ((TextView)findViewById(R.id.EditTime)).getText().toString();
        String RdateText = ((TextView)findViewById(R.id.EditRDate)).getText().toString();
        String RtimeText = ((TextView)findViewById(R.id.EditRTime)).getText().toString();
        String Repeat_Interval;
        //create itent for going straight to the overview when edit button is clicked
        Intent intent2 = new Intent();
        //set the class of the intent
        intent2.setClass(edittodo.this, OverviewFragment.class);

        //if Edit Repeat is false "NO INTERVAL" will be added to database instead of selecting a interval
        if (EditRepeat == false){
            Repeat_Interval = "NO INTERVAL";
        }
        //the selected interval will be added
        else {
            Repeat_Interval = ((TextView)findViewById(R.id.EditRepeatType)).getText().toString();

        }

        //create geti]Intent for the todoId
        Intent intent = getIntent();
        //get the todoId from the detailscreen
        String todoId = intent.getStringExtra("todoId");
        Log.d("TODOID", todoId);

        //if one of the textviews isn't filled in, an toast with an error will be given instead of an app crash
        if (messageText.equals("") || dateText.equals("") || timeText.equals("") || RdateText.equals("") || RtimeText.equals("")){
            Toast.makeText(this, "PLEASE FILL IN ALL THE BLANKS", Toast.LENGTH_SHORT).show();

        }
        else {


            // create body parameters
            Pair[] parameters = new Pair[]{
                    new Pair("Id", todoId),
                    new Pair("Title", messageText),
                    new Pair("Description", DescText),
                    new Pair("Date", dateText+ "T" + timeText),
                    new Pair("DateReminder", RdateText+ "T" + RtimeText),
                    new Pair("Todo_Status", Status),
                    new Pair("Repeat", EditRepeat),
                    new Pair("Repeat_Interval", Repeat_Interval)

            };
            // set the web api task
            HttpTask task = new HttpTask(this.getApplicationContext(),
                    //declare the task that will be used
                    HttpMethod.PUT,
                    //give the link for the task
                    "http://ashittyscheduler.azurewebsites.net/api/todo/update",
                    new AsyncHttpListener() {
                        private ProgressDialog progressDialog;

                        //create a pop-up progressbar for the task
                        @Override
                        public void onBeforeExecute() {
                            // show a progress dialog (duh)
                            progressDialog = ProgressDialog.show(edittodo.this,
                                    "Updating todo",
                                    "Please wait");
                        }


                        @Override
                        public void onResponse(HttpResponse httpResponse) {

                            int code = httpResponse.getCode();
                            //give signal if the task is completed
                            if (code == HttpStatusCode.OK.getCode()){
                                Toast.makeText(getApplicationContext(), "TODO UPDATED", Toast.LENGTH_SHORT).show();
                            }
                            //give message if the task fails
                            else{
                                Toast.makeText(getApplicationContext(), "FAILED TO update TODO" + httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onError() {


                        }
                        //remove progress pop-up when the task is finished
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

            //start the activity delcared in the intents
            startActivity(intent2);
        }
    }

}









