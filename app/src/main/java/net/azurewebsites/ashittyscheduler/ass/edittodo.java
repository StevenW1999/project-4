package net.azurewebsites.ashittyscheduler.ass;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class edittodo extends addtodo {
    String messageText;
    int position;
    String dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);

        Intent intent = getIntent();
//        dateText = intent.getStringExtra(Intent_Constants.KEY_DATE);
        messageText = intent.getStringExtra(Intent_Constants.INTENT_MESSAGE_DATA);
        position = intent.getIntExtra(Intent_Constants.INTENT_ITEM_POSITION, -1);
        EditText messageData = (EditText) findViewById(R.id.titleText);
//        TextView dateData = (TextView) findViewById(R.id.date);

        messageData.setText(messageText);
//        dateData.setText(dateText);
    }
    public void addButtonClicked(View v ){
//        String changedDate = ((TextView)findViewById(R.id.date)).getText().toString();
        String changedMessageText = ((EditText)findViewById(R.id.titleText)).getText().toString();
        Intent intent = new Intent();
        intent.putExtra(Intent_Constants.INTENT_CHANGED_MESSAGE, changedMessageText);
//        intent.putExtra(Intent_Constants.CHANGED_KEY_DATE, changedDate);
        intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION, position);
        setResult(Intent_Constants.INTENT_RESULT_CODE_TWO, intent);
        finish();
    }


    public void editTodo(View view) {
    }
}
