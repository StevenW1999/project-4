package net.azurewebsites.ashittyscheduler.ass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import java.io.IOException;

public class detailscreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailscreen);
        try {
            HttpTask httpTask = new HttpTask(this.getApplicationContext(),
                    HttpMethod.GET,
                    "http://ashittyscheduler.azurewebsites.net/api/todo/get",

                    new AsyncHttpListener()

                    {
                        @Override
                        public void onBeforeExecute() {

                        }

                        @Override
                        public void onResponse(HttpResponse httpResponse) {
                            int code = httpResponse.getCode();
                            if (code == HttpStatusCode.OK.getCode()) {



                            }

                        }

                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onFinishExecuting() {

                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void EditButtonClicked(View view) {



}
}
