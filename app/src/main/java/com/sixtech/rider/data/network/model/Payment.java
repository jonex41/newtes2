package com.sixtech.rider.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("request_id")
    @Expose
    private int requestId;
    @SerializedName("promocode_id")
    @Expose
    private Object promocodeId;
    @SerializedName("payment_id")
    @Expose
    private Object paymentId;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("fixed")
    @Expose
    private double fixed;
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("minute")
    @Expose
    private double minute;
    @SerializedName("provider_pay")
    @Expose
    private double providerPay;
    @SerializedName("commision")
    @Expose
    private double commision;
    @SerializedName("discount")
    @Expose
    private double discount;
    @SerializedName("tax")
    @Expose
    private double tax;
    @SerializedName("wallet")
    @Expose
    private double wallet;
    @SerializedName("surge")
    @Expose
    private double surge;
    @SerializedName("tips")
    @Expose
    private double tips;
    @SerializedName("total")
    @Expose
    private double total;
    @SerializedName("payable")
    @Expose
    private double payable;
    @SerializedName("waiting_fare")
    @Expose
    private double waitingFare;
    @SerializedName("provider_commission")
    @Expose
    private double providerCommission;
    @SerializedName("hour")
    @Expose
    private double hour;
    @SerializedName("waiting_charges")
    @Expose
    private double waitingCharges;
    @SerializedName("flat_rate")
    @Expose
    private String flatRate;
    @SerializedName("rate_type")
    @Expose
    private String rateType;
    @SerializedName("total_kilometer")
    @Expose
    private double totalKilometer;
    @SerializedName("per_kilometer")
    @Expose
    private double perKilometer;
    @SerializedName("base_distance")
    @Expose
    private double baseDistance;
    @SerializedName("admin_fee")
    @Expose
    private double adminFee;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Object getPromocodeId() {
        return promocodeId;
    }

    public void setPromocodeId(Object promocodeId) {
        this.promocodeId = promocodeId;
    }

    public Object getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Object paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public double getFixed() {
        return fixed;
    }

    public void setFixed(double fixed) {
        this.fixed = fixed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCommision() {
        return commision;
    }

    public void setCommision(double commision) {
        this.commision = commision;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public double getSurge() {
        return surge;
    }

    public void setSurge(double surge) {
        this.surge = surge;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPayable() {
        return payable;
    }

    public void setPayable(double payable) {
        this.payable = payable;
    }

    public double getWaitingFare() {
        return waitingFare;
    }

    public void setWaitingFare(double waitingFare) {
        this.waitingFare = waitingFare;
    }

    public double getProviderCommission() {
        return providerCommission;
    }

    public void setProviderCommission(double providerCommission) {
        this.providerCommission = providerCommission;
    }

    public double getProviderPay() {
        return providerPay;
    }

    public void setProviderPay(double providerPay) {
        this.providerPay = providerPay;
    }

    public double getMinute() {
        return minute;
    }

    public void setMinute(double minute) {
        this.minute = minute;
    }

    public double getHour() {
        return hour;
    }

    public void setHour(double hour) {
        this.hour = hour;
    }

    public double getTips() {
        return tips;
    }

    public void setTips(double tips) {
        this.tips = tips;
    }

    public double getWaitingCharges() {
        return waitingCharges;
    }

    public void setWaitingCharges(double waitingCharges) {
        this.waitingCharges = waitingCharges;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getFlatRate() {
        return flatRate;
    }

    public void setFlatRate(String flatRate) {
        this.flatRate = flatRate;
    }

    public double getTotalKilometer() {
        return totalKilometer;
    }

    public void setTotalKilometer(double totalKilometer) {
        this.totalKilometer = totalKilometer;
    }

    public double getPerKilometer() {
        return perKilometer;
    }

    public void setPerKilometer(double perKilometer) {
        this.perKilometer = perKilometer;
    }

    public double getBaseDistance() {
        return baseDistance;
    }

    public void setBaseDistance(double baseDistance) {
        this.baseDistance = baseDistance;
    }

    public double getAdminFee() {
        return adminFee;
    }

    public void setAdminFee(double adminFee) {
        this.adminFee = adminFee;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", promocodeId=" + promocodeId +
                ", paymentId=" + paymentId +
                ", paymentMode='" + paymentMode + '\'' +
                ", fixed=" + fixed +
                ", distance=" + distance +
                ", minute=" + minute +
                ", providerPay=" + providerPay +
                ", commision=" + commision +
                ", discount=" + discount +
                ", tax=" + tax +
                ", wallet=" + wallet +
                ", surge=" + surge +
                ", tips=" + tips +
                ", total=" + total +
                ", payable=" + payable +
                ", waitingFare=" + waitingFare +
                ", providerCommission=" + providerCommission +
                ", hour=" + hour +
                ", waitingCharges=" + waitingCharges +
                ", flatRate=" + flatRate +
                ", rateType=" + rateType +
                ", totalKilometer=" + totalKilometer +
                ", perKilometer=" + perKilometer +
                ", baseDistance=" + baseDistance +
                ", adminFee=" + adminFee +
                '}';
    }
}
