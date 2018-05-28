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

public class HttpTask extends AsyncTask<Void, Void, HttpResponse>{

    public static final String USER_AGENT = "Mozilla/5.0";

    private final Context context;
    private URL url;
    private String params;
    private HttpMethod method;

    private final AsyncHttpListener listener;

    public HttpTask(Context context, HttpMethod method, String url, Pair<String,String>[] params, AsyncHttpListener listener) throws IOException {
        this.context = context;
        this.url = new URL(url);
        this.method = method;
        this.params = convertParams(params);
        // append parameters to the URL (if needed)
        if (method == HttpMethod.GET || method == HttpMethod.PUT || method == HttpMethod.DELETE) {
            this.url = new URL(url + this.params);
        }
        else {
            this.url = new URL(url);
        }
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
    protected HttpResponse doInBackground(Void... voids) {
        HttpResponse httpResponse = null;

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            httpConnection.setRequestProperty("Accept-Language", "UTF-8");

            switch(method) {
                case POST:
                    httpConnection.setRequestMethod("POST");
                    httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpConnection.setRequestProperty("Content-Length", Integer.toString(params.getBytes(StandardCharsets.UTF_8).length));
                    httpConnection.setDoOutput(true);

                    // append parameters to the body (if needed)
                    if (params.length() != 0) {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpConnection.getOutputStream());
                        outputStreamWriter.write(params);
                        outputStreamWriter.flush();
                    }

                    break;
                case GET:
                    httpConnection.setRequestMethod("GET");
                case PUT:
                    httpConnection.setRequestMethod("PUT");
                    httpConnection.setDoOutput(true);
                    break;
                case DELETE:
                    httpConnection.setRequestMethod("DELETE");
                    httpConnection.setDoOutput(true);
                    break;
            }

            // read the input stream
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String read;

            while((read=br.readLine()) != null) {
                sb.append(read);
            }
            br.close();

            // create the custom response
            httpResponse = new HttpResponse(
                    httpConnection.getResponseCode(),
                    sb.toString()
            );

            // and finally, close the connection
            httpConnection.disconnect();
        }
        catch(IOException ex) {
            Log.e("HTTPCONNECTION", "ERROR!");
            ex.printStackTrace();
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

        listener.onFinishExecuting();
    }

}
