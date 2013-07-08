/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.engine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author yccheok
 */
public class GoogleStockServer implements StockServer {

    public GoogleStockServer(Country country) {
        this.country = country;
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

    @Override
    public List<Stock> getStocks(List<Code> codes) throws StockNotFoundException {
        assert(codes.isEmpty() == false);
        
        Map<String, Code> map = new HashMap<String, Code>();
        for (Code code : codes) {
            map.put(code.toString().trim().toUpperCase(), code);
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
            final String respond = Utils.GoogleRespondToJSON(org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location));
            // Google returns "// [ { "id": ... } ]".
            // We need to turn them into "[ { "id": ... } ]".
            final List<Map> jsonArray = mapper.readValue(respond, List.class);

            final List<Stock> stocks = new ArrayList<Stock>();
            for (int i = 0, size = jsonArray.size(); i < size; i++) {
                final Map<String, String> jsonObject = jsonArray.get(i);
                final String name;
                final String _code0;
                final String _code1;
                
                try {
                    name = jsonObject.get("name");

                    if (country == Country.India) {
                        _code0 = jsonObject.get("t").toUpperCase() + ".NS";
                        _code1 = "NSE:" + jsonObject.get("t").toUpperCase();
                    } else {
                        assert(false);
                        _code0 = jsonObject.get("t").toUpperCase();
                        _code1 = _code0;
                    }
                } catch (Exception ex) {
                    log.error(null, ex);
                    continue;
                }
                
                // Code
                Code code = map.get(_code0);
                if (code == null) {
                    // I know lah!
                    if (_code0 != _code1) {
                        code = map.get(_code1);
                    }
                    
                    if (code == null) {
                        continue;
                    }
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
                try { vo = (long)Double.parseDouble(jsonObject.get("vo").replaceAll("[^0-9\\.]", "")); } catch (NumberFormatException ex) { log.error(null, ex); }
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
        } catch (IOException ex) {
            throw new StockNotFoundException(null, ex);
        } catch (Exception ex) {
            // Jackson library may cause runtime exception if there is error
            // in the JSON string.
            throw new StockNotFoundException(null, ex);
        }
    }
    
    // Will it be better if we make this as static?
    private final ObjectMapper mapper = new ObjectMapper(); 
    private final Country country;
    private static final Log log = LogFactory.getLog(GoogleStockServer.class);
}
