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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.StockNotFoundException;
import org.yccheok.jstock.engine.StockServerFactory;
import org.yccheok.jstock.internationalization.GUIBundle;

/**
 *
 * @author yccheok
 */
public class StockServerFactoryJRadioButton extends JRadioButton {

    public StockServerFactoryJRadioButton(StockServerFactory stockServerFactory) {
        this.stockServerFactory = stockServerFactory;
        this.setStatus(Status.Busy);
        this.setToolTipText("Checking for server health...");

        final Class c = stockServerFactory.getClass();
        if (c == org.yccheok.jstock.engine.GoogleStockServerFactory.class) {
            // Hacking from preventing GoogleStockServerFactory being selected
            // as primary server. This is because GoogleStockServerFactory is
            // not fully completed yet.
            this.setEnabled(false);
        }

        initSwingWorker();
    }

    public static String toReadableText(StockServerFactory stockServerFactory) {
        Class c = stockServerFactory.getClass();
        if (c == org.yccheok.jstock.engine.SingaporeYahooStockServerFactory.class) {
            return GUIBundle.getString("StockServerFactoryJRadioButton_SingaporeYahooStockServerFactory");
        }
        else if (c == org.yccheok.jstock.engine.BrazilYahooStockServerFactory.class) {
            return GUIBundle.getString("StockServerFactoryJRadioButton_BrazilYahooStockServerFactory");
        }
        else if (c == org.yccheok.jstock.engine.YahooStockServerFactory.class) {
            return GUIBundle.getString("StockServerFactoryJRadioButton_YahooStockServerFactory");
        }
        else if (c == org.yccheok.jstock.engine.GoogleStockServerFactory.class) {
            return GUIBundle.getString("StockServerFactoryJRadioButton_GoogleStockServerFactory");
        }
        return c.getSimpleName();
    }

    // It is possible that we can avoid from hard coding?
    // MSFT and PBBANK were choosen, because they are the best stock in the town.
    private Health getServerHealth() {
        final Health health = new Health();

        /* Test For Stock */
        Class c = stockServerFactory.getStockServer().getClass();

        // Microsoft.
        Code code = Code.newInstance("MSFT");
        if (c == org.yccheok.jstock.engine.SingaporeYahooStockServer.class) {
            // PBBANK.
            code = Code.newInstance("1295.KL");
        }
        else if (c == org.yccheok.jstock.engine.BrazilYahooStockServer.class) {
            // ALL AMER LAT-UNT N2
            code = Code.newInstance("ALLL11.SA");
        }
        else if (c == org.yccheok.jstock.engine.YahooStockServer.class) {
            code = Code.newInstance("MSFT");
        }
        else if (c == org.yccheok.jstock.engine.GoogleStockServerFactory.class) {
            code = Code.newInstance("MSFT");
        }
        else if (c == org.yccheok.jstock.engine.KLSEInfoStockServerFactory.class) {
            // PBBANK.
            code = Code.newInstance("1295.KL");
        }

        try {
            stockServerFactory.getStockServer().getStock(code);
            health.stock = true;
        } catch (StockNotFoundException ex) {
            log.error(null, ex);
        }

        /* Test For History */
        if (null != stockServerFactory.getStockHistoryServer(code))
        {
            health.history = true;
        }

        /* Test for Market */
        if (null != stockServerFactory.getMarketServer().getMarket())
        {
            health.market = true;
        }
        
        return health;
    }

    private String toHTML(Health health) {
        String html = "<html><body>";
        if (health == null || health.market == false) {
            html += "Index : <b>Failed</b><br/>";
        }
        else {
            html += "Index : <b>Success</b><br/>";
        }
        if (health == null || health.stock == false) {
            html += "Stock : <b>Failed</b><br/>";
        }
        else {
            html += "Stock : <b>Success</b><br/>";
        }
        if (health == null || health.history == false) {
            html += "History : <b>Failed</b><br/>";
        }
        else {
            html += "History : <b>Success</b><br/>";
        }
        html += "</body></html>";
        return html;
    }

    private void initSwingWorker() {
        SwingWorker worker = new SwingWorker<Health, Void>() {
            @Override
            public Health doInBackground() {
                return getServerHealth();
            }

            @Override
            public void done() {
                // The done Method: When you are informed that the SwingWorker
                // is done via a property change or via the SwingWorker object's
                // done method, you need to be aware that the get methods can
                // throw a CancellationException. A CancellationException is a
                // RuntimeException, which means you do not need to declare it
                // thrown and you do not need to catch it. Instead, you should
                // test the SwingWorker using the isCancelled method before you
                // use the get method.
                if (this.isCancelled()) {
                    return;
                }

                Health health = null;
                try {
                    health = get();
                } catch (InterruptedException ex) {
                    log.error(null, ex);
                } catch (ExecutionException ex) {
                    log.error(null, ex);
                } catch (CancellationException ex) {
                    // Some developers suggest to catch this exception, instead of 
                    // checking on isCancelled. As I am not confident by merely 
                    // isCancelled check can prevent CancellationException (What 
                    // if cancellation is happen just after isCancelled check?),
                    // I will apply both techniques.
                    log.error(null, ex);
                }

                if (health == null || health.isGood() == false) {
                    StockServerFactoryJRadioButton.this.setStatus(Status.Failed);
                }
                else {
                    StockServerFactoryJRadioButton.this.setStatus(Status.Success);
                }
                StockServerFactoryJRadioButton.this.setToolTipText(toHTML(health));
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
        String label = "<html><table cellpadding=0><tr><td><img src=\""
            // The location of the icon
            + status.getFileName()
            + "\"/></td><td width="

            // The gap, in pixels, between icon and text
            + 3
            + "><td>"

            // Retrieve the current label text
            + text
            + "</td></tr></table></html>";

        this.setText(label);
    }

    private static class Health {
        public boolean market = false;
        public boolean history = false;
        public boolean stock = false;

        // All good, only considered as good.
        public boolean isGood() {
            return market && history && stock;
        }
    }

    private enum Status {
        Busy(Utils.toHTMLFileSrcFormat(Utils.getExtraDataDirectory() + "spinner.gif")),
        Success(Utils.toHTMLFileSrcFormat(Utils.getExtraDataDirectory() + "network-transmit-receive.png")),
        Failed(Utils.toHTMLFileSrcFormat(Utils.getExtraDataDirectory() + "network-error.png"));

        Status(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        private final String fileName;
    }

    private Status status = Status.Busy;
    private final StockServerFactory stockServerFactory;

    private static final Log log = LogFactory.getLog(StockServerFactoryJRadioButton.class);
}
