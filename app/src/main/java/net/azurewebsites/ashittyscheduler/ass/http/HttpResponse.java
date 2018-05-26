package net.azurewebsites.ashittyscheduler.ass.http;

public class HttpResponse {

    private int code;
    private String message;

    HttpResponse(int code, String response) {
        this.code = code;
        this.message = response;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage(){
        return this.message;
    }

}
