package net.azurewebsites.ashittyscheduler.ass.Overview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class OverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    String messageText;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Spinner spinner =(Spinner) getActivity().findViewById(R.id.dropDownFilter);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.dropdownYear, android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Nice", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView = (ListView) getActivity().findViewById(R.id.textView);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
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

        final ArrayAdapter<ToDo> toDoItems = new ArrayAdapter<ToDo>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(toDoItems);

        toDoItems.clear();

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

                            //TODO: Add Date, DateReminder etc...

                            toDoItems.add(todo);

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
            HttpTask task = new HttpTask(getActivity().getApplicationContext(),
                    HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/todo/getmytodos",
                    listener);
            task.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "wow", Toast.LENGTH_SHORT);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }
}
