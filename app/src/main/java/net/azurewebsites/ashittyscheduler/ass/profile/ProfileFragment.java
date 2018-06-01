package net.azurewebsites.ashittyscheduler.ass.profile;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.LoginActivity;
import net.azurewebsites.ashittyscheduler.ass.MainMenu;
import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TextView
                tv_displayname = getActivity().findViewById(R.id.profile_displayname),
                tv_username = getActivity().findViewById(R.id.profile_username),
                tv_description = getActivity().findViewById(R.id.profile_description),
                tv_location = getActivity().findViewById(R.id.profile_location);

        // parameters
        //TODO: ADD PARAMETER
        Pair[] parameters = new Pair[] {
                new Pair<>("tokenId", ""),
        };

        try {
            HttpTask task = new HttpTask(this.getContext(),
                    HttpMethod.POST,
                    "http://ashittyscheduler.azurewebsites.net/api/users/self",
                    new AsyncHttpListener() {

                private ProgressDialog progressDialog;

                @Override
                public void onBeforeExecute() {
                   // progressDialog = ProgressDialog.show(getContext(),"Loading profile","Please wait");
                }

                @Override
                public void onResponse(HttpResponse httpResponse) {
                    int code = httpResponse.getCode();

                    if (code == HttpStatusCode.OK.getCode()) {
                        try {
                            JSONObject userObj = new JSONObject(httpResponse.getMessage());

                            String username = userObj.getString("Username");
                            String displayName = userObj.getString("DisplayName");
                            String email = userObj.getString("Email");
                            boolean isOnline = userObj.getBoolean("IsOnline");

                            tv_displayname.setText(displayName);
                            tv_username.setText(username);
                            tv_description.setText("TODO: Create description for user profile");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(getActivity().getApplicationContext(), "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinishExecuting() {
                    //progressDialog.dismiss();
                }
            });

            task.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

}
