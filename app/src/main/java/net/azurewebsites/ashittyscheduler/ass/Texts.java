package net.azurewebsites.ashittyscheduler.ass;

public class Texts {
    final String message;
    final boolean sender;

    public Texts(String message,boolean sender){
        this.message = message;
        this.sender= sender;
    }
    public boolean isSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
