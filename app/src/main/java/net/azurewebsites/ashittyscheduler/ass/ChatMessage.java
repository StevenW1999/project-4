package net.azurewebsites.ashittyscheduler.ass;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMessage implements Serializable {

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getReceiverId() {
        return ReceiverId;
    }

    public void setReceiverId(String receiverId) {
        ReceiverId = receiverId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public LocalDateTime getDate() {
        return Date;
    }

    public void setDate(LocalDateTime date) {
        Date = date;
    }

    public Boolean getSeen() {
        return Seen;
    }

    public void setSeen(Boolean seen) {
        Seen = seen;
    }

    private String MessageId ;
    private String SenderId ;
    private String ReceiverId;
    private String Message;
    private LocalDateTime Date ;
    private Boolean Seen ;

}
