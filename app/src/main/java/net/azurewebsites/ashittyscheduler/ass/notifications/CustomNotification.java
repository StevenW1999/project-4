package net.azurewebsites.ashittyscheduler.ass.notifications;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomNotification {

    public String Title;
    public String Message;

    public static CustomNotification fromJson(JSONObject obj) {
        CustomNotification n = new CustomNotification();

        try {
            n.Title = obj.getString("Title");
            n.Message = obj.getString("Message");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return n;
    }

}
