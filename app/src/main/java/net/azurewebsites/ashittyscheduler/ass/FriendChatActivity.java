package net.azurewebsites.ashittyscheduler.ass;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class FriendChatActivity extends AppCompatActivity {
    private ListAdapter1 adapter;
    private ListView listView;
    private User user;
    private String talkingTo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        this.user = (User)getIntent().getSerializableExtra("User");
        this.talkingTo = user.getUsername();
        TextView textView = (TextView)findViewById(R.id.FriendUsernameChat);
        textView.setText(talkingTo);
        ArrayList<Texts> texts = new ArrayList<>();
        this.adapter = new ListAdapter1(this,texts);
        this.listView = (ListView)findViewById(R.id.chatList);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(true);
        adapter.getData(this.user);
        final EditText editText = (EditText)findViewById(R.id.chatMessageBox);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == KeyEvent.KEYCODE_ENTER) &&(event.getAction() == KeyEvent.ACTION_DOWN)){
                    SendMessage(new Texts(editText.getText().toString(),true));
                    editText.getText().clear();
                    return true;
                }
                return false;
            }
        });
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void SendMessage(Texts texts){
        final Context context = FriendChatActivity.this;
        final ListAdapter1 listAdapter1 = this.adapter;
        final User user = this.user;
        Pair[] parameters = new Pair[] {
                new Pair<>("tokenId", getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("Token" , null)),
                new Pair<>("UsernameFriend", this.talkingTo),
                new Pair<>("Message" , texts.message)
        };
        try {
            HttpTask task = new HttpTask(context, HttpMethod.POST, "http://ashittyscheduler.azurewebsites.net/api/chat/sendMessage", new AsyncHttpListener() {
                private ProgressDialog progressDialog;
                @Override
                public void onBeforeExecute() {
                    progressDialog = ProgressDialog.show(context,
                            "Sending message",
                            "Please wait");
                }

                @Override
                public void onResponse(HttpResponse httpResponse) {
                    int code = httpResponse.getCode();
                    httpResponse.getMessage();
                    if(code == HttpStatusCode.OK.getCode()){
                        Toast.makeText(context,"Message send", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"Could not submit message", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(getApplicationContext(), "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinishExecuting() {
                    progressDialog.dismiss();
                    adapter.getData(user);
                }
            });
            task.setBodyParameters(parameters);
            task.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                rowView     = inflater.inflate(R.layout.row_right, parent,false);
            else
                rowView     = inflater.inflate(R.layout.row_left, parent,false);
            TextView message      = (TextView) rowView.findViewById(R.id.message);
            message.setText(texts.get(position).getMessage());
        return rowView;
    }

    public void setData(ArrayList<Texts> texts){
        this.texts.clear();
        for(Texts item : texts){
            this.texts.add(item);
        }
        notifyDataSetChanged();
    }

    public void getData(User user){
        final Context context = this.context;
        final ArrayList<Texts> textsArrayList = new ArrayList<>();
        final String friendId = user.getId();
        Pair[] parameters = new Pair[] {
                new Pair<>("receiverId", user.getId()),
        };
        try {
            HttpTask task = new HttpTask(context, HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/chat/getmessages", new AsyncHttpListener() {
                private ProgressDialog progressDialog;
                @Override
                public void onBeforeExecute() {
                    progressDialog = ProgressDialog.show(context,
                            "Getting messages",
                            "Please wait");
                }

                @Override
                public void onResponse(HttpResponse httpResponse) {
                    try {
                        JSONArray textsList = new JSONArray(httpResponse.getMessage());

                        for(int i =0; i<textsList.length(); ++i) {
                            JSONObject texts = textsList.getJSONObject(i);
                            Texts textMessage;
                            if(texts.getString("SenderId").equals(friendId)){
                                textMessage = new Texts(texts.getString("Message"), false);
                            }else{
                                textMessage = new Texts(texts.getString("Message"), true);
                            }
                            textsArrayList.add(textMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(context, "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinishExecuting() {
                    progressDialog.dismiss();
                    setData(textsArrayList);
                }
            });
            task.setUriParameters(parameters);
            task.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
