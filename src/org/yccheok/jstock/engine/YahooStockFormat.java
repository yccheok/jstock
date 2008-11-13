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
import java.util.regex.Pattern;
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
    // v = Volume           <-- We need to take special care on this, it may give us 1,234. This will
    //                          make us difficult to parse csv file. The only workaround is to make integer
    //                          in between two string literal (which will always contains "). By using regular
    //                          expression, we will manually remove the comma.
    // c1 = Change
    // p2 = Change Percent
    // k3 = Last Trade Size <-- We need to take special care on this, it may give us 1,234...
    // b = Bid
    // b6 = Bid Size        <-- We need to take special care on this, it may give us 1,234...
    // a = Ask
    // a5 = Ask Size        <-- We need to take special care on this, it may give us 1,234...
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
            final String tmp = YahooStockFormat.digitPattern.matcher(string).replaceAll("$1");
            // Some string contain comma, remove them as well. If not, we face problem during csv parsing.
            final String stringDigitWithoutComma = stringCommaPattern.matcher(tmp).replaceAll("$1");
            System.out.println("---> " + stringDigitWithoutComma);
            
            String[] fields = stringDigitWithoutComma.split(",");
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
                if(length < 1) break; code = Code.newInstance(quotePattern.matcher(fields[0]).replaceAll("").trim());
                symbol = Symbol.newInstance(code.toString());
                
                if(length < 2) break; name = quotePattern.matcher(fields[1]).replaceAll("").trim();
                
                if(length < 3) break;
                
                try {
                    board = Stock.Board.valueOf(quotePattern.matcher(fields[2]).replaceAll("").trim());                    
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

                if(length < 9) break;               
                try { volume = Integer.parseInt(fields[8]); } catch(NumberFormatException exp) {}                

                if(length < 11) break;               
                try { changePrice = Double.parseDouble(quotePattern.matcher(fields[10]).replaceAll("").trim()); } catch(NumberFormatException exp) {}                

                if(length < 12) break;
                String _changePricePercentage = quotePattern.matcher(fields[11]).replaceAll("");
                _changePricePercentage = percentagePattern.matcher(_changePricePercentage).replaceAll("");
                try { changePricePercentage = Double.parseDouble(_changePricePercentage); } catch(NumberFormatException exp) {}                

                if(length < 14) break;               
                try { lastVolume = Integer.parseInt(fields[13]); } catch(NumberFormatException exp) {}
                
                if(length < 16) break;
                try { buyPrice = Double.parseDouble(fields[15]); } catch(NumberFormatException exp) {}

                if(length < 18) break;
                try { buyQuantity = Integer.parseInt(fields[17]); } catch(NumberFormatException exp) {}
                
                if(length < 20) break;
                try { sellPrice = Double.parseDouble(fields[19]); } catch(NumberFormatException exp) {}

                if(length < 22) break;
                try { sellQuantity = Integer.parseInt(fields[21]); } catch(NumberFormatException exp) {}
                
                if(length < 25) break;
                java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat)java.text.DateFormat.getInstance();
                String data_and_time = quotePattern.matcher(fields[23]).replaceAll("").trim() + " " + quotePattern.matcher(fields[24]).replaceAll("").trim();
                dateFormat.applyPattern("MM/dd/yyyy hh:mmaa");
                java.util.Date serverDate;
                try {
                    serverDate = dateFormat.parse(data_and_time);
                    calendar = Calendar.getInstance();
                    calendar.setTime(serverDate);
                } catch (ParseException exp) {
                    log.error(fields[23] + ", " + fields[24] + ", " + data_and_time, exp);
                }
                
                break;
            }while(true);
            
            if(code == null || symbol == null || name == null || board == null || industry == null) {
                System.out.println("SHIT SHIT SHIT ---> " + stringDigitWithoutComma);
                continue;
            }
            
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
    // Used to remove the comma within an integer digit. The digit must be located
    // in between two string. Replaced with $1.
    private static final Pattern digitPattern = Pattern.compile("(\",)|,(?=[\\d,]+,\")");
    private static final Pattern stringCommaPattern = Pattern.compile("(\"[^\",]+)(,)(?=[^\",]+\")");
    private static final Pattern quotePattern = Pattern.compile("\"");
    private static final Pattern percentagePattern = Pattern.compile("%");
    
    private static final Log log = LogFactory.getLog(YahooStockFormat.class);
}
