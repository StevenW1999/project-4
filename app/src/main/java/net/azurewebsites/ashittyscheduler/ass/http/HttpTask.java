package net.azurewebsites.ashittyscheduler.ass.http;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public abstract class HttpTask extends AsyncTask<Void, Void, HttpResponse>{

    public static final String USER_AGENT = "Mozilla/5.0";

    protected final Context context;
    protected URL url;
    protected String params;

    private final AsyncHttpListener listener;

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
        listener.onBeforeExecute();
    }

    @Override
    protected void onPostExecute(HttpResponse httpResponse) {
        if (httpResponse != null) {
            listener.onResponse(httpResponse);
        }
        else {
            listener.onError();
        }

        listener.onFinishExecuting();
    }

}
