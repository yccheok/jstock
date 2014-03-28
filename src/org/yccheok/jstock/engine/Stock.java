/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

/**
 *
 * @author yccheok
 */
public class Stock {
    
    // Builder pattern.
    // We are using a builder pattern when face with many constructor 
    // parameters.
    public static class Builder {
        // Required parameters.
        private final Code code;
        private final Symbol symbol;

        // Optional parameters - initialized to default values.
        private String name = "";
        private Board board = Board.Unknown;
        private Industry industry = Industry.Unknown;
        private double prevPrice = 0.0;
        private double openPrice = 0.0;
        private double lastPrice = 0.0;
        private double highPrice = 0.0;
        private double lowPrice = 0.0;
        private long volume = 0;
        private double changePrice = 0.0;
        private double changePricePercentage = 0.0;
        private int lastVolume = 0;
        private double buyPrice = 0.0;
        private int buyQuantity = 0;
        private double sellPrice = 0.0;
        private int sellQuantity = 0;
        private double secondBuyPrice = 0.0;
        private int secondBuyQuantity = 0;
        private double secondSellPrice = 0.0;
        private int secondSellQuantity = 0;
        private double thirdBuyPrice = 0.0;
        private int thirdBuyQuantity = 0;
        private double thirdSellPrice = 0.0;
        private int thirdSellQuantity = 0;
        // We suppose to provide a default value for calendar. However, it may
        // seem expensive. We will do it later during build.
        private long timestamp = 0;
        private volatile boolean hasTimestampInitialized = false;

        public Builder(Code code, Symbol symbol) {
            this.code = code;
            this.symbol = symbol;
        }

        /**
         * @param name the name to set
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * @param board the board to set
         */
        public Builder board(Board board) {
            this.board = board;
            return this;
        }

        /**
         * @param industry the industry to set
         */
        public Builder industry(Industry industry) {
            this.industry = industry;
            return this;
        }

        /**
         * @param prevPrice the prevPrice to set
         */
        public Builder prevPrice(double prevPrice) {
            this.prevPrice = prevPrice;
            return this;
        }

        /**
         * @param openPrice the openPrice to set
         */
        public Builder openPrice(double openPrice) {
            this.openPrice = openPrice;
            return this;
        }

        /**
         * @param lastPrice the lastPrice to set
         */
        public Builder lastPrice(double lastPrice) {
            this.lastPrice = lastPrice;
            return this;
        }

        /**
         * @param highPrice the highPrice to set
         */
        public Builder highPrice(double highPrice) {
            this.highPrice = highPrice;
            return this;
        }

        /**
         * @param lowPrice the lowPrice to set
         */
        public Builder lowPrice(double lowPrice) {
            this.lowPrice = lowPrice;
            return this;
        }

        /**
         * @param volume the volume to set
         */
        public Builder volume(long volume) {
            this.volume = volume;
            return this;
        }

        /**
         * @param changePrice the changePrice to set
         */
        public Builder changePrice(double changePrice) {
            this.changePrice = changePrice;
            return this;
        }

        /**
         * @param changePricePercentage the changePricePercentage to set
         */
        public Builder changePricePercentage(double changePricePercentage) {
            this.changePricePercentage = changePricePercentage;
            return this;
        }

        /**
         * @param lastVolume the lastVolume to set
         */
        public Builder lastVolume(int lastVolume) {
            this.lastVolume = lastVolume;
            return this;
        }

        /**
         * @param buyPrice the buyPrice to set
         */
        public Builder buyPrice(double buyPrice) {
            this.buyPrice = buyPrice;
            return this;
        }

        /**
         * @param buyQuantity the buyQuantity to set
         */
        public Builder buyQuantity(int buyQuantity) {
            this.buyQuantity = buyQuantity;
            return this;
        }

        /**
         * @param sellPrice the sellPrice to set
         */
        public Builder sellPrice(double sellPrice) {
            this.sellPrice = sellPrice;
            return this;
        }

        /**
         * @param sellQuantity the sellQuantity to set
         */
        public Builder sellQuantity(int sellQuantity) {
            this.sellQuantity = sellQuantity;
            return this;
        }

        /**
         * @param secondBuyPrice the secondBuyPrice to set
         */
        public Builder secondBuyPrice(double secondBuyPrice) {
            this.secondBuyPrice = secondBuyPrice;
            return this;
        }

        /**
         * @param secondBuyQuantity the secondBuyQuantity to set
         */
        public Builder secondBuyQuantity(int secondBuyQuantity) {
            this.secondBuyQuantity = secondBuyQuantity;
            return this;
        }

