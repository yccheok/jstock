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
        
        final List<Duration> durations = this.toSmallerDurationPieces();
        
        try {
            for (Duration d : durations) {
                buildHistory(this.code, d);
            }
        } catch (java.lang.OutOfMemoryError exp) {
            // Thrown from method.getResponseBodyAsString
            log.error(null, exp);
            throw new StockHistoryNotFoundException("Out of memory", exp);
        }
    }
    
    @Override
    public Stock getStock(Calendar calendar) {
        SimpleDate simpleDate = new SimpleDate(calendar);
        return historyDatabase.get(simpleDate);
    }

    @Override
    public Calendar getCalendar(int index) {
        return simpleDates.get(index).getCalendar();
    }

    @Override
    public int getNumOfCalendar() {
        return simpleDates.size();
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
            newEndCalendar.roll(Calendar.DAY_OF_YEAR, -1);
            endDate = new SimpleDate(newEndCalendar);
        } while (true);
        return durations;
    }
    
    private void buildHistory(Code code, Duration theDuration) throws StockHistoryNotFoundException
    {
        final StringBuilder stringBuilder = new StringBuilder(KLSE_INFO_BASED_URL);

        final String c;
        try {
            c = java.net.URLEncoder.encode(code.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockHistoryNotFoundException("code.toString()=" + code.toString(), ex);
        }

        stringBuilder.append(c);

        // Do not use "duration". Use "theDuration".
        final String endMonth = dateFormatThredLocal.get().format(theDuration.getEndDate().getMonth());
        final String endDate = dateFormatThredLocal.get().format(theDuration.getEndDate().getDate());
        final int endYear = theDuration.getEndDate().getYear();
        final String startMonth = dateFormatThredLocal.get().format(theDuration.getStartDate().getMonth());
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

            if (success) {
                break;
            }
        }

        if (success == false) {
            throw new StockHistoryNotFoundException(code.toString());
        }
    }
    
    private boolean parse(String respond)
    {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        final Calendar calendar = Calendar.getInstance();
        
        String[] stockDatas = respond.split("\r\n|\r|\n");

        // There must be at least two lines : header information and history information.
        final int length = stockDatas.length;

        if (length <= 1) {
            return false;
        }

        Symbol symbol = Symbol.newInstance(code.toString());
        String name = symbol.toString();
        Stock.Board board = Stock.Board.Unknown;
        Stock.Industry industry = Stock.Industry.Unknown;

        try {
            Stock stock = stockServer.getStock(Code.newInstance(code.toString() + ".KL"));
            symbol = stock.getSymbol();
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
                calendar.setTime(dateFormat.parse(fields[0]));
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
            }
            catch(NumberFormatException exp) {
                log.error(null, exp);
            }

            double changePrice = (previousClosePrice == Double.MAX_VALUE) ? 0 : closePrice - previousClosePrice;
            double changePricePercentage = ((previousClosePrice == Double.MAX_VALUE) || (previousClosePrice == 0.0)) ? 0 : changePrice / previousClosePrice * 100.0;

            SimpleDate simpleDate = new SimpleDate(calendar);

            Stock stock = new Stock(
                    code,
                    symbol,
                    name,
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
                    simpleDate.getCalendar()
                    );

            historyDatabase.put(simpleDate, stock);
            simpleDates.add(simpleDate);
            previousClosePrice = closePrice;
        }

        return (historyDatabase.size() > 0);
    }
    
    private static final int NUM_OF_RETRY = 2;
    private static final Duration DEFAULT_HISTORY_DURATION =  Duration.getTodayDurationByYears(10);
    private static final String KLSE_INFO_BASED_URL = "http://www.klse.info/jstock/historical-prices?s=";
    
    private final java.util.Map<SimpleDate, Stock> historyDatabase = new HashMap<SimpleDate, Stock>();
    private final java.util.List<SimpleDate> simpleDates = new ArrayList<SimpleDate>();
    
    private final Code code;
    private final Duration duration;

    // Use for internal calculation. Do not expose it.
    private double previousClosePrice = Double.MAX_VALUE;
        
    private final StockServer stockServer = new SingaporeYahooStockServer(Country.Malaysia);
    private static final Log log = LogFactory.getLog(KLSEInfoStockHistoryServer.class);    
}
