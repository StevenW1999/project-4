package net.azurewebsites.ashittyscheduler.ass;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class edittodo extends AppCompatActivity {


    TimePickerDialog timePickerDialog;
    Calendar calendar;
    private TextView AccesTime;
    private int CalendarHour;
    private  int CalendarMinute;
    private String format;
    TextView DisplayTime;
    TextView repeattText;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private TextView datepickerdialogbutton;
    private TextView selecteddate;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText, mRepeatIntervalText;
    private Switch repeatSwitch, notificationSwitch;
    private TextView notificationText;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittodo);
        String timeText;
        String titleText;
        String detText;
        String dateText;


        AccesTime = (TextView) findViewById(R.id.EditTime);
        DisplayTime = (TextView) findViewById(R.id.EditTime);
        Intent intent = getIntent();


        timeText = intent.getStringExtra(Intent_Constants.TIME_DATA);
        TextView detailTime = (TextView)findViewById(R.id.EditTime);
        detailTime.setText(timeText);
        titleText = intent.getStringExtra(Intent_Constants.INTENT_MESSAGE_DATA);
        TextView detailTitle = (TextView)findViewById(R.id.EditTitle);
        detailTitle.setText(titleText);



//Description

        detText = intent.getStringExtra(Intent_Constants.DETAIL_MESSAGE_DATA);
        TextView detailDes = (TextView)findViewById(R.id.EditDescription);
        detailDes.setText(detText);

        //date
        dateText = intent.getStringExtra(Intent_Constants.DATE_DATA);
        TextView detailDate = (TextView)findViewById(R.id.EditDate);
        detailDate.setText(dateText);

//        repeattText = (TextView) findViewById(R.id.repeatText) ;
//        mRepeatTypeText = (TextView)findViewById(R.id.repeatType);
//        mRepeatText = (TextView) findViewById(R.id.repeatType);
//        mRepeatNoText = (TextView) findViewById(R.id.repeatType);
//        repeatSwitch = (Switch) findViewById(R.id.repeatSwitch);


        AccesTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                CalendarHour= calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(edittodo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";

                        } else if (hourOfDay == 12) {
                            format = "PM";
                        } else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        } else {
                            format = "AM";
                        }
                        DisplayTime.setText(hourOfDay + ":" + minute + format);
                    }
                },
                        CalendarHour,CalendarMinute, false);
                timePickerDialog.show();

            }
        });

        datepickerdialogbutton = (TextView) findViewById(R.id.EditDate);
        selecteddate = (TextView)findViewById(R.id.EditDate);

        datepickerdialogbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                DialogFragment dialogfragment = new edittodo.DatePickerDialogClass();

                dialogfragment.show(getFragmentManager(), "Date Picker Dialog");

//            intent.putExtra(Intent_Constants.KEY_DATE, dateText);




            }
        });
    }


    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datepickerdialog;


        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            TextView textview = (TextView)getActivity().findViewById(R.id.EditDate);

            textview.setText(day + ":" + (month+1) + ":" + year);
        }
    }
//
    public void EditButtonClicked(View view) {
        String messageText = ((EditText)findViewById(R.id.EditTitle)).getText().toString();
        String DescText = ((EditText)findViewById(R.id.EditDescription)).getText().toString();
        String dateText = ((TextView)findViewById(R.id.EditDate)).getText().toString();
        String timeText = ((TextView)findViewById(R.id.EditTime)).getText().toString();
//        String repeatTxt = ((TextView)findViewById(R.id.repeatText)).getText().toString();
//        String notificationText = ((TextView)findViewById(R.id.notificationsTextView)).getText().toString();
        if (messageText.equals("")){
            Toast.makeText(this, "PLEASE GIVE THE TODO A TITLE", Toast.LENGTH_SHORT).show();

        }
        else {
            Intent intent = new Intent();
            intent.setClass(edittodo.this, MainMenu.class);
            intent.putExtra(Intent_Constants.INTENT_CHANGED_MESSAGE,messageText);
            intent.putExtra(Intent_Constants.INTENT_CHANGED_DETAIL, DescText);
            intent.putExtra(Intent_Constants.INTENT_CHANGED_TIME, timeText);
            intent.putExtra(Intent_Constants.INTENT_CHANGED_DATE,dateText);
//            intent.putExtra(Intent_Constants.REPEAT_FIELD, repeatTxt);
//            intent.putExtra(Intent_Constants.NOTIF_FIELD, notificationText);
            setResult(Intent_Constants.INTENT_RESULT_CODE_TWO, intent);


            finish();
        }

    }



}
