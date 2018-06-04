package net.azurewebsites.ashittyscheduler.ass.settings;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

            editDisplayName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    updatePreference(preference, newValue.toString(), "https://ashittyscheduler.azurewebsites.net/api/settings/changedisplayname");
                    return true;
                }
            });

            editEmail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatePreference(preference, newValue.toString(), "https://ashittyscheduler.azurewebsites.net/api/settings/changeemail");
                    return true;
                }
            });

            editUsername.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    updatePreference(preference, newValue.toString(), "https://ashittyscheduler.azurewebsites.net/api/settings/changeusername");
                    return true;
                }
            });

            editPassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatePreference(preference, newValue.toString(), "https://ashittyscheduler.azurewebsites.net/api/settings/changepassword");
                    return true;
                }
            });

            editDescription.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatePreference(preference, newValue.toString(), "https://ashittyscheduler.azurewebsites.net/api/settings/changedescription");
                    return true;
                }
            });

            loadSettings();
        }

        public void loadSettings(){

            try {
                HttpTask task = new HttpTask(this.getContext(),
                        HttpMethod.GET,
                        "http://ashittyscheduler.azurewebsites.net/api/users/self",
                        new AsyncHttpListener() {

                            private ProgressDialog progressDialog;

                            @Override
                            public void onBeforeExecute() {
                                progressDialog = ProgressDialog.show(getContext(),"Loading settings","Please wait");
                            }

                            @Override
                            public void onResponse(HttpResponse httpResponse) {
                                int code = httpResponse.getCode();

                                if (code == HttpStatusCode.OK.getCode()) {
                                    try {
                                        JSONObject userObj = new JSONObject(httpResponse.getMessage());

                                        String username = userObj.getString("Username");
                                        String displayName = userObj.getString("DisplayName");
                                        String description = userObj.getString("Description");
                                        String email = userObj.getString("Email");
                                        boolean isOnline = userObj.getBoolean("IsOnline");

                                        editDisplayName.setText(displayName);
                                        editDisplayName.setSummary(displayName);

                                        editUsername.setText(username);
                                        editUsername.setSummary(username);

                                        editEmail.setText(email);
                                        editEmail.setSummary(email);

                                        editDescription.setText(description);
                                        editDescription.setSummary(description);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getContext(), "An error occured. Please try again later ☹", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFinishExecuting() {
                                progressDialog.dismiss();
                            }
                        });

                task.execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void updatePreference(final Preference preference, final String value, final String url) {
            // parameters
            Pair[] parameters = new Pair[] {
                    new Pair<>("", value),
            };

            try {
                HttpTask task = new HttpTask(getPreferenceScreen().getContext(), HttpMethod.PUT,
                        url,
                        new AsyncHttpListener() {

                            private ProgressDialog progressDialog;

                            @Override
                            public void onBeforeExecute() {
                                // show a progress dialog (duh)
                                progressDialog = ProgressDialog.show(getPreferenceScreen().getContext(),
                                        "Updating",
                                        "Please wait");
                            }

                            @Override
                            public void onResponse(HttpResponse httpResponse) {

                                // obtain code
                                int code = httpResponse.getCode();

                                if(code == HttpStatusCode.OK.getCode()){
                                    // obtain response message (our token in this case)
                                    preference.setSummary(value.toString());
                                }

                            }

                            @Override
                            public void onError() {
                                new AlertDialog.Builder(getPreferenceScreen().getContext())
                                        .setTitle("Error")
                                        .setMessage("Something went wrong. Please try again later.")
                                        .setNeutralButton("OK", null).show();
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
        }

    }

}
