/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.SwingUtilities;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.ui.RectangleEdge;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.gui.Utils;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;
import org.yccheok.jstock.portfolio.Activities;
import org.yccheok.jstock.portfolio.Activity;

/**
 *
 * @author yccheok
 */
public class InvestmentFlowLayerUI<V extends javax.swing.JComponent> extends AbstractLayerUI<V> {

    private Point2D investPoint = null;
    private int investPointIndex = -1;
    private Point2D ROIPoint = null;
    private int ROIPointIndex = -1;

    private final Rectangle2D drawArea = new Rectangle();
    
    private final InvestmentFlowChartJDialog investmentFlowChartJDialog;

    private static final Color COLOR_BLUE = new Color(85, 85, 255);
    private static final Color COLOR_RED = new Color(255, 85, 85);
    private static final Color COLOR_RED_BORDER = new Color(255, 85, 85);
    private static final Color COLOR_RED_BACKGROUND = new Color(255, 255, 153);
    private static final Color COLOR_BLUE_BORDER = new Color(85, 85, 255);
    private static final Color COLOR_BLUE_BACKGROUND = new Color(255, 255, 153);
    private static final Color COLOR_BACKGROUND = new Color(255, 255, 153);
    private static final Color COLOR_BORDER = new Color(255, 204, 0);

    /* Will be updated by updateROIInformationBox. */
    private final List<String> ROIValues = new ArrayList<String>();
    private final List<String> ROIParams = new ArrayList<String>();
    private final Rectangle2D ROIRect = new Rectangle();
    private double totalROIValue = 0.0;

    /* Will be updated by updateInvestInformationBox. */
    private final List<String> investValues = new ArrayList<String>();
    private final List<String> investParams = new ArrayList<String>();
    private final Rectangle2D investRect = new Rectangle();
    private double totalInvestValue = 0.0;

    public InvestmentFlowLayerUI(InvestmentFlowChartJDialog cashFlowChartJDialog) {
        this.investmentFlowChartJDialog = cashFlowChartJDialog;
    }

