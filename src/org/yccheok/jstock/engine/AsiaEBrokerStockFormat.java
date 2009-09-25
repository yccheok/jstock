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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class AsiaEBrokerStockFormat implements StockFormat {
    @Override
    public java.util.List<Stock> parse(String source) {
        List<Stock> stocks = new ArrayList<Stock>();
        final String[] tokens = source.split("\r\n|\r|\n");
        /* Lazy initialization. I assume calendar creation is expensive. */
        java.util.Calendar calendar = null;
        for (String token : tokens) {
            String[] token_elements = token.split(",");
            if (token_elements.length <= MAX_TOKEN_INDEX) {
                continue;
            }
            try {
                final Code code = Code.newInstance(token_elements[CODE_TOKEN_INDEX].trim());
                final Symbol symbol = Symbol.newInstance(token_elements[SYMBOL_TOKEN_INDEX].trim());
                final String name = symbol.toString();
                final Stock.Board board = Stock.Board.Unknown;
                final Stock.Industry industry = Stock.Industry.Unknown;
                final double prevPrice = Double.parseDouble(token_elements[PREV_PRICE_TOKEN_INDEX]);
                final double lastPrice = Double.parseDouble(token_elements[LAST_PRICE_TOKEN_INDEX]);
                final double highPrice = Double.parseDouble(token_elements[HIGH_PRICE_TOKEN_INDEX]);
                final double lowPrice = Double.parseDouble(token_elements[LOW_PRICE_TOKEN_INDEX]);
                final int volume = Integer.parseInt(token_elements[VOLUME_TOKEN_INDEX]);

                java.math.BigDecimal _prevPrice = new java.math.BigDecimal("" + prevPrice);
                java.math.BigDecimal _lastPrice = new java.math.BigDecimal("" + lastPrice);
                java.math.BigDecimal _changePrice = _lastPrice.subtract(_prevPrice);
                final double changePrice = _changePrice.round(new MathContext(2)).doubleValue();
                BigDecimal _changePricePercentage = (_prevPrice.compareTo(BigDecimal.ZERO) == 1) ? (_changePrice.multiply(new BigDecimal(100.0)).divide(_prevPrice, BigDecimal.ROUND_HALF_UP)) : BigDecimal.ZERO;
                final double changePricePercentage = _changePricePercentage.round(new MathContext(2)).doubleValue();

                final int lastVolume = Integer.parseInt(token_elements[LAST_VOLUME_TOKEN_INDEX]);
                final double buyPrice = Double.parseDouble(token_elements[BUY_PRICE_TOKEN_INDEX]);
                final int buyQuantity = Integer.parseInt(token_elements[BUY_QUANTITY_TOKEN_INDEX]);
                double sellPrice = Double.parseDouble(token_elements[SELL_PRICE_TOKEN_INDEX]);
                int sellQuantity = Integer.parseInt(token_elements[SELL_QUANTITY_TOKEN_INDEX]);
                double secondBuyPrice = Double.parseDouble(token_elements[SECOND_BUY_PRICE_TOKEN_INDEX]);
                int secondBuyQuantity = Integer.parseInt(token_elements[SECOND_BUY_QUANTITY_TOKEN_INDEX]);
                double secondSellPrice = Double.parseDouble(token_elements[SECOND_SELL_PRICE_TOKEN_INDEX]);
                int secondSellQuantity = Integer.parseInt(token_elements[SECOND_SELL_QUANTITY_TOKEN_INDEX]);
                double thirdBuyPrice = Double.parseDouble(token_elements[THIRD_BUY_PRICE_TOKEN_INDEX]);
                int thirdBuyQuantity = Integer.parseInt(token_elements[THIRD_BUY_QUANTITY_TOKEN_INDEX]);
                double thirdSellPrice = Double.parseDouble(token_elements[THIRD_SELL_PRICE_TOKEN_INDEX]);
                int thirdSellQuantity = Integer.parseInt(token_elements[THIRD_SELL_QUANTITY_TOKEN_INDEX]);

                if (calendar == null) {
                    calendar = Calendar.getInstance();
                }
                Stock stock = new Stock(
                        code,
                        symbol,
                        name,
                        board,
                        industry,
                        prevPrice,
                        0.0,    /* Sad. I have no idea how to retrieve open price. */
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
                log.error(null, exp);
                continue;
            }
        }
        return stocks;
    }

    public static StockFormat getInstance() {
        return stockFormat;
    }

    private static final StockFormat stockFormat = new AsiaEBrokerStockFormat();

    private static final int CODE_TOKEN_INDEX = 0;
    private static final int SYMBOL_TOKEN_INDEX = 1;
    private static final int PREV_PRICE_TOKEN_INDEX = 2;
    private static final int BUY_PRICE_TOKEN_INDEX = 3;
    private static final int BUY_QUANTITY_TOKEN_INDEX = 4;
    private static final int SELL_PRICE_TOKEN_INDEX = 5;
    private static final int SELL_QUANTITY_TOKEN_INDEX = 6;
    private static final int LAST_PRICE_TOKEN_INDEX = 7;
    private static final int LAST_VOLUME_TOKEN_INDEX = 8;
    private static final int VOLUME_TOKEN_INDEX = 9;
    private static final int HIGH_PRICE_TOKEN_INDEX = 10;
    private static final int LOW_PRICE_TOKEN_INDEX = 11;
    private static final int SECOND_BUY_PRICE_TOKEN_INDEX = 12;
    private static final int THIRD_BUY_PRICE_TOKEN_INDEX = 13;
    private static final int FOURTH_BUY_PRICE_TOKEN_INDEX = 14;
    private static final int FIFTH_BUY_PRICE_TOKEN_INDEX = 15;
    private static final int SECOND_SELL_PRICE_TOKEN_INDEX = 16;
    private static final int THIRD_SELL_PRICE_TOKEN_INDEX = 17;
    private static final int FOURTH_SELL_PRICE_TOKEN_INDEX = 18;
    private static final int FIFTH_SELL_PRICE_TOKEN_INDEX = 19;
    private static final int SECOND_BUY_QUANTITY_TOKEN_INDEX = 20;
    private static final int THIRD_BUY_QUANTITY_TOKEN_INDEX = 21;
    private static final int FOURTH_BUY_QUANTITY_TOKEN_INDEX = 22;
    private static final int FIFTH_BUY_QUANTITY_TOKEN_INDEX = 23;
    private static final int SECOND_SELL_QUANTITY_TOKEN_INDEX = 24;
    private static final int THIRD_SELL_QUANTITY_TOKEN_INDEX = 25;
    private static final int FOURTH_SELL_QUANTITY_TOKEN_INDEX = 26;
    private static final int FIFTH_SELL_QUANTITY_TOKEN_INDEX = 27;
    // Must be the largest value of all index.
    private static final int MAX_TOKEN_INDEX = 27;

    private static final Log log = LogFactory.getLog(AsiaEBrokerStockFormat.class);
}
