/*
 * Stock.java
 *
 * Created on April 16, 2007, 12:17 AM
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

/**
 *
 * @author yccheok
 */
public class Stock {
    
    /** Creates a new instance of Stock */
    public Stock(
        String code,
        String symbol,
        String name,
        Board board,
        Industry industry,
        double openPrice,
        double lastPrice,
        double highPrice,
        double lowPrice,
        int volume,
        double changePrice,
        double changePricePercentage,
        int lastVolume,
        double buyPrice,
        int buyQuantity,
        double sellPrice,
        int sellQuantity,
        double secondBuyPrice,
        int secondBuyQuantity,
        double secondSellPrice,
        int secondSellQuantity,
        double thirdBuyPrice,
        int thirdBuyQuantity,
        double thirdSellPrice,
        int thirdSellQuantity,
        java.util.Calendar calendar
                ) 
    {
        this.code = code;
        this.symbol = symbol;
        this.name = name;
        this.board = board;
        this.industry = industry;
        this.openPrice = openPrice;
        this.lastPrice = lastPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
        this.changePrice = changePrice;
        this.changePricePercentage = changePricePercentage;
        this.lastVolume = lastVolume;
        this.buyPrice = buyPrice;
        this.buyQuantity = buyQuantity;
        this.sellPrice = sellPrice;
        this.sellQuantity = sellQuantity;
        this.secondBuyPrice = secondBuyPrice;
        this.secondBuyQuantity = secondBuyQuantity;
        this.secondSellPrice = secondSellPrice;
        this.secondSellQuantity = secondSellQuantity;
        this.thirdBuyPrice = thirdBuyPrice;
        this.thirdBuyQuantity = thirdBuyQuantity;
        this.thirdSellPrice = thirdSellPrice;
        this.thirdSellQuantity = thirdSellQuantity;
        this.calendar = calendar;
    }
    
    public Stock(Stock stock) {
        this.code = stock.code;
        this.symbol = stock.symbol;
        this.name = stock.name;
        this.board = stock.board;
        this.industry = stock.industry;
        this.openPrice = stock.openPrice;
        this.lastPrice = stock.lastPrice;
        this.highPrice = stock.highPrice;
        this.lowPrice = stock.lowPrice;
        this.volume = stock.volume;
        this.changePrice = stock.changePrice;
        this.changePricePercentage = stock.changePricePercentage;
        this.lastVolume = stock.lastVolume;
        this.buyPrice = stock.buyPrice;
        this.buyQuantity = stock.buyQuantity;
        this.sellPrice = stock.sellPrice;
        this.sellQuantity = stock.sellQuantity;
        this.secondBuyPrice = stock.secondBuyPrice;
        this.secondBuyQuantity = stock.secondBuyQuantity;
        this.secondSellPrice = stock.secondSellPrice;
        this.secondSellQuantity = stock.secondSellQuantity;
        this.thirdBuyPrice = stock.thirdBuyPrice;
        this.thirdBuyQuantity = stock.thirdBuyQuantity;
        this.thirdSellPrice = stock.thirdSellPrice;
        this.thirdSellQuantity = stock.thirdSellQuantity;
        this.calendar = stock.calendar;
    }
    
    public String getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public Board getBoard() {
        return board;
    }
    
