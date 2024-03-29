package net.azurewebsites.ashittyscheduler.ass.Overview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.Adapters.recyclerViewAdapter;
import net.azurewebsites.ashittyscheduler.ass.ApplicationConstants;
import net.azurewebsites.ashittyscheduler.ass.FriendsActivity;
import net.azurewebsites.ashittyscheduler.ass.Intent_Constants;
import net.azurewebsites.ashittyscheduler.ass.LoginActivity;
import net.azurewebsites.ashittyscheduler.ass.MainMenu;
import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.ToDo;
import net.azurewebsites.ashittyscheduler.ass.addtodo;
import net.azurewebsites.ashittyscheduler.ass.detailscreen;
//import net.azurewebsites.ashittyscheduler.ass.edittodo;
import net.azurewebsites.ashittyscheduler.ass.edittodo;
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
    SwipeRefreshLayout refreshTodos;
    private RecyclerView recyclerViewTest;
    private RecyclerView.LayoutManager layoutManager;
    final ArrayList<ToDo> allTodos = new ArrayList<>();

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

        //Method for setting up the spinner and it's function
        setSpinner();

        //Update todos after refresh
        refreshTodos();

        recyclerViewTest = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerViewTest.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewTest.setLayoutManager(layoutManager);

//        String token = getContext().getSharedPreferences(ApplicationConstants.PREFERENCES,Context.MODE_PRIVATE).getString("Token", null);
//        if(token == null || token.equals("")){
//            Log.d("EXCEPTION: ",  "NO TOKEN FOUND");
//
//            final AlertDialog.Builder messageBox = new AlertDialog.Builder(getContext());
//            messageBox.setTitle("You are not logged in!");
//            messageBox.setMessage("Please log back in.");
//            messageBox.setCancelable(false);
//            messageBox.setNeutralButton("OK" ,new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//
//                    Intent intent = new Intent(getContext(), LoginActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//
//                    getActivity().finish();
//                }
//            });
//            messageBox.show();
//        }

        /*
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
        });*/
    }

    //Fills data with all the user's todos
    private void fillDataToDo(final boolean showDialog) {

        AsyncHttpListener listener = new AsyncHttpListener() {

            private ProgressDialog progressDialog;

            @Override
            public void onBeforeExecute() {
                allTodos.clear();
                recyclerViewTest.setLayoutManager(null);

                if (showDialog) {
                    progressDialog = ProgressDialog.show(getContext(),
                            "Loading your todos",
                            "Please wait ☺");
                }
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


                            JSONObject sortedTodosGet = sortedTodos.getJSONObject(i);
                            jsonValues.add(sortedTodosGet);

                            // Construct TODO
                            ToDo todo = ToDo.fromJson(sortedTodosGet);

                            // add to the list of all todos
                            allTodos.add(todo);
                        }

                        refreshTodos.setRefreshing(false);

                        recyclerViewAdapter newAdapter = new recyclerViewAdapter(allTodos);
                        recyclerViewTest.setAdapter(newAdapter);

                        layoutManager = new LinearLayoutManager(getContext());
                        recyclerViewTest.setLayoutManager(layoutManager);

                        recyclerViewTest.getAdapter().notifyDataSetChanged();
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

                if (showDialog) {
                    progressDialog.dismiss();
                }

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

        // TODO: REMOVE

        // TEST!!!
        // Hide the spinner
        spinner.setVisibility(View.INVISIBLE);


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
                        try {
                            // Fill data, but don't show a dialog(!)
                            fillDataToDo(false);
                        }
                        catch(Exception o_O) {

                        }
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            fillDataToDo(true);
        }
        catch(Exception o_O) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }
}
