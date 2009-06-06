/*
 * CIMBStockFormat.java
 *
 * Created on April 16, 2007, 12:43 AM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class CIMBStockFormat implements StockFormat {
    
    /** Creates a new instance of CIMBStockFormat */
    private CIMBStockFormat() {
    }
       
    @Override
    public java.util.List<Stock> parse(String source)
    {
        java.util.List<Stock> stocks = new java.util.ArrayList<Stock>();
        
        // We will use current time in client computer, if we are unable to retrieve time
        // information from the server.
        java.util.Calendar calendar = Calendar.getInstance();
                
        String time = Utils.subString(source, "--_BeginFeedTime_", "--_EndFeedTime_").trim();
        
        if(time.length() != 0) {               
            try {
                java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat)java.text.DateFormat.getInstance();
                dateFormat.applyPattern("HH:mm:ss");
                java.util.Date serverDate = dateFormat.parse(time);
                Calendar container = new GregorianCalendar();
                container.setTime(serverDate);
                    
                // After setTime, container only contains correct time, but not date.
                // calendar only contains correct date, but not time. Now, let's make calendar
				// contain both correct date and time,
                calendar.set(Calendar.HOUR_OF_DAY, container.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, container.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, container.get(Calendar.SECOND));
            }
            catch(java.text.ParseException exp) {
                log.error("", exp);
            }
        
        } 
    
        String data = Utils.subString(source, "--_BeginData_", "--_EndData_").trim();
        
        if(data.length() != 0) {
                
            String[] stockDatas = data.split("\\r\\n");
            for(String stockData :stockDatas) {
                    
                final String trimedStockData = stockData.trim();
                    
                if(trimedStockData.length() != 0) { 
                        
                    String[] stockFields = trimedStockData.split("\\|");
                    
                    Code code;
                    Symbol symbol;
                    String name;
                    Stock.Board board;
                    Stock.Industry industry;
                    double openPrice;
                    double lastPrice;    
                    double highPrice;  
                    double lowPrice;
                    int volume;
                    double changePrice;
                    double changePricePercentage;
                    int lastVolume;    
                    double buyPrice;
                    int buyQuantity;    
                    double sellPrice;
                    int sellQuantity; 
                    double secondBuyPrice;
                    int secondBuyQuantity;
                    double secondSellPrice;
                    int secondSellQuantity;
                    double thirdBuyPrice;
                    int thirdBuyQuantity;
                    double thirdSellPrice;
                    int thirdSellQuantity;

                    if(stockFields.length > THIRD_SELL_QUANTITY_TOKEN_INDEX) {
                        try
                        {
                            // 4065  |PPB|PPB GROUP BHD                           |0101|CONSUMER                                                    |A|Y|MYL4065OO008|N|R|1|7.5|7.75|7.75|7.5|Y|3905|0.25|3.33|1|7.7|1|7.75|68|7.6|196|7.8|750|7.55|20|7.85|165|0901|7.5|7.75|0100|1185499882|0|0|0|1324|2581|38|45|2961230.0|1.0
                            
                            final String tmp  = stockFields[3];
                            if(tmp.length() < 4) continue;
                            
                            Stock.Board tmpBoard = stringToBoardMap.get(tmp.substring(0, 2));
                            Stock.Industry tmpIndustry = stringToIndustryMap.get(tmp.substring(2, 4));
                            if(tmpBoard == null) tmpBoard = Stock.Board.Unknown;
                            if(tmpIndustry == null) tmpIndustry = Stock.Industry.Unknown;

                            // Yahoo format is our key reference format. This is to avoid problem occur for different
                            // stock code format found in different stock servers.
                            code = Utils.toYahooFormat(Code.newInstance(stockFields[CODE_TOKEN_INDEX].trim()), Country.Malaysia);
                            symbol = Symbol.newInstance(stockFields[SYMBOL_TOKEN_INDEX].trim());
                            name = stockFields[NAME_TOKEN_INDEX].trim();
                            board = tmpBoard;
                            industry = tmpIndustry;
                            
                            // industry = stockFields[INDUSTRY_TOKEN_INDEX].trim();
                            openPrice = Double.parseDouble(stockFields[OPEN_PRICE_TOKEN_INDEX]);
                            lastPrice = Double.parseDouble(stockFields[LAST_PRICE_TOKEN_INDEX]);
                            highPrice = Double.parseDouble(stockFields[HIGH_PRICE_TOKEN_INDEX]);
                            lowPrice = Double.parseDouble(stockFields[LOW_PRICE_TOKEN_INDEX]);
                            volume = Integer.parseInt(stockFields[VOLUME_TOKEN_INDEX]);
                            changePrice = Double.parseDouble(stockFields[CHANGE_PRICE_TOKEN_INDEX]);
                            changePricePercentage = Double.parseDouble(stockFields[CHANGE_PRICE_PERCENTAGE_TOKEN_INDEX]);
                            lastVolume = Integer.parseInt(stockFields[LAST_VOLUME_TOKEN_INDEX]);
                            buyPrice = Double.parseDouble(stockFields[BUY_PRICE_TOKEN_INDEX]);
                            buyQuantity = Integer.parseInt(stockFields[BUY_QUANTITY_TOKEN_INDEX]);  
                            sellPrice = Double.parseDouble(stockFields[SELL_PRICE_TOKEN_INDEX]);
                            sellQuantity = Integer.parseInt(stockFields[SELL_QUANTITY_TOKEN_INDEX]);
                            secondBuyPrice = Double.parseDouble(stockFields[SECOND_BUY_PRICE_TOKEN_INDEX]);
                            secondBuyQuantity = Integer.parseInt(stockFields[SECOND_BUY_QUANTITY_TOKEN_INDEX]);  
                            secondSellPrice = Double.parseDouble(stockFields[SECOND_SELL_PRICE_TOKEN_INDEX]);
                            secondSellQuantity = Integer.parseInt(stockFields[SECOND_SELL_QUANTITY_TOKEN_INDEX]);
                            thirdBuyPrice = Double.parseDouble(stockFields[THIRD_BUY_PRICE_TOKEN_INDEX]);
                            thirdBuyQuantity = Integer.parseInt(stockFields[THIRD_BUY_QUANTITY_TOKEN_INDEX]);  
                            thirdSellPrice = Double.parseDouble(stockFields[THIRD_SELL_PRICE_TOKEN_INDEX]);
                            thirdSellQuantity = Integer.parseInt(stockFields[THIRD_SELL_QUANTITY_TOKEN_INDEX]);

                            Stock stock = new Stock(
                                    code,
                                    symbol,
                                    name,
                                    board,
                                    industry,
                                    openPrice,
                                    lastPrice,
                                    highPrice,
                                    lowPrice,
                                    volume,
                                    changePrice,
                                    changePricePercentage,
                                    lastVolume,
                                    buyPrice,
                                    buyQuantity,
                                    sellPrice,
                                    sellQuantity,
                                    secondBuyPrice,
                                    secondBuyQuantity,
                                    secondSellPrice,
                                    secondSellQuantity,
                                    thirdBuyPrice,
                                    thirdBuyQuantity,
                                    thirdSellPrice,
                                    thirdSellQuantity,
                                    calendar                                        
                                    );

                            stocks.add(stock);
                        }
                        catch(NumberFormatException exp) {
                            log.error("", exp);
                            continue;
                        }                            
                    }
                    else if(stockFields.length > (THIRD_SELL_QUANTITY_TOKEN_INDEX - ADJUST_OFFSET)) {
                        try
                        {                                                        
                            // 1295|PBBANK|0110|A|9.65|9.8|9.8|9.6| |19824|0.15|1.55|350|9.75|2270|9.8|3723|9.7|2817|9.85|719|9.65|4517|9.9|993|0210|9.65|9.8|0100
                            // For the third token, it carries board and industry information. The high two char is board, and the
                            // low two char is industry :
                            /*
                            01 = Main Board
                            02 = Second Board
                            03 = Call Warrant
                            04 = Mesdaq

                            00 = Unknown
                            01 = Consumer Products
                            02 = Industrial Products
                            03 = Construction
                            04 = Trading / Services
                            05 = Technology
                            07 = Infrastructure
                            10 = Finance
                            15 = Hotels
                            20 = Properties
                            25 = Plantation
                            30 = Mining
                            50 = Trusts
                            55 = Close-End Fund
                            56 = ETF
                            60 = Loans
                            */
                            String tmp  = stockFields[2];
                            if(tmp.length() < 4) continue;
                            
                            Stock.Board tmpBoard = stringToBoardMap.get(tmp.substring(0, 2));
                            Stock.Industry tmpIndustry = stringToIndustryMap.get(tmp.substring(2, 4));
                            if(tmpBoard == null) tmpBoard = Stock.Board.Unknown;
                            if(tmpIndustry == null) tmpIndustry = Stock.Industry.Unknown;

                            // Yahoo format is our key reference format. This is to avoid problem occur for different
                            // stock code format found in different stock servers.
                            code = Utils.toYahooFormat(Code.newInstance(stockFields[CODE_TOKEN_INDEX].trim()), Country.Malaysia);
                            symbol = Symbol.newInstance(stockFields[SYMBOL_TOKEN_INDEX].trim());
                            name = "";
                            board = tmpBoard;
                            industry = tmpIndustry;
                            openPrice = Double.parseDouble(stockFields[OPEN_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            lastPrice = Double.parseDouble(stockFields[LAST_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            highPrice = Double.parseDouble(stockFields[HIGH_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            lowPrice = Double.parseDouble(stockFields[LOW_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            volume = Integer.parseInt(stockFields[VOLUME_TOKEN_INDEX - ADJUST_OFFSET]);
                            changePrice = Double.parseDouble(stockFields[CHANGE_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            changePricePercentage = Double.parseDouble(stockFields[CHANGE_PRICE_PERCENTAGE_TOKEN_INDEX - ADJUST_OFFSET]);
                            lastVolume = Integer.parseInt(stockFields[LAST_VOLUME_TOKEN_INDEX - ADJUST_OFFSET]);
                            buyPrice = Double.parseDouble(stockFields[BUY_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            buyQuantity = Integer.parseInt(stockFields[BUY_QUANTITY_TOKEN_INDEX - ADJUST_OFFSET]);  
                            sellPrice = Double.parseDouble(stockFields[SELL_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            sellQuantity = Integer.parseInt(stockFields[SELL_QUANTITY_TOKEN_INDEX - ADJUST_OFFSET]);
                            secondBuyPrice = Double.parseDouble(stockFields[SECOND_BUY_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            secondBuyQuantity = Integer.parseInt(stockFields[SECOND_BUY_QUANTITY_TOKEN_INDEX - ADJUST_OFFSET]);  
                            secondSellPrice = Double.parseDouble(stockFields[SECOND_SELL_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            secondSellQuantity = Integer.parseInt(stockFields[SECOND_SELL_QUANTITY_TOKEN_INDEX - ADJUST_OFFSET]);
                            thirdBuyPrice = Double.parseDouble(stockFields[THIRD_BUY_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            thirdBuyQuantity = Integer.parseInt(stockFields[THIRD_BUY_QUANTITY_TOKEN_INDEX - ADJUST_OFFSET]);  
                            thirdSellPrice = Double.parseDouble(stockFields[THIRD_SELL_PRICE_TOKEN_INDEX - ADJUST_OFFSET]);
                            thirdSellQuantity = Integer.parseInt(stockFields[THIRD_SELL_QUANTITY_TOKEN_INDEX - ADJUST_OFFSET]);

                            Stock stock = new Stock(
                                    code,
                                    symbol,
                                    name,
                                    board,
                                    industry,
                                    openPrice,
                                    lastPrice,
                                    highPrice,
                                    lowPrice,
                                    volume,
                                    changePrice,
                                    changePricePercentage,
                                    lastVolume,
                                    buyPrice,
                                    buyQuantity,
                                    sellPrice,
                                    sellQuantity,
                                    secondBuyPrice,
                                    secondBuyQuantity,
                                    secondSellPrice,
                                    secondSellQuantity,
                                    thirdBuyPrice,
                                    thirdBuyQuantity,
                                    thirdSellPrice,
                                    thirdSellQuantity,
                                    calendar                                        
                                    );

                            stocks.add(stock);
                        }
                        catch(NumberFormatException exp) {
                            log.error("", exp);
                            continue;
                        }                            
                    }
                    else {
                        continue;
                    }
                    
                }   /* if(trimedStockData.length() != 0) */
            }   /* for(String stockData :stockDatas) */
        }   /* if(data.length() != 0) */
   
        
        return stocks;
    }
    
    public static StockFormat getInstance() {
        return stockFormat;
    }
    
    private static final StockFormat stockFormat = new CIMBStockFormat();
    
    private static final int CODE_TOKEN_INDEX = 0;
    private static final int SYMBOL_TOKEN_INDEX = 1;
    private static final int NAME_TOKEN_INDEX = 2;    
    private static final int ADJUST_OFFSET = 7;    
    private static final int INDUSTRY_TOKEN_INDEX = 4;   
    private static final int OPEN_PRICE_TOKEN_INDEX = 11;
    private static final int LAST_PRICE_TOKEN_INDEX = 12;
    private static final int HIGH_PRICE_TOKEN_INDEX = 13;
    private static final int LOW_PRICE_TOKEN_INDEX = 14;
    private static final int VOLUME_TOKEN_INDEX = 16;    
    private static final int CHANGE_PRICE_TOKEN_INDEX = 17;
    private static final int CHANGE_PRICE_PERCENTAGE_TOKEN_INDEX = 18;
    private static final int LAST_VOLUME_TOKEN_INDEX = 19;
    private static final int BUY_PRICE_TOKEN_INDEX = 20;
    private static final int BUY_QUANTITY_TOKEN_INDEX = 21;    
    private static final int SELL_PRICE_TOKEN_INDEX = 22;
    private static final int SELL_QUANTITY_TOKEN_INDEX = 23;   
    private static final int SECOND_BUY_PRICE_TOKEN_INDEX = 24;
    private static final int SECOND_BUY_QUANTITY_TOKEN_INDEX = 25;    
    private static final int SECOND_SELL_PRICE_TOKEN_INDEX = 26;
    private static final int SECOND_SELL_QUANTITY_TOKEN_INDEX = 27;
    private static final int THIRD_BUY_PRICE_TOKEN_INDEX = 28;
    private static final int THIRD_BUY_QUANTITY_TOKEN_INDEX = 29;    
    private static final int THIRD_SELL_PRICE_TOKEN_INDEX = 30;
    private static final int THIRD_SELL_QUANTITY_TOKEN_INDEX = 31;
        
    private static final java.util.Map<String, Stock.Board> stringToBoardMap = new HashMap<String, Stock.Board>();
    private static final java.util.Map<String, Stock.Industry> stringToIndustryMap = new HashMap<String, Stock.Industry>();
    
    private static final Log log = LogFactory.getLog(CIMBStockFormat.class);
    
    static {
        stringToBoardMap.put("01", Stock.Board.Main);
        stringToBoardMap.put("02", Stock.Board.Second);
        stringToBoardMap.put("03", Stock.Board.CallWarrant);
        stringToBoardMap.put("04", Stock.Board.Mesdaq);
       
        
        stringToIndustryMap.put("00", Stock.Industry.Unknown);
        stringToIndustryMap.put("01", Stock.Industry.ConsumerProducts);
        stringToIndustryMap.put("02", Stock.Industry.IndustrialProducts);
        stringToIndustryMap.put("03", Stock.Industry.Construction);        
        stringToIndustryMap.put("04", Stock.Industry.TradingServices);        
        stringToIndustryMap.put("05", Stock.Industry.Technology);
        stringToIndustryMap.put("07", Stock.Industry.Infrastructure);
        stringToIndustryMap.put("10", Stock.Industry.Finance);
        stringToIndustryMap.put("15", Stock.Industry.Hotels);        
        stringToIndustryMap.put("20", Stock.Industry.Properties);        
        stringToIndustryMap.put("25", Stock.Industry.Plantation);
        stringToIndustryMap.put("30", Stock.Industry.Mining);
        stringToIndustryMap.put("50", Stock.Industry.Trusts);        
        stringToIndustryMap.put("55", Stock.Industry.CloseEndFund);     
        stringToIndustryMap.put("56", Stock.Industry.ETF);        
        stringToIndustryMap.put("60", Stock.Industry.Loans);
    }    
}
