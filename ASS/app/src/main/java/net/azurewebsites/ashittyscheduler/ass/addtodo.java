package net.azurewebsites.ashittyscheduler.ass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class addtodo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);
    }

    public void backToMain(View view) {
        Intent i = new Intent();
        EditText nameAddInput = (EditText) findViewById(R.id.titleText);
        String userNameText = nameAddInput.getText().toString();
        i.putExtra("nameInput", userNameText);
        setResult(RESULT_OK, i);
        finish();
    }}









