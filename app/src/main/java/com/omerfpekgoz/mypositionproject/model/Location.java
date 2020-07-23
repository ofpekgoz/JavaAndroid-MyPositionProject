package com.omerfpekgoz.mypositionproject.model;

public class Location {

    private String latitude;   //enlem
    private String longitude;  //boylam
    private String time;

    public Location() {
    }

    public Location(String latitude, String longitude, String time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
