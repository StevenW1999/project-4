package net.azurewebsites.ashittyscheduler.ass.settings;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.ApplicationConstants;
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

public class SettingsFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Add preferences
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new MyPreferenceFragment())
                .commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private EditTextPreference editDisplayName, editDescription;
        private EditTextPreference editUsername, editEmail, editPassword;
        private SwitchPreference notificationPreference, locationServicesPreference;

        public MyPreferenceFragment() {
            // required empty constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Get preferences
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            editDisplayName = (EditTextPreference)preferenceScreen.findPreference("edit_displayname");
            editDescription = (EditTextPreference)preferenceScreen.findPreference("edit_description");
            editUsername = (EditTextPreference)preferenceScreen.findPreference("edit_username");
            editEmail = (EditTextPreference)preferenceScreen.findPreference("edit_email");
            editPassword = (EditTextPreference)preferenceScreen.findPreference("edit_password");
            notificationPreference = (SwitchPreference)preferenceScreen.findPreference("switch_notifications_todos");
            locationServicesPreference = (SwitchPreference)preferenceScreen.findPreference("switch_location_services");

            editUsername.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {

                    Toast.makeText(getActivity().getApplicationContext(), "IT WORKS", Toast.LENGTH_SHORT).show();

                    // parameters
                    Pair[] parameters = new Pair[] {
                            new Pair<>("username", newValue.toString()),
                    };

                    try {
                        HttpTask task = new HttpTask(getActivity().getApplicationContext(), HttpMethod.PUT,
                                "https://ashittyscheduler.azurewebsites.net/api/settings/changeusername",
                                new AsyncHttpListener() {

                                    private ProgressDialog progressDialog;

                                    @Override
                                    public void onBeforeExecute() {
                                        // show a progress dialog (duh)
                                        progressDialog = ProgressDialog.show(getActivity().getApplicationContext(),
                                                "Updating username...",
                                                "Please wait");
                                    }

                                    @Override
                                    public void onResponse(HttpResponse httpResponse) {

                                        // obtain code
                                        int code = httpResponse.getCode();

                                        if(code == HttpStatusCode.OK.getCode()){
                                            // obtain response message (our token in this case)
                                            preference.setSummary(newValue.toString());
                                        }
                                    }

                                    @Override
                                    public void onError() {

                                    }

                                    @Override
                                    public void onFinishExecuting() {
                                        // dismiss the progress dialog (duh)
                                        progressDialog.dismiss();
                                    }
                                });

                        task.setBodyParameters(parameters);
                        task.execute();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            });

            editDescription.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    return true;
                }
            });


        }
    }

}
