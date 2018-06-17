package net.azurewebsites.ashittyscheduler.ass;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ToDo {
    private String id;
    private String title;
    private String description;
    private Calendar date;
    private boolean status;
    private boolean repeat;
    private String interval;


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


    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

