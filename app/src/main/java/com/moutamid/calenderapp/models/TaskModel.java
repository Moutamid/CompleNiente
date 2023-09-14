package com.moutamid.calenderapp.models;

public class TaskModel {
    String ID, name, description;
    String userID, username, userHandle, userImage;
    CalendarDate date;
    boolean isEnded;

    // YES (YES) , REJ (REJECTED), PEN (PENDING)
    String isAccepted;

    public TaskModel() {
    }

    public TaskModel(String ID, String name, String description, String userID, String username, String userHandle, String userImage, CalendarDate date, boolean isEnded, String isAccepted) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.userID = userID;
        this.username = username;
        this.userHandle = userHandle;
        this.userImage = userImage;
        this.date = date;
        this.isEnded = isEnded;
        this.isAccepted = isAccepted;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }



    public String isAccepted() {
        return isAccepted;
    }

    public void setAccepted(String accepted) {
        isAccepted = accepted;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
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

    public CalendarDate getDate() {
        return date;
    }

    public void setDate(CalendarDate date) {
        this.date = date;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }
}
