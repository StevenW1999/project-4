package net.azurewebsites.ashittyscheduler.ass;

public class User {
    private String id;
    private String username;
    private String password;
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

}
