/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.engine;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class KLSEInfoStockHistoryServer implements StockHistoryServer {

    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <NumberFormat> dateFormat = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            // Turns 1 ~ 31 to 01 ~ 31.
            return new DecimalFormat("00");
        }
    };
    
    public KLSEInfoStockHistoryServer(Code code) throws StockHistoryNotFoundException
    {
        this(code, DEFAULT_HISTORY_DURATION);
    }

    public KLSEInfoStockHistoryServer(Code code, Duration duration) throws StockHistoryNotFoundException
    {
        if (code == null || duration == null)
        {
            throw new IllegalArgumentException("Code or duration cannot be null");
        }

        // Ensure the code is in non-Yahoo format.
        this.code = Utils.toNonYahooFormat(code);
        this.duration = duration;
        try {
            buildHistory(this.code);
        }
        catch (java.lang.OutOfMemoryError exp) {
            // Thrown from method.getResponseBodyAsString
            log.error(null, exp);
            throw new StockHistoryNotFoundException("Out of memory", exp);
        }
    }
    
    @Override
    public Stock getStock(Calendar calendar) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Calendar getCalendar(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNumOfCalendar() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getSharesIssued() {
        return 0;
    }

    @Override
    public long getMarketCapital() {
        return 0;
    }
    
    private void buildHistory(Code code) throws StockHistoryNotFoundException
    {
        final StringBuilder stringBuilder = new StringBuilder(KLSE_INFO_BASED_URL);

        final String c;
        try {
            c = java.net.URLEncoder.encode(code.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockHistoryNotFoundException("code.toString()=" + code.toString(), ex);
        }

        stringBuilder.append(c);

        final String endMonth = dateFormat.get().format(duration.getEndDate().getMonth());
        final String endDate = dateFormat.get().format(duration.getEndDate().getDate());
        final int endYear = duration.getEndDate().getYear();
        final String startMonth = dateFormat.get().format(duration.getStartDate().getMonth());
        final String startDate = dateFormat.get().format(duration.getStartDate().getDate());
        final int startYear = duration.getStartDate().getYear();

        stringBuilder.append("&start=").append(endYear).append(endMonth).append(endDate).append("&end=").append(startYear).append(startMonth).append(startDate);

        final String location = stringBuilder.toString();

        boolean success = false;

        for (int retry = 0; retry < NUM_OF_RETRY; retry++) {
            final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);

            if (respond == null) {
                continue;
            }

            success = parse(respond);

            if (success) {
                break;
            }
        }

        if (success == false) {
            throw new StockHistoryNotFoundException(code.toString());
        }
    }
    
    private static final int NUM_OF_RETRY = 2;
    private static final Duration DEFAULT_HISTORY_DURATION =  Duration.getTodayDurationByYears(10);
    private static final String KLSE_INFO_BASED_URL = "http://www.klse.info/jstock/historical-prices?s=";
    
    private final Code code;
    private final Duration duration;

    private static final Log log = LogFactory.getLog(KLSEInfoStockHistoryServer.class);    
}
