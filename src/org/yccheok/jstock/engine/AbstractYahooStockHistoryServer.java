/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng CHEOK <yccheok@yahoo.com>
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public abstract class AbstractYahooStockHistoryServer implements StockHistoryServer {        
    // Use ThreadLocal to ensure thread safety.
    private static final ThreadLocal <SimpleDateFormat> simpleDateFormatThreadLocal = new ThreadLocal <SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() {
            return new java.text.SimpleDateFormat("yyyy-MM-dd");
        }
    };
    
    protected abstract StockServer getStockServer(Country country);
    
    public AbstractYahooStockHistoryServer(Country country, Code code) throws StockHistoryNotFoundException
    {
        this(country, code, DEFAULT_HISTORY_DURATION);
    }

    public AbstractYahooStockHistoryServer(Country country, Code code, Duration duration) throws StockHistoryNotFoundException
    {
        if (code == null || duration == null)
        {
            throw new IllegalArgumentException("Code or duration cannot be null");
        }

        this.country = country;
        this.code = Utils.toYahooFormat(code, country);
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

    private boolean parse(String respond)
    {
        historyDatabase.clear();
        simpleDates.clear();

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
            Stock stock = getStockServer(this.country).getStock(code);
            symbol = stock.getSymbol();
            name = stock.getName();
            board = stock.getBoard();
            industry = stock.getIndustry();
        }
        catch (StockNotFoundException exp) {
            log.error(null, exp);
        }

        double previousClosePrice = Double.MAX_VALUE;

        for (int i = length - 1; i > 0; i--)
        {
            // Use > instead of >=, to avoid header information (Date,Open,High,Low,Close,Volume,Adj Close)
            String[] fields = stockDatas[i].split(",");

            // Date,Open,High,Low,Close,Volume,Adj Close
            if (fields.length < 7) {
                continue;
            }

            try {
                calendar.setTime(simpleDateFormatThreadLocal.get().parse(fields[0]));
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

    private void buildHistory(Code code) throws StockHistoryNotFoundException
    {
        final StringBuilder stringBuilder = new StringBuilder(YAHOO_ICHART_BASED_URL);

        final String symbol;
        try {
            symbol = java.net.URLEncoder.encode(code.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockHistoryNotFoundException("code.toString()=" + code.toString(), ex);
        }

        stringBuilder.append(symbol);

        final int endMonth = duration.getEndDate().getMonth();
        final int endDate = duration.getEndDate().getDate();
        final int endYear = duration.getEndDate().getYear();
        final int startMonth = duration.getStartDate().getMonth();
        final int startDate = duration.getStartDate().getDate();
        final int startYear = duration.getStartDate().getYear();

        final StringBuilder formatBuilder = new StringBuilder("&d=");
        formatBuilder.append(endMonth).append("&e=").append(endDate).append("&f=").append(endYear).append("&g=d&a=").append(startMonth).append("&b=").append(startDate).append("&c=").append(startYear).append("&ignore=.csv");

        final String location = stringBuilder.append(formatBuilder).toString();

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

    public Duration getDuration() {
        return duration;
    }

    // http://ichart.yahoo.com/table.csv?s=JAVA&d=10&e=14&f=2008&g=d&a=2&b=11&c=1987&ignore=.csv
    // d = end month (0-11)
    // e = end date
    // f = end year
    // g = daily?
    // a = start month (0-11)
    // b = start date
    // c = start year
    //
    // Date,Open,High,Low,Close,Volume,Adj Close
    // 2008-11-07,4.32,4.41,4.12,4.20,10882100,4.20
    // 2008-11-06,4.57,4.60,4.25,4.25,10717900,4.25
    // 2008-11-05,4.83,4.90,4.62,4.62,9250800,4.62

    private static final int NUM_OF_RETRY = 2;
    private static final Duration DEFAULT_HISTORY_DURATION =  Duration.getTodayDurationByYears(10);
    private static final String YAHOO_ICHART_BASED_URL = "http://ichart.yahoo.com/table.csv?s=";

    private final java.util.Map<SimpleDate, Stock> historyDatabase = new HashMap<SimpleDate, Stock>();
    private final java.util.List<SimpleDate> simpleDates = new ArrayList<SimpleDate>();

    private final Code code;
    private final Country country;
    private final Duration duration;

    private static final Log log = LogFactory.getLog(AbstractYahooStockHistoryServer.class);
}
