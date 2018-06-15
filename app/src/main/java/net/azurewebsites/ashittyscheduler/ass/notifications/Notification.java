package net.azurewebsites.ashittyscheduler.ass.notifications;

import org.json.JSONException;
import org.json.JSONObject;

public class Notification {

    public String Title;
    public String Message;

    public static Notification fromJson(JSONObject obj) {
        Notification n = new Notification();

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
