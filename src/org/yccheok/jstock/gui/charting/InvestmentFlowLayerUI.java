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
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
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
import org.yccheok.jstock.gui.Icons;
import org.yccheok.jstock.gui.JStockOptions;
import org.yccheok.jstock.gui.Utils;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.internationalization.MessagesBundle;
import org.yccheok.jstock.portfolio.Activities;
import org.yccheok.jstock.portfolio.Activity;
import org.yccheok.jstock.portfolio.ActivitySummary;

/**
 *
 * @author yccheok
 */
public class InvestmentFlowLayerUI<V extends javax.swing.JComponent> extends AbstractLayerUI<V> {

    private Point2D investPoint = null;
    private int investPointIndex = -1;
    private Point2D ROIPoint = null;
    private int ROIPointIndex = -1;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");

    private final Rectangle2D drawArea = new Rectangle2D.Double();
    
    private final InvestmentFlowChartJDialog cashFlowChartJDialog;

    private static final Color COLOR_BLUE = new Color(85, 85, 255);
    private static final Color COLOR_RED = new Color(255, 85, 85);
    private static final Color COLOR_BACKGROUND = new Color(255, 255, 153);
    private static final Color COLOR_BORDER = new Color(255, 204, 0);

    public InvestmentFlowLayerUI(InvestmentFlowChartJDialog cashFlowChartJDialog) {
        this.cashFlowChartJDialog = cashFlowChartJDialog;
    }

    private void drawInvestInformationBox(Graphics2D g2, JXLayer<? extends V> layer) {
        final Font oldFont = g2.getFont();
        final Font paramFont = new Font(oldFont.getFontName(), oldFont.getStyle(), oldFont.getSize());
        final FontMetrics paramFontMetrics = g2.getFontMetrics(paramFont);
        final Font valueFont = new Font(oldFont.getFontName(), oldFont.getStyle() + Font.BOLD, oldFont.getSize() + 1);
        final FontMetrics valueFontMetrics = g2.getFontMetrics(valueFont);
        final Font dateFont = new Font(oldFont.getFontName(), oldFont.getStyle(), oldFont.getSize() - 1);
        final FontMetrics dateFontMetrics = g2.getFontMetrics(dateFont);

        final Activities activities = this.cashFlowChartJDialog.getInvestActivities(this.investPointIndex);

        List<String> values = new ArrayList<String>();
        List<String> params = new ArrayList<String>();

        double total = 0.0;

        for (int i = 0, size = activities.size(); i < size; i++) {
            final Activity activity = activities.get(i);
            // Buy or Sell only.
            params.add(activity.getType() + " " + activity.get(Activity.Param.Quantity) + " " + ((Stock)activity.get(Activity.Param.Stock)).getSymbol());

            if (activity.getType() == Activity.Type.Buy) {
                total += activity.getAmount();
                values.add(org.yccheok.jstock.portfolio.Utils.currencyNumberFormat(activity.getAmount()));
            }
            else if (activity.getType() == Activity.Type.Sell) {
                total -= activity.getAmount();
                values.add(org.yccheok.jstock.portfolio.Utils.currencyNumberFormat(-activity.getAmount()));
            }
            else {
                assert(false);
            }
        }

        final boolean isTotalNeeded = params.size() > 1;
        final String totalParam = GUIBundle.getString("InvestmentFlowLayerUI_Total");
        final String totalValue = org.yccheok.jstock.portfolio.Utils.currencyNumberFormat(total);
        /* This is the height for "total" information. */
        int totalHeight = 0;

        assert(values.size() == params.size());

        int index = 0;
        final int paramValueWidthMargin = 10;
        final int paramValueHeightMargin = 0;
        int maxInfoWidth = -1;
        // paramFontMetrics will always "smaller" than valueFontMetrics.
        int totalInfoHeight = Math.max(paramFontMetrics.getHeight(), valueFontMetrics.getHeight()) * values.size() + paramValueHeightMargin * (values.size() - 1);
        for (String param : params) {
            final String value = values.get(index++);
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
        final String dateString = simpleDateFormat.format(date);
        final int dateStringWidth = dateFontMetrics.stringWidth(dateString);
        final int dateStringHeight = dateFontMetrics.getHeight();
        final int maxStringWidth = Math.max(dateStringWidth, maxInfoWidth);
        final int dateInfoHeightMargin = 5;
        final int infoTotalHeightMargin = 5;
        final int maxStringHeight = isTotalNeeded ? 
                                    (dateStringHeight + dateInfoHeightMargin + totalInfoHeight + infoTotalHeightMargin + totalHeight) :
                                    (dateStringHeight + dateInfoHeightMargin + totalInfoHeight);

        final int padding = 5;
        final int boxPointMargin = 8;
        final int width = maxStringWidth + (padding << 1);
        final int height = maxStringHeight + (padding << 1);
        final int suggestedX = (int)(this.investPoint.getX() - width - boxPointMargin);
        final int suggestedY = (int)(this.investPoint.getY() - (height >> 1));
        final int x = suggestedX > this.drawArea.getX() ? suggestedX : (int)(this.investPoint.getX() + boxPointMargin);
        final int y = suggestedY > this.drawArea.getY() ? suggestedY : (int)(this.drawArea.getY() + boxPointMargin);

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

        int yy = y  + padding + dateFontMetrics.getAscent();
        g2.setFont(dateFont);
        g2.setColor(COLOR_BLUE);
        g2.drawString(dateString,
                ((width - dateFontMetrics.stringWidth(dateString)) >> 1) + x,
                yy);

        index = 0;
        yy += dateFontMetrics.getDescent() + dateInfoHeightMargin + valueFontMetrics.getAscent();
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
            yy += paramValueHeightMargin + valueFontMetrics.getHeight();
        }
        
        if (isTotalNeeded) {
            yy -= paramValueHeightMargin;
            yy += infoTotalHeightMargin;
            if (total > 0.0) {
                g2.setColor(JStockOptions.DEFAULT_HIGHER_NUMERICAL_VALUE_FOREGROUND_COLOR);
            }
            else if (total < 0.0) {
                g2.setColor(JStockOptions.DEFAULT_LOWER_NUMERICAL_VALUE_FOREGROUND_COLOR);
            }

            g2.setFont(paramFont);
            g2.drawString(totalParam + ":",
                padding + x,
                yy);
            g2.setFont(valueFont);
            g2.drawString(totalValue,
                width - padding - valueFontMetrics.stringWidth(totalValue) + x,
                yy);
        }

        g2.setColor(oldColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
        g2.setFont(oldFont);
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

        final ChartPanel chartPanel = this.cashFlowChartJDialog.getChartPanel();
        final JFreeChart chart = chartPanel.getChart();
        final XYPlot plot = (XYPlot) chart.getPlot();
        final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection)plot.getDataset();
        // 0 are the invest information. 1 is the ROI information.
        final TimeSeries timeSeries = timeSeriesCollection.getSeries(0);

        // I also not sure why. This is what are being done in Mouse Listener Demo 4.
        final Point2D p2 = chartPanel.translateScreenToJava2D((Point)_investPoint);
        final Rectangle2D _plotArea = chartPanel.getScreenDataArea();

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
            final long target = day.getFirstMillisecond();
            final long cmp = target - time;

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
        // this.point = new Point2D.Double(xJava2D, yJava2D);
        final Point2D tmpPoint = chartPanel.translateJava2DToScreen(new Point2D.Double(xJava2D, yJava2D));
        // This _plotArea is including the axises. We do not want axises. We only
        // want the center draw area.
        // However, I really have no idea how to obtain rect for center draw area.
        // This is just a try-n-error hack.
        this.drawArea.setRect(_plotArea.getX() + 2, _plotArea.getY() + 3,
                _plotArea.getWidth() - 4 > 0 ? _plotArea.getWidth() - 4 : 1,
                _plotArea.getHeight() - 5 > 0 ? _plotArea.getHeight() - 5 : 1);

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

        if (this.updateInvestPoint(mousePoint)) {
            this.setDirty(true);
        }
    }
    