    public Industry getIndustry() {
        return industry;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public int getVolume() {
        return volume;
    }

    public double getChangePrice() {
        return changePrice;
    }

    public double getChangePricePercentage() {
        return changePricePercentage;
    }

    public int getLastVolume() {
        return lastVolume;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public int getSellQuantity() {
        return sellQuantity;
    }
    
    public double getSecondBuyPrice() {
        return secondBuyPrice;
    }
    
    public int getSecondBuyQuantity() {
        return secondBuyQuantity;
    }
    
    public double getSecondSellPrice() {
        return secondSellPrice;
    }
    
    public int getSecondSellQuantity() {
        return secondSellQuantity;
    }
    
    public double getThirdBuyPrice() {
        return thirdBuyPrice;
    }
    
    public int getThirdBuyQuantity() {
        return thirdBuyQuantity;
    }
    
    public double getThirdSellPrice() {
        return thirdSellPrice;
    }
    
    public int getThirdSellQuantity() {
        return thirdSellQuantity;
    }
    
    public java.util.Calendar getCalendar() {
        return (java.util.Calendar)calendar.clone();        
    }
    
    public String toString() {
        return Stock.class.getName() + "[code=" + code + ",symbol=" + symbol + ",name=" + name + ",board=" + board + ",industry=" + industry +
                ",openPrice=" + openPrice + ",lastPrice=" + lastPrice + ",highPrice=" + highPrice + ",lowPrice=" + lowPrice +
                ",volume=" + volume + ",changePrice=" + changePrice + ",changePricePercentage=" + changePricePercentage + ",lastVolume=" + lastVolume +
                ",buyPrice=" + buyPrice + ",buyQuantity=" + buyQuantity + ",sellPrice=" + sellPrice + ",sellQuantity" + sellQuantity +
                ",secondBuyPrice=" + secondBuyPrice + ",secondBuyQuantity=" + secondBuyQuantity + ",secondSellPrice=" + secondSellPrice + ",secondSellQuantity" + secondSellQuantity +
                ",thirdBuyPrice=" + thirdBuyPrice + ",thirdBuyQuantity=" + thirdBuyQuantity + ",thirdSellPrice=" + thirdSellPrice + ",thirdSellQuantity" + thirdSellQuantity +
                ",calendar=" + calendar + "]"
                ;
    }
    
    public enum Board {
                                                // The following are naming conventions from CIMB :
        Main("Main Board"),                     // Main
        Second("Second Board"),                 // 2nd
        CallWarrant("Call Warrant"),            // ??
        Mesdaq("Mesdaq"),                       // MESDAQ
        Unknown("Unknown"),
        
        DJI("Dow"),                             // Dow Jones Industrial Average (^DHI)
        Nasdaq("Nasdaq");                       // NASDAQ COMPOSITE (^IXIC)
        
        private final String name;

        Board(String name) {
            this.name = name;
        }
        
        public String toString() {
            return name;
        }
    }
        
    public enum Industry {
                                                    // The following are naming conventions from CIMB :
        ConsumerProducts("Consumer Products"),      // CONSUMER
        IndustrialProducts("Industrial Products"),  // IND-PROD
        Construction("Construction"),               // CONSTRUCTN
        TradingServices("Trading / Services"),      // TRAD/SERV
        Technology("Technology"),                   // TECHNOLOGY
        Infrastructure("Infrastructure"),           // IPC
        Finance("Finance"),                         // FINANCE
        Hotels("Hotels"),                           // HOTELS
        Properties("Properties"),                   // PROPERTIES 
        Plantation("Plantation"),                   // PLANTATION
        Mining("Mining"),                           // MINING
        Trusts("Trusts"),                           // REITS
        CloseEndFund("Close-End Fund"),             // CLOSED/FUND 
        ETF("ETF"),                                 // ETF
        Loans("Loans"),                             // LOANS
        CallWarrant("Call Warrant"),                // CALL-WARR
        Unknown("Unknown");
        
        private final String name;

        Industry(String name) {
            this.name = name;
        }
        
        public String toString() {
            return name;
        }
    }
            
    private final String code;
    private final String symbol;
    private final String name;
    private final Board board;
    private final Industry industry;
    private final double openPrice;
    private final double lastPrice;    
    private final double highPrice;  
    private final double lowPrice;
    private final int volume;
    private final double changePrice;
    private final double changePricePercentage;
    private final int lastVolume;    
    private final double buyPrice;
    private final int buyQuantity;    
    private final double sellPrice;
    private final int sellQuantity;
    private final double secondBuyPrice;
    private final int secondBuyQuantity;
    private final double secondSellPrice;
    private final int secondSellQuantity;
    private final double thirdBuyPrice;
    private final int thirdBuyQuantity;
    private final double thirdSellPrice;
    private final int thirdSellQuantity;
    private final java.util.Calendar calendar;
}
