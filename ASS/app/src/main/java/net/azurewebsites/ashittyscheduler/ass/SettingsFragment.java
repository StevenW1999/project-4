package net.azurewebsites.ashittyscheduler.ass;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // To get a preference
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SwitchPreference preference = (SwitchPreference)preferenceScreen.findPreference("test_switch");

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                if (((SwitchPreference)preference).isChecked()) {
                    SharedPreferences preferences = getActivity().getSharedPreferences(MainMenu.PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Test", "19823902390");
                    editor.commit();
                }


                Toast.makeText(getActivity(), ( ((SwitchPreference)preference).isChecked() ? "On" : "Off"), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //change title
        preference.setTitle("my_title");

        // etc
    }

}
