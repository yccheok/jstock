/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2011 Yan Cheng CHEOK <yccheok@yahoo.com>
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
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ComboBoxModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.labels.CustomXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
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
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.gui.PortfolioManagementJPanel;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.Activities;
import org.yccheok.jstock.portfolio.Activity;
import org.yccheok.jstock.portfolio.ActivitySummary;
import org.yccheok.jstock.portfolio.Contract;
import org.yccheok.jstock.portfolio.Dividend;
import org.yccheok.jstock.portfolio.DividendSummary;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;


/**
 *
 * @author yccheok
 */
public class InvestmentFlowChartJDialog extends javax.swing.JDialog implements Observer<RealTimeStockMonitor, java.util.List<Stock>> {

    /** Creates new form InvestmentFlowChartJDialog */
    public InvestmentFlowChartJDialog(java.awt.Frame parent, boolean modal, PortfolioManagementJPanel portfolioManagementJPanel) {
        super(parent, modal);
        initComponents();

        // Initialize main data structures.
        this.portfolioManagementJPanel = portfolioManagementJPanel;
        
        initJComboBox();

        // We need a stock price monitor, to update all the stocks value.
        initRealTimeStockMonitor();

        initSummaries(this.portfolioManagementJPanel);

        // Renderer must be assigned before calling createOrUpdateStockTimeSeries.
        // createOrUpdateStockTimeSeries is going to access renderer at index 1.
        this.ROIRenderer = new StandardXYItemRenderer();

        final JFreeChart freeChart = createChart();
        org.yccheok.jstock.charting.Utils.applyChartTheme(freeChart);
        this.chartPanel = new ChartPanel(freeChart, true, true, true, true, true);

        // Make chartPanel able to receive key event.
        // So that we may use arrow key to move around yellow information boxes.
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
        this.chartPanel.getChart().addChangeListener(new ChartChangeListener() {
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
        jComboBox1 = new javax.swing.JComboBox();
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

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-750)/2, (screenSize.height-500)/2, 750, 500);
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
        this.lookUpCodes = null;
        this.ROITimeSeries = null;
        initSummaries(this.portfolioManagementJPanel);
        final JFreeChart freeChart = createChart();
        org.yccheok.jstock.charting.Utils.applyChartTheme(freeChart);
        this.chartPanel = new ChartPanel(freeChart, true, true, true, true, true);

        // Make chartPanel able to receive key event.
        // So that we may use arrow key to move around yellow information boxes.
        this.chartPanel.setFocusable(true);
        this.chartPanel.requestFocus();

        getContentPane().remove(this.layer);

        this.layer = new org.jdesktop.jxlayer.JXLayer<ChartPanel>(this.chartPanel);
        this.investmentFlowLayerUI = new InvestmentFlowLayerUI<ChartPanel>(this);
        layer.setUI(this.investmentFlowLayerUI);

        getContentPane().add(layer, java.awt.BorderLayout.CENTER);
        getContentPane().invalidate();
        getContentPane().validate();
        this.chartPanel.updateUI();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void saveDimension() {
        org.yccheok.jstock.gui.Utils.toXML(this.getSize(), org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "cashflowchartjdialog.xml");
    }

    private void loadDimension() {
        Dimension dimension = org.yccheok.jstock.gui.Utils.fromXML(Dimension.class, org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "config" + File.separator + "cashflowchartjdialog.xml");
        if (dimension != null) {
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            setBounds((screenSize.width-(int)dimension.getWidth())/2, (screenSize.height-(int)dimension.getHeight())/2, (int)dimension.getWidth(), (int)dimension.getHeight());
        }
    }

    private TimeSeries createOrUpdateROITimeSeries() {
        if (this.ROITimeSeries == null) {
            this.ROITimeSeries = new TimeSeries(GUIBundle.getString("InvestmentFlowChartJDialog_ReturnOfInvestment"));
        }

        final CustomXYToolTipGenerator stock_ttg = new CustomXYToolTipGenerator();
        final ArrayList<String> toolTips = new ArrayList<String>();


        final boolean firstTime = (this.lookUpCodes == null);
        if (firstTime) {
            this.lookUpCodes = new ArrayList<Code>();
        }

        double _totalROIValue = 0.0;
        for (int i = 0, count = this.ROISummary.size(); i < count; i++) {
            final Activities activities = this.ROISummary.get(i);
            double amount = 0.0;
            for (int j = 0, count2 = activities.size(); j < count2; j++) {
                final Activity activity = activities.get(j);
                final Activity.Type type = activity.getType();

                if (type == Activity.Type.Buy) {
                    final int quantity = (Integer)activity.get(Activity.Param.Quantity);
                    final Stock stock = (Stock)activity.get(Activity.Param.Stock);
                    if (firstTime) {
                        if (this.lookUpCodes.contains(stock.getCode()) == false) {
                            this.lookUpCodes.add(stock.getCode());
                        }
                    }
                    final Double price = this.codeToPrice.get(stock.getCode());
                    if (price != null) {
                        amount += (price * quantity);
                    }
                }
                if (type == Activity.Type.Sell) {
                    amount += activity.getAmount();
                }
                else if (type == Activity.Type.Dividend) {
                    amount += activity.getAmount();
                }
                else {
                    assert(false);
                }
            }   // for (int j = 0, count2 = activities.size(); j < count2; j++)

            if (MainFrame.getInstance().getJStockOptions().isPenceToPoundConversionEnabled() == true) {
                amount = amount / 100.0;
            }

            _totalROIValue += amount;

            final SimpleDate date = activities.getDate();
            final Date d = date.getTime();
            this.ROITimeSeries.addOrUpdate(new Day(d), _totalROIValue);
            final SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy");
            final java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
            final String tips = "<html>Return: (" + dateFormat.format(d) + ", " + numberFormat.format(amount) + ")<br> " + activities.toSummary() + "</html>";
            toolTips.add(tips);
        }   // for (int i = 0, count = this.ROISummary.size(); i < count; i++)

        this.totalROIValue = _totalROIValue;

        stock_ttg.addToolTipSeries(toolTips);
        this.ROIRenderer.setBaseToolTipGenerator(stock_ttg);

        // We cannot iterate over this.lookUpCodes.
        // realTimeStockMonitor's callback may remove lookUpCodes item during
        // iterating process.
        if (firstTime) {
            final List<Code> codes = new ArrayList<Code>(this.lookUpCodes);
            for (Code code : codes) {
                this.realTimeStockMonitor.addStockCode(code);
            }
        }
        return this.ROITimeSeries;
    }

    private XYDataset createInvestDataset() {
        final TimeSeries series = new TimeSeries(GUIBundle.getString("InvestmentFlowChartJDialog_Invest"));
        final ArrayList<String> toolTips = new ArrayList<String>();
        
        this.totalInvestValue = 0.0;
        for (int i = 0, count = this.investSummary.size(); i < count; i++) {
            final Activities activities = this.investSummary.get(i);
            double amount = 0.0;
            for (int j = 0, count2 = activities.size(); j < count2; j++) {
                final Activity activity = activities.get(j);
                final Activity.Type type = activity.getType();

                if (type == Activity.Type.Buy) {
                    amount += activity.getAmount();
                }
                else if (type == Activity.Type.Sell) {
                    amount -= activity.getAmount();
                }
                else {
                    assert(false);
                }
            }   // for (int j = 0, count2 = activities.size(); j < count2; j++)

            if (MainFrame.getInstance().getJStockOptions().isPenceToPoundConversionEnabled() == true) {
                amount = amount / 100.0;
            }
            
            this.totalInvestValue += amount;

            final SimpleDate date = activities.getDate();
            final Date d = date.getTime();
            series.add(new Day(d), this.totalInvestValue);
            final SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy");
            final java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
            final String tips = "<html>Invest: (" + dateFormat.format(d) + ", " + numberFormat.format(amount) + ")<br> " + activities.toSummary() + "</html>";
            toolTips.add(tips);

        }   // for (int i = 0, count = this.investSummary.size(); i < count; i++)

        cash_ttg.addToolTipSeries(toolTips);
        return new TimeSeriesCollection(series);
    }

    private JFreeChart createChart() {
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
        final JStockOptions jStockOptions = MainFrame.getInstance().getJStockOptions();
        final String currencySymbol = jStockOptions.getCurrencySymbol(jStockOptions.getCountry());
        // Use apostrophes to escape currencySymbol. If currencySymbol contains
        // apostrophes, we may need to escape those by doubling them.
        //
        // 0 decimal place, to save up some display area.
        final NumberFormat currencyFormat = new DecimalFormat("'" + currencySymbol.replace("'", "''") + "'#,##0");
        rangeAxis1.setNumberFormatOverride(currencyFormat);

        XYItemRenderer renderer0 = plot.getRenderer();
        renderer0.setBaseToolTipGenerator(cash_ttg);
        
        plot.setRenderer(1, this.ROIRenderer);
        plot.setDataset(1, new TimeSeriesCollection(this.createOrUpdateROITimeSeries()));

        return chart;
    }

    private void initJComboBox() {
        final List<TransactionSummary> transactionSummaries = portfolioManagementJPanel.getTransactionSummariesFromPortfolios();
        final DividendSummary dividendSummary = portfolioManagementJPanel.getDividendSummary();

        for (TransactionSummary transactionSummary : transactionSummaries) {
            for (int i = 0, count = transactionSummary.getChildCount(); i < count; i++) {
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);
                final Contract contract = transaction.getContract();
                StockInfo stockInfo = new StockInfo(contract.getStock().getCode(), contract.getStock().getSymbol());
                if (stockInfos.contains(stockInfo) == false) {
                    stockInfos.add(stockInfo);
                }
            }
        }

        for (int i = 0, size = dividendSummary.size(); i < size; i++) {
            final Dividend dividend = dividendSummary.get(i);
            final Stock stock = dividend.getStock();
            final StockInfo stockInfo = new StockInfo(stock.getCode(), stock.getSymbol());
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
        
        for (TransactionSummary transactionSummary : transactionSummaries) {
            final int count = transactionSummary.getChildCount();
            for (int i = 0; i < count; i++) {
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(i);
                final Contract contract = transaction.getContract();
                if (selectedIndex != 0) {
                    // selectedIndex - 1, as the first item in combo box is "All Stock(s)".
                    final Code code = this.stockInfos.get(selectedIndex - 1).code;
                    if (false == contract.getStock().getCode().equals(code)) {
                        continue;
                    }
                }
                Contract.Type type = contract.getType();
                if (type == Contract.Type.Buy) {
                    final Activity activity = new Activity.Builder(Activity.Type.Buy, transaction.getNetTotal()).
                            put(Activity.Param.Stock, contract.getStock()).
                            put(Activity.Param.Quantity, contract.getQuantity()).
                            build();
                    this.ROISummary.add(contract.getDate(), activity);
                    this.investSummary.add(contract.getDate(), activity);
                }
                else if (type == Contract.Type.Sell) {
                    final Activity activity0 = new Activity.Builder(Activity.Type.Buy, transaction.getReferenceTotal()).
                            put(Activity.Param.Stock, contract.getStock()).
                            put(Activity.Param.Quantity, contract.getQuantity()).
                            build();
                    this.investSummary.add(contract.getReferenceDate(), activity0);
                    final Activity activity1 = new Activity.Builder(Activity.Type.Sell, transaction.getNetTotal()).
                            put(Activity.Param.Stock, contract.getStock()).
                            put(Activity.Param.Quantity, contract.getQuantity()).
                            build();
                    this.ROISummary.add(contract.getDate(), activity1);
                }
                else {
                    throw new java.lang.UnsupportedOperationException("Unsupported contract type " + type);
                }
            }
        }

        for (int i = 0, count = dividendSummary.size(); i < count; i++) {
            final Dividend dividend = dividendSummary.get(i);

            if (selectedIndex != 0) {
                // selectedIndex - 1, as the first item in combo box is "All Stock(s)".
                final Code code = this.stockInfos.get(selectedIndex - 1).code;
                if (false == dividend.getStock().getCode().equals(code)) {
                    continue;
                }
            }

            final Activity activity = new Activity.Builder(Activity.Type.Dividend, dividend.getAmount()).
                    put(Activity.Param.Stock, dividend.getStock()).build();
            this.ROISummary.add(dividend.getDate(), activity);
        }
    }

    private void initRealTimeStockMonitor() {
        if (this.realTimeStockMonitor != null) {
            final RealTimeStockMonitor oldRealTimeStockMonitor = this.realTimeStockMonitor;
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

        this.realTimeStockMonitor = new RealTimeStockMonitor(4, 20, MainFrame.getInstance().getJStockOptions().getScanningSpeed());
        this.realTimeStockMonitor.setStockServerFactories(MainFrame.getInstance().getStockServerFactories());

        this.realTimeStockMonitor.attach(this);
    }

    @Override
    public void update(RealTimeStockMonitor subject, List<Stock> arg) {
        for (Stock stock : arg) {
            this.codeToPrice.put(stock.getCode(), stock.getLastPrice());
            this.lookUpCodes.remove(stock.getCode());
        }
        this.createOrUpdateROITimeSeries();

        if (this.lookUpCodes.isEmpty()) {
            this.finishLookUpPrice = true;
            // Clear the busy message box drawn by JXLayer.
            this.investmentFlowLayerUI.setDirty(true);
            // Do I need to stop myself. Will user like to have continued
            // real-time update?
            // this.initRealTimeStockMonitor();
        }
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

    private final List<StockInfo> stockInfos = new ArrayList<StockInfo>();

    /* How much I had invested. */
    /* Contains Buy, Sell. When Sell, it will pull down your investment value. */
    private ActivitySummary investSummary = new ActivitySummary();
    /* Return of investment. */
    /* Contains Buy, Sell and Dividend. */
    private ActivitySummary ROISummary = new ActivitySummary();

    /* For ROI charting information. */
    private final XYItemRenderer ROIRenderer;
    private volatile TimeSeries ROITimeSeries;
    private double totalROIValue = 0.0;

    /* For Invest charting information. */
    private double totalInvestValue = 0.0;

    private ChartPanel chartPanel;
    private final PortfolioManagementJPanel portfolioManagementJPanel;
    private final CustomXYToolTipGenerator cash_ttg = new CustomXYToolTipGenerator();

    /* For real time stock information. */
    private RealTimeStockMonitor realTimeStockMonitor;    
    private final Map<Code, Double> codeToPrice = new ConcurrentHashMap<Code, Double>();
    /* Whether we had finished scan through all the BUY stocks. */
    private volatile boolean finishLookUpPrice = false;
    private volatile List<Code> lookUpCodes = null;
    
    /* Overlay layer. */
    private InvestmentFlowLayerUI<ChartPanel> investmentFlowLayerUI;

    private org.jdesktop.jxlayer.JXLayer<ChartPanel> layer;

    private static final Log log = LogFactory.getLog(InvestmentFlowChartJDialog.class);
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
