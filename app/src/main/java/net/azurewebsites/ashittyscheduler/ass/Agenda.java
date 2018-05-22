package net.azurewebsites.ashittyscheduler.ass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import java.util.Calendar;

public class Agenda extends AppCompatActivity {

    CalendarView calendarView;
    Button btnToday;
    ListView listViewToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        btnToday = (Button) findViewById(R.id.btnToday);
        listViewToDo = (ListView) findViewById(R.id.listViewToDo);

        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
            }
        });

    }
}
