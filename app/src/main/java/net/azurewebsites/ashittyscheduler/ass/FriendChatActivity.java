package net.azurewebsites.ashittyscheduler.ass;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
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

/**
 * CAREFUL EDITING THIS. {@code DANGEROUS CODE>}
 * @see AppCompatActivity for base class
 * This is the class that contains our code for setting up the chat.
 * @author Robin
 */
public class FriendChatActivity extends AppCompatActivity {
    private ListAdapter1 adapter;
    private ListView listView;
    private User user;
    private String talkingTo;
    private Thread getChatData;
    private Handler aHandler = new Handler();

    /**
     * @param savedInstanceState takes a bundle from the intent that called this class.
     * Sets up the base for our chat UI.
     * Also sets up most of the Class variables like the listview.
     */
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
        //adapter.getData(this.user);
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
        startDataThread();
    }


    /**
     * @param texts Takes a texts object that can be send to another user youre currently in chat with.
     * Calls the get data method from our adapter after sending data to the web api.
     * Sends a texts object to the web api.
     * After executing the {@link HttpTask} the message will be send to the user through our web api.
     */
    private void SendMessage(final Texts texts){
        final Context context = FriendChatActivity.this;
        final ListAdapter1 listAdapter1 = this.adapter;
        final User user = this.user;
        Pair[] parameters = new Pair[] {
                new Pair<>("tokenId", getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE).getString("Token" , null)),
                new Pair<>("UsernameFriend", this.talkingTo),
                new Pair<>("Message" , texts.message)
        };
        HttpTask task = new HttpTask(context, HttpMethod.POST, "http://ashittyscheduler.azurewebsites.net/api/chat/sendMessage", new AsyncHttpListener() {
            private ProgressDialog progressDialog;

            @Override
            public void onBeforeExecute() {
                //progressDialog = ProgressDialog.show(context,"Sending message","Please wait");
                Texts t = new Texts(texts.message, true);
                adapter.add(t);
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                int code = httpResponse.getCode();
                if(code == HttpStatusCode.OK.getCode()){
                    Toast.makeText(context,"Message sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"Could not submit message.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinishExecuting() {
                //progressDialog.dismiss();
                //adapter.getData(user);
            }
        });
        task.setBodyParameters(parameters);
        task.execute();
    }


    /**
     * This method sets up the data thread.
     * This thread runs on a seperate UI thread that is handled by the aHandler.
     * Calls the Adapter,getData function to continiously update the chat data.
     */
    private void startDataThread(){
        this.getChatData = new Thread(){
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.getData(user);
                        aHandler.postDelayed(this, 5000);
                    }
                });
            }
        };
        this.getChatData.start();
    }

    /**
     * This method defines what happens when the back button is pressed.
     * In our case it accesses the handler that controls the thread.
     * When within the handler its stops the thread from executing.
     * Also calls the base method.
     */
    @Override
    public void onBackPressed() {
        //To stop the thread
        aHandler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }
}

/**
 * Arrayadapter for our chat list view.
 * @see android.widget.ArrayAdapter : for base class.
 * This is the class that defines our listview that is used in the: {@link FriendChatActivity}.
 */
class ListAdapter1 extends ArrayAdapter<Texts> {

    //Class variables.
    private final Context context;
    private ArrayList<Texts> texts;

    /**
     * @param context : Takes a context object , this can be an acitivty.
     * @param texts : takes an arraylist of Texts objects.
     * @see ArrayAdapter for base class.
     */
    public ListAdapter1(Context context , ArrayList<Texts>texts) {
        super(context, R.layout.customlistviewfriends, texts);
        this.context=context;
        this.texts = texts;
    }

    /**
     * @return the size of the amount of texts with ur friend.
     */
    @Override
    public int getCount(){
        return this.texts.size();
    }

    /**
     * @param position takes a position that defines the message location.
     * @param convertView takes a view object that cannot be null. This comes from our base class.
     * @param parent takes a parent viewgroup.?
     * @return Returns a view for the listview. This si used for our listview in -> {@link FriendChatActivity}
     */
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

    /**
     * @param texts : takes an arraylist of Texts objects.
     * This method sets the data for the listview.
     * After this method the {@code notifyOnChanged} method is called.
     * This method resets the view.
     */
    public void setData(ArrayList<Texts> texts){
        this.texts.clear();
        for(Texts item : texts){
            this.texts.add(item);
        }
        notifyDataSetChanged();
    }

    public void add(Texts text) {
        this.texts.add(text);
        notifyDataSetChanged();
    }

    /**
     * @param user : Takes a user objects as a input.
     * This function allows the Adapter itself to get the data from our web api.
     * It uses the {@link HttpTask} to connect to the web api and getting the data.
     * After getting the data it will call the setData to set the data of the listview.
     */
    public void getData(User user){
        final Context context = this.context;
        final ArrayList<Texts> textsArrayList = new ArrayList<>();
        final String friendId = user.getId();
        Pair[] parameters = new Pair[] {
                new Pair<>("receiverId", user.getId()),
        };
        HttpTask task = new HttpTask(context, HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/chat/getmessages", new AsyncHttpListener() {
            //private ProgressDialog progressDialog;
            @Override
            public void onBeforeExecute() {
                //progressDialog = ProgressDialog.show(context,
                //        "Getting messages",
                //        "Please wait");
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
                //progressDialog.dismiss();
                setData(textsArrayList);
            }
        });
        task.setUriParameters(parameters);
        task.execute();
    }

}
