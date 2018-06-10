package net.azurewebsites.ashittyscheduler.ass.settings;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.ApplicationConstants;
import net.azurewebsites.ashittyscheduler.ass.LoginActivity;
import net.azurewebsites.ashittyscheduler.ass.MainMenu;
import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.User;
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
        private EditTextPreference editUsername, editEmail;
        private Preference editPassword;
        private SwitchPreference notificationPreference_todo, notificationPreference_chat, notificationPreference_friends;

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

            // Get preferences screen
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            // find and store all preferences
            editDisplayName = (EditTextPreference)preferenceScreen.findPreference("edit_displayname");
            editDescription = (EditTextPreference)preferenceScreen.findPreference("edit_description");
            editUsername = (EditTextPreference)preferenceScreen.findPreference("edit_username");
            editEmail = (EditTextPreference)preferenceScreen.findPreference("edit_email");
            editPassword = preferenceScreen.findPreference("edit_password");
            notificationPreference_todo = (SwitchPreference)preferenceScreen.findPreference("switch_notifications_todos");
            notificationPreference_chat = (SwitchPreference)preferenceScreen.findPreference("switch_notifications_chat");
            notificationPreference_friends = (SwitchPreference)preferenceScreen.findPreference("switch_notifications_friends");

            editDisplayName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    updatePreference(preference, newValue.toString(), "https://ashittyscheduler.azurewebsites.net/api/settings/changedisplayname", new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            preference.setSummary(newValue.toString());
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "Your Display Name has been updated succesfully.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    return true;
                }
            });

            editEmail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    updatePreference(preference, newValue.toString(), "https://ashittyscheduler.azurewebsites.net/api/settings/changeemail", new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            preference.setSummary(newValue.toString());
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "Your e-mail has been updated succesfully.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    return true;
                }
            });

            editUsername.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    updatePreference(preference, newValue.toString(), "https://ashittyscheduler.azurewebsites.net/api/settings/changeusername", new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            preference.setSummary(newValue.toString());
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "Your username has been updated succesfully.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    return true;
                }
            });

            editPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getPreferenceScreen().getContext());

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final View v = inflater.inflate(R.layout.dialog_changepassword, null);  // this line
                    alertDialogBuilder.setView(v);

                    final AlertDialog changePasswordDialog = alertDialogBuilder.create();

                    final EditText passwordField = v.findViewById(R.id.passwordField);
                    final EditText passwordField2 = v.findViewById(R.id.passwordField2);

                    Button btnCancel = v.findViewById(R.id.cancelButton);
                    Button btnChange = v.findViewById(R.id.changeButton);

                    btnChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // passwords must have a length of at least 5
                            if (passwordField.length() < 5) {
                                passwordField.setError("Your new password must contain at least 5 characters.");
                                return;
                            }

                            // passwords must match
                            if (!passwordField.getText().toString().equals(passwordField2.getText().toString())) {
                                passwordField2.setError("Passwords do not match.");
                                return;
                            }

                            // Update the preference
                            updatePreference(editPassword, passwordField.getText().toString(),
                                    "https://ashittyscheduler.azurewebsites.net/api/settings/changepassword",
                                    new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Your password has been updated succesfully.", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                    });

                            changePasswordDialog.cancel();
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changePasswordDialog.cancel();
                        }
                    });

                    changePasswordDialog.show();

                    return true;
                }
            });

            editDescription.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    updatePreference(preference, newValue.toString(), "https://ashittyscheduler.azurewebsites.net/api/settings/changedescription", new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            preference.setSummary(newValue.toString());
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "Your description has been updated succesfully.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    return true;
                }
            });

            // TODO: Add onPreferenceChangeListeners for the switch (toggle) preferences !!!!!!!!!!!!!!!!!!!!!!!!!!1

            // load the user settings
            loadSettings();
        }

        public void loadSettings(){

            // Load the existing user settings into the fields
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
                                    editDisplayName.setDefaultValue(displayName);

                                    editUsername.setText(username);
                                    editUsername.setSummary(username);
                                    editUsername.setDefaultValue(username);

                                    editEmail.setText(email);
                                    editEmail.setSummary(email);
                                    editEmail.setDefaultValue(email);

                                    editDescription.setText(description);
                                    editDescription.setSummary(description);
                                    editDescription.setDefaultValue(description);

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
        }

        /*
        Updates a single preference and optionally updates its summary with the given value
         */
        public void updatePreference(final Preference preference, final String value, final String url, final UpdateListener listener) {
            // Body parameters
            Pair[] parameters = new Pair[] {
                    // Name is empty since it's a single value.
                    new Pair<>("", value),
            };

            HttpTask task = new HttpTask(getPreferenceScreen().getContext(), HttpMethod.PUT,
                    url,
                    new AsyncHttpListener() {

                        private ProgressDialog progressDialog;

                        @Override
                        public void onBeforeExecute() {
                            // show a progress dialog? currently disabled
                            progressDialog = ProgressDialog.show(getPreferenceScreen().getContext(),"Updating","Please wait");
                        }

                        @Override
                        public void onResponse(HttpResponse httpResponse) {

                            // obtain code
                            int code = httpResponse.getCode();

                            // preference update succeeded, update the summary (if needed)
                            if(code == HttpStatusCode.OK.getCode()){
                                listener.onSuccess();
                            }
                            else {
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "Something went wrong. Please try again later.",
                                        Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.RED).show();
                            }

                        }

                        @Override
                        public void onError() {
                            // Show an error dialog
                            new AlertDialog.Builder(getPreferenceScreen().getContext())
                                    .setTitle("Error")
                                    .setMessage("Something went wrong. Please try again later.")
                                    .setNeutralButton("OK", null).show();
                        }

                        @Override
                        public void onFinishExecuting() {
                            progressDialog.dismiss();
                        }
                    });

            task.setBodyParameters(parameters);
            task.execute();
        }

    }

}
