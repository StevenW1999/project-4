package net.azurewebsites.ashittyscheduler.ass.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.preference.Preference;
import android.support.design.widget.Snackbar;

import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import java.io.IOException;

public class UpdatePreferenceTask extends HttpTask {

    private Context context;
    private Preference preference;

    public UpdatePreferenceTask(final Context context, String url, Preference preference) throws IOException {
        super(context, HttpMethod.PUT, url, new AsyncHttpListener() {
            private ProgressDialog progressDialog;

            @Override
            public void onBeforeExecute() {
                progressDialog = ProgressDialog.show(context,"Updating","Please wait");
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                int code = httpResponse.getCode();
            }

            @Override
            public void onError() {
                // TODO: Handle error (show error message)
            }

            @Override
            public void onFinishExecuting() {
                progressDialog.dismiss();
            }
        });

        this.context = context;
        this.preference = preference;

        // Update the summary to the new value whenever the preference is changed
        this.preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                return true;
            }
        });
    }


}
