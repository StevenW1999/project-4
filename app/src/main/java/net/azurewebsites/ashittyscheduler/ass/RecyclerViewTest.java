package net.azurewebsites.ashittyscheduler.ass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.azurewebsites.ashittyscheduler.ass.Adapters.recyclerViewAdapter;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//For testing purposes
public class RecyclerViewTest extends AppCompatActivity {

    private RecyclerView recyclerViewTest;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_test);

        final ArrayList<ToDo> allTodos = new ArrayList<>();


        recyclerViewTest = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerViewTest.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerViewTest.setLayoutManager(layoutManager);

        AsyncHttpListener listener = new AsyncHttpListener() {
            @Override
            public void onBeforeExecute() {

            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                int code = httpResponse.getCode();
                if (code == HttpStatusCode.OK.getCode()) {
                    try {
                        JSONArray todos = new JSONArray(httpResponse.getMessage());
                        JSONArray sortedTodos = new JSONArray();
                        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
                        for (int i = 0; i < todos.length(); i++) {
                            jsonValues.add(todos.getJSONObject(i));
                        }

                        Collections.sort(jsonValues, new Comparator<JSONObject>() {
                            private static final String KEY_NAME = "Date";

                            @Override
                            public int compare(JSONObject o1, JSONObject o2) {
                                String valA = new String();
                                String valB = new String();

                                try {
                                    valA = (String) o1.get(KEY_NAME);
                                    valB = (String) o2.get(KEY_NAME);
                                }
                                catch (JSONException e){

                                }

                                return valA.compareTo(valB);
                            }
                        });

                        for (int i = 0; i < todos.length() ; i++) {
                            sortedTodos.put(jsonValues.get(i));
                        }

                        for (int i = 0; i < sortedTodos.length(); i++) {
                            ToDo todo = new ToDo();
                            JSONObject sortedTodosGet = sortedTodos.getJSONObject(i);
                            jsonValues.add(sortedTodosGet);
                            todo.setId(sortedTodosGet.getString("Id"));
                            todo.setTitle(sortedTodosGet.getString("Title"));
                            allTodos.add(todo);
                            recyclerViewAdapter newAdapter = new recyclerViewAdapter(allTodos);
                            recyclerViewTest.setAdapter(newAdapter);
                            //refreshTodos.setRefreshing(false);
                            //JSONObject todoJSON = todos.getJSONObject(i);

//                            ToDo todo = new ToDo();
//                            todo.setId(todoJSON.getString("Id"));
//                            todo.setTitle(todoJSON.getString("Title"));
//                            todo.setDescription(todoJSON.getString("Description"));

                            //TODO: Add Date, DateReminder etc...

//                            todoItems.add(todo);
//                            refreshTodos.setRefreshing(false);
//                            Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT);

                        }
                    }
                    catch (JSONException e) {
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

        HttpTask task = new HttpTask(getApplicationContext(),
                HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/todo/getmytodos",
                listener);
        task.execute();
    }
}