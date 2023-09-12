package com.moutamid.calenderapp.models;

public class UserModel {
    String ID, username, name, email, password, dob, bio, image;

    public UserModel() {
    }

    public UserModel(String ID, String username, String name, String email, String password, String dob, String bio, String image) {
        this.ID = ID;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.bio = bio;
        this.image = image;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
