package com.sixtech.rider.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Request {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("request_id")
    @Expose
    private int request_id;
    @SerializedName("provider_id")
    @Expose
    private int provider_id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("dropped")
    @Expose
    private String dropped;
    @SerializedName("offer_price")
    @Expose
    private Double offer_price;
    @SerializedName("provider")
    @Expose
    private Provider provider;


    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Double getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(Double offer_price) {
        this.offer_price = offer_price;
    }

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDropped() {
        return dropped;
    }

    public void setDropped(String dropped) {
        this.dropped = dropped;
    }
}