    private void drawTitle(Graphics2D g2) {
        final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        final Color oldColor = g2.getColor();
        final Font oldFont = g2.getFont();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final Font titleFont = oldFont.deriveFont(oldFont.getStyle() | Font.BOLD, (float)oldFont.getSize() * 1.5f);

        final int margin = 5;

        final FontMetrics titleFontMetrics = g2.getFontMetrics(titleFont);
        final FontMetrics oldFontMetrics = g2.getFontMetrics(oldFont);
        final java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        final String invest = org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(this.investmentFlowChartJDialog.getTotalInvestValue());
        final String roi = org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(this.investmentFlowChartJDialog.getTotalROIValue());
        final double gain = this.investmentFlowChartJDialog.getTotalROIValue() - this.investmentFlowChartJDialog.getTotalInvestValue();
        final double percentage = this.investmentFlowChartJDialog.getTotalInvestValue() > 0.0 ? gain / this.investmentFlowChartJDialog.getTotalInvestValue() * 100.0 : 0.0;
        final String gain_str = org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(gain);
        final String percentage_str = numberFormat.format(percentage);

        final String INVEST = GUIBundle.getString("InvestmentFlowLayerUI_Invest");
        final String RETURN = GUIBundle.getString("InvestmentFlowLayerUI_Return");
        final String GAIN = GUIBundle.getString("InvestmentFlowLayerUI_Gain");
        final String LOSS = GUIBundle.getString("InvestmentFlowLayerUI_Loss");

        final int string_width = oldFontMetrics.stringWidth(INVEST + ": ") + titleFontMetrics.stringWidth(invest + " ") +
                oldFontMetrics.stringWidth(RETURN + ": ") + titleFontMetrics.stringWidth(roi + " ") +
                oldFontMetrics.stringWidth((gain >= 0 ? GAIN : LOSS) + ": ") + titleFontMetrics.stringWidth(gain_str + " (" + percentage_str + "%)");

        int x = (int)(this.investmentFlowChartJDialog.getChartPanel().getWidth() - string_width) >> 1;
        final int y = margin + titleFontMetrics.getAscent();

        g2.setFont(oldFont);
        g2.drawString(INVEST + ": ", x, y);
        x += oldFontMetrics.stringWidth(INVEST + ": ");
        g2.setFont(titleFont);
        g2.drawString(invest + " ", x, y);
        x += titleFontMetrics.stringWidth(invest + " ");
        g2.setFont(oldFont);
        g2.drawString(RETURN + ": ", x, y);
        x += oldFontMetrics.stringWidth(RETURN + ": ");
        g2.setFont(titleFont);
        g2.drawString(roi + " ", x, y);
        x += titleFontMetrics.stringWidth(roi + " ");
        g2.setFont(oldFont);
        if (gain >= 0) {
            if (gain > 0) {
                if (org.yccheok.jstock.engine.Utils.isFallBelowAndRiseAboveColorReverse()) {
                    g2.setColor(MainFrame.getInstance().getJStockOptions().getLowerNumericalValueForegroundColor());
                } else {
                    g2.setColor(MainFrame.getInstance().getJStockOptions().getHigherNumericalValueForegroundColor());
                }
            }
            g2.drawString(GAIN + ": ", x, y);
            x += oldFontMetrics.stringWidth(GAIN + ": ");
        }
        else {
            if (org.yccheok.jstock.engine.Utils.isFallBelowAndRiseAboveColorReverse()) {
                g2.setColor(MainFrame.getInstance().getJStockOptions().getHigherNumericalValueForegroundColor());
            } else {
                g2.setColor(MainFrame.getInstance().getJStockOptions().getLowerNumericalValueForegroundColor());
            }
            g2.drawString(LOSS + ": ", x, y);
            x += oldFontMetrics.stringWidth(GAIN + ": ");
        }
        g2.setFont(titleFont);
        g2.drawString(gain_str + " (" + percentage_str + "%)", x, y);

        g2.setColor(oldColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
        g2.setFont(oldFont);
    }

    private void drawInformationBox(
            Graphics2D g2, Activities activities, Rectangle2D rect,
            List<String> params, List<String> values, String totalParam, double totalValue,
            Color background_color, Color border_color) {
        final Font oldFont = g2.getFont();
        final Font paramFont = oldFont;
        final FontMetrics paramFontMetrics = g2.getFontMetrics(paramFont);
        final Font valueFont = oldFont.deriveFont(oldFont.getStyle() | Font.BOLD, (float)oldFont.getSize() + 1);
        final FontMetrics valueFontMetrics = g2.getFontMetrics(valueFont);
        final Font dateFont = oldFont.deriveFont((float)oldFont.getSize() - 1);
        final FontMetrics dateFontMetrics = g2.getFontMetrics(dateFont);
        
        final int x = (int)rect.getX();
        final int y  = (int)rect.getY();
        final int width = (int)rect.getWidth();
        final int height = (int)rect.getHeight();
        
        final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        final Composite oldComposite = g2.getComposite();
        final Color oldColor = g2.getColor();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(border_color);
        g2.drawRoundRect(x - 1, y - 1, width + 1, height + 1, 15, 15);
        g2.setColor(background_color);
        g2.setComposite(Utils.makeComposite(0.75f));
        g2.fillRoundRect(x, y, width, height, 15, 15);
        g2.setComposite(oldComposite);
        g2.setColor(oldColor);

        final Date date =  activities.getDate().getTime();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        final String dateString = simpleDateFormat.format(date);

        final int padding = 5;

        int yy = y  + padding + dateFontMetrics.getAscent();
        g2.setFont(dateFont);
        g2.setColor(COLOR_BLUE);
        g2.drawString(dateString,
                ((width - dateFontMetrics.stringWidth(dateString)) >> 1) + x,
                yy);

        int index = 0;
        final int dateInfoHeightMargin = 5;
        final int infoTotalHeightMargin = 5;
        final int paramValueHeightMargin = 0;

        yy += dateFontMetrics.getDescent() + dateInfoHeightMargin + Math.max(paramFontMetrics.getAscent(), valueFontMetrics.getAscent());
        for (String param : params) {
            final String value = values.get(index++);
            g2.setColor(Color.BLACK);
            g2.setFont(paramFont);
            g2.drawString(param + ":",
                padding + x,
                yy);
            g2.setFont(valueFont);
            g2.drawString(value,
                width - padding - valueFontMetrics.stringWidth(value) + x,
                yy);
            // Same as yy += valueFontMetrics.getDescent() + paramValueHeightMargin + valueFontMetrics.getAscent()
            yy += paramValueHeightMargin + Math.max(paramFontMetrics.getHeight(), valueFontMetrics.getHeight());
        }

        if (values.size() > 1) {
            yy -= paramValueHeightMargin;
            yy += infoTotalHeightMargin;
            if (totalValue > 0.0) {
                g2.setColor(JStockOptions.DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR);
            }
            else if (totalValue < 0.0) {
                g2.setColor(JStockOptions.DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR);
            }

            g2.setFont(paramFont);
            g2.drawString(totalParam + ":",
                padding + x,
                yy);
            g2.setFont(valueFont);
            final String totalValueStr = org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(totalValue);
            g2.drawString(totalValueStr,
                width - padding - valueFontMetrics.stringWidth(totalValueStr) + x,
                yy);
        }

        g2.setColor(oldColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
        g2.setFont(oldFont);
    }

    private void updateROIInformationBox(Graphics2D g2) {
        final Font oldFont = g2.getFont();
        final Font paramFont = oldFont;
        final FontMetrics paramFontMetrics = g2.getFontMetrics(paramFont);
        final Font valueFont = oldFont.deriveFont(oldFont.getStyle() | Font.BOLD, (float)oldFont.getSize() + 1);
        final FontMetrics valueFontMetrics = g2.getFontMetrics(valueFont);
        final Font dateFont = oldFont.deriveFont((float)oldFont.getSize() - 1);
        final FontMetrics dateFontMetrics = g2.getFontMetrics(dateFont);

        final Activities activities = this.investmentFlowChartJDialog.getROIActivities(this.ROIPointIndex);

        this.ROIValues.clear();
        this.ROIParams.clear();
        this.totalROIValue = 0.0;

        for (int i = 0, size = activities.size(); i < size; i++) {
            final Activity activity = activities.get(i);
            // Buy, Sell or Dividend only.
            if (activity.getType() == Activity.Type.Buy) {
                final int quantity = (Integer)activity.get(Activity.Param.Quantity);
                final Stock stock = (Stock)activity.get(Activity.Param.Stock);
                this.ROIParams.add(GUIBundle.getString("InvestmentFlowLayerUI_Own") + " " + quantity + " " + stock.getSymbol());
                final double amount = quantity * this.investmentFlowChartJDialog.getStockPrice(stock.getCode());
                this.totalROIValue += amount;
                this.ROIValues.add(org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(amount));
            }
            else if (activity.getType() == Activity.Type.Sell) {
                final int quantity = (Integer)activity.get(Activity.Param.Quantity);
                final Stock stock = (Stock)activity.get(Activity.Param.Stock);
                this.ROIParams.add(activity.getType() + " " + quantity + " " + stock.getSymbol());
                this.totalROIValue += activity.getAmount();
                this.ROIValues.add(org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(activity.getAmount()));
            }
            else if (activity.getType() == Activity.Type.Dividend) {
                final Stock stock = (Stock)activity.get(Activity.Param.Stock);
                this.ROIParams.add(activity.getType() + " " + stock.getSymbol());
                this.totalROIValue += activity.getAmount();
                this.ROIValues.add(org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(activity.getAmount()));
            }
            else {
                assert(false);
            }
        }

        final boolean isTotalNeeded = this.ROIParams.size() > 1;
        final String totalParam = GUIBundle.getString("InvestmentFlowLayerUI_Total_Return");
        final String totalValue = org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(this.totalROIValue);
        /* This is the height for "total" information. */
        int totalHeight = 0;

        assert(this.ROIParams.size() == this.ROIValues.size());

        int index = 0;
        final int paramValueWidthMargin = 10;
        final int paramValueHeightMargin = 0;
        int maxInfoWidth = -1;
        // paramFontMetrics will always "smaller" than valueFontMetrics.
        int totalInfoHeight = Math.max(paramFontMetrics.getHeight(), valueFontMetrics.getHeight()) * this.ROIValues.size() + paramValueHeightMargin * (this.ROIValues.size() - 1);
        for (String param : this.ROIParams) {
            final String value = this.ROIValues.get(index++);
            final int paramStringWidth = paramFontMetrics.stringWidth(param + ":") + paramValueWidthMargin + valueFontMetrics.stringWidth(value);
            if (maxInfoWidth < paramStringWidth) {
                maxInfoWidth = paramStringWidth;
            }
        }

        if (isTotalNeeded) {
            final int tmp = paramFontMetrics.stringWidth(totalParam + ":") + paramValueWidthMargin + valueFontMetrics.stringWidth(totalValue);
            if (maxInfoWidth < tmp) {
                maxInfoWidth = tmp;
            }
            totalHeight = Math.max(paramFontMetrics.getHeight(), valueFontMetrics.getHeight());
        }

        final Date date =  activities.getDate().getTime();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        final String dateString = simpleDateFormat.format(date);
        final int dateStringWidth = dateFontMetrics.stringWidth(dateString);
        final int dateStringHeight = dateFontMetrics.getHeight();
        final int maxStringWidth = Math.max(dateStringWidth, maxInfoWidth);
        final int dateInfoHeightMargin = 5;
        final int infoTotalHeightMargin = 5;
        final int maxStringHeight = isTotalNeeded ?
                                    (dateStringHeight + dateInfoHeightMargin + totalInfoHeight + infoTotalHeightMargin + totalHeight) :
                                    (dateStringHeight + dateInfoHeightMargin + totalInfoHeight);

        // Now, We have a pretty good information on maxStringWidth and maxStringHeight.

        final int padding = 5;
        final int boxPointMargin = 8;
        final int width = maxStringWidth + (padding << 1);
        final int height = maxStringHeight + (padding << 1);

        final int borderWidth = width + 2;
        final int borderHeight = height + 2;
        // On left side of the ball.
        final double suggestedBorderX = this.ROIPoint.getX() - borderWidth - boxPointMargin;
        final double suggestedBorderY = this.ROIPoint.getY() - (borderHeight >> 1);
        final double bestBorderX = suggestedBorderX > this.drawArea.getX() ?
                        (suggestedBorderX + borderWidth) < (this.drawArea.getX() + this.drawArea.getWidth()) ? suggestedBorderX : this.drawArea.getX() + this.drawArea.getWidth() - borderWidth - boxPointMargin :
                        this.ROIPoint.getX() + boxPointMargin;
        final double bestBorderY = suggestedBorderY > this.drawArea.getY() ?
                        (suggestedBorderY + borderHeight) < (this.drawArea.getY() + this.drawArea.getHeight()) ? suggestedBorderY : this.drawArea.getY() + this.drawArea.getHeight() - borderHeight - boxPointMargin :
                        this.drawArea.getY() + boxPointMargin;
        
        final double x = bestBorderX + 1;
        final double y = bestBorderY + 1;

        this.ROIRect.setRect(x, y, width, height);
    }

    private void updateInvestInformationBox(Graphics2D g2) {
        final Font oldFont = g2.getFont();
        final Font paramFont = oldFont;
        final FontMetrics paramFontMetrics = g2.getFontMetrics(paramFont);
        final Font valueFont = oldFont.deriveFont(oldFont.getStyle() | Font.BOLD, (float)oldFont.getSize() + 1);
        final FontMetrics valueFontMetrics = g2.getFontMetrics(valueFont);
        final Font dateFont = oldFont.deriveFont((float)oldFont.getSize() - 1);
        final FontMetrics dateFontMetrics = g2.getFontMetrics(dateFont);

        final Activities activities = this.investmentFlowChartJDialog.getInvestActivities(this.investPointIndex);

        this.investValues.clear();
        this.investParams.clear();
        this.totalInvestValue = 0.0;

        for (int i = 0, size = activities.size(); i < size; i++) {
            final Activity activity = activities.get(i);
            // Buy only.
            this.investParams.add(activity.getType() + " " + activity.get(Activity.Param.Quantity) + " " + ((Stock)activity.get(Activity.Param.Stock)).getSymbol());

            if (activity.getType() == Activity.Type.Buy) {
                this.totalInvestValue += activity.getAmount();
                this.investValues.add(org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(activity.getAmount()));
            }
            else {
                assert(false);
            }
        }

        final boolean isTotalNeeded = this.investParams.size() > 1;
        final String totalParam = GUIBundle.getString("InvestmentFlowLayerUI_Total_Invest");
        final String totalValue = org.yccheok.jstock.portfolio.Utils.toCurrencyWithSymbol(this.totalInvestValue);
        /* This is the height for "total" information. */
        int totalHeight = 0;

        assert(this.investValues.size() == this.investParams.size());

        int index = 0;
        final int paramValueWidthMargin = 10;
        final int paramValueHeightMargin = 0;
        int maxInfoWidth = -1;
        // paramFontMetrics will always "smaller" than valueFontMetrics.
        int totalInfoHeight = Math.max(paramFontMetrics.getHeight(), valueFontMetrics.getHeight()) * this.investValues.size() + paramValueHeightMargin * (this.investValues.size() - 1);
        for (String param : this.investParams) {
            final String value = this.investValues.get(index++);
            final int paramStringWidth = paramFontMetrics.stringWidth(param + ":") + paramValueWidthMargin + valueFontMetrics.stringWidth(value);
            if (maxInfoWidth < paramStringWidth) {
                maxInfoWidth = paramStringWidth;
            }
        }

        if (isTotalNeeded) {
            final int tmp = paramFontMetrics.stringWidth(totalParam + ":") + paramValueWidthMargin + valueFontMetrics.stringWidth(totalValue);
            if (maxInfoWidth < tmp) {
                maxInfoWidth = tmp;
            }
            totalHeight = Math.max(paramFontMetrics.getHeight(), valueFontMetrics.getHeight());
        }

        final Date date =  activities.getDate().getTime();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        final String dateString = simpleDateFormat.format(date);
        final int dateStringWidth = dateFontMetrics.stringWidth(dateString);
        final int dateStringHeight = dateFontMetrics.getHeight();
        final int maxStringWidth = Math.max(dateStringWidth, maxInfoWidth);
        final int dateInfoHeightMargin = 5;
        final int infoTotalHeightMargin = 5;
        final int maxStringHeight = isTotalNeeded ? 
                                    (dateStringHeight + dateInfoHeightMargin + totalInfoHeight + infoTotalHeightMargin + totalHeight) :
                                    (dateStringHeight + dateInfoHeightMargin + totalInfoHeight);

        // Now, We have a pretty good information on maxStringWidth and maxStringHeight.

        final int padding = 5;
        final int boxPointMargin = 8;
        final int width = maxStringWidth + (padding << 1);
        final int height = maxStringHeight + (padding << 1);
        // On left side of the ball.
        final int suggestedX = (int)(this.investPoint.getX() - width - boxPointMargin);
        final int suggestedY = (int)(this.investPoint.getY() - (height >> 1));
        final int x =   suggestedX > this.drawArea.getX() ?
                        (suggestedX + width) < (this.drawArea.getX() + this.drawArea.getWidth()) ? suggestedX : (int)(this.drawArea.getX() + this.drawArea.getWidth() - width - boxPointMargin) :
                        (int)(investPoint.getX() + boxPointMargin);
        final int y =   suggestedY > this.drawArea.getY() ?
                        (suggestedY + height) < (this.drawArea.getY() + this.drawArea.getHeight()) ? suggestedY : (int)(this.drawArea.getY() + this.drawArea.getHeight() - height - boxPointMargin) :
                        (int)(this.drawArea.getY() + boxPointMargin);

        this.investRect.setRect(x, y, width, height);
    }

    private boolean updateROIPoint(Point2D _ROIPoint) {
        if (_ROIPoint == null) {
            return false;
        }

        final ChartPanel chartPanel = this.investmentFlowChartJDialog.getChartPanel();
        final JFreeChart chart = chartPanel.getChart();
        final XYPlot plot = (XYPlot) chart.getPlot();
        // 0 are the invest information. 1 is the ROI information.
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset(1);
        final TimeSeries timeSeries = timeSeriesCollection.getSeries(0);

        // I also not sure why. This is what are being done in Mouse Listener Demo 4.
        final Point2D p2 = chartPanel.translateScreenToJava2D((Point)_ROIPoint);

        /* Try to get correct main chart area. */
        final Rectangle2D _plotArea = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();

        /* Believe it? When there is another thread keep updateing time series data,
         * and keep calling setDirty, _plotArea can be 0 size sometimes. Ignore it.
         * Just assume we had processed it.
         */
        if (_plotArea.getWidth() == 0.0 && _plotArea.getHeight() == 0.0) {
            /* Cheat the caller. */
            return true;
        }

        final ValueAxis domainAxis = plot.getDomainAxis();
        final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
        final ValueAxis rangeAxis = plot.getRangeAxis();
        final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
        final double coordinateX = domainAxis.java2DToValue(p2.getX(), _plotArea,
                domainAxisEdge);

        int low = 0;
        int high = timeSeries.getItemCount() - 1;
        Date date = new Date((long)coordinateX);
        final long time = date.getTime();
        long bestDistance = Long.MAX_VALUE;
        int bestMid = 0;

        while (low <= high) {
            int mid = (low + high) >>> 1;

            final TimeSeriesDataItem timeSeriesDataItem = timeSeries.getDataItem(mid);
            final Day day = (Day)timeSeriesDataItem.getPeriod();
            final long search = day.getFirstMillisecond();
            final long cmp = search - time;

            if (cmp < 0) {
                low = mid + 1;
            }
            else if (cmp > 0) {
                high = mid - 1;
            }
            else {
                bestDistance = 0;
                bestMid = mid;
                break;
            }

            final long abs_cmp = Math.abs(cmp);
            if (abs_cmp < bestDistance) {
                bestDistance = abs_cmp;
                bestMid = mid;
            }
        }

        final TimeSeriesDataItem timeSeriesDataItem = timeSeries.getDataItem(bestMid);
        final double xValue = timeSeriesDataItem.getPeriod().getFirstMillisecond();
        final double yValue = timeSeriesDataItem.getValue().doubleValue();
        final double xJava2D = domainAxis.valueToJava2D(xValue, _plotArea, domainAxisEdge);
        final double yJava2D = rangeAxis.valueToJava2D(yValue, _plotArea, rangeAxisEdge);

        final int tmpIndex = bestMid;
        // translateJava2DToScreen will internally convert Point2D.Double to Point.
        final Point2D tmpPoint = chartPanel.translateJava2DToScreen(new Point2D.Double(xJava2D, yJava2D));
        this.drawArea.setRect(_plotArea);
        
        if (this.drawArea.contains(tmpPoint)) {
            this.ROIPointIndex = tmpIndex;
            this.ROIPoint = tmpPoint;
            return true;
        }
        return false;
    }

    public void updateROIPoint() {
        if (this.updateROIPoint(this.ROIPoint) == false) {
            /* Clear the point. */
            this.ROIPoint = null;
        }
        this.setDirty(true);
    }

    public void updateInvestPoint() {
        if (this.updateInvestPoint(this.investPoint) == false) {
            /* Clear the point. */
            this.investPoint = null;
        }
        this.setDirty(true);
    }

    // Update this.drawArea, this.investPoint and this.investPointIndex.
    private boolean updateInvestPoint(Point2D _investPoint) {
        if (_investPoint == null) {
            return false;
        }

        final ChartPanel chartPanel = this.investmentFlowChartJDialog.getChartPanel();
        final JFreeChart chart = chartPanel.getChart();
        final XYPlot plot = (XYPlot) chart.getPlot();
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();
        final TimeSeries timeSeries = timeSeriesCollection.getSeries(0);

        // I also not sure why. This is what are being done in Mouse Listener Demo 4.
        final Point2D p2 = chartPanel.translateScreenToJava2D((Point)_investPoint);

        /* Try to get correct main chart area. */
        final Rectangle2D _plotArea = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();

        /* Believe it? When there is another thread keep updateing time series data,
         * and keep calling setDirty, _plotArea can be 0 size sometimes. Ignore it.
         * Just assume we had processed it.
         */
        if (_plotArea.getWidth() == 0.0 && _plotArea.getHeight() == 0.0) {
            /* Cheat the caller. */
            return true;
        }

        final ValueAxis domainAxis = plot.getDomainAxis();
        final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
        final ValueAxis rangeAxis = plot.getRangeAxis();
        final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
        final double coordinateX = domainAxis.java2DToValue(p2.getX(), _plotArea,
                domainAxisEdge);

        int low = 0;
        int high = timeSeries.getItemCount() - 1;
        Date date = new Date((long)coordinateX);
        final long time = date.getTime();
        long bestDistance = Long.MAX_VALUE;
        int bestMid = 0;

        while (low <= high) {
            int mid = (low + high) >>> 1;

            final TimeSeriesDataItem timeSeriesDataItem = timeSeries.getDataItem(mid);
            final Day day = (Day)timeSeriesDataItem.getPeriod();
            final long search = day.getFirstMillisecond();
            final long cmp = search - time;

            if (cmp < 0) {
                low = mid + 1;
            }
            else if (cmp > 0) {
                high = mid - 1;
            }
            else {
                bestDistance = 0;
                bestMid = mid;
                break;
            }

            final long abs_cmp = Math.abs(cmp);
            if (abs_cmp < bestDistance) {
                bestDistance = abs_cmp;
                bestMid = mid;
            }
        }

        final TimeSeriesDataItem timeSeriesDataItem = timeSeries.getDataItem(bestMid);
        final double xValue = timeSeriesDataItem.getPeriod().getFirstMillisecond();
        final double yValue = timeSeriesDataItem.getValue().doubleValue();
        final double xJava2D = domainAxis.valueToJava2D(xValue, _plotArea, domainAxisEdge);
        final double yJava2D = rangeAxis.valueToJava2D(yValue, _plotArea, rangeAxisEdge);

        final int tmpIndex = bestMid;
        // translateJava2DToScreen will internally convert Point2D.Double to Point.
        final Point2D tmpPoint = chartPanel.translateJava2DToScreen(new Point2D.Double(xJava2D, yJava2D));
        this.drawArea.setRect(_plotArea);

        if (this.drawArea.contains(tmpPoint)) {
            this.investPointIndex = tmpIndex;
            this.investPoint = tmpPoint;
            return true;
        }
        return false;
    }

    private void processEvent(MouseEvent e, JXLayer layer) {
        if (MouseEvent.MOUSE_DRAGGED == e.getID()) {
            return;
        }
        
        final Point mousePoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), layer);

        final boolean status0 = this.updateInvestPoint(mousePoint);
        final boolean status1 = this.updateROIPoint(mousePoint);

        if (status0 || status1) {
            this.setDirty(true);
        }
    }
    
