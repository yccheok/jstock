
package org.yccheok.jstock.engine.iex.batch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BatchResponse {

    @SerializedName("quote")
    @Expose
    private Quote quote;
    @SerializedName("stats")
    @Expose
    private Stats stats;

    /**
     * No args constructor for use in serialization
     * 
     */
    public BatchResponse() {
    }

    /**
     * 
     * @param stats
     * @param quote
     */
    public BatchResponse(Quote quote, Stats stats) {
        super();
        this.quote = quote;
        this.stats = stats;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

}
