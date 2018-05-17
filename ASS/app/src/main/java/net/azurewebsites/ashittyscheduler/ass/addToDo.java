package net.azurewebsites.ashittyscheduler.ass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class addtodo extends AppCompatActivity{
    ArrayList<String> todoList;
    ArrayAdapter<String>adapter;
    EditText todoTitle;
    EditText todoDesc;
    Date todoDate;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);

    }

}
