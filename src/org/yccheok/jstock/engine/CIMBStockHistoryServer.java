/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

import java.io.*;
import java.util.*;
import java.math.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class CIMBStockHistoryServer implements StockHistoryServer {
    
    /** Creates a new instance of CIMBStockHistoryServer */
    public CIMBStockHistoryServer(Code code) throws StockHistoryNotFoundException {
        this("", "", code);
    }
    
    public CIMBStockHistoryServer(String username, String password, Code code) throws StockHistoryNotFoundException {
        this.username = username;
        this.password = password;

        initServers();
        // this.servers = Utils.getCIMBHistoryServers();

        this.code = Utils.toCIMBFormat(code, Country.Malaysia);
        // Later, use this.code instead of code in the rest of the function code.

        this.historyDatabase = new HashMap<SimpleDate, Stock>();
        
        byte data[] = new byte[1024];
        
        Thread currentThread = Thread.currentThread();

        String encodedCode;
        try {
            encodedCode = java.net.URLEncoder.encode(this.code.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new StockHistoryNotFoundException(this.code.toString(), ex);
        }

        int index = -1;

        for (String server : servers) {
            index++;
            
            if (currentThread.isInterrupted()) {
                throw new StockHistoryNotFoundException("Thread has been interrupted");
            }
            
            final String request = server + "java/jar/data/" + encodedCode + ".dat.gz";
            final StringBuilder s = new StringBuilder(data.length);

            final org.yccheok.jstock.gui.Utils.InputStreamAndMethod inputStreamAndMethod = org.yccheok.jstock.gui.Utils.getResponseBodyAsStreamBasedOnProxyAuthOption(request);
            if (inputStreamAndMethod.inputStream == null) {
                inputStreamAndMethod.method.releaseConnection();
                continue;
            }
            java.util.zip.GZIPInputStream gZipInputStream = null;            
            try {
                gZipInputStream = new java.util.zip.GZIPInputStream(inputStreamAndMethod.inputStream);
				int n = 0;
                while((n = gZipInputStream.read(data, 0, data.length)) != -1) {
                    s.append(new String(data, 0, n));
                }
            }
            catch (java.io.IOException exp) {
                log.error(null, exp);
                continue;
            }
            finally {                
                // Do not do anything if fail to clean up. Just ignore the
                // exception.
                if (gZipInputStream != null) {
                    try {
                        gZipInputStream.close();
                    } catch (IOException exp) {
                        log.error(null, exp);
                    }
                }

                if (inputStreamAndMethod.inputStream != null) {
                    try {
                        inputStreamAndMethod.inputStream.close();
                    } catch (IOException exp) {
                        log.error(null, exp);
                    }
                }
                
                inputStreamAndMethod.method.releaseConnection();
            }

            HistoryDatabaseResult historyDatebaseResult = this.getHistoryDatabase(s.toString());

            if (historyDatebaseResult != null) {
                historyDatabase.putAll(historyDatebaseResult.database);
                SimpleDate latestDate = historyDatebaseResult.lastestDate;

                // Should we stop here?
                long days = Utils.getDifferenceInDays(latestDate.getCalendar(), Calendar.getInstance());

                // As long as the history is less than or equal to MAX_DAY_DIFFERENCE_AMONG_TODAY_AND_LATEST_HISTORY,
                // we will not continue to search for the history.
                if(days <= MAX_DAY_DIFFERENCE_AMONG_TODAY_AND_LATEST_HISTORY) {
                    // Sort the best server.
                    if (bestServerAlreadySorted == false) {
                        synchronized(servers_lock) {
                            if (bestServerAlreadySorted == false) {
                                bestServerAlreadySorted = true;
                                String tmp = servers.get(0);
                                servers.set(0, servers.get(index));
                                servers.set(index, tmp);
                            }
                        }
                    }

                    break;
                }
            }   /* if (historyDatebaseResult != null) */
        }   /*  for (String server : servers) */
        
        if(historyDatabase.size() == 0) {
            throw new StockHistoryNotFoundException("code = " + this.code);
        }
        else {
            simpleDates = new ArrayList<SimpleDate>(this.historyDatabase.keySet());
            Collections.sort(simpleDates);
        }
    }
    
    // Helper class.
    private static class HistoryDatabaseResult {
        public HistoryDatabaseResult(java.util.Map<SimpleDate, Stock> database, SimpleDate latestDate) {
            this.database = database;
            this.lastestDate = latestDate;
        }
        
        public java.util.Map<SimpleDate, Stock> database;
        public SimpleDate lastestDate;
    }
    
    // Return null if not success.
    private HistoryDatabaseResult getHistoryDatabase(String source) {
        String[] stockDatas = source.split("\\r\\n");
        
        // There must be at least two lines : header information and history information.
        if(stockDatas.length <= 1) return null;
        
        String[] fields = stockDatas[0].split("\\|");

        // Header information as below :
        // 1295|PBBANK|PUBLIC BANK BHD|Active|KLCI,Emas|Main|FINANCE|0100|3492545323|33179180569
        
        if(fields.length < 10) return null;
        
        final String _code = fields[0];
        final String symbol = fields[1];
        final String name = fields[2];
        final Stock.Board board = stringToBoardMap.get(fields[5]) != null ? stringToBoardMap.get(fields[5]) : Stock.Board.Unknown;
        final Stock.Industry industry = stringToIndustryMap.get(fields[6]) != null ? stringToIndustryMap.get(fields[6]) : Stock.Industry.Unknown;
        long _sharesIssued = 0;
        long _marketCapital = 0;
        
        try {
            _sharesIssued = Long.parseLong(fields[8]);
            _marketCapital = Long.parseLong(fields[9]);
        }
        catch(java.lang.NumberFormatException exp) {
            log.debug(exp);
            return null;
        }
          
        sharesIssued = _sharesIssued;
        marketCapital = _marketCapital;
        
        final java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat)java.text.DateFormat.getInstance();
        dateFormat.applyPattern("yyMMdd");       
        
        java.util.Map<SimpleDate, Stock> map = new java.util.HashMap<SimpleDate, Stock>();
        SimpleDate latestDate = null;
        
        for(int i = 1; i < stockDatas.length; i++) {
            final String stockData = stockDatas[i];
            String[] stockFields = stockData.split("\\|");
            
            java.util.Date date = null;
            
            try {
                date = dateFormat.parse(stockFields[0]);
            }
            catch(java.text.ParseException exp) {
                log.error(stockFields[0], exp);
                return null;
            }
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            
            try {
                final SimpleDate simpleDate = new SimpleDate(calendar);
                /* Now, I really have no idea this is openPrice or prevPrice.
                 * I will make openPrice same as prevPrice.
                 * Is OK. We are no longer using CIMB to retrieve history.
                 */
                final double openPrice = Double.parseDouble(stockFields[1]);
                final double highPrice = Double.parseDouble(stockFields[2]);
                final double lowPrice = Double.parseDouble(stockFields[3]);
                final double lastPrice = Double.parseDouble(stockFields[4]);
                // TODO: CRITICAL LONG BUG REVISED NEEDED.
                /* Our system will use 100 units as 1 lot. Our system are in lot unit. */
                final long volume = Long.parseLong(stockFields[5]) / 100;
                java.math.BigDecimal _openPrice = new java.math.BigDecimal("" + openPrice);
                java.math.BigDecimal _lastPrice = new java.math.BigDecimal("" + lastPrice);
                java.math.BigDecimal _changePrice = _lastPrice.subtract(_openPrice);
                final double changePrice = _changePrice.round(new MathContext(2)).doubleValue();
                
                BigDecimal _changePricePercentage = (_openPrice.compareTo(BigDecimal.ZERO) == 1) ? (_changePrice.multiply(new BigDecimal(100.0)).divide(_openPrice, BigDecimal.ROUND_HALF_UP)) : BigDecimal.ZERO;
                final double changePricePercentage = _changePricePercentage.round(new MathContext(2)).doubleValue();

                Stock stock = new Stock(
                        Code.newInstance(_code),
                        Symbol.newInstance(symbol),
                        name,
                        board,
                        industry,
                        openPrice,  // ?? Which is prevPrice? Which is openPrice?
                        openPrice,  // ?? Which is prevPrice? Which is openPrice?
                        lastPrice,
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
                
                map.put(simpleDate, stock);
                
                if(latestDate == null) {
                    latestDate = simpleDate;
                }
                else {
                    if(simpleDate.getCalendar().after(latestDate.getCalendar())) {
                        latestDate = simpleDate;
                    }
                }
                        
            }
            catch(NumberFormatException exp) {
                log.debug(exp);
                return null;
            }
        }        
                
        return new HistoryDatabaseResult(map, latestDate);
    }
    
    @Override
    public Stock getStock(java.util.Calendar calendar)
    {
        SimpleDate simpleDate = new SimpleDate(calendar);
        return historyDatabase.get(simpleDate);        
    }
    
    @Override
    public java.util.Calendar getCalendar(int index)
    {
        SimpleDate simpleDate = simpleDates.get(index);
        return simpleDate.getCalendar();
    }
    
    @Override
    public int getNumOfCalendar()
    {
        return simpleDates.size();
    }

    private static void initServers() {
        // Already initialized. Return early.
        if (CIMBStockHistoryServer.servers != null) {
            return;
        }

        synchronized(servers_lock) {
            // Already initialized. Return early.
            if (CIMBStockHistoryServer.servers != null) {
                return;
            }

            CIMBStockHistoryServer.servers = Utils.getCIMBHistoryServers();
        }
    }

    private final String username;
    private final String password;
    private final Code code;
    private final java.util.Map<SimpleDate, Stock> historyDatabase;
    private final java.util.List<SimpleDate> simpleDates;
    
    private static final int MAX_DAY_DIFFERENCE_AMONG_TODAY_AND_LATEST_HISTORY = 30;

    // Make it as static. Unlike CIMBStockServer and CIMBMarketServer,
    // a new instance will be created for every stock. Hence, it is more
    // efficient if servers is persistance across class. To avoid from lossing
    // previous sorted information.
    private volatile static List<String> servers;
    private static final Object servers_lock = new Object();

    // We had already discover the best server. Please take note that,
    // synchronized is required during best server sorting. Hence, we will
    // use this flag to help us only perform sorting once.
    private volatile boolean bestServerAlreadySorted = false;
    
    @Override
    public long getSharesIssued()
    {
        return sharesIssued;
    }
    
    @Override
    public long getMarketCapital()
    {
        return marketCapital;
    }

    private static final java.util.Map<String, Stock.Board> stringToBoardMap = new HashMap<String, Stock.Board>();
    private static final java.util.Map<String, Stock.Industry> stringToIndustryMap = new HashMap<String, Stock.Industry>();
    private long sharesIssued = 0;
    private long marketCapital = 0;
    
    static {
        stringToBoardMap.put("Main", Stock.Board.Main);
        stringToBoardMap.put("2nd", Stock.Board.Second);
        // stringToBoardMap.put(??, Stock.Board.CallWarrant);
        stringToBoardMap.put("MESDAQ", Stock.Board.Mesdaq);
                       
        stringToIndustryMap.put("00", Stock.Industry.Unknown);
        stringToIndustryMap.put("CONSUMER", Stock.Industry.ConsumerProducts);
        stringToIndustryMap.put("IND-PROD", Stock.Industry.IndustrialProducts);
        stringToIndustryMap.put("CONSTRUCTN", Stock.Industry.Construction);        
        stringToIndustryMap.put("TRAD/SERV", Stock.Industry.TradingServices);        
        stringToIndustryMap.put("TECHNOLOGY", Stock.Industry.Technology);
        stringToIndustryMap.put("IPC", Stock.Industry.Infrastructure);
        stringToIndustryMap.put("FINANCE", Stock.Industry.Finance);
        stringToIndustryMap.put("HOTELS", Stock.Industry.Hotels);        
        stringToIndustryMap.put("PROPERTIES", Stock.Industry.Properties);        
        stringToIndustryMap.put("PLANTATION", Stock.Industry.Plantation);
        stringToIndustryMap.put("MINING", Stock.Industry.Mining);
        stringToIndustryMap.put("REITS", Stock.Industry.Trusts);        
        stringToIndustryMap.put("CLOSED/FUND", Stock.Industry.CloseEndFund);     
        stringToIndustryMap.put("ETF", Stock.Industry.ETF);        
        stringToIndustryMap.put("LOANS", Stock.Industry.Loans);
        stringToIndustryMap.put("CALL-WARR", Stock.Industry.CallWarrant);
    }

    private static final Log log = LogFactory.getLog(CIMBStockServer.class);
}
