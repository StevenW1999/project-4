package net.azurewebsites.ashittyscheduler.ass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable{
    private String id;
    private String username;
    private String description;
    private String email;
    private boolean isOnline;
    private boolean isFriend;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public static User fromJson(JSONObject obj) {
        User u = new User();

        try {
            u.setUsername(obj.getString("Username"));
            u.setName(obj.getString("DisplayName"));
            u.setDescription(obj.getString("Description"));
            u.setEmail(obj.getString("Email"));
            u.setOnline(obj.getBoolean("IsOnline"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return u;
    }
}
