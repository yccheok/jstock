package org.yccheok.jstock.engine.iex.dividends;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DividendsResponse {

    @SerializedName("exDate")
    @Expose
    private String exDate;
    @SerializedName("paymentDate")
    @Expose
    private String paymentDate;
    @SerializedName("recordDate")
    @Expose
    private String recordDate;
    @SerializedName("declaredDate")
    @Expose
    private String declaredDate;
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("qualified")
    @Expose
    private String qualified;

    public String getExDate() {
        return exDate;
    }

    public void setExDate(String exDate) {
        this.exDate = exDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getDeclaredDate() {
        return declaredDate;
    }

    public void setDeclaredDate(String declaredDate) {
        this.declaredDate = declaredDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQualified() {
        return qualified;
    }

    public void setQualified(String qualified) {
        this.qualified = qualified;
    }

}
