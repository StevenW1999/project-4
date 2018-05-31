package net.azurewebsites.ashittyscheduler.ass;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpPostTask;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import java.io.IOException;
import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.friendsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friends");
        FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.AddFriendButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                View DialogView = getLayoutInflater().inflate(R.layout.search_friends,null);
                Button friendAddButton = (Button)DialogView.findViewById(R.id.AddAfriend);
                final EditText usernameGiven = (EditText)DialogView.findViewById(R.id.givenUsername);

                builder.setView(DialogView);
                final AlertDialog dialog = builder.create();
                dialog.show();
                friendAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Send request to : " + usernameGiven.getText(),Toast.LENGTH_SHORT).show();
                        Pair[] parameters = new Pair[] {
                                new Pair<>("UsernameFriend", usernameGiven.getText().toString())
                        };
                        try {
                            HttpTask task = new HttpPostTask(getApplicationContext(), "https://ashittyscheduler.azurewebsites.net/api/friend/friendRequest",parameters,new AsyncHttpListener(){

                                @Override
                                public void onBeforeExecute() {
                                    Log.d("TEST SUBMIT Friend", "onBeforeExecute: TESTING ");
                                }

                                @Override
                                public void onResponse(HttpResponse httpResponse) {
                                    Log.d("Reponse",Integer.toString(httpResponse.getCode()));
                                }

                                @Override
                                public void onError() {

                                }

                                @Override
                                public void onFinishExecuting() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                    }
                });
            }
        });
        final ArrayList<String> usernames = new ArrayList<>();
        usernames.add("Test");
        usernames.add("Test2");
        usernames.add("Test3");
        usernames.add("Test4");
        CustomAdapter adapter = new CustomAdapter(this, usernames);
        final ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                String o = listView.getItemAtPosition(position).toString();
                Log.d("USERNAME:", "onItemClick: " + o);
                Toast.makeText(getApplicationContext(), o, Toast.LENGTH_SHORT).show();
                LoadFriendChat(FriendChatActivity.class , o);
            }
        });
    }
    private void LoadFriendChat(Class ActivityName , String o) {
        Intent loadPage = new Intent(this,ActivityName);
        loadPage.putExtra("Username" , o);
        startActivity(loadPage);
    }
}

class CustomAdapter extends ArrayAdapter<String>{

    private final Context context;
    private ArrayList<String> usernames;

    public CustomAdapter(Context context, ArrayList<String> usernames) {
        super(context, R.layout.customlistviewfriends, usernames);
        this.context = context;
        this.usernames = usernames;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.customlistviewfriends, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.FriendsUsername);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.FriendsUserImage);
        textView.setText(this.usernames.get(position));
        // Change the icon for Windows and iPhone
        String s = this.usernames.get(position);
        /*if (s.startsWith("Windows7") || s.startsWith("iPhone")
                || s.startsWith("Solaris")) {
            imageView.setImageResource(R.drawable.no);
        } else {
            imageView.setImageResource(R.drawable.ok);
        }*/

        return rowView;
    }
}
