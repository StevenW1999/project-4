package net.azurewebsites.ashittyscheduler.ass.examples;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.SwitchPreference;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.MainMenu;

public class SharedPreferencesExample extends Activity {

    public void example() {


        SharedPreferences preferences = this.getSharedPreferences(MainMenu.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("Test1", "9048239042390");
        editor.putString("Test2", "9048239042390");

        // Retrieving something from the shared preferences
        String s = preferences.getString("Test1", null);

        // Removing items
        editor.remove("Test11");

        // Committing all changes
        editor.apply(); // use apply to asynchronously apply changes

        boolean success = editor.commit(); // use commit to synchronously apply changes (waits for a return value)
    }

}
