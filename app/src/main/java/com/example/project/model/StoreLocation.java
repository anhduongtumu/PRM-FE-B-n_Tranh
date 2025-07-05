
package com.example.project.model;

import java.io.Serializable;

public class StoreLocation implements Serializable {
    private int id;
    private double latitude;
    private double longitude;
    private String address;

    public StoreLocation() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