    @Override
    public void setDirty(boolean isDirty) {
        super.setDirty(isDirty);
    }

    private void drawBusyBox(Graphics2D g2, JXLayer<? extends V> layer) {
        final Font oldFont = g2.getFont();
        final Font font = new Font(oldFont.getFontName(), oldFont.getStyle(), oldFont.getSize());
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

        // This _plotArea is including the axises. We do not want axises. We only
        // want the center draw area.
        // However, I really have no idea how to obtain rect for center draw area.
        // This is just a try-n-error hack.
        final Rectangle2D _plotArea = chartPanel.getScreenDataArea();

        this.drawArea.setRect(_plotArea.getX() + 2, _plotArea.getY() + 3,
                _plotArea.getWidth() - 4 > 0 ? _plotArea.getWidth() - 4 : 1,
                _plotArea.getHeight() - 5 > 0 ? _plotArea.getHeight() - 5 : 1);

        if (false == this.cashFlowChartJDialog.isFinishLookUpPrice()) {
            this.drawBusyBox(g2, layer);
        }

        if (this.investPoint != null) {
            final int radius = 8;
            final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            final Color oldColor = g2.getColor();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_RED);
            g2.fillOval((int)(this.investPoint.getX() - (radius >> 1) + 0.5), (int)(this.investPoint.getY() - (radius >> 1) + 0.5), radius, radius);
            g2.setColor(oldColor);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
            this.drawInvestInformationBox(g2, layer);
        }
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
