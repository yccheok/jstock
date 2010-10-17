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

package org.yccheok.jstock.gui;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.AjaxYahooSearchEngine;
import org.yccheok.jstock.engine.AjaxYahooSearchEngine.ResultSetType;
import org.yccheok.jstock.engine.AjaxYahooSearchEngineMonitor;
import org.yccheok.jstock.engine.Observer;

/**
 * This is a combo box, which will provides a list of suggested stocks, by
 * getting asynchronous Ajax search result from server.
 *
 * @author yccheok
 */
public class AjaxAutoCompleteJComboBox extends JComboBox {

    /**
     * Create an instance of AjaxAutoCompleteJComboBox.
     */
    public AjaxAutoCompleteJComboBox() {
        super();
        ajaxYahooSearchEngineMonitor.attach(getMonitorObserver());

        // Do not use keyAdapter to handle auto-complete feature, as it doesn't
        // handle IME input well. For example, you type "wm" and press "3",
        // keyAdapter will have no idea you are trying to choose the 3rd choice
        // provided by your IME. Instead, use documentListener, which will be
        // much more reliable.
        final Component component = this.getEditor().getEditorComponent();
        if (component instanceof JTextComponent) {
            ((JTextComponent)component).getDocument().addDocumentListener(this.getDocumentListener());
        } else {
            log.error("Unable to attach DocumentListener to AjaxAutoCompleteJComboBox.");
        }
    }

    private DocumentListener getDocumentListener() {
       return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    final String string = e.getDocument().getText(0, e.getDocument().getLength());
                    _handle(string);
                } catch (BadLocationException ex) {
                    log.error(null, ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    final String string = e.getDocument().getText(0, e.getDocument().getLength());
                    _handle(string);
                } catch (BadLocationException ex) {
                    log.error(null, ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    final String string = e.getDocument().getText(0, e.getDocument().getLength());
                    _handle(string);
                } catch (BadLocationException ex) {
                    log.error(null, ex);
                }
            }

            private void _handle(final String string) {
                ajaxYahooSearchEngineMonitor.clearAndPut(string);
            }
       };
    }

    private Observer<AjaxYahooSearchEngineMonitor, AjaxYahooSearchEngine.ResultSetType> getMonitorObserver() {
        return new Observer<AjaxYahooSearchEngineMonitor, AjaxYahooSearchEngine.ResultSetType>() {
            @Override
            public void update(AjaxYahooSearchEngineMonitor subject, ResultSetType arg) {
                //FIXME:
                //final String string = ((JTextComponent)AjaxAutoCompleteJComboBox.this.getEditor()).getText().trim();

            }
        };
    }

    /**
     * Stop any threading activities.
     */
    public void stop() {
        ajaxYahooSearchEngineMonitor.stop();
    }

    private final AjaxYahooSearchEngineMonitor ajaxYahooSearchEngineMonitor = new AjaxYahooSearchEngineMonitor();
    private static final Log log = LogFactory.getLog(AjaxAutoCompleteJComboBox.class);
}
