
package org.yccheok.jstock.engine.iex.batch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Quote {

    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("companyName")
    @Expose
    private String companyName;
    @SerializedName("primaryExchange")
    @Expose
    private String primaryExchange;
    @SerializedName("sector")
    @Expose
    private String sector;
    @SerializedName("calculationPrice")
    @Expose
    private String calculationPrice;
    @SerializedName("open")
    @Expose
    private double open;
    @SerializedName("openTime")
    @Expose
    private long openTime;
    @SerializedName("close")
    @Expose
    private double close;
    @SerializedName("closeTime")
    @Expose
    private long closeTime;
    @SerializedName("high")
    @Expose
    private double high;
    @SerializedName("low")
    @Expose
    private double low;
    @SerializedName("latestPrice")
    @Expose
    private double latestPrice;
    @SerializedName("latestSource")
    @Expose
    private String latestSource;
    @SerializedName("latestTime")
    @Expose
    private String latestTime;
    @SerializedName("latestUpdate")
    @Expose
    private long latestUpdate;
    @SerializedName("latestVolume")
    @Expose
    private long latestVolume;
    @SerializedName("iexRealtimePrice")
    @Expose
    private double iexRealtimePrice;
    @SerializedName("iexRealtimeSize")
    @Expose
    private long iexRealtimeSize;
    @SerializedName("iexLastUpdated")
    @Expose
    private long iexLastUpdated;
    @SerializedName("delayedPrice")
    @Expose
    private double delayedPrice;
    @SerializedName("delayedPriceTime")
    @Expose
    private long delayedPriceTime;
    @SerializedName("previousClose")
    @Expose
    private double previousClose;
    @SerializedName("change")
    @Expose
    private double change;
    @SerializedName("changePercent")
    @Expose
    private double changePercent;
    @SerializedName("iexMarketPercent")
    @Expose
    private double iexMarketPercent;
    @SerializedName("iexVolume")
    @Expose
    private long iexVolume;
    @SerializedName("avgTotalVolume")
    @Expose
    private long avgTotalVolume;
    @SerializedName("iexBidPrice")
    @Expose
    private double iexBidPrice;
    @SerializedName("iexBidSize")
    @Expose
    private long iexBidSize;
    @SerializedName("iexAskPrice")
    @Expose
    private double iexAskPrice;
    @SerializedName("iexAskSize")
    @Expose
    private long iexAskSize;
    @SerializedName("marketCap")
    @Expose
    private long marketCap;
    @SerializedName("peRatio")
    @Expose
    private double peRatio;
    @SerializedName("week52High")
    @Expose
    private double week52High;
    @SerializedName("week52Low")
    @Expose
    private double week52Low;
    @SerializedName("ytdChange")
    @Expose
    private double ytdChange;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Quote() {
    }

    /**
     * 
     * @param sector
     * @param latestSource
     * @param peRatio
     * @param avgTotalVolume
     * @param change
     * @param openTime
     * @param delayedPrice
     * @param latestPrice
     * @param close
     * @param primaryExchange
     * @param open
     * @param delayedPriceTime
     * @param changePercent
     * @param iexBidSize
     * @param iexRealtimePrice
     * @param iexVolume
     * @param iexLastUpdated
     * @param iexRealtimeSize
     * @param marketCap
     * @param previousClose
     * @param week52High
     * @param symbol
     * @param iexMarketPercent
     * @param latestTime
     * @param companyName
     * @param ytdChange
     * @param iexBidPrice
     * @param iexAskSize
     * @param closeTime
     * @param week52Low
     * @param latestVolume
     * @param iexAskPrice
     * @param calculationPrice
     * @param latestUpdate
     */
    public Quote(String symbol, String companyName, String primaryExchange, String sector, String calculationPrice, double open, long openTime, double close, long closeTime, double latestPrice, String latestSource, String latestTime, long latestUpdate, long latestVolume, double iexRealtimePrice, long iexRealtimeSize, long iexLastUpdated, double delayedPrice, long delayedPriceTime, double previousClose, double change, double changePercent, double iexMarketPercent, long iexVolume, long avgTotalVolume, double iexBidPrice, long iexBidSize, double iexAskPrice, long iexAskSize, long marketCap, double peRatio, double week52High, double week52Low, double ytdChange) {
        super();
        this.symbol = symbol;
        this.companyName = companyName;
        this.primaryExchange = primaryExchange;
        this.sector = sector;
        this.calculationPrice = calculationPrice;
        this.open = open;
        this.openTime = openTime;
        this.close = close;
        this.closeTime = closeTime;
        this.latestPrice = latestPrice;
        this.latestSource = latestSource;
        this.latestTime = latestTime;
        this.latestUpdate = latestUpdate;
        this.latestVolume = latestVolume;
        this.iexRealtimePrice = iexRealtimePrice;
        this.iexRealtimeSize = iexRealtimeSize;
        this.iexLastUpdated = iexLastUpdated;
        this.delayedPrice = delayedPrice;
        this.delayedPriceTime = delayedPriceTime;
        this.previousClose = previousClose;
        this.change = change;
        this.changePercent = changePercent;
        this.iexMarketPercent = iexMarketPercent;
        this.iexVolume = iexVolume;
        this.avgTotalVolume = avgTotalVolume;
        this.iexBidPrice = iexBidPrice;
        this.iexBidSize = iexBidSize;
        this.iexAskPrice = iexAskPrice;
        this.iexAskSize = iexAskSize;
        this.marketCap = marketCap;
        this.peRatio = peRatio;
        this.week52High = week52High;
        this.week52Low = week52Low;
        this.ytdChange = ytdChange;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPrimaryExchange() {
        return primaryExchange;
    }

    public void setPrimaryExchange(String primaryExchange) {
        this.primaryExchange = primaryExchange;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCalculationPrice() {
        return calculationPrice;
    }

    public void setCalculationPrice(String calculationPrice) {
        this.calculationPrice = calculationPrice;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
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

    public double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public String getLatestSource() {
        return latestSource;
    }

    public void setLatestSource(String latestSource) {
        this.latestSource = latestSource;
    }

    public String getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(String latestTime) {
        this.latestTime = latestTime;
    }

    public long getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(long latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    public long getLatestVolume() {
        return latestVolume;
    }

    public void setLatestVolume(long latestVolume) {
        this.latestVolume = latestVolume;
    }

    public double getIexRealtimePrice() {
        return iexRealtimePrice;
    }

    public void setIexRealtimePrice(double iexRealtimePrice) {
        this.iexRealtimePrice = iexRealtimePrice;
    }

    public long getIexRealtimeSize() {
        return iexRealtimeSize;
    }

    public void setIexRealtimeSize(long iexRealtimeSize) {
        this.iexRealtimeSize = iexRealtimeSize;
    }

    public long getIexLastUpdated() {
        return iexLastUpdated;
    }

    public void setIexLastUpdated(long iexLastUpdated) {
        this.iexLastUpdated = iexLastUpdated;
    }

    public double getDelayedPrice() {
        return delayedPrice;
    }

    public void setDelayedPrice(double delayedPrice) {
        this.delayedPrice = delayedPrice;
    }

    public long getDelayedPriceTime() {
        return delayedPriceTime;
    }

    public void setDelayedPriceTime(long delayedPriceTime) {
        this.delayedPriceTime = delayedPriceTime;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
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

    public double getIexMarketPercent() {
        return iexMarketPercent;
    }

    public void setIexMarketPercent(double iexMarketPercent) {
        this.iexMarketPercent = iexMarketPercent;
    }

    public long getIexVolume() {
        return iexVolume;
    }

    public void setIexVolume(long iexVolume) {
        this.iexVolume = iexVolume;
    }

    public long getAvgTotalVolume() {
        return avgTotalVolume;
    }

    public void setAvgTotalVolume(long avgTotalVolume) {
        this.avgTotalVolume = avgTotalVolume;
    }

    public double getIexBidPrice() {
        return iexBidPrice;
    }

    public void setIexBidPrice(double iexBidPrice) {
        this.iexBidPrice = iexBidPrice;
    }

    public long getIexBidSize() {
        return iexBidSize;
    }

    public void setIexBidSize(long iexBidSize) {
        this.iexBidSize = iexBidSize;
    }

    public double getIexAskPrice() {
        return iexAskPrice;
    }

    public void setIexAskPrice(double iexAskPrice) {
        this.iexAskPrice = iexAskPrice;
    }

    public long getIexAskSize() {
        return iexAskSize;
    }

    public void setIexAskSize(long iexAskSize) {
        this.iexAskSize = iexAskSize;
    }

    public long getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(long marketCap) {
        this.marketCap = marketCap;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }

    public double getWeek52High() {
        return week52High;
    }

    public void setWeek52High(double week52High) {
        this.week52High = week52High;
    }

    public double getWeek52Low() {
        return week52Low;
    }

    public void setWeek52Low(double week52Low) {
        this.week52Low = week52Low;
    }

    public double getYtdChange() {
        return ytdChange;
    }

    public void setYtdChange(double ytdChange) {
        this.ytdChange = ytdChange;
    }

}