    @Override
    public void setDirty(boolean isDirty) {
        super.setDirty(isDirty);
    }

    private void drawBusyBox(Graphics2D g2, JXLayer<? extends V> layer) {
        final Font oldFont = g2.getFont();
        final Font font = oldFont;
        final FontMetrics fontMetrics = g2.getFontMetrics(font);

        // Not sure why. Draw GIF image on JXLayer, will cause endless setDirty
        // being triggered by system.
        //final Image image = ((ImageIcon)Icons.BUSY).getImage();
        //final int imgWidth = Icons.BUSY.getIconWidth();
        //final int imgHeight = Icons.BUSY.getIconHeight();
        //final int imgMessageWidthMargin = 5;
        final int imgWidth = 0;
        final int imgHeight = 0;
        final int imgMessageWidthMargin = 0;

        final String message = MessagesBundle.getString("info_message_retrieving_latest_stock_price");
        final int maxWidth = imgWidth + imgMessageWidthMargin + fontMetrics.stringWidth(message);
        final int maxHeight = Math.max(imgHeight, fontMetrics.getHeight());

        final int padding = 5;
        final int width = maxWidth + (padding << 1);
        final int height = maxHeight + (padding << 1);
        final int x = (int)this.drawArea.getX() + (((int)this.drawArea.getWidth() - width) >> 1);
        final int y = (int)this.drawArea.getY() + (((int)this.drawArea.getHeight() - height) >> 1);

        final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        final Composite oldComposite = g2.getComposite();
        final Color oldColor = g2.getColor();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(COLOR_BORDER);
        g2.drawRoundRect(x - 1, y - 1, width + 1, height + 1, 15, 15);
        g2.setColor(COLOR_BACKGROUND);
        g2.setComposite(Utils.makeComposite(0.75f));
        g2.fillRoundRect(x, y, width, height, 15, 15);
        g2.setComposite(oldComposite);
        g2.setColor(oldColor);

        //g2.drawImage(image, x + padding, y + ((height - imgHeight) >> 1), layer.getView());

        g2.setFont(font);
        g2.setColor(COLOR_BLUE);

        int yy = y + ((height - fontMetrics.getHeight()) >> 1) + fontMetrics.getAscent();
        g2.setFont(font);
        g2.setColor(COLOR_BLUE);
        g2.drawString(message,
                x + padding + imgWidth + imgMessageWidthMargin,
                yy);
        g2.setColor(oldColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
        g2.setFont(oldFont);
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends V> layer) {
        super.paintLayer(g2, layer);

        final ChartPanel chartPanel = ((ChartPanel)layer.getView());

        final Rectangle2D _plotArea = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();

        this.drawArea.setRect(_plotArea);

        if (false == this.investmentFlowChartJDialog.isFinishLookUpPrice()) {
            this.drawBusyBox(g2, layer);
        }

        if (this.investPoint != null) {
            final int RADIUS = 8;
            final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            final Color oldColor = g2.getColor();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_RED);
            g2.fillOval((int)(this.investPoint.getX() - (RADIUS >> 1) + 0.5), (int)(this.investPoint.getY() - (RADIUS >> 1) + 0.5), RADIUS, RADIUS);
            g2.setColor(oldColor);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
            this.updateInvestInformationBox(g2);
        }

        if (this.ROIPoint != null) {
            final int RADIUS = 8;
            final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            final Color oldColor = g2.getColor();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_BLUE);
            g2.fillOval((int)(this.ROIPoint.getX() - (RADIUS >> 1) + 0.5), (int)(this.ROIPoint.getY() - (RADIUS >> 1) + 0.5), RADIUS, RADIUS);
            g2.setColor(oldColor);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
            this.updateROIInformationBox(g2);
        }

