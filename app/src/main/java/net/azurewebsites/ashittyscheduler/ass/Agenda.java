package net.azurewebsites.ashittyscheduler.ass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Adapter;
import android.widget.AdapterView;

import java.util.Calendar;

public class Agenda extends AppCompatActivity {

    private CalendarView calendarView;
    private Button btnToday;
    private ListView listViewToDo;
    String[] toDo = {"To do 1", "To do 2", "To do 3", "To do 4", "To do 5", "To do 6", "To do 7", "To do 8", "To do 9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        //calendarView = (CalendarView) findViewById(R.id.calendarView);
        //btnToday = (Button) findViewById(R.id.btnToday);
        listViewToDo = (ListView) findViewById(R.id.listViewToDo);

        ArrayAdapter<String> toDoItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listViewToDo.setAdapter(toDoItems);

        toDoItems.clear();
        toDoItems.addAll(toDo);

        //calendarView.getDate();

        //btnToday.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //    }
        //});

    //    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
    //        @Override
    //        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
    //            if (calendarView.getFirstDayOfWeek() != calendarView.getDate()) {
    //                calendarView.getMinDate();
    //                System.out.println("Date changed");
    //            }
    //        }
    //    });

    }
}