        /**
         * @param secondSellPrice the secondSellPrice to set
         */
        public Builder secondSellPrice(double secondSellPrice) {
            this.secondSellPrice = secondSellPrice;
            return this;
        }

        /**
         * @param secondSellQuantity the secondSellQuantity to set
         */
        public Builder secondSellQuantity(int secondSellQuantity) {
            this.secondSellQuantity = secondSellQuantity;
            return this;
        }

        /**
         * @param thirdBuyPrice the thirdBuyPrice to set
         */
        public Builder thirdBuyPrice(double thirdBuyPrice) {
            this.thirdBuyPrice = thirdBuyPrice;
            return this;
        }

        /**
         * @param thirdBuyQuantity the thirdBuyQuantity to set
         */
        public Builder thirdBuyQuantity(int thirdBuyQuantity) {
            this.thirdBuyQuantity = thirdBuyQuantity;
            return this;
        }

        /**
         * @param thirdSellPrice the thirdSellPrice to set
         */
        public Builder thirdSellPrice(double thirdSellPrice) {
            this.thirdSellPrice = thirdSellPrice;
            return this;
        }

        /**
         * @param thirdSellQuantity the thirdSellQuantity to set
         */
        public Builder thirdSellQuantity(int thirdSellQuantity) {
            this.thirdSellQuantity = thirdSellQuantity;
            return this;
        }

        /**
         * @param timestamp the timestamp to set
         */
        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            this.hasTimestampInitialized = true;
            return this;
        }

