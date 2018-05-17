package net.azurewebsites.ashittyscheduler.ass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;


import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class addtodo extends AppCompatActivity{
    ArrayList<String> todos;
    ArrayAdapter<String>adapter;
    EditText todoTitle;
    EditText todoDesc;
    Date todoDate;
    Button addKnop;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);

        todoTitle = (EditText)findViewById(R.id.titleText);
        todoDesc = (EditText)findViewById(R.id.descriptionText);
        addKnop = (Button)findViewById(R.id.addButton);


        adapter = new ArrayAdapter<>(addtodo.this, android.R.layout.simple_list_item_1, todos);



    }

}
