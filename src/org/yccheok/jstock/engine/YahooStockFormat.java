/*
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
 * Copyright (C) 2008 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.engine;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class YahooStockFormat implements StockFormat {

    private YahooStockFormat() {}
    
    // s = Symbol
    // n = Name
    // x = Stock Exchange
    // o = Open     <-- We are no longer using this one. It will not tally with change and change percentage
    // p = Previous Close
    // l1 = Last Trade (Price Only)
    // h = Day's high
    // g = Day's low
    // v = Volume
    // c1 = Change
    // p2 = Change Percent
    // k3 = Last Trade Size
    // b3 = Bid
    // b6 = Bid Size    <-- We are no longer using this one. It sometimes give us two data???
    // a = Ask
    // a5 = Ask Size    <-- We are no longer using this one. It sometimes give us two data???
    // d1 = Last Trade Date
    // t1 = Last Trade Time
    //
    // c6k2c1p2c -> Change (Real-time), Change Percent (Real-time), Change, Change in Percent, Change & Percent Change
    // "+1400.00","N/A - +4.31%",+1400.00,"+4.31%","+1400.00 - +4.31%"
    //
    // "MAERSKB.CO","AP MOELLER-MAERS-","Copenhagen",32500.00,33700.00,34200.00,33400.00,660,"+1200.00","N/A - +3.69%",33,33500.00,54,33700.00,96,"11/10/2008","10:53am"
    public List<Stock> parse(String source) {
        List<Stock> stocks = new ArrayList<Stock>();
        
        if(source == null) {
            return stocks;
        }
        
        final String[] strings = source.split("\\r\\n");
        
        for(String string : strings) {
            String[] fields = string.split(",");
            final int length = fields.length;
            
            Code code = null;
            Symbol symbol = null;
            String name = null;
            Stock.Board board = null;
            Stock.Industry industry = null;
            double openPrice = 0.0;
            double lastPrice = 0.0;    
            double highPrice = 0.0;  
            double lowPrice = 0.0;
            int volume = 0;
            double changePrice = 0.0;
            double changePricePercentage = 0.0;
            int lastVolume = 0;    
            double buyPrice = 0.0;
            int buyQuantity = 0;    
            double sellPrice = 0.0;
            int sellQuantity = 0; 
            double secondBuyPrice = 0.0;
            int secondBuyQuantity = 0;
            double secondSellPrice = 0.0;
            int secondSellQuantity = 0;
            double thirdBuyPrice = 0.0;
            int thirdBuyQuantity = 0;
            double thirdSellPrice = 0.0;
            int thirdSellQuantity = 0;
            java.util.Calendar calendar = null;
            
            for(int i=0; i<fields.length; i++) {
                System.out.println(i+"="+fields[i]);
            }
            
            do {
                if(length < 1) break; code = Code.newInstance(fields[0].replaceAll("\"", "").trim());
                symbol = Symbol.newInstance(code.toString());
                
                if(length < 2) break; name = fields[1].replaceAll("\"", "").trim();
                
                if(length < 3) break;
                
                try {
                    board = Stock.Board.valueOf(fields[2].replaceAll("\"", "").trim());                    
                }
                catch(java.lang.IllegalArgumentException exp) {
                    board = Stock.Board.Unknown;
                }
                
                industry = Stock.Industry.Unknown;
                
                if(length < 4) break;                
                try { openPrice = Double.parseDouble(fields[3]); } catch(NumberFormatException exp) {}
                
                if(length < 5) break;                
                try { lastPrice = Double.parseDouble(fields[4]); } catch(NumberFormatException exp) {}
                
                if(length < 6) break;                
                try { highPrice = Double.parseDouble(fields[5]); } catch(NumberFormatException exp) {}

                if(length < 7) break;                
                try { lowPrice = Double.parseDouble(fields[6]); } catch(NumberFormatException exp) {}

                if(length < 8) break;               
                try { volume = Integer.parseInt(fields[7]); } catch(NumberFormatException exp) {}                

                if(length < 9) break;               
                try { changePrice = Double.parseDouble(fields[8].replaceAll("\"", "").trim()); } catch(NumberFormatException exp) {}                

                if(length < 10) break;
                String _changePricePercentage = fields[9].replaceAll("\"", "").trim();
                final String[] tmp = _changePricePercentage.split("\\s+");
                _changePricePercentage = tmp[tmp.length - 1];                
                _changePricePercentage = _changePricePercentage.replaceAll("%", "");
                try { changePricePercentage = Integer.parseInt(_changePricePercentage); } catch(NumberFormatException exp) {}                

                if(length < 11) break;               
                try { lastVolume = Integer.parseInt(fields[10]); } catch(NumberFormatException exp) {}
                
                if(length < 12) break;
                try { buyPrice = Double.parseDouble(fields[11]); } catch(NumberFormatException exp) {}

                //if(length < 13) break;
                //try { buyQuantity = Integer.parseInt(fields[12]); } catch(NumberFormatException exp) {}
                
                if(length < 13) break;
                try { sellPrice = Double.parseDouble(fields[12]); } catch(NumberFormatException exp) {}

                //if(length < 15) break;
                //try { sellQuantity = Integer.parseInt(fields[14]); } catch(NumberFormatException exp) {}
                
                //if(length < 14)
                if(length < 15) break;
                java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat)java.text.DateFormat.getInstance();
                String data_and_time = fields[13].replaceAll("\"", "").trim() + " " + fields[14].replaceAll("\"", "").trim();
                dateFormat.applyPattern("MM/dd/yyyy hh:mmaa");
                java.util.Date serverDate;
                try {
                    serverDate = dateFormat.parse(data_and_time);
                    calendar = Calendar.getInstance();
                    calendar.setTime(serverDate);
                } catch (ParseException exp) {
                    log.error(fields[13] + ", " + fields[14] + ", " + data_and_time, exp);
                }
                
                break;
            }while(true);
            
            if(code == null || symbol == null || name == null || board == null || industry == null)
                continue;
            
            if(calendar == null) calendar = Calendar.getInstance();
            
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
        
        return stocks;
    }

    public static StockFormat getInstance() {
        return stockFormat;
    }
    
    private static final StockFormat stockFormat = new YahooStockFormat();
    
    private static final Log log = LogFactory.getLog(YahooStockFormat.class);
}
