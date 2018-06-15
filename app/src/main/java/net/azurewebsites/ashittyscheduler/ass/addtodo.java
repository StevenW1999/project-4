package net.azurewebsites.ashittyscheduler.ass;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import java.io.IOException;
import java.util.Calendar;

import static android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;

public class addtodo extends AppCompatActivity{
    //declare Textviews etc
private TimePickerDialog timePickerDialog;
private Calendar calendar;
private TextView AccesTime;
private Calendar remindercalendar;
private TextView DisplayTime;
private TextView repeattText;
private TextView reminderdatepickerdialogbutton;
private TextView datepickerdialogbutton;
private TextView selecteddate;
private TextView reminderselecteddate;
private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText, mRepeatIntervalText;
private Switch repeatSwitch, notificationSwitch;
private TextView notificationText;
private AlarmManager alarmManager;
private PendingIntent alarmIntent;
private TextView reminderTime;
private TextView reminderDisplayTime;
private TextView reminderDate;


// declare variables
private Boolean Repeat;
private String mRepeat;
private String mRepeatNo;
private String mRepeatType;
private TextView notificationsText;
private int ReminderCalendarHour;
private  int ReminderCalendarMinute;
private String format;
private int CalendarHour;
private  int CalendarMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);


        //Set Time Picker
        AccesTime = (TextView) findViewById(R.id.timePlainText);
        DisplayTime = (TextView) findViewById(R.id.timePlainText);
        //set notifcation text
        notificationsText = (TextView)findViewById(R.id.notificationsTextView);

        //set reminder time
        reminderTime = (TextView) findViewById(R.id.remindertime);
        reminderDisplayTime = (TextView) findViewById(R.id.remindertime);
        reminderDate = (TextView)findViewById(R.id.reminderdate);
        reminderDate.setAlpha(0.0f);
        reminderTime.setAlpha(0.0f);

        //set repeat text
        repeattText = (TextView) findViewById(R.id.repeatText) ;
        mRepeatTypeText = (TextView)findViewById(R.id.repeatType);
        mRepeatText = (TextView) findViewById(R.id.repeatType);
        mRepeatNoText = (TextView) findViewById(R.id.repeatType);

        //set switches
        repeatSwitch = (Switch) findViewById(R.id.repeatSwitch);
        notificationSwitch = (Switch)findViewById(R.id.notificationsSwitch) ;

        //set repeat as false when you started creating a todo
        Repeat = false;

        //ref the switches to this class
        repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //if repeat switch is checked
                if (repeatSwitch.isChecked()){
                    repeattText.setText("Repeat ON");
                    mRepeatText.setText(  "Select Repeat Type");
                    //set the Repeat as true
                    Repeat = true;

                    //make repeat text clickable
                    mRepeatTypeText.setClickable(true);
                    // make the repeat text visible
                    mRepeatTypeText.setAlpha(1.0f);

                }
                else {
                    //if repeat switch is unchecked
                    //set the text as OFF
                    repeattText.setText("Repeat OFF");
                    //set the repeat as false
                    Repeat = false;
                    //make repeat text unclickable
                    mRepeatTypeText.setClickable(false);
                    //make text invisible
                    mRepeatTypeText.setAlpha(0.0f);

                }
            }
        });
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (notificationSwitch.isChecked()){
                    //if notification switch is checked
                    //set the text of the switch
                    notificationsText.setText("Notifications ON");
                    reminderDate.setText("Date dd/mm/yy");
                    reminderTime.setText("Time 00:00");
                    //make the textviews visible and clickable
                    reminderTime.setAlpha(1.0f);
                    reminderDate.setAlpha(1.0f);
                    reminderDate.setClickable(true);
                    reminderTime.setClickable(true);


                }
                else {
                    //if notification switch is checked
                    //set the text of the switch
                    notificationsText.setText("Notifications OFF");
                    //make the textviews invisible and unclickable
                    reminderTime.setAlpha(0.0f);
                    reminderDate.setAlpha(0.0f);
                    reminderDate.setClickable(false);
                    reminderTime.setClickable(false);


                }
            }
        });

