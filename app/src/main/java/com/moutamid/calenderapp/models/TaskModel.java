package com.moutamid.calenderapp.models;

import java.util.ArrayList;

public class TaskModel {
    String ID, name, description, location;
    ArrayList<UserModel> user;
    String taskImage, recurrence;
    CalendarDate date;
    boolean isEnded;
    long startTime;
    // YES (YES) , REJ (REJECTED), PEN (PENDING)
    String isAccepted;

    public TaskModel() {
    }

    public TaskModel(String ID, String name, String description, String location, ArrayList<UserModel> user, String taskImage, String recurrence, CalendarDate date, boolean isEnded, long startTime, String isAccepted) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.location = location;
        this.user = user;
        this.taskImage = taskImage;
        this.recurrence = recurrence;
        this.date = date;
        this.isEnded = isEnded;
        this.startTime = startTime;
        this.isAccepted = isAccepted;
    }

    public ArrayList<UserModel> getUser() {
        return user;
    }

    public void setUser(ArrayList<UserModel> user) {
        this.user = user;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTaskImage() {
        return taskImage;
    }

    public void setTaskImage(String taskImage) {
        this.taskImage = taskImage;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public String getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String isAccepted() {
        return isAccepted;
    }

    public void setAccepted(String accepted) {
        isAccepted = accepted;
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
