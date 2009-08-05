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

package org.yccheok.jstock.gui;

import java.util.concurrent.ExecutionException;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.StockNotFoundException;
import org.yccheok.jstock.engine.StockServerFactory;

/**
 *
 * @author yccheok
 */
public class StockServerFactoryJRadioButton extends JRadioButton {

    public StockServerFactoryJRadioButton(StockServerFactory stockServerFactory) {
        this.stockServerFactory = stockServerFactory;
        this.setStatus(Status.Busy);
        initSwingWorker();
    }

    public static String toReadableText(StockServerFactory stockServerFactory) {
        Class c = stockServerFactory.getClass();
        if (c == org.yccheok.jstock.engine.CIMBStockServerFactory.class) {
            return "CIMB Stock Server";
        }
        else if (c == org.yccheok.jstock.engine.SingaporeYahooStockServerFactory.class) {
            return "Singapore Yahoo Stock Server";
        }
        else if (c == org.yccheok.jstock.engine.YahooStockServerFactory.class) {
            return "Yahoo Stock Server";
        }
        return c.getSimpleName();
    }

    // It is possible that we can avoid from hard coding?
    // MSFT and PBBANK were choosen, because they are the best stock in the town.
    private boolean isServerInGoodHealth() {
        /* Test For Stock */
        Class c = stockServerFactory.getStockServer().getClass();

        // Microsoft.
        Code code = Code.newInstance("MSFT");
        if (c == org.yccheok.jstock.engine.CIMBStockServer.class) {
            // PBBANK.
            code = Code.newInstance("1295.KL");
        }
        else if (c == org.yccheok.jstock.engine.SingaporeYahooStockServer.class) {
            // PBBANK.
            code = Code.newInstance("1295.KL");
        }
        else if (c == org.yccheok.jstock.engine.YahooStockServer.class) {
            code = Code.newInstance("MSFT");
        }
        try {
            stockServerFactory.getStockServer().getStock(code);
        } catch (StockNotFoundException ex) {
            log.error(null, ex);
            return false;
        }

        /* Test For History */
        if (null == stockServerFactory.getStockHistoryServer(code))
        {
            return false;
        }

        /* Test for Market */
        if (null == stockServerFactory.getMarketServer())
        {
            return false;
        }
        
        return true;
    }

    private void initSwingWorker() {
        SwingWorker worker = new SwingWorker<Status, Void>() {
            @Override
            public Status doInBackground() {
                if (isServerInGoodHealth()) {
                    return Status.Connected;
                }
                return Status.NotConnected;
            }

            @Override
            public void done() {
                Status status = Status.NotConnected;
                try {
                    status = get();
                } catch (InterruptedException ex) {
                    log.error(null, ex);
                } catch (ExecutionException ex) {
                    log.error(null, ex);
                }
                StockServerFactoryJRadioButton.this.setStatus(status);
            }
        };

        worker.execute();
    }

    public StockServerFactory getStockServerFactory() {
        return this.stockServerFactory;
    }

    // Please refer to http://www.exampledepot.com/egs/javax.swing/checkbox_AddIcon.html
    // for details.
    private void setStatus(Status status) {
        this.status = status;

        final String text = toReadableText(stockServerFactory);

        // Define an HTML fragment with an icon on the left and text on the right.
        // The elements are embedded in a 3-column table.
        String label = "<html><table cellpadding=0><tr><td><img src=file:"
            // The location of the icon
            + status.getFileName()
            + "/></td><td width="

            // The gap, in pixels, between icon and text
            + 3
            + "><td>"

            // Retrieve the current label text
            + text
            + "</td></tr></table></html>";

        this.setText(label);
        this.setToolTipText(status.getInfo());
    }

    public enum Status {
        Busy(Utils.getExtraDataDirectory() + "spinner.gif", "Checking for stock server health..."),
        Connected(Utils.getExtraDataDirectory() + "network-transmit-receive.png", "This server is in good health"),
        NotConnected(Utils.getExtraDataDirectory() + "network-error.png", "This server is in bad health. Try again later");

        Status(String fileName, String info) {
            this.fileName = fileName;
            this.info = info;
        }

        public String getFileName() {
            return fileName;
        }

        public String getInfo() {
            return info;
        }

        private final String info;
        private final String fileName;
    }

    private Status status = Status.Busy;
    private final StockServerFactory stockServerFactory;

    private static final Log log = LogFactory.getLog(StockServerFactoryJRadioButton.class);
}
