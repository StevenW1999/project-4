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


import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class addtodo extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
TimePickerDialog timePickerDialog;
Calendar calendar;
private TextView AccesTime;
private int CalendarHour;
private  int CalendarMinute;
Calendar remindercalendar;
private int ReminderCalendarHour;
private  int ReminderCalendarMinute;
private String format;
TextView DisplayTime;
TextView repeattText;
private String mRepeat;
private String mRepeatNo;
private String mRepeatType;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);


        //Set Time Picker
        AccesTime = (TextView) findViewById(R.id.timePlainText);
        DisplayTime = (TextView) findViewById(R.id.timePlainText);

        reminderTime = (TextView) findViewById(R.id.remindertime);
        reminderDisplayTime = (TextView) findViewById(R.id.remindertime);



        repeattText = (TextView) findViewById(R.id.repeatText) ;
        mRepeatTypeText = (TextView)findViewById(R.id.repeatType);
        mRepeatText = (TextView) findViewById(R.id.repeatType);
        mRepeatNoText = (TextView) findViewById(R.id.repeatType);
        repeatSwitch = (Switch) findViewById(R.id.repeatSwitch);

        mRepeatIntervalText = (TextView) findViewById(R.id.repeatInterval);

        repeatSwitch.setOnCheckedChangeListener(this);

//Clock
       reminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindercalendar = Calendar.getInstance();
                ReminderCalendarHour= remindercalendar.get(Calendar.HOUR_OF_DAY);
                ReminderCalendarMinute = remindercalendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(addtodo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDayr, int minuter) {
                        String timeStamp = String.format("%02d:%02d", hourOfDayr, minuter);
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
                calendar = Calendar.getInstance();
                CalendarHour= calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(addtodo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeStamp = String.format("%02d:%02d", hourOfDay, minute);
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

    //Repeat Switch

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (repeatSwitch.isChecked()){
            repeattText.setText("Repeat ON");
            mRepeatText.setText(  "SELECT REPEAT TYPE");
            mRepeatIntervalText.setText("Interval");
            mRepeatTypeText.setEnabled(true);
            mRepeatIntervalText.setEnabled(true);


        }
        else {
            repeattText.setText("Repeat OFF");
            mRepeatText.setText("Repeat OFF");
            mRepeatIntervalText.setText("Repeat OFF");
            mRepeatTypeText.setEnabled(false);
            mRepeatIntervalText.setEnabled(false);




        }

    }

    public void CancelButtonClicked(View view) {
//        Intent intent = new Intent();
//        intent.setClass(addtodo.this, MainMenu.class);
//        startActivity(intent);
        finish();
    }





    //Calendar
    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, year, month, day);


            return datepickerdialog;


        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            TextView textview = (TextView) getActivity().findViewById(R.id.date);

            textview.setText(year + "-" + (month + 1) + "-" + day);
        }
    }
    public static class ReminderDatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar remindercalendar = Calendar.getInstance();
            int ryear = remindercalendar.get(Calendar.YEAR);
            int rmonth = remindercalendar.get(Calendar.MONTH);
            int rday = remindercalendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog reminderdatepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, ryear, rmonth, rday);


            return reminderdatepickerdialog;


        }

        public void onDateSet(DatePicker view, int ryear, int rmonth, int rday) {

            TextView textview = (TextView) getActivity().findViewById(R.id.reminderdate);

            textview.setText(ryear + "-" + (rmonth + 1) + "-" + rday);
        }
    }
















//select repeat type
    public void selectRepeatType(View v){
        final String[] items = new String[3];
        items[0] = "Day";
        items[1] = "Week";
        items[2] = "Month";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = items[item];
                mRepeatTypeText.setText(mRepeatType);
                mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

//input repeat interval
    public void setRepeatNo(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            mRepeatNo = Integer.toString(1);
                            mRepeatNoText.setText(mRepeatNo);
                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                        else {
                            mRepeatNo = input.getText().toString().trim();
                            mRepeatNoText.setText(mRepeatNo);
                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                    }
                });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }









    //add todo to the listview
    public void addButtonClicked (View v){
        String messageText = ((EditText)findViewById(R.id.titleText)).getText().toString();
        String DescText = ((EditText)findViewById(R.id.descriptionText)).getText().toString();
        String dateText = ((TextView)findViewById(R.id.date)).getText().toString();
        String timeText = ((TextView)findViewById(R.id.timePlainText)).getText().toString();
        String reminderdateText = ((TextView)findViewById(R.id.reminderdate)).getText().toString();
        String remindertimeText = ((TextView)findViewById(R.id.remindertime)).getText().toString();
        String repeatTxt = ((TextView)findViewById(R.id.repeatText)).getText().toString();
        String notificationText = ((TextView)findViewById(R.id.notificationsTextView)).getText().toString();


        if (messageText.equals("")){
            Toast.makeText(this, "PLEASE GIVE THE TODO A TITLE", Toast.LENGTH_SHORT).show();

        }
        else {

            try {

                // create body parameters
                Pair[] parameters = new Pair[]{
                        new Pair("Title", messageText),
                        new Pair("Description", DescText),
                        new Pair("Date", dateText+ "T" + timeText),
                        new Pair("DateReminder", reminderdateText+ "T" + remindertimeText) // TODO: Add reminder date
                };

                HttpTask task = new HttpTask(this.getApplicationContext(),
                        HttpMethod.POST,
                        "http://ashittyscheduler.azurewebsites.net/api/todo/create",
                        new AsyncHttpListener() {
                            private ProgressDialog progressDialog;

                            @Override
                            public void onBeforeExecute() {
                                // show a progress dialog (duh)
                                progressDialog = android.app.ProgressDialog.show(addtodo.this,
                                        "Creating todo",
                                        "Please wait");
                            }

                            @Override
                            public void onResponse(HttpResponse httpResponse) {

                                int code = httpResponse.getCode();

                                if (code == HttpStatusCode.OK.getCode()){
                                    Toast.makeText(getApplicationContext(), "TODO CREATED", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "FAILED TO CREATE TODO" + httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }


                            }

                            @Override
                            public void onError() {
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


            } catch (IOException e) {
                e.printStackTrace();
            }


            finish();
        }
    }


        }









