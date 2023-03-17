package com.sixtech.rider.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stop {

@SerializedName("id")
@Expose
private int id;
@SerializedName("user_request_id")
@Expose
private int userRequestId;
@SerializedName("d_address")
@Expose
private String dAddress;
@SerializedName("d_latitude")
@Expose
private double dLatitude;
@SerializedName("d_longitude")
@Expose
private double dLongitude;
@SerializedName("order")
@Expose
private int order;
@SerializedName("status")
@Expose
private String status;
@SerializedName("created_at")
@Expose
private String createdAt;
@SerializedName("updated_at")
@Expose
private String updatedAt;

public int getId() {
return id;
}

public void setId(int id) {
this.id = id;
}

public int getUserRequestId() {
return userRequestId;
}

public void setUserRequestId(int userRequestId) {
this.userRequestId = userRequestId;
}

public String getDAddress() {
return dAddress;
}

public void setDAddress(String dAddress) {
this.dAddress = dAddress;
}

public double getDLatitude() {
return dLatitude;
}

public void setDLatitude(double dLatitude) {
this.dLatitude = dLatitude;
}

public double getDLongitude() {
return dLongitude;
}

public void setDLongitude(double dLongitude) {
this.dLongitude = dLongitude;
}

public int getOrder() {
return order;
}

public void setOrder(int order) {
this.order = order;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getCreatedAt() {
return createdAt;
}

public void setCreatedAt(String createdAt) {
this.createdAt = createdAt;
}

public String getUpdatedAt() {
return updatedAt;
}

public void setUpdatedAt(String updatedAt) {
this.updatedAt = updatedAt;
}

}