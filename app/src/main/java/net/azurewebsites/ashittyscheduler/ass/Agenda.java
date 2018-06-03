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
import android.widget.Toast;
import android.widget.Adapter;
import android.widget.AdapterView;

import net.azurewebsites.ashittyscheduler.ass.Overview.OverviewFragment;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class Agenda extends AppCompatActivity {

    private CalendarView calendarView;
    private Button btnToday;
    private ListView listViewToDo;
    String[] toDo = {"To do 1", "To do 2", "To do 3", "To do 4", "To do 5", "To do 6"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        btnToday = (Button) findViewById(R.id.btnToday);
        listViewToDo = (ListView) findViewById(R.id.listViewToDo);

        final ArrayAdapter<String> toDoItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listViewToDo.setAdapter(toDoItems);

        toDoItems.clear();
        toDoItems.addAll(toDo);

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
                            JSONObject todo = todos.getJSONObject(i);
                            toDoItems.add(todo.getString("Title"));
                            toDoItems.add(todo.getString("Date"));
                            toDoItems.add(todo.getString("DateReminder"));
                            toDoItems.add(todo.getString("Description"));

                        }
                    } catch (JSONException e) {
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
            HttpTask task = new HttpTask(this,
                    HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/todo/getmytodos",
                    listener);
            task.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Test to new Intent
        listViewToDo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < toDo.length) {
                    Intent myIntent = new Intent(view.getContext(), MainMenu.class);
                    startActivityForResult(myIntent, 0);
                }
            }
        });


        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
