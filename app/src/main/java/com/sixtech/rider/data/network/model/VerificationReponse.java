package com.sixtech.rider.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerificationReponse {


    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("isVerified")
    @Expose
    private int isVerified;
    @SerializedName("message")
    @Expose
    private String message;

    public int getIsVerified() {
        return isVerified;
    }

    public String getMessage() {
        return message;
    }

    public boolean getStatus() {
        return status;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
