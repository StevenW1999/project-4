package net.azurewebsites.ashittyscheduler.ass.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import net.azurewebsites.ashittyscheduler.ass.LoginActivity;
import net.azurewebsites.ashittyscheduler.ass.MainMenu;
import net.azurewebsites.ashittyscheduler.ass.R;
import net.azurewebsites.ashittyscheduler.ass.http.AsyncHttpListener;
import net.azurewebsites.ashittyscheduler.ass.http.HttpMethod;
import net.azurewebsites.ashittyscheduler.ass.http.HttpResponse;
import net.azurewebsites.ashittyscheduler.ass.http.HttpTask;

public class NotificationService extends Service {

    public static final String NOTIFICATION_CHANNEL = "NOTIFICATION_CHANNEL_ASS";

    private int checkCount = 0;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, "ASS", importance);
            channel.setDescription("A notification channel for the ASS application.");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(Context context, Class gotoActivity, String title, String message) {
        // Intent
        Intent notifyIntent = new Intent(context, gotoActivity);

        // Intent flags
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Set the notification channel in case
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL);
        }

        Notification notification = builder.build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
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

        while(checkCount < 60) {

            showNotification(this, LoginActivity.class,"Notification " + checkCount, "Test");

            checkCount++;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while(true) {
            if (task == null) {
                Log.d("NOTIFICATIONSERVICE", "Checkcount = " + ++checkCount);
                task = new HttpTask(this, HttpMethod.GET, "http://ashittyscheduler.azurewebsites.net/api/todo/getmytodos", notificationListener);
                task.execute();
                try {
                    Thread.sleep(1000);
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

        Log.d("NOTIFICATIONSERVICE", "Started.");

        Thread t = new Thread() {
            @Override
            public void run() {
                //createNotificationChannel();
                //checkForNotifications();
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
