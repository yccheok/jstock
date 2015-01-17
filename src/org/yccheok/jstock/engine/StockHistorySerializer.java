/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.io.*;
import java.text.DecimalFormat;

import org.yccheok.jstock.file.Statements;


/**
 *
 * @author yccheok
 */
public class StockHistorySerializer {

    public StockHistorySerializer(String directory)
    {
        org.yccheok.jstock.gui.Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory);

        this.directory = directory;
    }

    public boolean save(StockHistoryServer stockHistoryServer, Duration duration)
    {
        // If the history server doesn't contain any information, return early.
        if (stockHistoryServer.size() == 0) {
            return false;
        }
        
        final long timestamp = stockHistoryServer.getTimestamp(0);
        final Code code = stockHistoryServer.getStock(timestamp).code;
        final Statements statements = Statements.newInstanceFromStockHistoryServer(stockHistoryServer, true);
        final File file = new File(getFileName(code, duration));
        return statements.saveAsCSVFile(file);
    }

    public boolean save(StockHistoryServer stockHistoryServer, Period period)
    {
        // If the history server doesn't contain any information, return early.
        if (stockHistoryServer.size() == 0) {
            return false;
        }

        final long timestamp = stockHistoryServer.getTimestamp(0);
        final Code code = stockHistoryServer.getStock(timestamp).code;
        final Statements statements = Statements.newInstanceFromStockHistoryServer(stockHistoryServer, true);
        final File file = new File(getFileName(code, period));
        return statements.saveAsCSVFile(file);
    }

    public StockHistoryServer load(Code code, Duration duration)
    {
        final File file = new File(getFileName(code, duration));
        final Statements statements = Statements.newInstanceFromCSVFile(file);
        return StatementsStockHistoryServer.newInstance(statements);
    }

    public StockHistoryServer load(Code code, Period period)
    {
        final File file = new File(getFileName(code, period));
        final Statements statements = Statements.newInstanceFromCSVFile(file);
        return StatementsStockHistoryServer.newInstance(statements);
    }

    private String getFileName(Code code, Duration duration) {
        final int startYear = duration.getStartDate().getYear();
        // +1, as we prefer based 1 month, for readability.
        final int startMonth = duration.getStartDate().getMonth() + 1;
        final int startDay = duration.getStartDate().getDate();
        final int endYear = duration.getEndDate().getYear();
        final int endMonth = duration.getEndDate().getMonth() + 1;
        final int endDay = duration.getEndDate().getDate();

        DecimalFormat decimalFormat = new DecimalFormat("00");

        final String fileName = directory + File.separator + code + 
                "-start_date=" + startYear + "-" + decimalFormat.format(startMonth) + "-" + decimalFormat.format(startDay) +
                "-end_date=" + endYear + "-" + decimalFormat.format(endMonth) + "-" + decimalFormat.format(endDay) +
                ".csv";

        return fileName;
    }

    private String getFileName(Code code, Period period) {
        final String fileName = directory + File.separator + code +
                "-" + period.name() +
                ".csv";

        return fileName;
    }

    private final String directory;     
}
