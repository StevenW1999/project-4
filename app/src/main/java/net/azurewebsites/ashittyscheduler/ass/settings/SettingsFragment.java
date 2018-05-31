package net.azurewebsites.ashittyscheduler.ass.settings;

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
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.R;

public class SettingsFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new MyPreferenceFragment())
                .commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private SwitchPreference notificationPreference;
        private SwitchPreference locationServicesPreference;

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
            notificationPreference = (SwitchPreference)preferenceScreen.findPreference("switch_notifications_todos");
            locationServicesPreference = (SwitchPreference)preferenceScreen.findPreference("switch_location_services");

            // Add event listeners
            notificationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (locationServicesPreference.isEnabled()) {
                        Toast.makeText(getActivity().getApplicationContext(), "Enabled.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity().getApplicationContext(), "DISABLED.", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        }
    }

}
