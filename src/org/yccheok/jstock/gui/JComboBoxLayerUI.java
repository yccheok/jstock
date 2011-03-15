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

package org.yccheok.jstock.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.Timer;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.yccheok.jstock.engine.Observer;

/**
 *
 * @author yccheok
 */
public class JComboBoxLayerUI<V extends JComboBox> extends AbstractLayerUI<V> implements Observer<V, Boolean> {
    private volatile boolean isBusy = false;
    private Timer busyTimer = null;

    @Override
    public void update(V subject, Boolean arg) {
        final boolean _isBusy = arg;
        // Display busy indicator immediately.
        isBusy = _isBusy;
//        Timer me = busyTimer;
//        if (me != null) {
//            // Stop previous timer from displaying busy indicator.
//            me.stop();
//        }
//        if (_isBusy == false) {
//            this.setBusy(_isBusy);
//        } else {
//            // Do not display busy indicator immediately to avoid from
//            // annoying the user. Wait for some time. 1 second should be
//            // good enough, as under normal network connection, the
//            // specified time shall be enough to obtain result from
//            // server.
//            busyTimer = new Timer(1000, new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    JComboBoxLayerUI.this.setBusy(_isBusy);
//                }
//            });
//            busyTimer.start();
//        }
    }

    public void setBusy(boolean isBusy) {
        final boolean oldFlag = this.isBusy;
        this.isBusy = isBusy;
        // Do we need to repaint?
        if (oldFlag != this.isBusy) {
            // Update Immediately.
            this.setDirty(true);
        }
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends V> layer) {
        super.paintLayer(g2, layer);

        if (this.isBusy == false) {
            return;
        }

        // Store previous attributes.
        //final Color oldColor = g2.getColor();
        //final Font oldFont = g2.getFont();
        //final Object oldValueAntiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

        // Set new attributes.
        //g2.setColor(javax.swing.UIManager.getDefaults().getColor("ComboBox.disabledForeground"));
        //g2.setFont(oldFont.deriveFont((float)oldFont.getSize() * 0.8f));
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //final FontMetrics fontMetrics = g2.getFontMetrics();
        //final int fontHeight = fontMetrics.getHeight();
        //final int fontWidth = fontMetrics.stringWidth("Busy...");
        final int height = layer.getView().getEditor().getEditorComponent().getHeight();
        final int width = layer.getView().getEditor().getEditorComponent().getWidth();
        final int padding = 2;
        //final int x = width - fontWidth - padding;
        //final int y = ((height - fontHeight) >> 1) + fontHeight;
        //g2.drawString("Busy...", x, y);

        final Image image = ((ImageIcon)Icons.BUSY).getImage();
        final int imgWidth = Icons.BUSY.getIconWidth();
        final int imgHeight = Icons.BUSY.getIconHeight();
        g2.drawImage(image, width - imgWidth - padding, (height - imgHeight) >> 1, layer.getView());

        // Restore old attributes.
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValueAntiAlias);
        //g2.setFont(oldFont);
        //g2.setColor(oldColor);
    }
}
