package com.example.taller3;

public class LocationPojo {
    private double latitude;
    private double longitude;
    private String name;

    public LocationPojo() {
        
    }

    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public void setName(String name) {
        this.name = name;
    }
}
