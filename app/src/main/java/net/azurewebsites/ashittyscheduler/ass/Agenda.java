package net.azurewebsites.ashittyscheduler.ass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.Overview.OverviewFragment;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import static java.util.Calendar.MONDAY;
import static java.util.Calendar.getInstance;

public class Agenda extends AppCompatActivity {

    private CalendarView calendarView;
    private Button btnToday;
    private ListView listViewToDo;
    String[] toDo = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        listViewToDo = (ListView) findViewById(R.id.listViewToDo);

        final ArrayAdapter<ToDo> toDoItems = new ArrayAdapter<ToDo>(this, android.R.layout.simple_list_item_1);
        listViewToDo.setAdapter(toDoItems);

        toDoItems.clear();
        //toDoItems.addAll();

        AsyncHttpListener listener = new AsyncHttpListener() {
            @Override
            public void onBeforeExecute() {

            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                int code = httpResponse.getCode();
                if (code == HttpStatusCode.OK.getCode())
                {
                    try {
                        JSONArray todos = new JSONArray(httpResponse.getMessage());

                        for (int i = 0; i < todos.length(); i++) {
                            JSONObject todoJSON = todos.getJSONObject(i);

                            ToDo todo = new ToDo();
                            todo.setId(todoJSON.getString("Id"));
                            todo.setTitle(todoJSON.getString("Title"));
                            todo.setDescription(todoJSON.getString("Description"));

                            // format date
                            Calendar calendar = getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                            calendar.setTime(sdf.parse(todoJSON.getString("Date")));

                            todo.setDate(calendar);
                            //TODO: Add dateReminder etc...

                            toDoItems.add(todo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinishExecuting() {

            }
        };

        try {
            HttpTask task = new HttpTask(getApplicationContext(),
                    HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/todo/getmytodos",
                    listener);
            task.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }



        //For every item onclick in listview, go to detailscreen for that item
        listViewToDo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < toDo.length) {
                    Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                    startActivityForResult(intent, 0);
                }
            }
        });

        //Todo: On button click, return to current month
        btnToday = (Button) findViewById(R.id.btnToday);
        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Agenda.this, "You clicked on this button! ☺", Toast.LENGTH_SHORT).show();
            }
        });

        //Calendar View on date change listener
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String currentDay = String.valueOf(dayOfMonth);
                String currentMonth = String.valueOf(month + 1);
                String currentYear = String.valueOf(year);
                Toast.makeText(getApplicationContext(), "Selected date: " + currentDay + "/" + currentMonth + "/" + currentYear, Toast.LENGTH_SHORT).show();

                Calendar selectedDate = new GregorianCalendar(year, month, dayOfMonth);

                final ArrayAdapter<ToDo> filteredTodoItems = new ArrayAdapter<ToDo>(getApplicationContext(), android.R.layout.simple_list_item_1);

                // Loop door de lijst met todo's
                for(int i=0; i< toDoItems.getCount(); ++i) {
                    ToDo todo = (ToDo) toDoItems.getItem(i);

                    Calendar todoDate = todo.getDate();

                    // Als de datum van de todo gelijk is aan de geselecteerde datum
                    if (todoDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                            todoDate.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR)) {
                        // Toevoegen aan lijst met gefilterde todos
                        filteredTodoItems.add(todo);
                    }
                }

                // set adapter to filtered list
                listViewToDo.setAdapter(filteredTodoItems);

                // update the listview
                filteredTodoItems.notifyDataSetChanged();
            }
        });
    }
}
