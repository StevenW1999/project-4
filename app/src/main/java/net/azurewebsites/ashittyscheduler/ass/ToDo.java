package net.azurewebsites.ashittyscheduler.ass;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.getInstance;

public class ToDo {
    private String id;
    private String title;
    private String description;
    private Calendar date;


    // TODO: Add getters and setters for these
    private String time;
    private Calendar reminderDate;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Calendar getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Calendar getReminderDate() {
        return reminderDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setReminderDate(Calendar reminderDate) {
        this.reminderDate = reminderDate;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public static ToDo fromJson(JSONObject obj) {
        ToDo t = new ToDo();

        try {
            // format date
            Calendar calendar = getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            calendar.setTime(sdf.parse(obj.getString("Date")));

            t.setId(obj.getString("Id"));
            t.setTitle(obj.getString("Title"));
            t.setDescription(obj.getString("Description"));
            t.setDate(calendar);

            return t;

        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}

