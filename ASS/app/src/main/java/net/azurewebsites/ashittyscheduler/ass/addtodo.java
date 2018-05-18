package net.azurewebsites.ashittyscheduler.ass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;


import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class addtodo extends AppCompatActivity {
    ArrayList<String> todolist;
    ArrayAdapter<String> adapter;
    EditText todoTitle;
    EditText todoDesc;
    Date todoDate;
    Button addKnop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);

        todoTitle = (EditText) findViewById(R.id.titleText);
        todoDesc = (EditText) findViewById(R.id.descriptionText);
        addKnop = (Button) findViewById(R.id.addButton);
        todolist = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(addtodo.this, android.R.layout.simple_list_item_1, todolist);

        View.OnClickListener addListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todolist.add(Integer.parseInt(todoTitle.getText().toString()), todoDesc.getText().toString());
                todoTitle.setText("");
                todoDesc.setText("");

                adapter.notifyDataSetChanged();


            }

        };
        addKnop.setOnClickListener(addListner);




    }
}









