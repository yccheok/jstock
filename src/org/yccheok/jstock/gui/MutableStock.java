/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng CHEOK <yccheok@yahoo.com>
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

package org.yccheok.jstock.gui;

import org.yccheok.jstock.engine.*;

/**
 *
 * @author yccheok
 */
public class MutableStock {
    
    /** Creates a new instance of MutableStock */
    public MutableStock(Stock stock) {
        this.stock = stock;
    }
    
    public Stock getStock() {
        return this.stock;
    }
    
    public Code getCode() {
        return stock.code;
    }

    public Symbol getSymbol() {
        return stock.symbol;
    }

    public String getName() {
        return stock.getName();
    }

    public Board getBoard() {
        return stock.getBoard();
    }
    
    public Industry getIndustry() {
        return stock.getIndustry();
    }

    public double getOpenPrice() {
        return stock.getOpenPrice();
    }

    public double getPrevPrice() {
        return stock.getPrevPrice();
    }

    public double getLastPrice() {
        return stock.getLastPrice();
    }

    public double getHighPrice() {
        return stock.getHighPrice();
    }

    public double getLowPrice() {
        return stock.getLowPrice();
    }

    // TODO: CRITICAL LONG BUG REVISED NEEDED.
    public long getVolume() {
        return stock.getVolume();
    }

    public double getChangePrice() {
        return stock.getChangePrice();
    }

    public double getChangePricePercentage() {
        return stock.getChangePricePercentage();
    }

    public int getLastVolume() {
        return stock.getLastVolume();
    }

    public double getBuyPrice() {
        return stock.getBuyPrice();
    }

    public int getBuyQuantity() {
        return stock.getBuyQuantity();
    }

    public double getSellPrice() {
        return stock.getSellPrice();
    }

    public int getSellQuantity() {
        return stock.getSellQuantity();
    }
    
    @Override
    public String toString() {
        return stock.toString();
    }

    /*
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getSecondBuyPrice(),
                stock.getSecondBuyQuantity(),
                stock.getSecondSellPrice(),
                stock.getSecondSellQuantity(),
                stock.getThirdBuyPrice(),
                stock.getThirdBuyQuantity(),
                stock.getThirdSellPrice(),
                stock.getThirdSellQuantity(),
                stock.getCalendar()
                );
     */
    public void setOpenPrice(double openPrice) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                openPrice,
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;
    }

    public void setPrevPrice(double prevPrice) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                prevPrice,
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );

        this.stock = s;
    }
    
    public void setLastPrice(double lastPrice) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                lastPrice,
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;
    }
    
    public void setHighPrice(double highPrice) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                highPrice,
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;
    }

    public void setLowPrice(double lowPrice) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                lowPrice,
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;        
    }

    // TODO: CRITICAL LONG BUG REVISED NEEDED.
    public void setVolume(long volume) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                volume,
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;        
    }

    public void setChangePrice(double changePrice) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                changePrice,
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;        
    }

    public void setChangePricePercentage(double changePricePercentage) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                changePricePercentage,
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;        
    }

    public void setLastVolume(int lastVolume) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                lastVolume,
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;        
    }

    public void setBuyPrice(double buyPrice) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                buyPrice,
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;        
    }

    public void setBuyQuantity(int buyQuantity) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                buyQuantity,
                stock.getSellPrice(),
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;        
    }

    public void setSellPrice(double sellPrice) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                sellPrice,
                stock.getSellQuantity(),
                stock.getTimestamp()
                );
        
        this.stock = s;        
    }

    public void setSellQuantity(int sellQuantity) {
        final Stock s = new Stock(
                stock.code,
                stock.symbol,
                stock.getName(),
                stock.getCurrency(),
                stock.getBoard(),
                stock.getIndustry(),
                stock.getPrevPrice(),
                stock.getOpenPrice(),
                stock.getLastPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getChangePrice(),
                stock.getChangePricePercentage(),
                stock.getLastVolume(),
                stock.getBuyPrice(),
                stock.getBuyQuantity(),
                stock.getSellPrice(),
                sellQuantity,
                stock.getTimestamp()
                );
        
        this.stock = s;
    }

    private Stock stock;
}