//Clock
       reminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delclare clock
                remindercalendar = Calendar.getInstance();
                //delclare clock variables
                ReminderCalendarHour= remindercalendar.get(Calendar.HOUR_OF_DAY);
                ReminderCalendarMinute = remindercalendar.get(Calendar.MINUTE);
                //create clock pop-up
                timePickerDialog = new TimePickerDialog(addtodo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDayr, int minuter) {
                        String timeStamp = String.format("%02d:%02d", hourOfDayr, minuter);
                        //display the selected time
                        reminderDisplayTime.setText(timeStamp);
                    }
                },
                        ReminderCalendarHour,ReminderCalendarMinute, false);
                timePickerDialog.show();

                    }
        });

        AccesTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delclare clock
                calendar = Calendar.getInstance();
                //delclare clock variables
                CalendarHour= calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);
                //create clock pop-up
                timePickerDialog = new TimePickerDialog(addtodo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeStamp = String.format("%02d:%02d", hourOfDay, minute);
                        //display the selected time
                        DisplayTime.setText(timeStamp);
                    }
                },
                        CalendarHour,CalendarMinute, false);
                timePickerDialog.show();

            }
        });


        datepickerdialogbutton = (TextView) findViewById(R.id.date);
        selecteddate = (TextView)findViewById(R.id.date);

        reminderdatepickerdialogbutton = (TextView) findViewById(R.id.reminderdate);
        reminderselecteddate = (TextView)findViewById(R.id.reminderdate);


        datepickerdialogbutton.setOnClickListener(new View.OnClickListener() {

            @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            DialogFragment dialogfragment = new DatePickerDialogClass();

            dialogfragment.show(getFragmentManager(), "Date Picker Dialog");


        }

    });
        reminderdatepickerdialogbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment dialogfragment = new ReminderDatePickerDialogClass();

                dialogfragment.show(getFragmentManager(), "Date Picker Dialog");


            }});

    }

    //Repeat Switch + notification switch




    //select repeat type
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
                mRepeatType = items[item];
                mRepeatTypeText.setText(mRepeatType);
                mRepeatText.setText(mRepeatType);

            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    public void CancelButtonClicked(View view) {
        finish();
    }


    //Calendar
    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //declare calendar variables
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            //create calendar pop-up
            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, year, month, day);

            //return calendar pop-up
            return datepickerdialog;

        }
        //function for selecting a date
        public void onDateSet(DatePicker view, int year, int month, int day) {

            TextView textview = (TextView) getActivity().findViewById(R.id.date);
            //set the text for the selected date
            textview.setText(year + "-" + (month + 1) + "-" + day);
        }
    }


    //Reminder Calendar
    public static class ReminderDatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //create reminder calendar
            final Calendar remindercalendar = Calendar.getInstance();
            //declare reminder calendar variables
            int ryear = remindercalendar.get(Calendar.YEAR);
            int rmonth = remindercalendar.get(Calendar.MONTH);
            int rday = remindercalendar.get(Calendar.DAY_OF_MONTH);

            //create reminder calendar pop-up
            DatePickerDialog reminderdatepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, ryear, rmonth, rday);

            //return reminder calendar pop-up
            return reminderdatepickerdialog;


        }
        //function for selecting a date
        public void onDateSet(DatePicker view, int ryear, int rmonth, int rday) {

            TextView textview = (TextView) getActivity().findViewById(R.id.reminderdate);
            //set the text for the selected date
            textview.setText(ryear + "-" + (rmonth + 1) + "-" + rday);
        }
    }



    //add todo to the listview
    public void addButtonClicked (View v){
        //declare variables you want to add to the database
        String messageText = ((EditText)findViewById(R.id.titleText)).getText().toString();
        String DescText = ((EditText)findViewById(R.id.descriptionText)).getText().toString();
        String dateText = ((TextView)findViewById(R.id.date)).getText().toString();
        String timeText = ((TextView)findViewById(R.id.timePlainText)).getText().toString();
        String reminderdateText = ((TextView)findViewById(R.id.reminderdate)).getText().toString();
        String remindertimeText = ((TextView)findViewById(R.id.remindertime)).getText().toString();

        String repeatTxt = ((TextView)findViewById(R.id.repeatText)).getText().toString();
        String notificationText = ((TextView)findViewById(R.id.notificationsTextView)).getText().toString();

        String Repeat_Interval;
        //if Edit Repeat is false "NO INTERVAL" will be added to database instead of selecting a interval
        if (Repeat == false){
            Repeat_Interval = "NO INTERVAL";
        }
        //the selected interval will be added
        else {
            Repeat_Interval = ((TextView)findViewById(R.id.repeatType)).getText().toString();

        }


        String locationT = ((TextView)findViewById(R.id.locationText)).getText().toString();
        //set the standard status of the todo as false (undone)
        Boolean Status = false;
        if (locationT.equals("")) {
            locationT = "No Location Given.";
        }
        //set the location
        else {
            locationT = ((TextView)findViewById(R.id.locationText)).getText().toString();
        }
        //check if all the edittexts are filled in
        if (messageText.equals("") || dateText.equals("") || timeText.equals("") || reminderdateText.equals("") || remindertimeText.equals("")) {
            Toast.makeText(this, "PLEASE MAKE SURE ALL THE BLANKS ARE FILLED IN", Toast.LENGTH_SHORT).show();
            // When there is no location entered
        }

        else {

            // create body parameters
            Pair[] parameters = new Pair[]{
                    new Pair("Title", messageText),
                    new Pair("Description", DescText),
                    new Pair("Date", dateText+ "T" + timeText),
                    new Pair("DateReminder", reminderdateText+ "T" + remindertimeText),
                    new Pair("Todo_Status", Status),
                    new Pair("Repeat", Repeat),
                    new Pair("Repeat_Interval", mRepeatType),
                    new Pair("Repeat_Interval", Repeat_Interval),
                    new Pair("Repeat_Interval", mRepeatType),
                    new Pair("Location", locationT)

            };
            // set the web api task
            HttpTask task = new HttpTask(this.getApplicationContext(),
                    //declare the task that will be used
                    HttpMethod.POST,
                    //give the link for the task
                    "http://ashittyscheduler.azurewebsites.net/api/todo/create",
                    //create a pop-up progressbar for the task
                    new AsyncHttpListener() {
                        private ProgressDialog progressDialog;

                        @Override
                        public void onBeforeExecute() {
                            // show a progress dialog (duh)
                            progressDialog = ProgressDialog.show(addtodo.this,
                                    "Creating todo",
                                    "Please wait");
                        }

                        @Override
                        public void onResponse(HttpResponse httpResponse) {

                            int code = httpResponse.getCode();

                            if (code == HttpStatusCode.OK.getCode()){
                                //give signal if the todo is added
                                Toast.makeText(getApplicationContext(), "TODO CREATED", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //give message if the task fails
                                Toast.makeText(getApplicationContext(), "FAILED TO CREATE TODO" + httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onError() {
                            //give message if the task fails
                            Toast.makeText(getApplicationContext(), "TFAILED TO CREATE TODO", Toast.LENGTH_SHORT).show();

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

            //end the activity
            finish();
        }
    }


        }












