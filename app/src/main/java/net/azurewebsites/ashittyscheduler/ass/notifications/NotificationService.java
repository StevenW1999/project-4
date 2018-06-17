package net.azurewebsites.ashittyscheduler.ass.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import net.azurewebsites.ashittyscheduler.ass.ApplicationConstants;
import net.azurewebsites.ashittyscheduler.ass.LoginActivity;
import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpStatusCode;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class NotificationService extends Service {

    public static final String NOTIFICATION_CHANNEL = "NOTIFICATION_CHANNEL_ASS";

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, "ASS", importance);
            channel.setDescription("A notification channel for the ASS application.");
            channel.setLightColor(Color.BLUE);
            channel.setVibrationPattern(new long[]{0, 250, 250, 250});

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showNotification(Context context, Class gotoActivity, CustomNotification customNotification) {
        // Intent
        Intent notifyIntent = new Intent(context, gotoActivity);

        // Intent flags
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        // Construct the notification
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.transparentbutt)
                .setContentTitle(customNotification.Title)
                .setContentText(customNotification.Message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Set the notification channel in case
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL);
        }

        // Build the notification
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //TODO: Set ID afhankelijk van type notification
        if (notificationManager != null) {
            notificationManager.notify(new Random().nextInt(), notification);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private AsyncHttpListener notificationListener = new AsyncHttpListener() {

        @Override
        public void onBeforeExecute() {

        }

        @Override
        public void onResponse(HttpResponse httpResponse) {

            if (httpResponse.getCode() == HttpStatusCode.OK.getCode()) {

                try {
                    // Obtain and convert the notification
                    JSONObject obj = new JSONObject(httpResponse.getMessage());
                    CustomNotification notification = CustomNotification.fromJson(obj);

                    SharedPreferences sp = getApplicationContext().getSharedPreferences(ApplicationConstants.PREFERENCES,Context.MODE_PRIVATE);
                    boolean todo = sp.getBoolean("notifications_todo", true);
                    boolean friends = sp.getBoolean("notifications_friends", true);
                    boolean chat = sp.getBoolean("notifications_chat", true);

                    // Show the notification!
                    showNotification(NotificationService.this, LoginActivity.class, notification);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        @Override
        public void onError() {

        }

        @Override
        public void onFinishExecuting() {

        }
    };

    public void checkForNotifications() {

        HttpTask task = null;

        while(true) {
            if (task == null) {
                task = new HttpTask(this, HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/notifications/get", notificationListener);
                task.execute();
                try {
                    // Wait one second
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (task.getStatus() == AsyncTask.Status.FINISHED) {
                task = null;
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(ApplicationConstants.LOG_IMPORTANT, "Notification Service has started.");

        Thread t = new Thread() {
            @Override
            public void run() {
                createNotificationChannel();
                checkForNotifications();
            }
        };
        t.start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Wont be called as service is not bound
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
