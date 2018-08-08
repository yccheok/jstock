
package org.yccheok.jstock.engine.iex.chart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DailyChartResponse {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("open")
    @Expose
    private double open;
    @SerializedName("high")
    @Expose
    private double high;
    @SerializedName("low")
    @Expose
    private double low;
    @SerializedName("close")
    @Expose
    private double close;
    @SerializedName("volume")
    @Expose
    private long volume;
    @SerializedName("unadjustedVolume")
    @Expose
    private long unadjustedVolume;
    @SerializedName("change")
    @Expose
    private double change;
    @SerializedName("changePercent")
    @Expose
    private double changePercent;
    @SerializedName("vwap")
    @Expose
    private double vwap;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("changeOverTime")
    @Expose
    private double changeOverTime;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DailyChartResponse() {
    }

    /**
     * 
     * @param open
     * @param vwap
     * @param changePercent
     * @param change
     * @param unadjustedVolume
     * @param volume
     * @param label
     * @param high
     * @param changeOverTime
     * @param low
     * @param date
     * @param close
     */
    public DailyChartResponse(String date, double open, double high, double low, double close, long volume, long unadjustedVolume, double change, double changePercent, double vwap, String label, double changeOverTime) {
        super();
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.unadjustedVolume = unadjustedVolume;
        this.change = change;
        this.changePercent = changePercent;
        this.vwap = vwap;
        this.label = label;
        this.changeOverTime = changeOverTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public long getUnadjustedVolume() {
        return unadjustedVolume;
    }

    public void setUnadjustedVolume(long unadjustedVolume) {
        this.unadjustedVolume = unadjustedVolume;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public double getVwap() {
        return vwap;
    }

    public void setVwap(double vwap) {
        this.vwap = vwap;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getChangeOverTime() {
        return changeOverTime;
    }

    public void setChangeOverTime(double changeOverTime) {
        this.changeOverTime = changeOverTime;
    }

}
