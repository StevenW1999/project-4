package net.azurewebsites.ashittyscheduler.ass.http;

public interface AsyncHttpListener {

    // Performed before a task has started executing.
    void onPreExecute();

    // Performed on receiving a successful http response.
    void onResponse(HttpResponse httpResponse);

    // Performed if an error has occurred during the request
    void onError();

    // Performed after the task has finished executing.
    void onPostExecute();
}
