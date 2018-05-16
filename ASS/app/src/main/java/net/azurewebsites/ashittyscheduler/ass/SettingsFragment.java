package net.azurewebsites.ashittyscheduler.ass;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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

        Preference preference2 = preferenceScreen.findPreference("check_box_preference_1");

        preference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences preferences = getActivity().getSharedPreferences(MainMenu.PREFERENCES, Context.MODE_PRIVATE);
                String s = preferences.getString("Test", null);
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                SharedPreferences sp = getActivity().getSharedPreferences(MainMenu.PREFERENCES, Context.MODE_PRIVATE);
                String s = sp.getString("Test", null);

                if (((SwitchPreference)preference).isChecked()) {
                    SharedPreferences preferences = getActivity().getSharedPreferences(MainMenu.PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Test", "19823902390");
                    editor.commit();
                    Toast.makeText(getActivity(), "Changes committed.", Toast.LENGTH_SHORT).show();
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
