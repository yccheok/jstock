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

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class GoogleStockServer implements StockServer {

    public GoogleStockServer() {
    }
    
    @Override
    public Stock getStock(Code code) throws StockNotFoundException {
        List<Code> codes = new ArrayList<Code>();
        codes.add(code);
        List<Stock> stocks = getStocks(codes);
        if (stocks.size() == 1) {
            return stocks.get(0);
        }
        throw new StockNotFoundException();
    }

    private Code getOriginalCode(Map<String, Code> originalCodes, String googleTicker, String googleExchange) {
        // 1st try...
        final String googleCodeStr0 = googleExchange + ":" + googleTicker;
        
        Code result = originalCodes.get(googleCodeStr0);
        
        if (result != null) {
            return result;
        }
        
        // 2nd try...
        final String googleCodeStr1 = googleTicker;

        result = originalCodes.get(googleCodeStr1);

        if (result != null) {
            return result;
        }

        // 3rd try...
        if (googleExchange.equals("NSE")) {
            result = originalCodes.get(googleTicker + ".N");

            if (result != null) {
                return result;
            }
        } else if (googleExchange.equals("BOM")) {
            result = originalCodes.get(googleTicker + ".B");

            if (result != null) {
                return result;
            }
        }

        final Code googleCode = Code.newInstance(googleCodeStr0);
        
        result = originalCodes.get(Utils.toYahooFormat(googleCode).toString());
        
        if (result != null) {
            return result;
        }
        
        // Legacy code handling. In old India stock market, we are using Yahoo
        // stock code format like TATAMOTORS.NS
        final int googleTickerLength = googleTicker.length();
        final int ns_length = ".NS".length();
        for (Map.Entry<String, Code> entry : originalCodes.entrySet()) {
            String key = entry.getKey();
            final Code value = entry.getValue();
            
            final int key_length = key.length();
            if (key.endsWith(".NS") && key_length > ns_length) {
                key = key.substring(0, key_length - ns_length);
            } else {
                continue;
            }
            
            if (googleTickerLength >= key.length()) {
                if (googleTicker.equals(key)) {
                    result = value;
                    // Early break.
                    break;
                }
                if (googleTicker.contains(key)) {
                    result = value;
                    // Don't break. Keep searching. We might
                    // have a better.
                }
            } else {
                if (key.contains(googleTicker)) {
                    result = value;
                    // Don't break. Keep searching. We might
                    // have a better.
                }
            } 
        }
      
        return result;
    }
    
    @Override
    public List<Stock> getStocks(List<Code> codes) throws StockNotFoundException {
        assert(codes.isEmpty() == false);
        
        Map<String, Code> originalCodes = new HashMap<String, Code>();
        for (Code code : codes) {
            originalCodes.put(code.toString().trim().toUpperCase(), code);
        }
                
        // Use StringBuilder instead of StringBuffer. We do not concern on 
        // thread safety.
        final StringBuilder builder = new StringBuilder("https://www.google.com/finance/info?infotype=infoquoteall&q=");
        try {
            // Exception will be thrown from apache httpclient, if we do not
            // perform URL encoding.
            builder.append(java.net.URLEncoder.encode(Utils.toGoogleFormat(codes.get(0)).toString(), "UTF-8"));

            for (int i = 1, size = codes.size(); i < size; i++) {
                builder.append(",");
                builder.append(java.net.URLEncoder.encode(Utils.toGoogleFormat(codes.get(i)).toString(), "UTF-8"));
            }
            
            final String location = builder.toString();
            final String _respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);
            if (_respond == null) {
                throw new StockNotFoundException();
            }
            final String respond = Utils.GoogleRespondToJSON(_respond);
            // Google returns "// [ { "id": ... } ]".
            // We need to turn them into "[ { "id": ... } ]".
            final List<Map> jsonArray = gson.fromJson(respond, List.class);
            
            if (jsonArray == null) {
                throw new StockNotFoundException();
            }
            
            final List<Stock> stocks = new ArrayList<Stock>();
            for (int i = 0, size = jsonArray.size(); i < size; i++) {
                final Map<String, String> jsonObject = jsonArray.get(i);
                String name;
                final String ticker;
                final String exchange;
                
                try {
                    name = jsonObject.get("name");
                    name = name.substring(0, Math.min(SYMBOL_MAX_LENGTH, name.length())).trim();
                    ticker = jsonObject.get("t").toUpperCase();
                    exchange = jsonObject.get("e").toUpperCase();
                } catch (Exception ex) {
                    log.error(null, ex);
                    continue;
                }
                
                Code code = getOriginalCode(originalCodes, ticker, exchange);
                if (code == null) {
                    continue;
                }
                
                double c = 0;
                double l = 0;
                double p = 0;
                double op = 0;
                double hi = 0;
                double lo = 0;
                long vo = 0;
                double cp = 0;
                
                // Change
                try { c = Double.parseDouble(jsonObject.get("c").replaceAll("[^0-9\\.\\-]", "")); } catch (NumberFormatException ex) { log.error(null, ex); }
                // Last
                try {l = Double.parseDouble(jsonObject.get("l").replaceAll("[^0-9\\.]", "")); } catch (NumberFormatException ex) { log.error(null, ex); }
                // Prev
                try { p = l - c; } catch (NumberFormatException ex) { log.error(null, ex); }
                // Open
                try { op = Double.parseDouble(jsonObject.get("op").replaceAll("[^0-9\\.\\-]", "")); } catch (NumberFormatException ex) { log.error(null, ex); }
                // High
                try { hi = Double.parseDouble(jsonObject.get("hi").replaceAll("[^0-9\\.]", "")); } catch (NumberFormatException ex) { log.error(null, ex); }
                // Low
                try { lo = Double.parseDouble(jsonObject.get("lo").replaceAll("[^0-9\\.]", "")); } catch (NumberFormatException ex) { log.error(null, ex); }
                // Vol
                try { 
                    String vo_string = jsonObject.get("vo");
                    vo = (long)Double.parseDouble(vo_string.replaceAll("[^0-9\\.]", "")); 
                    if (vo_string.endsWith("K")) {
                        vo = vo * 1000;
                    } else if (vo_string.endsWith("M")) {
                        vo = vo * 1000000;
                    } else if (vo_string.endsWith("B")) {
                        vo = vo * 1000000000;
                    }
                } catch (NumberFormatException ex) { log.error(null, ex); }
                // Change Percentage
                try { cp = Double.parseDouble(jsonObject.get("cp").replaceAll("[^0-9\\.\\-]", "")); } catch (NumberFormatException ex) { log.error(null, ex); }
                // No last volumne information for Google Finance.
                // No buy price information for Google Finance.
                // No buy volume information for Google Finance.
                // No sell price information for Google Finance.
                // No sell volume information for Google Finance.                    
                final Stock stock = new Stock.Builder(code, Symbol.newInstance(name))
                        .name(name)
                        .changePrice(c)
                        .lastPrice(l)
                        .prevPrice(p)
                        .openPrice(op)
                        .highPrice(hi)
                        .lowPrice(lo)
                        .volume(vo)
                        .changePricePercentage(cp)
                        .build();
                stocks.add(stock);
            } 

            Set<Code> currCodes = new HashSet<Code>();
            List<Stock> emptyStocks = new ArrayList<Stock>();

            for (Stock stock : stocks) {
                currCodes.add(stock.code);
            }

            for (Code code : codes) {
                if (currCodes.contains(code) == false) {
                    emptyStocks.add(org.yccheok.jstock.gui.Utils.getEmptyStock(code, Symbol.newInstance(code.toString())));
                }
            }

            stocks.addAll(emptyStocks);

            if (stocks.size() != codes.size()) {
                throw new StockNotFoundException("Stock size (" + stocks.size() + ") inconsistent with code size (" + codes.size() + ")");
            }  

            return stocks;                
        } catch (UnsupportedEncodingException ex) {
            throw new StockNotFoundException(null, ex);
        } catch (Exception ex) {
            // Jackson library may cause runtime exception if there is error
            // in the JSON string.
            throw new StockNotFoundException(null, ex);
        }
    }
    
    private static final int SYMBOL_MAX_LENGTH = 17;
    
    // Will it be better if we make this as static?
    private final Gson gson = new Gson();
    private static final Log log = LogFactory.getLog(GoogleStockServer.class);
}
