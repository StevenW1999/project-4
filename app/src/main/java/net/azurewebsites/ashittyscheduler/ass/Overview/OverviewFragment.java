package net.azurewebsites.ashittyscheduler.ass.Overview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.Intent_Constants;
import net.azurewebsites.ashittyscheduler.ass.MainMenu;
import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.ToDo;
import net.azurewebsites.ashittyscheduler.ass.addtodo;
import net.azurewebsites.ashittyscheduler.ass.detailscreen;
//import net.azurewebsites.ashittyscheduler.ass.edittodo;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class OverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    SwipeRefreshLayout refreshTodos;
    private static ArrayList<String> objects = new ArrayList<>();

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Method for setting up the spinner and it's function
        setSpinner();

        //Update todos after refresh
        refreshTodos();

        listView = (ListView) getActivity().findViewById(R.id.textView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get selected to do item
                ToDo todo = (ToDo) parent.getItemAtPosition(position);

                //TODO: Check if todo is null!!!!!!!!!!!
                if (todo == null) {

                }

                Intent intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(), detailscreen.class);
                intent.putExtra("todoId", todo.getId());

                startActivity(intent);
            }
        });
    }

    //Fills data with all the user's todos
    private void fillDataToDo() {
        final ArrayList<String> toDoItems = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1,toDoItems);
        listView.setAdapter(arrayAdapter);
        final ArrayAdapter<ToDo> todoItems = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1);
        ArrayList<ToDo> toDoItems2 = new ArrayList<ToDo>();
        listView.setAdapter(todoItems);
        todoItems.clear();

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
                            todoItems.add(todo);
                            refreshTodos.setRefreshing(false);
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

        HttpTask task = new HttpTask(getActivity().getApplicationContext(),
                HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/todo/getmytodos",
                listener);
        task.execute();
    }

    //Spinner used for filtering of todos by year
    private void setSpinner() {
        Spinner spinner =(Spinner) getActivity().findViewById(R.id.dropDownFilter);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity().getApplicationContext(), R.array.dropdownYear,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Dropdown list of years
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "It worked ☺", Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

        public void onClick(View v){
        Intent intent = new Intent();
        intent.setClass(getActivity().getApplicationContext(), addtodo.class);
        startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE);
    }

    //Swipe to refrsh/ update todos
    private void refreshTodos () {
        refreshTodos = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefresh);
        refreshTodos.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fillDataToDo();
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        fillDataToDo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }
}
