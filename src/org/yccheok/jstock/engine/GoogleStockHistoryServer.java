/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng Cheok <yccheok@yahoo.com>
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class GoogleStockHistoryServer implements StockHistoryServer {

    public GoogleStockHistoryServer(Code code) throws StockHistoryNotFoundException {
        this(code, DEFAULT_HISTORY_DURATION);
    }
    
    public GoogleStockHistoryServer(Code code, Duration duration) throws StockHistoryNotFoundException {
        this.code = code;
        this.duration = duration;
        
        String _googleCode = Utils.toCompleteUnitedStatesGoogleFormat(code);
        if (_googleCode == null) {
            _googleCode = Utils.toGoogleFormat(code);
        }
        
        this.googleCode = _googleCode;
        
        final StringBuilder stringBuilder = new StringBuilder("http://www.google.com/finance/getprices?f=d,c,v,o,h,l&i=86400&p=");
                
        // Google Finance doesn't provide a good facility to specific duration.
        // For instance, the below request will only return 50 rows, instead of
        // 3653 rows.        
        // http://www.google.com/finance/getprices?f=d,c,v,o,h,l&i=86400&p=3653d&ts=1383667200000&q=SAN
        //
        // In view with that, we will ignore duration completely.
        //
        stringBuilder.append("10Y&ts=").append(System.currentTimeMillis());
        //long days = duration.getDurationInDays();
        //stringBuilder.append(days).append("d&ts=");
        //stringBuilder.append(duration.getEndDate().getTime().getTime()).append("d");
        
        String googleCodeStr = googleCode;
        // Turn "INDEXDJX:.DJI" into "INDEXDJX" and ".DJI".
        String[] result = googleCodeStr.split(":");
        
        try {
            if (result.length == 2) {
                stringBuilder.append("&q=");
                stringBuilder.append(java.net.URLEncoder.encode(result[1], "UTF-8"));
                stringBuilder.append("&x=");
                stringBuilder.append(java.net.URLEncoder.encode(result[0], "UTF-8"));
            } else {
                stringBuilder.append("&q=");
                stringBuilder.append(java.net.URLEncoder.encode(googleCodeStr, "UTF-8"));
            }
        } catch (UnsupportedEncodingException ex) {
            throw new StockHistoryNotFoundException(null, ex);
        }
        
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
    
    private boolean parse(String respond) {
        historyDatabase.clear();
        timestamps.clear();
        
        final long startTimeInMilli = this.duration.getStartDate().getTime().getTime();
        final long endTimeInMilli = this.duration.getEndDate().getTime().getTime();
        
        String[] stockDatas = respond.split("\r\n|\r|\n");
        
        double previousClosePrice = Double.MAX_VALUE;
        long time = 0;
        
        Symbol symbol = Symbol.newInstance(code.toString());
        String name = symbol.toString();
        Stock.Board board = Stock.Board.Unknown;
        Stock.Industry industry = Stock.Industry.Unknown;
        Calendar calendar = null;
        boolean initialized = false;
        
        long TIMEZONE_OFFSET = 0;

        for (String stockData : stockDatas) {
            // NYSE : TIMEZONE_OFFSET=-300
            
            if (stockData.isEmpty()) {
                continue;
            }
            
            char c = stockData.charAt(0);
            if (c != 'a' && false == Character.isDigit(c)) {
                if (stockData.startsWith("TIMEZONE_OFFSET")) {
                    String[] fields = stockData.split("=");
                    if (fields.length == 2) {
                        try {
                            TIMEZONE_OFFSET = Long.parseLong(fields[1]);
                        } catch (NumberFormatException ex) {
                            log.error(null, ex);
                        }
                    }
                }
                continue;
            }
            
            String[] fields = stockData.split(",");
            
            // DATE,CLOSE,HIGH,LOW,OPEN,VOLUME
            if (fields.length < 6) {
                continue;
            }
            
            long currentTime = 0;
            
            final String fields0 = fields[0];
            if (fields0.charAt(0) == 'a') {
                if (fields0.length() > 1) {
                    String timeStr = fields0.substring(1);
                    try {
                        time = Long.parseLong(timeStr);
                    } catch (NumberFormatException ex) {
                        log.error(null, ex);
                        continue;
                    }
                    
                    currentTime = time;
                }
            } else {
                int index = 0;
                try {
                    index = Integer.parseInt(fields0);
                } catch (NumberFormatException ex) {
                    log.error(null, ex);
                    continue;
                } 
                
                currentTime = time + (index * 60*60*24);
            }
            
            if (initialized == false) {
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
                
                calendar = Calendar.getInstance();
                
                initialized = true;
            }
            
            long currentTimeInMilli = currentTime*1000;
            
            // Convert it to local time respect to stock exchange.
            // TIMEZONE_OFFSET is in minute.
            currentTimeInMilli = currentTimeInMilli + (TIMEZONE_OFFSET * 60 * 1000);
            
            // Remove time information, by resetting it to 00:00
            currentTimeInMilli = currentTimeInMilli / 1000 / 24 / 60 / 60;
            currentTimeInMilli = currentTimeInMilli * 60 * 60 * 24 * 1000;
            
            // Make it as local timestamp.
            //
            // For instance, Greenwich is 1:30pm right now.
            // We want to make Malaysia 1:30pm right now.
            //
            // That's why we are having -ve.
            calendar.setTimeInMillis(currentTimeInMilli);        
            int offset = -(calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET));
            currentTimeInMilli = currentTimeInMilli + offset;
            
            if (currentTimeInMilli < startTimeInMilli) {
                continue;
            }
            if (currentTimeInMilli > endTimeInMilli) {
                break;
            }
            
            double closePrice = 0.0;
            double highPrice = 0.0;
            double lowPrice = 0.0;
            double prevPrice = 0.0;
            double openPrice = 0.0;            
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            long volume = 0;
            //double adjustedClosePrice = 0.0;

            try {
                closePrice = Double.parseDouble(fields[1]);
                highPrice = Double.parseDouble(fields[2]);
                lowPrice = Double.parseDouble(fields[3]);
                openPrice = Double.parseDouble(fields[4]);
                prevPrice = (previousClosePrice == Double.MAX_VALUE) ? 0 : previousClosePrice;
                
                // TODO: CRITICAL LONG BUG REVISED NEEDED.
                volume = Long.parseLong(fields[5]);
                //adjustedClosePrice = Double.parseDouble(fields[6]);
            }
            catch (NumberFormatException exp) {
                log.error(null, exp);
            }
            
            double changePrice = (previousClosePrice == Double.MAX_VALUE) ? 0 : closePrice - previousClosePrice;
            double changePricePercentage = ((previousClosePrice == Double.MAX_VALUE) || (previousClosePrice == 0.0)) ? 0 : changePrice / previousClosePrice * 100.0;
            
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
                    currentTimeInMilli
                    );

            historyDatabase.put(currentTimeInMilli, stock);
            
            // Something we do not understand Google server.
            //if (timestamps.isEmpty()) {
                timestamps.add(currentTimeInMilli);
            //} else {
            //    if (timestamps.get(timestamps.size() - 1) != currentTimeInMilli) {
            //        timestamps.add(currentTimeInMilli);
            //    }
            //}
            previousClosePrice = closePrice;                        
        } 
        
        return (historyDatabase.size() > 0);
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
    
    // I believe Google server is much more reliable than Yahoo! server. 
    private static final int NUM_OF_RETRY = 1;
    private static final Duration DEFAULT_HISTORY_DURATION =  Duration.getTodayDurationByYears(10);
    private final java.util.Map<Long, Stock> historyDatabase = new HashMap<Long, Stock>();
    private final java.util.List<Long> timestamps = new ArrayList<Long>();       
    private final Code code;
    private final String googleCode;
    private final StockServer stockServer = new GoogleStockServer();
    private final Duration duration;
    
    private static final Log log = LogFactory.getLog(GoogleStockHistoryServer.class);
}
