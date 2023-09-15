package com.moutamid.calenderapp.models;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class ChatListModel implements Serializable {
    String ID, image, name, message, taskID;
    Date date;

    public ChatListModel() {}

    public ChatListModel(String ID, String image, String name, String message, String taskID, Date date) {
        this.ID = ID;
        this.image = image;
        this.name = name;
        this.message = message;
        this.taskID = taskID;
        this.date = date;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
