package com.moutamid.calenderapp.models;

public class ChatsModel {
    String message;
    String senderID;
    long timestamps;
    String type;

    public ChatsModel() {
    }

    public ChatsModel(String message, String senderID, long timestamps, String type) {
        this.message = message;
        this.senderID = senderID;
        this.timestamps = timestamps;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(long timestamps) {
        this.timestamps = timestamps;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
