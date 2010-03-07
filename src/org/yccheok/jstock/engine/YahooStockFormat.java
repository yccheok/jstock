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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author yccheok
 */
public class YahooStockFormat implements StockFormat {

    private YahooStockFormat() {}

    // Update on 19 March 2009 : We cannot assume certain parameters will always
    // be float. They may become integer too. For example, in the case of Korea
    // Stock Market, Previous Close is in integer. We shall apply string quote
    // protection method too on them.
    //
    // Here are the index since 19 March 2009 :
    // (0) Symbol
    // (1) Name
    // (2) Stock Exchange
    // (3) Symbol
    // (4) Previous Close
    // (5) Symbol
    // (6) Open
    // (7) Symbol
    // (8) Last Trade
    // (9) Symbol
    // (10) Day's high
    // (11) Symbol
    // (12) Day's low
    // (13) Symbol
    // (14) Volume
    // (15) Symbol
    // (16) Change
    // (17) Symbol
    // (18) Change Percent
    // (19) Symbol
    // (20) Last Trade Size
    // (21) Symbol
    // (22) Bid
    // (23) Symbol
    // (24) Bid Size
    // (25) Symbol
    // (26) Ask
    // (27) Symbol
    // (28) Ask Size
    // (29) Symbol
    // (30) Last Trade Date
    // (31) Last Trade Time.
    //
    // s = Symbol
    // n = Name
    // x = Stock Exchange
    // o = Open             <-- Although we will keep this value in our stock data structure, we will not show
    //                          it to clients. As some stock servers unable to retrieve open price.
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
    @Override
    public List<Stock> parse(String source) {
        List<Stock> stocks = new ArrayList<Stock>();
        
        if (source == null) {
            return stocks;
        }                         
        
        final String[] strings = source.split("\r\n|\r|\n");
        
        for (String string : strings) {
            final String tmp = YahooStockFormat.digitPattern.matcher(string).replaceAll("$1");
            // Some string contain comma, remove them as well. If not, we face problem during csv parsing.
            final String stringDigitWithoutComma = stringCommaPattern.matcher(tmp).replaceAll("$1");

            String[] fields = stringDigitWithoutComma.split(",");
            final int length = fields.length;
            
            Code code = null;
            Symbol symbol = null;
            String name = null;
            Stock.Board board = null;
            Stock.Industry industry = null;
            double prevPrice = 0.0;
            double openPrice = 0.0;
            double lastPrice = 0.0;    
            double highPrice = 0.0;  
            double lowPrice = 0.0;
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            long volume = 0;
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
            
            do {
                if (length < 1) break; code = Code.newInstance(quotePattern.matcher(fields[0]).replaceAll("").trim());
                
                if (length < 2) break; name = quotePattern.matcher(fields[1]).replaceAll("").trim();

                // We use name as symbol, to make it more readable.
                symbol = Symbol.newInstance(name.toString());

                if (length < 3) break;
                
                try {
                    board = Stock.Board.valueOf(quotePattern.matcher(fields[2]).replaceAll("").trim());
                }
                catch (java.lang.IllegalArgumentException exp) {
                    board = Stock.Board.Unknown;
                }
                
                industry = Stock.Industry.Unknown;
                
                if (length < 5) break;
                try { prevPrice = Double.parseDouble(fields[4]); } catch (NumberFormatException exp) {}

                if (length < 7) break;
                try { openPrice = Double.parseDouble(fields[6]); } catch (NumberFormatException exp) {}

                if (length < 9) break;
                try { lastPrice = Double.parseDouble(fields[8]); } catch (NumberFormatException exp) {}
                
                if (length < 11) break;
                try { highPrice = Double.parseDouble(fields[10]); } catch (NumberFormatException exp) {}

                if (length < 13) break;
                try { lowPrice = Double.parseDouble(fields[12]); } catch (NumberFormatException exp) {}

                if (length < 15) break;
                // TODO: CRITICAL LONG BUG REVISED NEEDED.
                try { volume = Long.parseLong(fields[14]); } catch (NumberFormatException exp) {}

                if (length < 17) break;
                try { changePrice = Double.parseDouble(quotePattern.matcher(fields[16]).replaceAll("").trim()); } catch (NumberFormatException exp) {}

                if (length < 19) break;
                String _changePricePercentage = quotePattern.matcher(fields[18]).replaceAll("");
                _changePricePercentage = percentagePattern.matcher(_changePricePercentage).replaceAll("");
                try { changePricePercentage = Double.parseDouble(_changePricePercentage); } catch (NumberFormatException exp) {}

                if (length < 21) break;
                try { lastVolume = Integer.parseInt(fields[20]); } catch (NumberFormatException exp) {}
                
                if (length < 23) break;
                try { buyPrice = Double.parseDouble(fields[22]); } catch (NumberFormatException exp) {}

                if (length < 25) break;
                try { buyQuantity = Integer.parseInt(fields[24]); } catch (NumberFormatException exp) {}
                
                if (length < 27) break;
                try { sellPrice = Double.parseDouble(fields[26]); } catch (NumberFormatException exp) {}

                if (length < 29) break;
                try { sellQuantity = Integer.parseInt(fields[28]); } catch (NumberFormatException exp) {}
                
                if (length < 32) break;
                java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat)java.text.DateFormat.getInstance();
                String data_and_time = quotePattern.matcher(fields[28]).replaceAll("").trim() + " " + quotePattern.matcher(fields[29]).replaceAll("").trim();
                dateFormat.applyPattern("MM/dd/yyyy hh:mmaa");
                java.util.Date serverDate;
                try {
                    serverDate = dateFormat.parse(data_and_time);
                    calendar = Calendar.getInstance();
                    calendar.setTime(serverDate);
                } catch (ParseException exp) {
                    // Most of the time, we just obtain "N/A"
                    // log.error(fields[23] + ", " + fields[24] + ", " + data_and_time, exp);
                }
                
                break;
            } while(true);
            
            if (code == null || symbol == null || name == null || board == null || industry == null) {
                continue;
            }
            
            if (calendar == null) calendar = Calendar.getInstance();
            
            Stock stock = new Stock(
                    code,
                    symbol,
                    name,
                    board,
                    industry,
                    prevPrice,
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
    //
    // digitPattern will change
    // ",100,000,"
    // to
    // ",100000,"
    private static final Pattern digitPattern = Pattern.compile("(\",)|,(?=[\\d,]+,\")");
    // stringCommaPattern will change
    // ","abc,def","
    // to
    // ","abcdef","
    private static final Pattern stringCommaPattern = Pattern.compile("(\",\")|,(?=[^\"[,]]*\",\")");
    private static final Pattern quotePattern = Pattern.compile("\"");
    private static final Pattern percentagePattern = Pattern.compile("%");
}
