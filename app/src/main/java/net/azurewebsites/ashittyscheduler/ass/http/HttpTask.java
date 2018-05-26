package net.azurewebsites.ashittyscheduler.ass.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import net.azurewebsites.ashittyscheduler.ass.LoginActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpTask extends AsyncTask<Void, Void, HttpResponse>{

    private static final String USER_AGENT = "Mozilla/5.0";

    private final Context context;
    private final URL url;
    private final String params;

    private AsyncHttpListener listener;

    public HttpTask(Context context, String url, Pair<String,String>[] params, AsyncHttpListener listener) throws IOException {
        this.context = context;
        this.url = new URL(url);
        this.params = convertParams(params);
        this.listener = listener;
    }

    private String convertParams(Pair<String, String>[] params) throws IOException {

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < params.length; ++i) {
            Pair<String, String> param = params[i];

            sb.append(URLEncoder.encode(param.first,"UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(param.second,"UTF-8"));

            if (i + 1 < params.length) {
                sb.append("&");
            }
        }

        return sb.toString();
    }

    @Override
    protected void onPreExecute() {
        listener.onPreExecute();
    }

    @Override
    protected HttpResponse doInBackground(Void... voids) {

        Log.d("HTTPCLIENT", "Do in background started...");

        HttpResponse httpResponse = null;

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("User-Agent", USER_AGENT);
            httpConnection.setRequestProperty("Accept-Language", "UTF-8");
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConnection.setRequestProperty("Content-Length", Integer.toString(params.getBytes(StandardCharsets.UTF_8).length));
            httpConnection.setDoOutput(true);

            // append parameters if needed
            if (params.length() != 0) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpConnection.getOutputStream());
                outputStreamWriter.write(params);
                outputStreamWriter.flush();
            }

            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String read;

            while((read=br.readLine()) != null) {
                sb.append(read);
            }
            br.close();

            httpResponse = new HttpResponse(
                    httpConnection.getResponseCode(),
                    sb.toString()
            );

            httpConnection.disconnect();
        }
        catch(IOException ex) {
            Log.e("HTTPCLIENT", "ERROR!");
        }

        return httpResponse;
    }

    @Override
    protected void onPostExecute(HttpResponse httpResponse) {
        if (httpResponse != null) {
            listener.onResponse(httpResponse);
        }
        else {
            listener.onError();
        }

        listener.onPostExecute();
    }

}
