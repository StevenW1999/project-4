package net.azurewebsites.ashittyscheduler.ass.http;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpGetTask extends HttpTask {
    public HttpGetTask(Context context, String url, Pair<String, String>[] params, AsyncHttpListener listener) throws IOException {
        super(context, url, params, listener);

        this.url = new URL(url + "?" + this.params);
    }

    @Override
    protected HttpResponse doInBackground(Void... voids) {

        HttpResponse httpResponse = null;

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            httpConnection.setRequestProperty("Accept-Language", "UTF-8");
            httpConnection.setDoOutput(true);

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
            ex.printStackTrace();
        }

        return httpResponse;
    }
}
