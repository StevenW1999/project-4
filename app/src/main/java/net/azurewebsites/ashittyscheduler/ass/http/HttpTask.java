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
    private String url;

    private String uriParameters;
    private String bodyParameters;
    private HttpMethod method;

    private final AsyncHttpListener listener;

    public HttpTask(Context context, HttpMethod method, String url, AsyncHttpListener listener) throws IOException {
        this.context = context;
        this.method = method;
        this.url = url;
        this.listener = listener;
        this.bodyParameters = "";
        this.uriParameters = "";
    }

    public void setBodyParameters(Pair<String,String>[] params) {
        try {
            this.bodyParameters = convertParams(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUriParameters(Pair<String,String>[] params) {
        try {
            this.uriParameters = convertParams(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(url + uriParameters).openConnection();
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            httpConnection.setRequestProperty("Accept-Language", "UTF-8");

            switch(method) {
                case POST:
                    httpConnection.setRequestMethod("POST");
                    break;
                case GET:
                    httpConnection.setRequestMethod("GET");
                case PUT:
                    httpConnection.setRequestMethod("PUT");
                    break;
                case DELETE:
                    httpConnection.setRequestMethod("DELETE");
                    break;
            }

            // append parameters to the body (if needed)
            if (bodyParameters.length() != 0) {
                httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpConnection.setRequestProperty("Content-Length", Integer.toString(this.bodyParameters.getBytes(StandardCharsets.UTF_8).length));
                httpConnection.setDoOutput(true);

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpConnection.getOutputStream());
                outputStreamWriter.write(this.bodyParameters);
                outputStreamWriter.flush();
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
