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
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.jfree.chart.ChartPanel;
import org.yccheok.jstock.gui.Icons;
import org.yccheok.jstock.gui.Utils;
import org.yccheok.jstock.internationalization.MessagesBundle;

/**
 *
 * @author yccheok
 */
public class InvestmentFlowLayerUI<V extends javax.swing.JComponent> extends AbstractLayerUI<V> {

    private static final Color COLOR_BLUE = new Color(85, 85, 255);
    private static final Color COLOR_BACKGROUND = new Color(255, 255, 153);
    private static final Color COLOR_BORDER = new Color(255, 204, 0);

    public InvestmentFlowLayerUI(InvestmentFlowChartJDialog cashFlowChartJDialog) {
        this.cashFlowChartJDialog = cashFlowChartJDialog;
    }

    @Override
    public void setDirty(boolean isDirty) {
        super.setDirty(isDirty);
    }

    private void drawBusyBox(Graphics2D g2, JXLayer<? extends V> layer) {
        final Font oldFont = g2.getFont();
        final Font font = new Font(oldFont.getFontName(), oldFont.getStyle(), oldFont.getSize());
        final FontMetrics fontMetrics = g2.getFontMetrics(font);

        final Image image = ((ImageIcon)Icons.BUSY).getImage();
        final int imgWidth = Icons.BUSY.getIconWidth();
        final int imgHeight = Icons.BUSY.getIconHeight();
        final int imgMessageWidthMargin = 5;

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

        g2.drawImage(image, x + padding, y + ((height - imgHeight) >> 1), layer.getView());

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
        else {            
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JXLayer<? extends V> layer) {
        super.processMouseEvent(e, layer);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JXLayer<? extends V> layer) {
        super.processMouseMotionEvent(e, layer);
    }

    private final Rectangle2D drawArea = new Rectangle2D.Double();
    private final InvestmentFlowChartJDialog cashFlowChartJDialog;
}
