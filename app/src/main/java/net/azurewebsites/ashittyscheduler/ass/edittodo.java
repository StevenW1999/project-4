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
    TimePickerDialog timePickerDialog;
    Calendar Eremindercalendar;
    Calendar calendar;
    private TextView EAccesTime;
    private int CalendarHour;
    private  int CalendarMinute;
    private int EReminderCalendarHour;
    private  int EReminderCalendarMinute;
    private TextView EDisplayTime;
    private TextView datepickerdialogbutton;
    private TextView Edatepickerdialogbutton;
    private TextView selecteddate;
    private TextView EreminderTime;
    private TextView EreminderDisplayTime;
    private TextView ETitle;
    private TextView EDescription;
    private String todoId;
    private TextView EEditDate;
    private TextView EReminderDate;
    private TextView EditRepeatText, EditRepeatTypeText,EditmRepeatText, EditNotificationText;
    private Switch EditRepeatSwitch, EditNotificationSwitch;
    private boolean EditRepeat;
    private String EditRepeatType;
    private boolean Status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittodo);

        ETitle = (TextView)findViewById(R.id.EditTitle) ;
        EDescription = (TextView)findViewById(R.id.EditDescription);

        EEditDate = (TextView)findViewById(R.id.EditDate);
        EReminderDate = (TextView) findViewById(R.id.EditRDate) ;


        //Set Time Picker
        EAccesTime = (TextView) findViewById(R.id.EditTime);
        EDisplayTime = (TextView) findViewById(R.id.EditTime);


        EreminderTime = (TextView) findViewById(R.id.EditRTime);
        EreminderDisplayTime = (TextView) findViewById(R.id.EditRTime);

        EditRepeatText = (TextView) findViewById(R.id.ERepeatText) ;
        EditRepeatTypeText = (TextView)findViewById(R.id.EditRepeatType);
        EditmRepeatText = (TextView) findViewById(R.id.EditRepeatType);

        EditNotificationText = (TextView)findViewById(R.id.ENotificationsTextView) ;

        EditRepeatSwitch = (Switch) findViewById(R.id.EditRepeatSwitch);
        EditNotificationSwitch = (Switch)findViewById(R.id.ENotificationsSwitch) ;

        EditRepeatSwitch.setOnCheckedChangeListener(this);
        EditNotificationSwitch.setOnCheckedChangeListener(this);


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
                        progressDialog = ProgressDialog.show(edittodo.this,
                                "Getting todo",
                                "Please wait");
                    }

                    @Override
                    public void onResponse(HttpResponse httpResponse) {
                        int code = httpResponse.getCode();
                        if (code == HttpStatusCode.OK.getCode()) {

                            try {
                                JSONObject todo = new JSONObject(httpResponse.getMessage());

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





//        mRepeatIntervalText = (TextView) findViewById(R.id.repeatInterval);


//Clock
        EAccesTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                CalendarHour= calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(edittodo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeStamp = String.format("%02d:%02d", hourOfDay, minute);
                        EDisplayTime.setText(timeStamp);
                    }
                },
                        CalendarHour,CalendarMinute, false);
                timePickerDialog.show();

            }
        });
        EreminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Eremindercalendar = Calendar.getInstance();
                EReminderCalendarHour= Eremindercalendar.get(Calendar.HOUR_OF_DAY);
                EReminderCalendarMinute = Eremindercalendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(edittodo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDayr, int minuter) {
                        String timeStamp = String.format("%02d:%02d", hourOfDayr, minuter);
                        EreminderDisplayTime.setText(timeStamp);
                    }
                },
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
                // TODO Auto-generated method stub

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


    //Repeat Switch





    public void CancelButtonClicked(View view) {
//        Intent intent = new Intent();
//        intent.setClass(addtodo.this, MainMenu.class);
//        startActivity(intent);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



        if (EditRepeatSwitch.isChecked()){
            EditRepeatText.setText("Repeat ON");

            EditRepeat = true;
            EditmRepeatText.setAlpha(1.0f);
            EditRepeatTypeText.setAlpha(1.0f);

            EditRepeatTypeText.setEnabled(true);



        }
        else {
            EditRepeatText.setText("Repeat OFF");
            EditRepeat = false;
            EditRepeatType = "";
            EditmRepeatText.setAlpha(0.0f);
            EditRepeatTypeText.setAlpha(0.0f);




            EditRepeatTypeText.setEnabled(false);




        }
        if (EditNotificationSwitch.isChecked()){
            EditNotificationText.setText("Notifications ON");
            EReminderDate.setAlpha(1.0f);
            EreminderTime.setAlpha(1.0f);
            EReminderDate.setClickable(true);
            EreminderTime.setClickable(true);
            EReminderDate.setEnabled(true);
            EreminderTime.setEnabled(true);




        }
        else {
            EReminderDate.setAlpha(0.0f);
            EreminderTime.setAlpha(0.0f);
            EReminderDate.setClickable(false);
            EreminderTime.setClickable(false);
            EditNotificationText.setText("Notifications OFF");
            EReminderDate.setEnabled(false);
            EreminderTime.setEnabled(false);




        }

    }
    public void selectRepeatType(View v){

        final String[] items = new String[3];
        items[0] = "Daily";
        items[1] = "Weekly";
        items[2] = "Monthly";





        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

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

            final Calendar Editcalendar = Calendar.getInstance();
            int Eyear = Editcalendar.get(Calendar.YEAR);
            int Emonth = Editcalendar.get(Calendar.MONTH);
            int Eday = Editcalendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog Editdatepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,Eyear,Emonth,Eday);

            return Editdatepickerdialog;





        }

        public void onDateSet(DatePicker view, int Eyear, int Emonth, int Eday){

            TextView textview = (TextView)getActivity().findViewById(R.id.EditDate);

            textview.setText(Eyear + "-" +(Emonth+1)  + "-" +Eday );
        }
    }
    public static class EditReminderDatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar Eremindercalendar = Calendar.getInstance();
            int Eryear = Eremindercalendar.get(Calendar.YEAR);
            int Ermonth = Eremindercalendar.get(Calendar.MONTH);
            int Erday = Eremindercalendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog Editreminderdatepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, Eryear, Ermonth, Erday);


            return Editreminderdatepickerdialog;


        }

        public void onDateSet(DatePicker view, int Eryear, int Ermonth, int Erday) {

            TextView textview = (TextView) getActivity().findViewById(R.id.EditRDate);

            textview.setText(Eryear + "-" + (Ermonth + 1) + "-" + Erday);
        }
    }












    //add todo to the listview
    public void EditButtonClicked (View v){
        String messageText = ((EditText)findViewById(R.id.EditTitle)).getText().toString();
        String DescText = ((EditText)findViewById(R.id.EditDescription)).getText().toString();
        String dateText = ((TextView)findViewById(R.id.EditDate)).getText().toString();
        String timeText = ((TextView)findViewById(R.id.EditTime)).getText().toString();
        String RdateText = ((TextView)findViewById(R.id.EditRDate)).getText().toString();
        String RtimeText = ((TextView)findViewById(R.id.EditRTime)).getText().toString();
        String Repeat_Interval;
        Intent intent2 = new Intent();
        intent2.setClass(edittodo.this, OverviewFragment.class);


        if (EditRepeat == false){
            Repeat_Interval = "NO INTERVAL";
        }
        else {
            Repeat_Interval = ((TextView)findViewById(R.id.EditRepeatType)).getText().toString();

        }



        Intent intent = getIntent();
        String todoId = intent.getStringExtra("todoId");
        Log.d("TODOID", todoId);

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

            HttpTask task = new HttpTask(this.getApplicationContext(),
                    HttpMethod.PUT,
                    "http://ashittyscheduler.azurewebsites.net/api/todo/update",
                    new AsyncHttpListener() {
                        private ProgressDialog progressDialog;

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


            startActivity(intent2);
        }
    }

}









