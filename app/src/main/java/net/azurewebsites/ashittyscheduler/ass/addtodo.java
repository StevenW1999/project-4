package net.azurewebsites.ashittyscheduler.ass;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class addtodo extends AppCompatActivity {
TimePickerDialog timePickerDialog;
Calendar calendar;
private TextView AccesTime;
private int CalendarHour;
private  int CalendarMinute;
private String format;
TextView DisplayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);
        AccesTime = (TextView) findViewById(R.id.timePlainText);
        DisplayTime = (TextView) findViewById(R.id.timePlainText);
        AccesTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                CalendarHour= calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(addtodo.this, new TimePickerDialog.OnTimeSetListener() {
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

    }
    public void addButtonClicked (View v){
        String messageText = ((EditText)findViewById(R.id.titleText)).getText().toString();
        if (messageText.equals("")){

        }
        else {
            Intent intent = new Intent();
            intent.putExtra(Intent_Constants.INTENT_MESSAGE_FIELD, messageText);
            setResult(Intent_Constants.INTENT_RESULT_CODE,intent);
            finish();
        }
    }

}









