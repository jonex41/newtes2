package com.sixtech.rider.data.network.model;

public class UpdateStops {
    public String stop_id;
    public String d_address;
    public double d_latitude,d_longitude;
    public String action;

    public UpdateStops(String stop_id,String address, double latitude, double longitude,String action) {
        this.stop_id= stop_id;
        this.d_address = address;
        this.d_latitude = latitude;
        this.d_longitude = longitude;
        this.action = action;
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

    public String getAction() {
        return action;
    }

    public String getStop_id() {
        return stop_id;
    }
}
