package com.moutamid.calenderapp.models;

import java.util.ArrayList;

public class TaskModel {
    String name, description;
    ArrayList<String> images;
    long date;

    public TaskModel() {
    }

    public TaskModel(String name, String description, ArrayList<String> images, long date) {
        this.name = name;
        this.description = description;
        this.images = images;
        this.date = date;
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

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
