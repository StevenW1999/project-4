package net.azurewebsites.ashittyscheduler.ass;
import java.util.Date;
import java.util.List;

public class ToDo {
    private String id;
    private String title;
    private String description;
    private Date date;

    // TODO: Add getters and setters for these
    private String time;
    private Date dateReminder;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
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

    public void setDate(Date date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return getTitle();
    }
    


}

