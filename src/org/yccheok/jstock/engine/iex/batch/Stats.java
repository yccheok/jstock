
package org.yccheok.jstock.engine.iex.batch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("companyName")
    @Expose
    private String companyName;
    @SerializedName("marketcap")
    @Expose
    private long marketcap;
    @SerializedName("beta")
    @Expose
    private double beta;
    @SerializedName("week52high")
    @Expose
    private double week52high;
    @SerializedName("week52low")
    @Expose
    private double week52low;
    @SerializedName("week52change")
    @Expose
    private double week52change;
    @SerializedName("shortInterest")
    @Expose
    private long shortInterest;
    @SerializedName("shortDate")
    @Expose
    private String shortDate;
    @SerializedName("dividendRate")
    @Expose
    private double dividendRate;
    @SerializedName("dividendYield")
    @Expose
    private double dividendYield;
    @SerializedName("exDividendDate")
    @Expose
    private String exDividendDate;
    @SerializedName("latestEPS")
    @Expose
    private double latestEPS;
    @SerializedName("latestEPSDate")
    @Expose
    private String latestEPSDate;
    @SerializedName("sharesOutstanding")
    @Expose
    private long sharesOutstanding;
    @SerializedName("float")
    @Expose
    private long _float;
    @SerializedName("returnOnEquity")
    @Expose
    private double returnOnEquity;
    @SerializedName("consensusEPS")
    @Expose
    private double consensusEPS;
    @SerializedName("numberOfEstimates")
    @Expose
    private long numberOfEstimates;
    @SerializedName("EPSSurpriseDollar")
    @Expose
    private double ePSSurpriseDollar;
    @SerializedName("EPSSurprisePercent")
    @Expose
    private double ePSSurprisePercent;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("EBITDA")
    @Expose
    private double eBITDA;
    @SerializedName("revenue")
    @Expose
    private double revenue;
    @SerializedName("grossProfit")
    @Expose
    private double grossProfit;
    @SerializedName("cash")
    @Expose
    private double cash;
    @SerializedName("debt")
    @Expose
    private double debt;
    @SerializedName("ttmEPS")
    @Expose
    private double ttmEPS;
    @SerializedName("revenuePerShare")
    @Expose
    private double revenuePerShare;
    @SerializedName("revenuePerEmployee")
    @Expose
    private Object revenuePerEmployee;
    @SerializedName("peRatioHigh")
    @Expose
    private double peRatioHigh;
    @SerializedName("peRatioLow")
    @Expose
    private double peRatioLow;
    @SerializedName("returnOnAssets")
    @Expose
    private double returnOnAssets;
    @SerializedName("returnOnCapital")
    @Expose
    private double returnOnCapital;
    @SerializedName("profitMargin")
    @Expose
    private double profitMargin;
    @SerializedName("priceToSales")
    @Expose
    private double priceToSales;
    @SerializedName("priceToBook")
    @Expose
    private double priceToBook;
    @SerializedName("day200MovingAvg")
    @Expose
    private double day200MovingAvg;
    @SerializedName("day50MovingAvg")
    @Expose
    private double day50MovingAvg;
    @SerializedName("institutionPercent")
    @Expose
    private double institutionPercent;
    @SerializedName("insiderPercent")
    @Expose
    private double insiderPercent;
    @SerializedName("shortRatio")
    @Expose
    private double shortRatio;
    @SerializedName("year5ChangePercent")
    @Expose
    private double year5ChangePercent;
    @SerializedName("year2ChangePercent")
    @Expose
    private double year2ChangePercent;
    @SerializedName("year1ChangePercent")
    @Expose
    private double year1ChangePercent;
    @SerializedName("ytdChangePercent")
    @Expose
    private double ytdChangePercent;
    @SerializedName("month6ChangePercent")
    @Expose
    private double month6ChangePercent;
    @SerializedName("month3ChangePercent")
    @Expose
    private double month3ChangePercent;
    @SerializedName("month1ChangePercent")
    @Expose
    private double month1ChangePercent;
    @SerializedName("day5ChangePercent")
    @Expose
    private double day5ChangePercent;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Stats() {
    }

    /**
     * 
     * @param day50MovingAvg
     * @param grossProfit
     * @param month1ChangePercent
     * @param _float
     * @param week52high
     * @param revenuePerShare
     * @param shortDate
     * @param shortRatio
     * @param peRatioLow
     * @param marketcap
     * @param latestEPSDate
     * @param month6ChangePercent
     * @param symbol
     * @param day200MovingAvg
     * @param institutionPercent
     * @param companyName
     * @param year5ChangePercent
     * @param profitMargin
     * @param returnOnEquity
     * @param priceToSales
     * @param ttmEPS
     * @param cash
     * @param ePSSurprisePercent
     * @param returnOnCapital
     * @param insiderPercent
     * @param year1ChangePercent
     * @param shortInterest
     * @param month3ChangePercent
     * @param latestEPS
     * @param sharesOutstanding
     * @param beta
     * @param exDividendDate
     * @param day5ChangePercent
     * @param dividendYield
     * @param returnOnAssets
     * @param week52low
     * @param revenue
     * @param consensusEPS
     * @param numberOfEstimates
     * @param ytdChangePercent
     * @param debt
     * @param priceToBook
     * @param week52change
     * @param revenuePerEmployee
     * @param year2ChangePercent
     * @param peRatioHigh
     * @param ePSSurpriseDollar
     * @param eBITDA
     * @param dividendRate
     */
    public Stats(String companyName, long marketcap, double beta, double week52high, double week52low, double week52change, long shortInterest, String shortDate, double dividendRate, double dividendYield, String exDividendDate, double latestEPS, String latestEPSDate, long sharesOutstanding, long _float, double returnOnEquity, double consensusEPS, long numberOfEstimates, double ePSSurpriseDollar, double ePSSurprisePercent, String symbol, double eBITDA, double revenue, double grossProfit, double cash, double debt, double ttmEPS, double revenuePerShare, Object revenuePerEmployee, double peRatioHigh, double peRatioLow, double returnOnAssets, double returnOnCapital, double profitMargin, double priceToSales, double priceToBook, double day200MovingAvg, double day50MovingAvg, double institutionPercent, double insiderPercent, double shortRatio, double year5ChangePercent, double year2ChangePercent, double year1ChangePercent, double ytdChangePercent, double month6ChangePercent, double month3ChangePercent, double month1ChangePercent, double day5ChangePercent) {
        super();
        this.companyName = companyName;
        this.marketcap = marketcap;
        this.beta = beta;
        this.week52high = week52high;
        this.week52low = week52low;
        this.week52change = week52change;
        this.shortInterest = shortInterest;
        this.shortDate = shortDate;
        this.dividendRate = dividendRate;
        this.dividendYield = dividendYield;
        this.exDividendDate = exDividendDate;
        this.latestEPS = latestEPS;
        this.latestEPSDate = latestEPSDate;
        this.sharesOutstanding = sharesOutstanding;
        this._float = _float;
        this.returnOnEquity = returnOnEquity;
        this.consensusEPS = consensusEPS;
        this.numberOfEstimates = numberOfEstimates;
        this.ePSSurpriseDollar = ePSSurpriseDollar;
        this.ePSSurprisePercent = ePSSurprisePercent;
        this.symbol = symbol;
        this.eBITDA = eBITDA;
        this.revenue = revenue;
        this.grossProfit = grossProfit;
        this.cash = cash;
        this.debt = debt;
        this.ttmEPS = ttmEPS;
        this.revenuePerShare = revenuePerShare;
        this.revenuePerEmployee = revenuePerEmployee;
        this.peRatioHigh = peRatioHigh;
        this.peRatioLow = peRatioLow;
        this.returnOnAssets = returnOnAssets;
        this.returnOnCapital = returnOnCapital;
        this.profitMargin = profitMargin;
        this.priceToSales = priceToSales;
        this.priceToBook = priceToBook;
        this.day200MovingAvg = day200MovingAvg;
        this.day50MovingAvg = day50MovingAvg;
        this.institutionPercent = institutionPercent;
        this.insiderPercent = insiderPercent;
        this.shortRatio = shortRatio;
        this.year5ChangePercent = year5ChangePercent;
        this.year2ChangePercent = year2ChangePercent;
        this.year1ChangePercent = year1ChangePercent;
        this.ytdChangePercent = ytdChangePercent;
        this.month6ChangePercent = month6ChangePercent;
        this.month3ChangePercent = month3ChangePercent;
        this.month1ChangePercent = month1ChangePercent;
        this.day5ChangePercent = day5ChangePercent;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public long getMarketcap() {
        return marketcap;
    }

    public void setMarketcap(long marketcap) {
        this.marketcap = marketcap;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getWeek52high() {
        return week52high;
    }

    public void setWeek52high(double week52high) {
        this.week52high = week52high;
    }

    public double getWeek52low() {
        return week52low;
    }

    public void setWeek52low(double week52low) {
        this.week52low = week52low;
    }

    public double getWeek52change() {
        return week52change;
    }

    public void setWeek52change(double week52change) {
        this.week52change = week52change;
    }

    public long getShortInterest() {
        return shortInterest;
    }

    public void setShortInterest(long shortInterest) {
        this.shortInterest = shortInterest;
    }

    public String getShortDate() {
        return shortDate;
    }

    public void setShortDate(String shortDate) {
        this.shortDate = shortDate;
    }

    public double getDividendRate() {
        return dividendRate;
    }

    public void setDividendRate(double dividendRate) {
        this.dividendRate = dividendRate;
    }

    public double getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(double dividendYield) {
        this.dividendYield = dividendYield;
    }

    public String getExDividendDate() {
        return exDividendDate;
    }

    public void setExDividendDate(String exDividendDate) {
        this.exDividendDate = exDividendDate;
    }

    public double getLatestEPS() {
        return latestEPS;
    }

    public void setLatestEPS(double latestEPS) {
        this.latestEPS = latestEPS;
    }

    public String getLatestEPSDate() {
        return latestEPSDate;
    }

    public void setLatestEPSDate(String latestEPSDate) {
        this.latestEPSDate = latestEPSDate;
    }

    public long getSharesOutstanding() {
        return sharesOutstanding;
    }

    public void setSharesOutstanding(long sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }

    public long getFloat() {
        return _float;
    }

    public void setFloat(long _float) {
        this._float = _float;
    }

    public double getReturnOnEquity() {
        return returnOnEquity;
    }

    public void setReturnOnEquity(double returnOnEquity) {
        this.returnOnEquity = returnOnEquity;
    }

    public double getConsensusEPS() {
        return consensusEPS;
    }

    public void setConsensusEPS(double consensusEPS) {
        this.consensusEPS = consensusEPS;
    }

    public long getNumberOfEstimates() {
        return numberOfEstimates;
    }

    public void setNumberOfEstimates(long numberOfEstimates) {
        this.numberOfEstimates = numberOfEstimates;
    }

    public double getEPSSurpriseDollar() {
        return ePSSurpriseDollar;
    }

    public void setEPSSurpriseDollar(double ePSSurpriseDollar) {
        this.ePSSurpriseDollar = ePSSurpriseDollar;
    }

    public double getEPSSurprisePercent() {
        return ePSSurprisePercent;
    }

    public void setEPSSurprisePercent(double ePSSurprisePercent) {
        this.ePSSurprisePercent = ePSSurprisePercent;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getEBITDA() {
        return eBITDA;
    }

    public void setEBITDA(double eBITDA) {
        this.eBITDA = eBITDA;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public double getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(double grossProfit) {
        this.grossProfit = grossProfit;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    public double getTtmEPS() {
        return ttmEPS;
    }

    public void setTtmEPS(double ttmEPS) {
        this.ttmEPS = ttmEPS;
    }

    public double getRevenuePerShare() {
        return revenuePerShare;
    }

    public void setRevenuePerShare(double revenuePerShare) {
        this.revenuePerShare = revenuePerShare;
    }

    public Object getRevenuePerEmployee() {
        return revenuePerEmployee;
    }

    public void setRevenuePerEmployee(Object revenuePerEmployee) {
        this.revenuePerEmployee = revenuePerEmployee;
    }

    public double getPeRatioHigh() {
        return peRatioHigh;
    }

    public void setPeRatioHigh(double peRatioHigh) {
        this.peRatioHigh = peRatioHigh;
    }

    public double getPeRatioLow() {
        return peRatioLow;
    }

    public void setPeRatioLow(double peRatioLow) {
        this.peRatioLow = peRatioLow;
    }

    public double getReturnOnAssets() {
        return returnOnAssets;
    }

    public void setReturnOnAssets(double returnOnAssets) {
        this.returnOnAssets = returnOnAssets;
    }

    public double getReturnOnCapital() {
        return returnOnCapital;
    }

    public void setReturnOnCapital(double returnOnCapital) {
        this.returnOnCapital = returnOnCapital;
    }

    public double getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(double profitMargin) {
        this.profitMargin = profitMargin;
    }

    public double getPriceToSales() {
        return priceToSales;
    }

    public void setPriceToSales(double priceToSales) {
        this.priceToSales = priceToSales;
    }

    public double getPriceToBook() {
        return priceToBook;
    }

    public void setPriceToBook(double priceToBook) {
        this.priceToBook = priceToBook;
    }

    public double getDay200MovingAvg() {
        return day200MovingAvg;
    }

    public void setDay200MovingAvg(double day200MovingAvg) {
        this.day200MovingAvg = day200MovingAvg;
    }

    public double getDay50MovingAvg() {
        return day50MovingAvg;
    }

    public void setDay50MovingAvg(double day50MovingAvg) {
        this.day50MovingAvg = day50MovingAvg;
    }

    public double getInstitutionPercent() {
        return institutionPercent;
    }

    public void setInstitutionPercent(double institutionPercent) {
        this.institutionPercent = institutionPercent;
    }

    public double getInsiderPercent() {
        return insiderPercent;
    }

    public void setInsiderPercent(double insiderPercent) {
        this.insiderPercent = insiderPercent;
    }

    public double getShortRatio() {
        return shortRatio;
    }

    public void setShortRatio(double shortRatio) {
        this.shortRatio = shortRatio;
    }

    public double getYear5ChangePercent() {
        return year5ChangePercent;
    }

    public void setYear5ChangePercent(double year5ChangePercent) {
        this.year5ChangePercent = year5ChangePercent;
    }

    public double getYear2ChangePercent() {
        return year2ChangePercent;
    }

    public void setYear2ChangePercent(double year2ChangePercent) {
        this.year2ChangePercent = year2ChangePercent;
    }

    public double getYear1ChangePercent() {
        return year1ChangePercent;
    }

    public void setYear1ChangePercent(double year1ChangePercent) {
        this.year1ChangePercent = year1ChangePercent;
    }

    public double getYtdChangePercent() {
        return ytdChangePercent;
    }

    public void setYtdChangePercent(double ytdChangePercent) {
        this.ytdChangePercent = ytdChangePercent;
    }

    public double getMonth6ChangePercent() {
        return month6ChangePercent;
    }

    public void setMonth6ChangePercent(double month6ChangePercent) {
        this.month6ChangePercent = month6ChangePercent;
    }

    public double getMonth3ChangePercent() {
        return month3ChangePercent;
    }

    public void setMonth3ChangePercent(double month3ChangePercent) {
        this.month3ChangePercent = month3ChangePercent;
    }

    public double getMonth1ChangePercent() {
        return month1ChangePercent;
    }

    public void setMonth1ChangePercent(double month1ChangePercent) {
        this.month1ChangePercent = month1ChangePercent;
    }

    public double getDay5ChangePercent() {
        return day5ChangePercent;
    }

    public void setDay5ChangePercent(double day5ChangePercent) {
        this.day5ChangePercent = day5ChangePercent;
    }

}
