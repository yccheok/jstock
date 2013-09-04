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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.portfolio.Dividend;

/**
 *
 * @author yccheok
 */
public class YahooDividendServer implements DividendServer {

    @Override
    public List<Dividend> getDividends(Code code, Duration duration) {
        // http://ichart.finance.yahoo.com/table.csv?s=0005.HK&a=0&b=3&c=2000&d=08&e=3&f=2013&g=v&ignore=.csv
        // Build up the URL.
        final StringBuilder stringBuilder = new StringBuilder("http://ichart.finance.yahoo.com/table.csv?s=");
        try {
            final String codeString = java.net.URLEncoder.encode(code.toString(), "UTF-8");
            stringBuilder.append(codeString);
        } catch (UnsupportedEncodingException ex) {
            log.error(null, ex);
            return java.util.Collections.emptyList();
        }
        
        final SimpleDate startDate = duration.getStartDate();
        stringBuilder.append("&a=").append(startDate.getMonth());
        stringBuilder.append("&b=").append(startDate.getDate());
        stringBuilder.append("&c=").append(startDate.getYear());
        
        final SimpleDate endDate = duration.getEndDate();
        stringBuilder.append("&d=").append(endDate.getMonth());
        stringBuilder.append("&e=").append(endDate.getDate());
        stringBuilder.append("&f=").append(endDate.getYear());
        
        // I think this is important to have dividend information.
        stringBuilder.append("&g=v&ignore=.csv");
        
        final String location = stringBuilder.toString();
        
        String respond = null;
        
        for (int retry = 0; retry < NUM_OF_RETRY; retry++) {
            respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);            
            if (respond != null) {
                break;
            }
        }
        
        if (respond == null) {
            return java.util.Collections.emptyList();
        }

        String[] stockDatas = respond.split("\r\n|\r|\n");

        // There must be at least two lines : header information and history information.
        final int length = stockDatas.length;


        List<Dividend> dividends = new ArrayList<Dividend>();
        
        for (int i = 1; i < length; i++) {
            String[] fields = stockDatas[i].split(",");
            if (fields.length != 2) {
                continue;
            }
            String[] dates = fields[0].split("-");
            if (dates.length != 3) {
                continue;
            }
            int date;
            int month;
            int year;
            double amount;
            try {
                year = Integer.parseInt(dates[0]);
                // Our month is 0 based.
                month = Integer.parseInt(dates[1]) - 1;
                date = Integer.parseInt(dates[2]);
                amount = Double.parseDouble(fields[1]);
            } catch (NumberFormatException ex) {
                log.error(null, ex);
                continue;
            }
            
            SimpleDate simpleDate = new SimpleDate(year, month, date);
            
            if (duration.isContains(simpleDate) == false) {
                continue;
            }
            
            Dividend dividend = new Dividend(
                    StockInfo.newInstance(code, Symbol.newInstance(code.toString())),
                    amount,
                    simpleDate
            );
            
            dividends.add(dividend);
        }
        
        return dividends;
    }
    
    private static final int NUM_OF_RETRY = 2;
    private static final Log log = LogFactory.getLog(YahooDividendServer.class);
}
