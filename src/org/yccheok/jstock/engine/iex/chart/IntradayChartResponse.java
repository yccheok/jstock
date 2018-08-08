
package org.yccheok.jstock.engine.iex.chart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IntradayChartResponse {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("minute")
    @Expose
    private String minute;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("high")
    @Expose
    private double high;
    @SerializedName("low")
    @Expose
    private double low;
    @SerializedName("average")
    @Expose
    private double average;
    @SerializedName("volume")
    @Expose
    private long volume;
    @SerializedName("notional")
    @Expose
    private double notional;
    @SerializedName("numberOfTrades")
    @Expose
    private long numberOfTrades;
    @SerializedName("changeOverTime")
    @Expose
    private double changeOverTime;

    /**
     * No args constructor for use in serialization
     * 
     */
    public IntradayChartResponse() {
    }

    /**
     * 
     * @param minute
     * @param numberOfTrades
     * @param notional
     * @param volume
     * @param label
     * @param high
     * @param changeOverTime
     * @param low
     * @param date
     * @param average
     */
    public IntradayChartResponse(String date, String minute, String label, double high, double low, double average, long volume, double notional, long numberOfTrades, double changeOverTime) {
        super();
        this.date = date;
        this.minute = minute;
        this.label = label;
        this.high = high;
        this.low = low;
        this.average = average;
        this.volume = volume;
        this.notional = notional;
        this.numberOfTrades = numberOfTrades;
        this.changeOverTime = changeOverTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getNotional() {
        return notional;
    }

    public void setNotional(double notional) {
        this.notional = notional;
    }

    public long getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(long numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public double getChangeOverTime() {
        return changeOverTime;
    }

    public void setChangeOverTime(double changeOverTime) {
        this.changeOverTime = changeOverTime;
    }

}
