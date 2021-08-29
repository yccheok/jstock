/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2015 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.gui.charting;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Observer;
import org.yccheok.jstock.engine.RealTimeStockMonitor;
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.currency.Currency;
import org.yccheok.jstock.file.UserDataDirectory;
import org.yccheok.jstock.file.UserDataFile;
import org.yccheok.jstock.gui.Constants;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.JStock;
import org.yccheok.jstock.gui.PortfolioManagementJPanel;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.Activities;
import org.yccheok.jstock.portfolio.Activity;
import org.yccheok.jstock.portfolio.ActivitySummary;
import org.yccheok.jstock.portfolio.Contract;
import org.yccheok.jstock.portfolio.Dividend;
import org.yccheok.jstock.portfolio.DividendSummary;
import org.yccheok.jstock.portfolio.PortfolioRealTimeInfo;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;


/**
 *
 * @author yccheok
 */
public class InvestmentFlowChartJDialog extends javax.swing.JDialog implements Observer<RealTimeStockMonitor, RealTimeStockMonitor.Result> {

    /** Creates new form InvestmentFlowChartJDialog */
    public InvestmentFlowChartJDialog(java.awt.Frame parent, boolean modal, PortfolioManagementJPanel portfolioManagementJPanel) {
        super(parent, modal);
        initComponents();

        // Initialize main data structures.
        this.portfolioManagementJPanel = portfolioManagementJPanel;
        
        initJComboBox();

        // We need a stock price monitor, to update all the stocks value.
        initRealTimeStockMonitor();

        final JFreeChart freeChart = createChart();
        org.yccheok.jstock.charting.Utils.applyChartTheme(freeChart);
        this.chartPanel = new ChartPanel(freeChart, true, true, true, true, true);

        // Make chartPanel able to receive key event.
        // So that we may use arrow left/right key to move around yellow
        // information boxes. We may also use up/down key to perform combo box
        // selection.
        this.chartPanel.setFocusable(true);
        this.chartPanel.requestFocus();
        
        this.layer = new org.jdesktop.jxlayer.JXLayer<ChartPanel>(this.chartPanel);
        this.investmentFlowLayerUI = new InvestmentFlowLayerUI<ChartPanel>(this);
        layer.setUI(this.investmentFlowLayerUI);

        getContentPane().add(layer, java.awt.BorderLayout.CENTER);

        loadDimension();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // Timeout. Remove busy message box.
                finishLookUpPrice = true;
                investmentFlowLayerUI.setDirty(true);
            }
        }, 15000);

        // Handle zoom-in.
        addChangeListener(this.chartPanel);

        // Handle resize.
        this.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e) {
                // Sequence is important. We will use invest information box as master.
                // ROI information box will be adjusted accordingly.
                investmentFlowLayerUI.updateInvestPoint();
                investmentFlowLayerUI.updateROIPoint();
            }
        });
    }

    // Add change listener to chart panel to handle zoom-in.
    private void addChangeListener(ChartPanel chartPanel) {
        chartPanel.getChart().addChangeListener(new ChartChangeListener() {
            @Override
            public void chartChanged(ChartChangeEvent event) {
                if (event.getType() == ChartChangeEventType.GENERAL) {
                    // Sequence is important. We will use invest information box as master.
                    // ROI information box will be adjusted accordingly.
                    investmentFlowLayerUI.updateInvestPoint();
                    investmentFlowLayerUI.updateROIPoint();
                }
            }
        });
    }

    public ChartPanel getChartPanel() {
        return this.chartPanel;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/yccheok/jstock/data/gui"); // NOI18N
        setTitle(bundle.getString("InvestmentFlowChartJDialog_Title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.BorderLayout(0, 5));

        jPanel2.setLayout(new java.awt.BorderLayout());

        jComboBox1.setModel(getComboBoxModel());
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox1, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.saveDimension();
        // This will shut down the old real time monitor, and create a new
        // real time monitor. Since there isn't any stock code being submitted
        // to the newly created monitor, it will not start any thread.
        // With this, we are able to perform thread clean up.
        this.initRealTimeStockMonitor();
    }//GEN-LAST:event_formWindowClosing

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        synchronized(jComboBox1) {
            final JFreeChart freeChart = createChart();
            org.yccheok.jstock.charting.Utils.applyChartTheme(freeChart);

            getContentPane().remove(this.layer);

            this.chartPanel = new ChartPanel(freeChart, true, true, true, true, true);
            this.layer = new org.jdesktop.jxlayer.JXLayer<ChartPanel>(this.chartPanel);
            this.investmentFlowLayerUI = new InvestmentFlowLayerUI<ChartPanel>(this);
            layer.setUI(this.investmentFlowLayerUI);

            getContentPane().add(layer, java.awt.BorderLayout.CENTER);
            getContentPane().invalidate();
            getContentPane().validate();

            // Make chartPanel able to receive key event.
            // So that we may use arrow left/right key to move around yellow
            // information boxes. We may also use up/down key to perform combo box
            // selection.
            this.chartPanel.setFocusable(true);
            this.chartPanel.requestFocus();

            // Handle zoom-in.
            addChangeListener(this.chartPanel);

            Dimension d = this.chartPanel.getSize();
            
            // setPreferredSize is to ensure that, this.pack will not resize 
            // dialog suddenly.
            this.chartPanel.setPreferredSize(d);
            
            // http://www.jfree.org/phpBB2/viewtopic.php?f=3&t=38098&p=92773#p92773
            // http://stackoverflow.com/questions/5420756/jfreechart-x-axis-scale-incorrect
            this.pack();
        }   // synchronized(jComboBox1)
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * Select next item in the combo box. Do nothing if the current selection is
     * the last item.
     */
    public void selectNextJComboBoxSelection() {
        int index = this.jComboBox1.getSelectedIndex();
        index++;
        if (index >= this.jComboBox1.getItemCount()) {
            return;
        }
        this.jComboBox1.setSelectedIndex(index);
    }

    /**
     * Select previous item in the combo box. Do nothing if the current 
     * selection is the first item.
     */
    public void selectPreviousJComboBoxSelection() {
        int index = this.jComboBox1.getSelectedIndex();
        index--;
        if (index < 0) {
            return;
        }
        this.jComboBox1.setSelectedIndex(index);
    }

    private void saveDimension() {
        org.yccheok.jstock.gui.Utils.toXML(this.getSize(), UserDataDirectory.Config.get() + UserDataFile.CashFlowChartJDialogXml.get());
    }

    private void loadDimension() {
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimension = org.yccheok.jstock.gui.Utils.fromXML(Dimension.class, UserDataDirectory.Config.get() + UserDataFile.CashFlowChartJDialogXml.get());
        if (dimension != null) {            
            setBounds((screenSize.width-(int)dimension.getWidth())/2, (screenSize.height-(int)dimension.getHeight())/2, (int)dimension.getWidth(), (int)dimension.getHeight());
        } else {
            // There is a bit hack here. This line of code should be used within
            // initComponents. However, currently, we are using pack. This is
            // caused by x-axis scaling problem may occur in certain case, if we
            // do not call pack. Hence, the hacking is, we will first call pack
            // (to resolve x-axis scaling problem), followed by setBounds.
            setBounds((screenSize.width-750)/2, (screenSize.height-500)/2, 750, 500);
        }
    }

    private void updateROITimeSeries() {
        final Currency localCurrency = org.yccheok.jstock.portfolio.Utils.getLocalCurrency();
        final boolean noCodeAddedToMonitor = this.realTimeStockMonitor.isEmpty();
        final List<Code> codesNeedToAddToRealTimeStockMonitor = new ArrayList<>();
        
        // Use local variables for thread safe.
        // I don't think we need local variables for thread safe purpose,
        // as we already have synchronized keyword. However, it gives no harm
        // for using local variables. We will just leave them.
        final ActivitySummary _ROISummary = this.ROISummary;
        final TimeSeries _ROITimeSeries = this.ROITimeSeries;

        double _totalROIValue = 0.0;
        for (int i = 0, count = _ROISummary.size(); i < count; i++) {
            final Activities activities = _ROISummary.get(i);
            double amount = 0.0;
            for (int j = 0, count2 = activities.size(); j < count2; j++) {
                final Activity activity = activities.get(j);
                final Activity.Type type = activity.getType();

                final StockInfo stockInfo = (StockInfo)activity.get(Activity.Param.StockInfo);
                double exchangeRate = org.yccheok.jstock.portfolio.Utils.getExchangeRate(this.portfolioManagementJPanel.getPortfolioRealTimeInfo(), localCurrency, stockInfo.code);
                
                if (type == Activity.Type.Buy) {
                    final double quantity = (Double)activity.get(Activity.Param.Quantity);                    
                    if (noCodeAddedToMonitor) {
                        // We might already have last price information in
                        // PortfolioManagementJPanel, we will still request
                        // stock monitor to provide continuous update.
                        codesNeedToAddToRealTimeStockMonitor.add(stockInfo.code);
                        // If PortfolioManagementJPanel already has last price
                        // information, just get it from there.
                        final double lastPrice = this.portfolioManagementJPanel.getStockPrice(stockInfo.code);
                        if (lastPrice != 0.0) {
                            this.codeToPrice.put(stockInfo.code, lastPrice);
                        } else {
                            this.lookUpCodes.add(stockInfo.code);
                        }
                    }
                    final Double price = this.codeToPrice.get(stockInfo.code);
                    if (price != null) {
                        amount += (price * quantity * exchangeRate);
                    }
                } else if (type == Activity.Type.Sell) {
                    amount += (activity.getAmount() * exchangeRate);
                } else if (type == Activity.Type.Dividend) {
                    double dividend = (activity.getAmount() * exchangeRate);
                    final Currency stockCurrency = org.yccheok.jstock.portfolio.Utils.getStockCurrency(this.portfolioManagementJPanel.getPortfolioRealTimeInfo(), stockInfo.code);
                    if (stockCurrency.isGBX() || stockCurrency.isZAC()) {
                        // Use will input dividend cash in GBP/ZAR instead of GBX/ZAC.
                        dividend = dividend * 100.0;
                    }

                    amount += dividend;
                } else {
                    assert(false);
                }
            }   // for (int j = 0, count2 = activities.size(); j < count2; j++)

            _totalROIValue += amount;

            final SimpleDate date = activities.getDate();
            final Date d = date.getTime();
            _ROITimeSeries.addOrUpdate(new Day(d), _totalROIValue);
        }   // for (int i = 0, count = _ROISummary.size(); i < count; i++)

        this.totalROIValue = _totalROIValue;

        // We cannot iterate over this.lookUpCodes.
        // realTimeStockMonitor's callback may remove lookUpCodes item during
        // iterating process.
        if (noCodeAddedToMonitor) {
            for (Code code : codesNeedToAddToRealTimeStockMonitor) {
                this.realTimeStockMonitor.addStockCode(code);
            }
            this.realTimeStockMonitor.startNewThreadsIfNecessary();
            this.realTimeStockMonitor.refresh();
            
            if (this.lookUpCodes.isEmpty()) {
                this.finishLookUpPrice = true;
            }            
        }
    }

    public PortfolioRealTimeInfo getPortfolioRealTimeInfo() {
        return this.portfolioManagementJPanel.getPortfolioRealTimeInfo();
    }
    
    private XYDataset createInvestDataset() {
        final Currency localCurrency = org.yccheok.jstock.portfolio.Utils.getLocalCurrency();
        
        final TimeSeries series = new TimeSeries(GUIBundle.getString("InvestmentFlowChartJDialog_Invest"));
        
        this.totalInvestValue = 0.0;
        for (int i = 0, count = this.investSummary.size(); i < count; i++) {
            final Activities activities = this.investSummary.get(i);
            double amount = 0.0;
            for (int j = 0, count2 = activities.size(); j < count2; j++) {
                final Activity activity = activities.get(j);
                final Activity.Type type = activity.getType();
                final StockInfo stockInfo = (StockInfo)activity.get(Activity.Param.StockInfo);
                double exchangeRate = org.yccheok.jstock.portfolio.Utils.getExchangeRate(this.portfolioManagementJPanel.getPortfolioRealTimeInfo(), localCurrency, stockInfo.code);
         
                if (type == Activity.Type.Buy) {
                    amount += (activity.getAmount() * exchangeRate);
                }
                else if (type == Activity.Type.Sell) {
                    amount -= (activity.getAmount() * exchangeRate);
                } else {
                    assert(false);
                }
            }   // for (int j = 0, count2 = activities.size(); j < count2; j++)

            this.totalInvestValue += amount;

            final SimpleDate date = activities.getDate();
            final Date d = date.getTime();
            series.add(new Day(d), this.totalInvestValue);

        }   // for (int i = 0, count = this.investSummary.size(); i < count; i++)

        return new TimeSeriesCollection(series);
    }

    // Synchronized against updateROITimeSeries.
    private synchronized JFreeChart createChart() {
        initSummaries(this.portfolioManagementJPanel);

        final XYDataset priceData = this.createInvestDataset();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            " ",
            GUIBundle.getString("InvestmentFlowChartJDialog_Date"),
            GUIBundle.getString("InvestmentFlowChartJDialog_Value"),
            priceData,
            true,       // create legend?
            true,       // generate tooltips?
            false       // generate URLs?
        );
        
        XYPlot plot = chart.getXYPlot();

        NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        final JStockOptions jStockOptions = JStock.instance().getJStockOptions();
        final String currencySymbol = jStockOptions.getCurrencySymbol(jStockOptions.getCountry());
        // Use apostrophes to escape currencySymbol. If currencySymbol contains
        // apostrophes, we may need to escape those by doubling them.
        //
        // 0 decimal place, to save up some display area.
        final NumberFormat currencyFormat = new DecimalFormat("'" + currencySymbol.replace("'", "''") + "'#,##0");
        rangeAxis1.setNumberFormatOverride(currencyFormat);
        
        plot.setRenderer(1, new StandardXYItemRenderer());
        this.ROITimeSeries = new TimeSeries(GUIBundle.getString("InvestmentFlowChartJDialog_ReturnOfInvestment"));
        plot.setDataset(1, new TimeSeriesCollection(this.ROITimeSeries));
        this.updateROITimeSeries();

        return chart;
    }

    private void initJComboBox() {
        final List<TransactionSummary> transactionSummaries = portfolioManagementJPanel.getTransactionSummariesFromPortfolios();
        final DividendSummary dividendSummary = portfolioManagementJPanel.getDividendSummary();

        for (TransactionSummary transactionSummary : transactionSummaries) {
            for (int i = 0, count = transactionSummary.getChildCount(); i < count; i++) {
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);
                StockInfo stockInfo = transaction.getStockInfo();
                if (stockInfos.contains(stockInfo) == false) {
                    stockInfos.add(stockInfo);
                }
            }
        }

        for (int i = 0, size = dividendSummary.size(); i < size; i++) {
            final Dividend dividend = dividendSummary.get(i);
            final StockInfo stockInfo = dividend.stockInfo;
            if (stockInfos.contains(stockInfo) == false) {
                stockInfos.add(stockInfo);
            }
        }

        // Ensure symbols are in alphabetical order.
        java.util.Collections.sort(stockInfos, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                return ((StockInfo)o1).symbol.toString().compareTo(((StockInfo)o2).symbol.toString());
            }

        });

        for (StockInfo stockInfo : stockInfos) {
            this.jComboBox1.addItem(stockInfo.symbol.toString());
        }
    }

    private void initSummaries(PortfolioManagementJPanel portfolioManagementJPanel) {
        final List<TransactionSummary> transactionSummaries = portfolioManagementJPanel.getTransactionSummariesFromPortfolios();
        final DividendSummary dividendSummary = portfolioManagementJPanel.getDividendSummary();
        final int selectedIndex = this.jComboBox1.getSelectedIndex();

        investSummary = new ActivitySummary();
        ROISummary = new ActivitySummary();
        
        final boolean isFeeCalculationEnabled = JStock.instance().getJStockOptions().isFeeCalculationEnabled();
        
        for (TransactionSummary transactionSummary : transactionSummaries) {
            final int count = transactionSummary.getChildCount();
            for (int i = 0; i < count; i++) {
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);
                if (selectedIndex != 0) {
                    // selectedIndex - 1, as the first item in combo box is "All Stock(s)".
                    final Code code = this.stockInfos.get(selectedIndex - 1).code;
                    if (false == transaction.getStockInfo().code.equals(code)) {
                        continue;
                    }
                }
                Contract.Type type = transaction.getType();
                final StockInfo stockInfo = transaction.getStockInfo();

                if (type == Contract.Type.Buy) {
                    final Activity activity = new Activity.Builder(Activity.Type.Buy, 
                            isFeeCalculationEnabled ? transaction.getNetTotal() : transaction.getTotal()).
                            put(Activity.Param.StockInfo, stockInfo).
                            put(Activity.Param.Quantity, transaction.getQuantity()).
                            build();
                    this.ROISummary.add(transaction.getDate(), activity);
                    this.investSummary.add(transaction.getDate(), activity);
                } else if (type == Contract.Type.Sell) {
                    final Activity activity0 = new Activity.Builder(Activity.Type.Buy, 
                            isFeeCalculationEnabled ? transaction.getNetReferenceTotal() : transaction.getReferenceTotal()).
                            put(Activity.Param.StockInfo, stockInfo).
                            put(Activity.Param.Quantity, transaction.getQuantity()).
                            build();
                    this.investSummary.add(transaction.getReferenceDate(), activity0);
                    final Activity activity1 = new Activity.Builder(Activity.Type.Sell, 
                            isFeeCalculationEnabled ? transaction.getNetTotal() : transaction.getTotal()).
                            put(Activity.Param.StockInfo, stockInfo).
                            put(Activity.Param.Quantity, transaction.getQuantity()).
                            build();
                    this.ROISummary.add(transaction.getDate(), activity1);
                } else {
                    throw new java.lang.UnsupportedOperationException("Unsupported contract type " + type);
                }
            }
        }

        for (int i = 0, count = dividendSummary.size(); i < count; i++) {
            final Dividend dividend = dividendSummary.get(i);

            if (selectedIndex != 0) {
                // selectedIndex - 1, as the first item in combo box is "All Stock(s)".
                final Code code = this.stockInfos.get(selectedIndex - 1).code;
                if (false == dividend.stockInfo.code.equals(code)) {
                    continue;
                }
            }

            final Activity activity = new Activity.Builder(Activity.Type.Dividend, dividend.amount).
                    put(Activity.Param.StockInfo, dividend.stockInfo).build();
            this.ROISummary.add(dividend.date, activity);
        }
        
        this.investSummary.ensureSorted();
        this.ROISummary.ensureSorted();
    }

    private void initRealTimeStockMonitor() {
        final RealTimeStockMonitor oldRealTimeStockMonitor = this.realTimeStockMonitor;
        if (oldRealTimeStockMonitor != null) {            
            org.yccheok.jstock.gui.Utils.getZoombiePool().execute(new Runnable() {
                @Override
                public void run() {
                    log.info("Prepare to shut down " + oldRealTimeStockMonitor + "...");
                    oldRealTimeStockMonitor.clearStockCodes();
                    oldRealTimeStockMonitor.dettachAll();
                    oldRealTimeStockMonitor.stop();
                    log.info("Shut down " + oldRealTimeStockMonitor + " peacefully.");
                }
            });
        }

        this.realTimeStockMonitor = new RealTimeStockMonitor(
                Constants.REAL_TIME_STOCK_MONITOR_MAX_THREAD, 
                Constants.REAL_TIME_STOCK_MONITOR_MAX_STOCK_SIZE_PER_SCAN, 
                JStock.instance().getJStockOptions().getScanningSpeed());

        this.realTimeStockMonitor.attach(this);
    }

    @Override
    public void update(RealTimeStockMonitor subject,RealTimeStockMonitor.Result result) {
        SwingUtilities.invokeLater(() -> {
            _update(subject, result);
        });
    }
    
    private void _update(RealTimeStockMonitor subject,RealTimeStockMonitor.Result result) {    
        for (Stock stock : result.stocks) {
            this.codeToPrice.put(stock.code, stock.getLastPrice());
            this.lookUpCodes.remove(stock.code);
        }

        // Mutual exclusive with combo box event. So that user thread and
        // event dispatching thread will not be accessing the same variables
        // through updateROITimeSeries, updateInvestPoint and updateROIPoint
        // simultaneously.
        synchronized(jComboBox1) {
            final double beforeUpdateTotalROIValue = this.totalROIValue;
            // Calling updateROITimeSeries will update this.totalROIValue.
            this.updateROITimeSeries();
            if (this.totalROIValue != beforeUpdateTotalROIValue) {
                // We will update yellow information boxes, if there is update
                // in ROI value.
                investmentFlowLayerUI.updateInvestPoint();
                investmentFlowLayerUI.updateROIPoint();
            }
        }

        if (this.lookUpCodes.isEmpty()) {
            this.finishLookUpPrice = true;
            // Clear the busy message box drawn by JXLayer.
            this.investmentFlowLayerUI.setDirty(true);
            // Do I need to stop myself. Will user like to have continued
            // real-time update?
            // this.initRealTimeStockMonitor();
        }
    }

    /**
     * Returns current selected combo box string.
     *
     * @return current selected combo box string
     */
    public String getCurrentSelectedString() {
        if (this.jComboBox1.getSelectedIndex() == 0) {
            // Special case. We are not interested to display string for
            // "All Stock(s)".
            return "";
        }
        return this.jComboBox1.getSelectedItem().toString();
    }

    public double getTotalInvestValue() {
        return this.totalInvestValue;
    }

    public double getTotalROIValue() {
        return this.totalROIValue;
    }

    public boolean isFinishLookUpPrice() {
        return this.finishLookUpPrice;
    }

    public double getStockPrice(Code code) {
        final Double value = this.codeToPrice.get(code);
        if (value == null) {
            return 0.0;
        }
        return value;
    }
    
    public Activities getInvestActivities(int index) {
        return this.investSummary.get(index);
    }

    public Activities getROIActivities(int index) {
        return this.ROISummary.get(index);
    }

    private ComboBoxModel getComboBoxModel() {
        return new javax.swing.DefaultComboBoxModel(new String[] { GUIBundle.getString("InvestmentFlowChartJDialog_AllStock(s)") });
    }

    private final List<StockInfo> stockInfos = new ArrayList<>();

    /* How much I had invested. */
    /* Contains Buy, Sell. When Sell, it will pull down your investment value. */
    private ActivitySummary investSummary = new ActivitySummary();
    /* Return of investment. */
    /* Contains Buy, Sell and Dividend. */
    private ActivitySummary ROISummary = new ActivitySummary();

    /* For ROI charting information. */
    private volatile TimeSeries ROITimeSeries = null;
    private double totalROIValue = 0.0;

    /* For Invest charting information. */
    private double totalInvestValue = 0.0;

    private ChartPanel chartPanel;
    private final PortfolioManagementJPanel portfolioManagementJPanel;

    /* For real time stock information. */
    private RealTimeStockMonitor realTimeStockMonitor;    
    private final Map<Code, Double> codeToPrice = new ConcurrentHashMap<>();
    /* Whether we had finished scan through all the BUY stocks. */
    private volatile boolean finishLookUpPrice = false;
    // Use in conjuction with realTimeStockMonitor, to obtain current BUY stocks
    // price.
    private final Set<Code> lookUpCodes = new HashSet<>();
    
    /* Overlay layer. */
    private InvestmentFlowLayerUI<ChartPanel> investmentFlowLayerUI;

    private org.jdesktop.jxlayer.JXLayer<ChartPanel> layer;

    private static final Log log = LogFactory.getLog(InvestmentFlowChartJDialog.class);
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JComboBox jComboBox1 = new javax.swing.JComboBox();
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