        public Stock build() {
            if (hasTimestampInitialized == false) {
                // If we haven't initialized timestamp before, do it right now.
                this.timestamp = System.currentTimeMillis();
            }
            return new Stock(this);
        }
    }

    /** Creates a new instance of Stock */
    private Stock(Builder builder) {
        this(
            builder.code,
            builder.symbol,
            builder.name,
            builder.board,
            builder.industry,
            builder.prevPrice,
            builder.openPrice,
            builder.lastPrice,
            builder.highPrice,
            builder.lowPrice,
            builder.volume,
            builder.changePrice,
            builder.changePricePercentage,
            builder.lastVolume,
            builder.buyPrice,
            builder.buyQuantity,
            builder.sellPrice,
            builder.sellQuantity,
            builder.secondBuyPrice,
            builder.secondBuyQuantity,
            builder.secondSellPrice,
            builder.secondSellQuantity,
            builder.thirdBuyPrice,
            builder.thirdBuyQuantity,
            builder.thirdSellPrice,
            builder.thirdSellQuantity,
            builder.timestamp
            );
    }
    
    public Stock(
        Code code,
        Symbol symbol,
        String name,
        Board board,
        Industry industry,
        double prevPrice,
        double openPrice,
        double lastPrice,
        double highPrice,
        double lowPrice,
        // TODO: CRITICAL LONG BUG REVISED NEEDED.
        long volume,
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
        long timestamp
                ) 
    {
        this.code = code;
        this.symbol = symbol;
        this.name = name;
        this.board = board;
        this.industry = industry;
        this.prevPrice = prevPrice;
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
        this.timestamp = timestamp;
    }

    // I didn't make this construcotr private. As I would like to make user able
    // to construct Stock either through this constructor or Builder.
    public Stock(Stock stock) {
        this.code = stock.code;
        this.symbol = stock.symbol;
        this.name = stock.name;
        this.board = stock.board;
        this.industry = stock.industry;
        this.prevPrice = stock.prevPrice;
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
        this.timestamp = stock.timestamp;
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

    public double getPrevPrice() {
        return prevPrice;
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

    // TODO: CRITICAL LONG BUG REVISED NEEDED.
    public long getVolume() {
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
    
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns a clone copy of this stock with its name being modified to
     * specified name. This stock remains unchanged, as it is designed as
     * immutable class.
     *
     * @param name Specified name to be modified to
     * @return A clone copy of this stock with its name being modified to
     * specified name.
     */
    public Stock deriveStock(String name) {
        return new Stock(
            this.code,
            this.symbol,
            name,
            this.board,
            this.industry,
            this.prevPrice,
            this.openPrice,
            this.lastPrice,
            this.highPrice,
            this.lowPrice,
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            this.volume,
            this.changePrice,
            this.changePricePercentage,
            this.lastVolume,
            this.buyPrice,
            this.buyQuantity,
            this.sellPrice,
            this.sellQuantity,
            this.secondBuyPrice,
            this.secondBuyQuantity,
            this.secondSellPrice,
            this.secondSellQuantity,
            this.thirdBuyPrice,
            this.thirdBuyQuantity,
            this.thirdSellPrice,
            this.thirdSellQuantity,
            this.timestamp
        );
    }
    
    /**
     * Returns a clone copy of this stock with its symbol being modified to
     * specified symbol. This stock remains unchanged, as it is designed as
     * immutable class.
     *
     * @param symbol Specified symbol to be modified to
     * @return A clone copy of this stock with its symbol being modified to
     * specified symbol.
     */
    public Stock deriveStock(Symbol symbol) {
        return new Stock(
            this.code,
            symbol,
            this.name,
            this.board,
            this.industry,
            this.prevPrice,
            this.openPrice,
            this.lastPrice,
            this.highPrice,
            this.lowPrice,
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            this.volume,
            this.changePrice,
            this.changePricePercentage,
            this.lastVolume,
            this.buyPrice,
            this.buyQuantity,
            this.sellPrice,
            this.sellQuantity,
            this.secondBuyPrice,
            this.secondBuyQuantity,
            this.secondSellPrice,
            this.secondSellQuantity,
            this.thirdBuyPrice,
            this.thirdBuyQuantity,
            this.thirdSellPrice,
            this.thirdSellQuantity,
            this.timestamp
        );
    }

    @Override
    public String toString() {
        return symbol.toString();
    }
    
    public enum Board {
                                                // The following are naming conventions from CIMB :
        Main("Main Board"),                     // Main
        Second("Second Board"),                 // 2nd
        Mesdaq("Mesdaq"),                       // MESDAQ
        CallWarrant("Call Warrant"),            // ??
        KualaLumpur("Kuala Lumpur"),
        SES("SES"),                             // Singapore
        Copenhagen("Copenhagen"),               // Denmark        
        Paris("Paris"),                         // France
        Xetra("Xetra"),                         // Germany
        XETRA("XETRA"),
        Munich("Munich"),
        Stuttgart("Stuttgart"),
        Berlin("Berlin"),
        Hamburg("Hamburg"),
        Dusseldorf("Dusseldorf"),
        Frankfurt("Frankfurt"),
        Hannover("Hannover"),
        Milan("Milan"),                         // Italy
        Oslo("Oslo"),                           // Norway
        Madrid("Madrid"),                       // Spain
        MCE("MCE"),
        MercadoContinuo("Mercado Continuo"),
        Stockholm("Stockholm"),                 // Sweden
        FSI("FSI"),                             // UK
        London("London"),
        NasdaqSC("NasdaqSC"),                   // US
        DJI("DJI"),
        NasdaqNM("NasdaqNM"),
        NYSE("NYSE"),
        Nasdaq("Nasdaq"),
        AMEX("AMEX"),
        PinkSheet("Pink Sheet"),
        Sydney("Sydney"),                       // Australia
        ASX("ASX"),
        Vienna("Vienna"),                       // Austria
        Brussels("Brussels"),                   // Belgium
        Toronto("Toronto"),                     // Canada
        HKSE("HKSE"),                           // Hong Kong
        Jakarta("Jakarta"),                     // Indonesia
        KSE("KSE"),                             // Korea
        KOSDAQ("KOSDAQ"),
        Amsterdam("Amsterdam"),                 // Netherlands
        ENX("ENX"),                             // Portugal
        Lisbon("Lisbon"),
        VTX("VTX"),                             // Switzerland
        Virt_X("Virt-X"),
        Switzerland("Switzerland"),
        Taiwan("Taiwan"),                       // Taiwan
        BOM("Bombay"),                          // India
        NSE("NSE"),

        NZSX("NZ Stock Market"),                // New Zealand
        NZDX("NZ Debt Market"),
        NZAX("NZ Alternative Market"),
        
        TASE("Tel Aviv Stock Exchange"),        // Israel
        
        UserDefined("User Defined"),
        Unknown("Unknown");
        
        private final String name;

        Board(String name) {
            this.name = name;
        }
        
        @Override
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
        UserDefined("User Defined"),
        Unknown("Unknown");
        
        private final String name;

        Industry(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
            
    public final Code code;
    public final Symbol symbol;
    private final String name;
    private final Board board;
    private final Industry industry;
    private final double prevPrice;
    private final double openPrice;
    private final double lastPrice;    
    private final double highPrice;  
    private final double lowPrice;
    // TODO: CRITICAL LONG BUG REVISED NEEDED.
    private final long volume;
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
    // milliseconds. As timestamp in Java system always interpreted as 
    // milliseconds. 
    private final long timestamp;
}
