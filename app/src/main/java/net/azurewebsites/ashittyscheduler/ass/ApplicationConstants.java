package net.azurewebsites.ashittyscheduler.ass;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

public final class ApplicationConstants {

    public static final String PREFERENCES = "ASS";
    public static final int UNREADMESSAGENOTIFICATIONID = 0;
    public static final int RUNTIMEMESSAGENOTIFICATIONID = 1;

    public static final String LOG_IMPORTANT = "IMPORTANT";
    public static final String LOG_NORMAL = "NORMAL";


    // i know this doesnt belong here... SHUT UP
    public static void tokenCheck(final Context context, final Activity activity) {
        String token = context.getSharedPreferences(ApplicationConstants.PREFERENCES,Context.MODE_PRIVATE).getString("Token", null);
        if(token == null || token.equals("")){
            final AlertDialog.Builder messageBox = new AlertDialog.Builder(context);
            messageBox.setTitle("You are not logged in!");
            messageBox.setMessage("Please log back in.");
            messageBox.setCancelable(false);
            messageBox.setNeutralButton("OK" ,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    activity.finish();
                }
            });
            messageBox.show();
        }
    }

}
