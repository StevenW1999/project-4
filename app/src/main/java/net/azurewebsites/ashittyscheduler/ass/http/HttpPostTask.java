package net.azurewebsites.ashittyscheduler.ass.http;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class HttpPostTask extends HttpTask {
    public HttpPostTask(Context context, String url, Pair<String, String>[] params, AsyncHttpListener listener) throws IOException {
        super(context, url, params, listener);
    }

    @Override
    protected HttpResponse doInBackground(Void... voids) {

        HttpResponse httpResponse = null;

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
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
            ex.printStackTrace();
        }

        return httpResponse;
    }

}
