/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.yccheok.jstock.engine;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class KLSEInfoStockHistoryServer implements StockHistoryServer {

    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <NumberFormat> dateFormatThredLocal = new ThreadLocal <NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            // Turns 1 ~ 31 to 01 ~ 31.
            return new DecimalFormat("00");
        }
    };
    
    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <SimpleDateFormat> simpleDateFormatThreadLocal = new ThreadLocal <SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() {
            return new java.text.SimpleDateFormat("yyyy-MM-dd");            
        }
    };
    
    public KLSEInfoStockHistoryServer(Code code) throws StockHistoryNotFoundException
    {
        this(code, DEFAULT_HISTORY_PERIOD);
    }

    public KLSEInfoStockHistoryServer(Code code, Period period) throws StockHistoryNotFoundException {
        this(code, toBestDuration(period));
        long t0 = this.getTimestamp(0);
        long t1 = this.getTimestamp(this.size() - 1);
        long endTimestamp = Math.max(t0, t1);
        long startTimestamp = period.getStartTimestamp(endTimestamp);
        for (int i = 0, size = timestamps.size(); i < size; i++) {
            long timestamp = timestamps.get(i);
            if (startTimestamp > timestamp) {
                historyDatabase.remove(timestamp);
                timestamps.remove(i);
                size--;
                i--;
                continue;
            }
            break;
        }
    }

    private static Duration toBestDuration(Period period) {
        // 7 is for tolerance. Tolerance is in a way such that : Today is N days. However, we only
        // have latest data, which date is (N-tolerance) days.
        return Duration.getTodayDurationByPeriod(period).backStepStartDate(7);
    }

    public KLSEInfoStockHistoryServer(Code code, Duration duration) throws StockHistoryNotFoundException
    {
        if (code == null || duration == null)
        {
            throw new IllegalArgumentException("Code or duration cannot be null");
        }

        // Ensure the code is in non-Yahoo format.
        this.code = code;
        this.duration = duration;
        
        final List<Duration> durations = this.toSmallerDurationPieces();
        
        try {
            boolean flag = false;
            for (Duration d : durations) {
                // As long as one of the durations returns true will be good
                // enough. One or more durations may return false, as a stock
                // may listed in stock market less than 10 years.
                flag = flag | buildHistory(this.code, d);
            }
            if (flag == false) {
                throw new StockHistoryNotFoundException(code.toString());
            }
        } catch (java.lang.OutOfMemoryError exp) {
            // Thrown from method.getResponseBodyAsString
            log.error(null, exp);
            throw new StockHistoryNotFoundException("Out of memory", exp);
        }
    }
    
    @Override
    public Stock getStock(long timestamp) {
        return historyDatabase.get(timestamp);
    }

    @Override
    public long getTimestamp(int index) {
        return timestamps.get(index);
    }

    @Override
    public int size() {
        return timestamps.size();
    }

    @Override
    public long getSharesIssued() {
        return 0;
    }

    @Override
    public long getMarketCapital() {
        return 0;
    }
    
    private List<Duration> toSmallerDurationPieces() {
        List<Duration> durations = new ArrayList<Duration>();
        
        // There are 52 weeks in a year, 5 workdays per week is 260 workdays a 
        // year. KLSEInfo server is limiting 2000 rows per respond. So, we will
        // make it 5 years (260 * 7 = 1820 < 2000) per request.
        SimpleDate endDate = this.duration.getEndDate();
        final SimpleDate startDate = this.duration.getStartDate();
        
        do {
            SimpleDate tmpStartDate = new SimpleDate(endDate.getYear() - 7, endDate.getMonth(), endDate.getDate());
            if (tmpStartDate.compareTo(startDate) >= 0) {
                // Add to the front of the list.
                durations.add(0, new Duration(tmpStartDate, endDate));
            } else {
                if (startDate.compareTo(endDate) <= 0) {
                    // Add to the front of the list.
                    durations.add(0, new Duration(startDate, endDate));
                }
                // Early break!
                break;
            }
            Calendar newEndCalendar = tmpStartDate.getCalendar();
            newEndCalendar.add(Calendar.DAY_OF_YEAR, -1);
            endDate = new SimpleDate(newEndCalendar);
        } while (true);
        return durations;
    }
    
    private boolean buildHistory(Code code, Duration theDuration)
    {
        final StringBuilder stringBuilder = new StringBuilder(KLSE_INFO_BASED_URL);

        final String c;
        try {
            c = java.net.URLEncoder.encode(Utils.toNonYahooFormat(code).toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return false;
        }

        stringBuilder.append(c);

        // Do not use "duration". Use "theDuration".
        // Month is based 1.
        final String endMonth = dateFormatThredLocal.get().format(theDuration.getEndDate().getMonth() + 1);
        final String endDate = dateFormatThredLocal.get().format(theDuration.getEndDate().getDate());
        final int endYear = theDuration.getEndDate().getYear();
        // Month is based 1.
        final String startMonth = dateFormatThredLocal.get().format(theDuration.getStartDate().getMonth() + 1);
        final String startDate = dateFormatThredLocal.get().format(theDuration.getStartDate().getDate());
        final int startYear = theDuration.getStartDate().getYear();

        stringBuilder.append("&start=").append(endYear).append(endMonth).append(endDate).append("&end=").append(startYear).append(startMonth).append(startDate);

        final String location = stringBuilder.toString();

        boolean success = false;

        for (int retry = 0; retry < NUM_OF_RETRY; retry++) {
            final String respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOptionWithAgentInfo(location);

            if (respond == null) {
                continue;
            }

            success = parse(respond);

            // Do not continue even success is false. Just break. As if 
            // networking (getResponseBodyAsStringBasedOnProxyAuthOptionWithAgentInfo) 
            // is OK, parse should be OK too. If not, this is just an outdated stock
            // code. We are just wasting our network resource, by keep trying on
            // an outdated stock code.
            break;
        }

        return success;
    }
    
    private boolean parse(String respond)
    {
        long timestamp = System.currentTimeMillis();
        
        String[] stockDatas = respond.split("\r\n|\r|\n");

        // There must be at least two lines : header information and history information.
        final int length = stockDatas.length;

        if (length <= 1) {
            return false;
        }

        Symbol symbol = Symbol.newInstance(code.toString());
        String name = symbol.toString();
        Board board = Board.Unknown;
        Industry industry = Industry.Unknown;

        try {
            Stock stock = stockServer.getStock(code);
            symbol = stock.symbol;
            name = stock.getName();
            board = stock.getBoard();
            industry = stock.getIndustry();
        }
        catch (StockNotFoundException exp) {
            log.error(null, exp);
        }

        for (int i = length - 1; i > 0; i--)
        {
            // Use > instead of >=, to avoid header information (Date,Open,High,Low,Close,Volume,Adj Close)
            String[] fields = stockDatas[i].split(",");

            // Date,Open,High,Low,Close,Volume,Adj Close
            if (fields.length < 7) {
                continue;
            }

            try {
                timestamp = simpleDateFormatThreadLocal.get().parse(fields[0]).getTime();
            } catch (ParseException ex) {
                log.error(null, ex);
                continue;
            }

            double prevPrice = 0.0;
            double openPrice = 0.0;
            double highPrice = 0.0;
            double lowPrice = 0.0;
            double closePrice = 0.0;
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            long volume = 0;
            //double adjustedClosePrice = 0.0;

            try {
                prevPrice = (previousClosePrice == Double.MAX_VALUE) ? 0 : previousClosePrice;
                openPrice = Double.parseDouble(fields[1]);
                highPrice = Double.parseDouble(fields[2]);
                lowPrice = Double.parseDouble(fields[3]);
                closePrice = Double.parseDouble(fields[4]);
                // TODO: CRITICAL LONG BUG REVISED NEEDED.
                volume = Long.parseLong(fields[5]);
                //adjustedClosePrice = Double.parseDouble(fields[6]);
            } catch (NumberFormatException exp) {
                log.error(null, exp);
            }

            double changePrice = (previousClosePrice == Double.MAX_VALUE) ? 0 : closePrice - previousClosePrice;
            double changePricePercentage = ((previousClosePrice == Double.MAX_VALUE) || (previousClosePrice == 0.0)) ? 0 : changePrice / previousClosePrice * 100.0;

            Stock stock = new Stock(
                    code,
                    symbol,
                    name,
                    null,       // Although it makes sense to hard code the value
                                // to MYR, we will let it be null, as we don't
                                // concern the currency info in stock history.
                    board,
                    industry,
                    prevPrice,
                    openPrice,
                    closePrice, /* Last Price. */
                    highPrice,
                    lowPrice,
                    volume,
                    changePrice,
                    changePricePercentage,
                    0,
                    0.0,
                    0,
                    0.0,
                    0,
                    0.0,
                    0,
                    0.0,
                    0,
                    0.0,
                    0,
                    0.0,
                    0,
                    timestamp
                    );

            historyDatabase.put(timestamp, stock);
            timestamps.add(timestamp);
            previousClosePrice = closePrice;
        }

        return (historyDatabase.size() > 0);
    }
    
    private static final int NUM_OF_RETRY = 2;
    private static final Period DEFAULT_HISTORY_PERIOD =  Period.Years10;
    private static final String KLSE_INFO_BASED_URL = "http://www.klse.info/jstock/historical-prices?s=";
    
    private final java.util.Map<Long, Stock> historyDatabase = new HashMap<Long, Stock>();
    private final java.util.List<Long> timestamps = new ArrayList<Long>();
    
    private final Code code;
    private final Duration duration;

    // Use for internal calculation. Do not expose it.
    private double previousClosePrice = Double.MAX_VALUE;
        
    private final StockServer stockServer = new YahooStockServer();
    private static final Log log = LogFactory.getLog(KLSEInfoStockHistoryServer.class);    
}