        this.solveConflict();

        if (this.investPoint != null) {
            this.drawInformationBox(g2, this.investmentFlowChartJDialog.getInvestActivities(this.investPointIndex), investRect, 
                    investParams, investValues, GUIBundle.getString("InvestmentFlowLayerUI_Total_Invest"), totalInvestValue,
                    COLOR_RED_BACKGROUND, COLOR_RED_BORDER);
        }

        if (this.ROIPoint != null) {
            this.drawInformationBox(g2, this.investmentFlowChartJDialog.getROIActivities(this.ROIPointIndex), 
                    ROIRect, ROIParams, ROIValues, GUIBundle.getString("InvestmentFlowLayerUI_Total_Return"), totalROIValue,
                    COLOR_BLUE_BACKGROUND, COLOR_BLUE_BORDER);
        }

        this.drawTitle(g2);
    }

    private void solveConflict() {
        if (this.investPoint == null || this.ROIPoint == null) {
            /* No conflict to be solved. */
            return;
        }

        /* Take border into consideration. */
        final Rectangle ROIRectWithBorder = new Rectangle((Rectangle)this.ROIRect);
        final Rectangle investRectWithBorder = new Rectangle((Rectangle)this.investRect);
        ROIRectWithBorder.setLocation((int)ROIRectWithBorder.getX() - 1, (int)ROIRectWithBorder.getY() - 1);
        ROIRectWithBorder.setSize((int)ROIRectWithBorder.getWidth() + 2, (int)ROIRectWithBorder.getHeight() + 2);
        investRectWithBorder.setLocation((int)investRectWithBorder.getX() - 1, (int)investRectWithBorder.getY() - 1);
        investRectWithBorder.setSize((int)investRectWithBorder.getWidth() + 2, (int)investRectWithBorder.getHeight() + 2);
        if (false == ROIRectWithBorder.intersects(investRectWithBorder)) {
            return;
        }

        final Rectangle oldROIRect = new Rectangle((Rectangle)this.ROIRect);
        final Rectangle oldInvestRect = new Rectangle((Rectangle)this.investRect);

        // Move to Down.
        if (this.ROIRect.getY() > this.investRect.getY()) {
            ((Rectangle)this.ROIRect).translate(0, (int)(this.investRect.getY() + this.investRect.getHeight() - this.ROIRect.getY() + 4));
        }
        else {
            ((Rectangle)this.investRect).translate(0, (int)(this.ROIRect.getY() + this.ROIRect.getHeight() - this.investRect.getY() + 4));
        }

        if ((this.drawArea.getY() + this.drawArea.getHeight()) > (this.ROIRect.getY() + this.ROIRect.getHeight()) &&
            (this.drawArea.getY() + this.drawArea.getHeight()) > (this.investRect.getY() + this.investRect.getHeight())) {
            return;
        }

        this.ROIRect.setRect(oldROIRect);
        this.investRect.setRect(oldInvestRect);

        // Move to Up.
        if (this.ROIRect.getY() > this.investRect.getY()) {
            ((Rectangle)this.investRect).translate(0, -(int)(this.investRect.getY() + this.investRect.getHeight() - this.ROIRect.getY() + 4));
        }
        else {
            ((Rectangle)this.ROIRect).translate(0, -(int)(this.ROIRect.getY() + this.ROIRect.getHeight() - this.investRect.getY() + 4));
        }

        if ((this.drawArea.getY() < this.ROIRect.getY()) &&
            (this.drawArea.getY() < this.investRect.getY())) {
            return;
        }

        this.ROIRect.setRect(oldROIRect);
        this.investRect.setRect(oldInvestRect);
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JXLayer<? extends V> layer) {
        super.processMouseEvent(e, layer);
        this.processEvent(e, layer);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JXLayer<? extends V> layer) {
        super.processMouseMotionEvent(e, layer);
        this.processEvent(e, layer);
    }
}
