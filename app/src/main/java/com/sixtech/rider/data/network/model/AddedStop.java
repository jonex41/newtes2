package com.sixtech.rider.data.network.model;

public class AddedStop {

    public String d_address;
    public double d_latitude,d_longitude;

    public AddedStop(String address, double latitude, double longitude) {
        this.d_address = address;
        this.d_latitude = latitude;
        this.d_longitude = longitude;
    }

    public double getD_latitude() {
        return d_latitude;
    }

    public String getD_address() {
        return d_address;
    }

    public double getD_longitude() {
        return d_longitude;
    }
}
