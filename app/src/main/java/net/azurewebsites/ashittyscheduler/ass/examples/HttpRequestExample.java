package net.azurewebsites.ashittyscheduler.ass.examples;

import android.app.Activity;
import android.util.Pair;

import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import java.io.IOException;

public class HttpRequestExample extends Activity {

    public void example() {

        // The parameters to send with the request
        Pair[] parameters = new Pair[] {
                new Pair<>("param1", "value"),
                new Pair<>("param2", "value2")
        };

        // The URL to use
        String url = "http://ashittyscheduler.azurewebsites.net";

        AsyncHttpListener listener = new AsyncHttpListener() {
            @Override
            public void onBeforeExecute() {
                // This code will be run before the task is executed.
            }

            @Override
            public void onResponse(HttpResponse httpResponse) {
                // This code is run after getting a response from the server.
                int code = httpResponse.getCode();
                String content = httpResponse.getMessage();
            }

            @Override
            public void onError() {
                // This code is run when we don't receive a response.
            }

            @Override
            public void onFinishExecuting() {
                // This code is run when the task has finished executing.
            }
        };

        // Create a new task
        HttpTask task = new HttpTask(
                this,
                HttpMethod.POST,
                url,
                listener
        );

        task.setBodyParameters(parameters);

        // or set uri parameters
        //task.setUriParameters(parameters);

        // Finally, execute the task
        task.execute();


    }

}
