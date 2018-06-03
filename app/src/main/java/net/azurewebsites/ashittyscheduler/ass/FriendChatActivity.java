package net.azurewebsites.ashittyscheduler.ass;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FriendChatActivity extends AppCompatActivity {
    private ListAdapter1 adapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        String talkingTo = getIntent().getStringExtra("Username");
        TextView textView = (TextView)findViewById(R.id.FriendUsernameChat);
        textView.setText(talkingTo);
        ArrayList<Texts> texts = new ArrayList<>();
        texts.add(new Texts("Test send",true));
        texts.add(new Texts("Test receive",false));
        this.adapter = new ListAdapter1(this,texts);
        this.listView = (ListView)findViewById(R.id.chatList);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(true);
        final EditText editText = (EditText)findViewById(R.id.chatMessageBox);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == KeyEvent.KEYCODE_ENTER) &&(event.getAction() == KeyEvent.ACTION_DOWN)){
                    addMessage(new Texts(editText.getText().toString(),true));
                    editText.getText().clear();
                    return true;
                }
                return false;
            }
        });
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }
    private void addMessage(Texts text){
        this.adapter.setData(text);
        this.listView.smoothScrollToPosition(adapter.getCount() - 1);
    }
}
class ListAdapter1 extends ArrayAdapter<Texts> {

    private final Context context;
    private ArrayList<Texts> texts;

    public ListAdapter1(Context context , ArrayList<Texts>texts) {
        super(context, R.layout.customlistviewfriends, texts);
        this.context=context;
        this.texts = texts;
    }

    /*public void setData(ArrayList<Texts> list2) {
        this.list = list2;
    }*/
    @Override
    public int getCount(){
        return this.texts.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

            if (texts.get(position).isSender())
                rowView     = inflater.inflate(R.layout.row_left, parent,false);
            else
                rowView     = inflater.inflate(R.layout.row_right, parent,false);

            TextView message      = (TextView) rowView.findViewById(R.id.message);
            message.setText(texts.get(position).getMessage());


        return rowView;
    }

    public void setData(Texts text){
        this.texts.add(text);

    }


}
