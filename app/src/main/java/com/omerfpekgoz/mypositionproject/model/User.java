package com.omerfpekgoz.mypositionproject.model;

public class User {

    private String userName;
    private String email;
    private String password;
    private String image;


    public User() {
    }

    public User(String userName, String email, String password, String image) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.image = image;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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


}
