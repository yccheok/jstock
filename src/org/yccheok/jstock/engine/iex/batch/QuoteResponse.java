package org.yccheok.jstock.engine.iex.batch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yccheok on 5/11/2017.
 */

public class QuoteResponse {

    @SerializedName("quote")
    @Expose
    private Quote quote;

    /**
     * No args constructor for use in serialization
     *
     */
    public QuoteResponse() {
    }

    /**
     *
     * @param quote
     */
    public QuoteResponse(Quote quote) {
        super();
        this.quote = quote;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }
}
