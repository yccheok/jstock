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

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.metal.MetalComboBoxEditor;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.yccheok.jstock.engine.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.ResultSetType;
import org.yccheok.jstock.engine.ResultType;

/**
 *
 * @author yccheok
 */
public class AutoCompleteJComboBox extends JComboBox implements JComboBoxPopupAdjustable {
    
    // Use SubjectEx, in order to make notify method public.
    private static class SubjectEx<S, A> extends Subject<S, A> {
        @Override
        public void notify(S subject, A arg) {
            super.notify(subject, arg);
        }
    }

    /** Creates a new instance of AutoCompleteJComboBox */
    public AutoCompleteJComboBox() {
        this.stockInfoDatabase = null;

        // Save the offline mode renderer, so that we may reuse it when we
        // switch back to offline mode.
        this.oldListCellRenderer = this.getRenderer();

        this.changeMode(Mode.Offline);

        this.setEditable(true);
        
        this.keyAdapter = this.getEditorComponentKeyAdapter();

        // Use our own editor, in order to implement auto-complete feature.
        this.jComboBoxEditor = new MyJComboBoxEditor();
        this.setEditor(this.jComboBoxEditor);

        // Use to handle ENTER key pressed.
        this.getEditor().getEditorComponent().addKeyListener(this.keyAdapter);

        // Do not use keyAdapter to handle auto-complete feature, as it doesn't
        // handle IME input well. For example, you type "wm" and press "3",
        // keyAdapter will have no idea you are trying to choose the 3rd choice
        // provided by your IME. Instead, use documentListener, which will be
        // much more reliable.
        final Component component = this.getEditor().getEditorComponent();
        if (component instanceof JTextComponent) {
            ((JTextComponent)component).getDocument().addDocumentListener(this.getDocumentListener());
        } else {
            log.error("Unable to attach DocumentListener to AutoCompleteJComboBox.");
        }

        this.ajaxYahooSearchEngineMonitor.attach(getYahooMonitorObserver());
        this.ajaxGoogleSearchEngineMonitor.attach(getGoogleMonitorObserver());
        
        this.addActionListener(getActionListener());

        // Have a wide enough drop down list.
        this.addPopupMenuListener(this.getPopupMenuListener());
        
        // Create horizontal scroll bar if needed.
        // (I am not sure I still need this one as I already have adjustPopupWidth)
        adjustScrollBar();
    }

    private PopupMenuListener getPopupMenuListener() {
        return new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // We will have a much wider drop down list.
                Utils.adjustPopupWidth(AutoCompleteJComboBox.this);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Reset popup width.
                AutoCompleteJComboBox.this.setPopupWidth(-1);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Reset popup width.
                AutoCompleteJComboBox.this.setPopupWidth(-1);
            }
        };
    }

    private ActionListener getActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Handle mouse clicked. */
                if ((e.getModifiers() & java.awt.event.InputEvent.BUTTON1_MASK) == java.awt.event.InputEvent.BUTTON1_MASK) {
                    final Object object = AutoCompleteJComboBox.this.getEditor().getItem();
                    /* Let us be extra paranoid. */
                    if (object instanceof ResultType) {
                        // From Yahoo! Ajax result.
                        ResultType lastEnteredResult = (ResultType)object;
                        AutoCompleteJComboBox.this.resultSubject.notify(AutoCompleteJComboBox.this, lastEnteredResult);
                    } else if (object instanceof StockInfo) {
                        // From our offline database.
                        StockInfo lastEnteredStockInfo = (StockInfo)object;
                        AutoCompleteJComboBox.this.stockInfoSubject.notify(AutoCompleteJComboBox.this, lastEnteredStockInfo);
                    } else {
                        assert(object instanceof MatchType);
                        MatchType lastEnteredMatch = (MatchType)object;
                        AutoCompleteJComboBox.this.matchSubject.notify(AutoCompleteJComboBox.this, lastEnteredMatch);
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // We schedule the below actions in GUI event queue,
                            // so that DocumentListener will not be triggered.
                            // But I am not sure why.
                            AutoCompleteJComboBox.this.getEditor().setItem(null);
                            AutoCompleteJComboBox.this.hidePopup();
                            AutoCompleteJComboBox.this.removeAllItems();
                        }
                    });
                }
            }
        };
    }

    /**
     * Assign a stock info database to this combo box.
     *
     * @param stockInfoDatabase the stock info database
     */
    public void setStockInfoDatabase(StockInfoDatabase stockInfoDatabase) {
        this.stockInfoDatabase = stockInfoDatabase;
        
        KeyListener[] listeners = this.getEditor().getEditorComponent().getKeyListeners();
        
        for (KeyListener listener : listeners) {
            if (listener == keyAdapter) {
                return;
            }
        }
        
        // Bug in Java 6. Most probably this listener had been removed during look n feel updating, reassign!
        this.getEditor().getEditorComponent().addKeyListener(keyAdapter);
        log.info("Reassign key adapter to combo box");        
    }

    private DocumentListener getDocumentListener() {
        return new DocumentListener() {
            private volatile boolean ignore = false;

            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    final String string = e.getDocument().getText(0, e.getDocument().getLength()).trim();
                    handle(string);
                } catch (BadLocationException ex) {
                    log.error(null, ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    final String string = e.getDocument().getText(0, e.getDocument().getLength()).trim();
                    handle(string);
                } catch (BadLocationException ex) {
                    log.error(null, ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    final String string = e.getDocument().getText(0, e.getDocument().getLength()).trim();
                    handle(string);
                } catch (BadLocationException ex) {
                    log.error(null, ex);
                }
            }

            private void _handle(final String string) {
                // We are no longer busy.
                busySubject.notify(AutoCompleteJComboBox.this, false);

                if (AutoCompleteJComboBox.this.getSelectedItem() != null) {
                    // Remember to use toString(). As getSelectedItem() can be
                    // either StockInfo, or ResultSet.
                    if (AutoCompleteJComboBox.this.getSelectedItem().toString().equals(string)) {
                        // We need to differentiate, whether "string" is from user
                        // typing, or drop down list selection. This is because when
                        // user perform selection, document change event will be triggered
                        // too. When string is from drop down list selection, user
                        // are not expecting any auto complete suggestion. Return early.
                        return;
                    }
                }

                if (string.isEmpty()) {
                    // Empty string. Return early. Do not perform hidePopup and
                    // removeAllItems right here. As when user performs list
                    // selection, previous text field item will be removed, and
                    // cause us fall into this scope. We do not want to hidePopup
                    // and removeAllItems when user is selecting his item.
                    //
                    // hidePopup and removeAllItems when user clears off all items
                    // in text field, will be performed through keyReleased.
                    return;
                }                

                // Use to avoid endless DocumentEvent triggering.
                ignore = true;
                // During _handle operation, there will be a lot of ListDataListeners
                // trying to modify the content of our text field. We will not allow
                // them to do so.
                //
                // Without setReadOnly(true), when we type the first character "w", IME
                // will suggest "我". However, when we call removeAllItems and addItem,
                // JComboBox will "commit" this suggestion to JComboBox's text field.
                // Hence, if we continue to type second character "m", the string displayed
                // at JComboBox's text field will be "我我我".
                //
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(true);

                // Must hide popup. If not, the pop up windows will not be
                // resized.
                AutoCompleteJComboBox.this.hidePopup();
                AutoCompleteJComboBox.this.removeAllItems();
                
                boolean shouldShowPopup = false;
                
                if (AutoCompleteJComboBox.this.stockInfoDatabase != null) {
                    java.util.List<StockInfo> stockInfos = 
                            greedyEnabled ?
                            stockInfoDatabase.greedySearchStockInfos(string) :
                            stockInfoDatabase.searchStockInfos(string);

                    sortStockInfosIfPossible(stockInfos);
                    
                    if (stockInfos.isEmpty() == false) {
                        // Change to offline mode before adding any item.
                        changeMode(Mode.Offline);
                    }

                    for (StockInfo stockInfo : stockInfos) {
                        AutoCompleteJComboBox.this.addItem(stockInfo);
                        shouldShowPopup = true;
                    }

                    if (shouldShowPopup) {
                        AutoCompleteJComboBox.this.showPopup();
                    }
                    else {

                    }   // if (shouldShowPopup)
                }   // if (AutoCompleteJComboBox.this.stockInfoDatabase != null)

                if (shouldShowPopup == false) {
                    // OK. We found nothing from offline database. Let's
                    // ask help from online database.
                    // We are busy contacting server right now.

                    // TODO
                    // Only enable ajaxYahooSearchEngineMonitor, till we solve
                    // http://sourceforge.net/apps/mediawiki/jstock/index.php?title=TechnicalDisability
                    busySubject.notify(AutoCompleteJComboBox.this, true);
                    if (ajaxServiceProvider == AjaxServiceProvider.Yahoo) {
                        ajaxYahooSearchEngineMonitor.clearAndPut(string);
                    } else if (ajaxServiceProvider == AjaxServiceProvider.Google) {
                        ajaxGoogleSearchEngineMonitor.clearAndPut(string);
                    }                    
                }
                
                // When we are in windows look n feel, the text will always be selected. We do not want that.
                final Component component = AutoCompleteJComboBox.this.getEditor().getEditorComponent();
                if (component instanceof JTextField) {
                    JTextField jTextField = (JTextField)component;
                    jTextField.setSelectionStart(jTextField.getText().length());
                    jTextField.setSelectionEnd(jTextField.getText().length());
                    jTextField.setCaretPosition(jTextField.getText().length());
                }

                // Restore.
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(false);
                ignore = false;
            }

            private void handle(final String string) {
                if (ignore) {
                    return;
                }

                // Submit to GUI event queue. Used to avoid
                // Exception in thread "AWT-EventQueue-0" java.lang.IllegalStateException: Attempt to mutate in notification
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        _handle(string);
                    }
                });
            }
        };
    }

    // We should make this powerful combo box shared amoing different classes.
    private KeyAdapter getEditorComponentKeyAdapter() {
        
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                    // We are no longer busy.
                    busySubject.notify(AutoCompleteJComboBox.this, false);
                    
                    StockInfo lastEnteredStockInfo = null;
                    ResultType lastEnteredResultType = null;
                    MatchType lastEnteredMatchType = null;
                    
                    if (AutoCompleteJComboBox.this.getItemCount() > 0) {
                        int index = AutoCompleteJComboBox.this.getSelectedIndex();
                        if (index == -1) {
                            Object object = AutoCompleteJComboBox.this.getItemAt(0);
                            if (object instanceof StockInfo) {
                                lastEnteredStockInfo = (StockInfo)object;
                            } else if (object instanceof ResultType) {
                                lastEnteredResultType = (ResultType)object;
                            } else {
                                assert(object instanceof MatchType);
                                lastEnteredMatchType = (MatchType)object;
                            }
                        }
                        else {
                            Object object = AutoCompleteJComboBox.this.getItemAt(index);
                            if (object instanceof StockInfo) {
                                lastEnteredStockInfo = (StockInfo)object;
                            } else if (object instanceof ResultType) {
                                lastEnteredResultType = (ResultType)object;
                            } else {
                                assert(object instanceof MatchType);
                                lastEnteredMatchType = (MatchType)object;
                            }
                        }
                    }
                    else {
                        // If item count is 0, this means stockInfoDatabase
                        // unable provide us any result based on user query. I
                        // suspect we still need to below code, as 
                        // stockInfoDatabase will just return null. However, it
                        // should make no harm at this moment.
                        if (AutoCompleteJComboBox.this.stockInfoDatabase != null) {
                            final Object object = AutoCompleteJComboBox.this.getEditor().getItem();
                            if (object instanceof String) {
                                String lastEnteredString = ((String)object).trim();
                                lastEnteredStockInfo = AutoCompleteJComboBox.this.stockInfoDatabase.searchStockInfo(lastEnteredString);
                            }
                            else {
                                assert(false);
                            }
                        }
                    }

                    AutoCompleteJComboBox.this.removeAllItems();
                    if (lastEnteredStockInfo != null) {
                        AutoCompleteJComboBox.this.stockInfoSubject.notify(AutoCompleteJComboBox.this, lastEnteredStockInfo);
                    } else if (lastEnteredResultType != null) {
                        AutoCompleteJComboBox.this.resultSubject.notify(AutoCompleteJComboBox.this, lastEnteredResultType);
                    } else if (lastEnteredMatchType != null) {
                        AutoCompleteJComboBox.this.matchSubject.notify(AutoCompleteJComboBox.this, lastEnteredMatchType);
                    } else {
                        // Do nothing.
                    }

                    return;
                }   /* if(KeyEvent.VK_ENTER == e.getKeyCode()) */

                // If user removes item from text field, we will hidePopup and
                // removeAllItems. Please refer DocumentListener.handle, on why
                // don't we handle hidePopup and removeAllItems there.
                final Object object = AutoCompleteJComboBox.this.getEditor().getItem();
                if (object == null || object.toString().length() <= 0) {
                    AutoCompleteJComboBox.this.hidePopup();
                    AutoCompleteJComboBox.this.removeAllItems();
                }
            }   /* public void keyReleased(KeyEvent e) */
        };
    } 

    public boolean isGreedyEnabled() {
        return this.greedyEnabled;
    }
    
    public void setGreedyEnabled(boolean greedyEnabled, List<String> codeExtensionSortingOption) {
        this.greedyEnabled = greedyEnabled;
        this.codeExtensionSortingOption = codeExtensionSortingOption;
    }
    
    public void setAjaxProvider(AjaxServiceProvider ajaxServiceProvider, List<String> exchs) {
        this.ajaxServiceProvider = ajaxServiceProvider;
        if (this.ajaxServiceProvider == AjaxServiceProvider.Google) {
            this.ajaxGoogleSearchEngineMonitor.setExchs(exchs);
        }
    }
    
    private void adjustScrollBar() {
        final int max_search = 8;
        // i < max_search is just a safe guard when getAccessibleChildrenCount
        // returns an arbitary large number. 8 is magic number
        JPopupMenu popup = null;
        for (int i = 0, count = this.getUI().getAccessibleChildrenCount(this); i < count && i < max_search; i++) {
            Object o = this.getUI().getAccessibleChild(this, i);
            if (o instanceof JPopupMenu) {
                popup = (JPopupMenu)o;
                break;
            }
        }
        if (popup == null) {
            return;
        }
        JScrollPane scrollPane = null;
        for (int i = 0, count = popup.getComponentCount(); i < count && i < max_search; i++) {
            Component c = popup.getComponent(i);
            if (c instanceof JScrollPane) {
                scrollPane = (JScrollPane)c;
                break;
            }
        }
        if (scrollPane == null) {
            return;
        }
        scrollPane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    // WARNING : If Java is having a major refactor on BasicComboBoxEditor class,
    // the following workaround will break. However, this is the best we can do
    // at this moment.
    private class MyJComboBoxEditor extends BasicComboBoxEditor {
        @Override
        protected JTextField createEditorComponent() {
            final MyTextField _editor = new MyTextField("");
            // Is there a better way to configure the correct UI for
            // JTextField?
            if (UIManager.getLookAndFeel().getClass().getName().equals("javax.swing.plaf.metal.MetalLookAndFeel")) {
                final Component component = new MetalComboBoxEditor().getEditorComponent();
                if (component instanceof JComponent) {
                    final JComponent jComponent = (JComponent)component;
                    _editor.setBorder(jComponent.getBorder());
                }
            }
            else {
                _editor.setBorder(null);
            }
            return _editor;
        }

        public void setReadOnly(boolean readonly) {
            this.readonly = readonly;
        }

        private class MyTextField extends JTextField {
            public MyTextField(String s) {
                super(s);
            }
            
            // workaround for 4530952
            @Override
            public void setText(String s) {
                if (readonly || getText().equals(s)) {
                    return;
                }
                super.setText(s);
            }
        }

        private boolean readonly = false;
    }

    /***************************************************************************
     * START OF ONLINE DATABASE FEATURE
     **************************************************************************/
    private enum Mode {
        Offline,        // Suggestion will be getting through offline database.
        YahooOnline,    // Suggestion will be getting through online database.
        GoogleOnline    // Suggestion will be getting through online database.
    }

    private void changeMode(Mode mode) {
        ListCellRenderer me = null;

        if (mode == Mode.Offline) {
            // Check through JStockOptions, to determine which renderer to be
            // applied.
            if (JStock.getInstance().getJStockOptions().getStockInputSuggestionListOption() == JStockOptions.StockInputSuggestionListOption.OneColumn) {
                me = oldListCellRenderer;
            } else {
                assert(JStock.getInstance().getJStockOptions().getStockInputSuggestionListOption() == JStockOptions.StockInputSuggestionListOption.TwoColumns);
                me = offlineModeCellRenderer;
            }
        } else if (mode == Mode.YahooOnline) {
            me = yahooOnlineModeCellRenderer;
        } else if (mode == Mode.GoogleOnline) {
            me = googleOnlineModeCellRenderer;
        }

        if (this.currentListCellRenderer != me) {
            this.currentListCellRenderer = me;
            this.setRenderer(this.currentListCellRenderer);
            // When we change mode, the previous inserted item(s) no longer valid.
            // Let's clear them up first, before we switch to a new renderer.
            this.removeAllItems();
        }
    }

    private Observer<AjaxGoogleSearchEngineMonitor, MatchSetType> getGoogleMonitorObserver() {
        return new Observer<AjaxGoogleSearchEngineMonitor, MatchSetType>() {

            @Override
            public void update(final AjaxGoogleSearchEngineMonitor subject, final MatchSetType arg) {
                if (SwingUtilities.isEventDispatchThread()) {
                    _update(subject, arg);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            _update(subject, arg);
                        }
                    });
                }
            }
            
            public void _update(AjaxGoogleSearchEngineMonitor subject, MatchSetType arg)  {
                final String string = AutoCompleteJComboBox.this.getEditor().getItem().toString().trim();
                if (string.isEmpty() || false == string.equalsIgnoreCase(arg.Query)) {
                    return;
                }

                // We are no longer busy.
                busySubject.notify(AutoCompleteJComboBox.this, false);

                // During _update operation, there will be a lot of ListDataListeners
                // trying to modify the content of our text field. We will not allow
                // them to do so.
                //
                // Without setReadOnly(true), when we type the first character "w", IME
                // will suggest "我". However, when we call removeAllItems and addItem,
                // JComboBox will "commit" this suggestion to JComboBox's text field.
                // Hence, if we continue to type second character "m", the string displayed
                // at JComboBox's text field will be "我我我".
                //
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(true);

                // Must hide popup. If not, the pop up windows will not be
                // resized. But this causes flickering. :(
                AutoCompleteJComboBox.this.hidePopup();
                AutoCompleteJComboBox.this.removeAllItems();

                if (arg.Match.isEmpty() == false) {
                    // Change to online mode before adding any item.
                    changeMode(Mode.GoogleOnline);
                }

                boolean shouldShowPopup = false;
                for (MatchType match : arg.Match) {
                    AutoCompleteJComboBox.this.addItem(match);
                    shouldShowPopup = true;
                }
                if (shouldShowPopup) {
                    AutoCompleteJComboBox.this.showPopup();
                }

                // Restore.
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(false);
            }            
        };
    }
    
    private Observer<AjaxYahooSearchEngineMonitor, ResultSetType> getYahooMonitorObserver() {
        return new Observer<AjaxYahooSearchEngineMonitor, ResultSetType>() {
            @Override
            public void update(final AjaxYahooSearchEngineMonitor subject, final ResultSetType arg) {
                if (SwingUtilities.isEventDispatchThread()) {
                    _update(subject, arg);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            _update(subject, arg);
                        }
                    });
                }
            }

            public void _update(AjaxYahooSearchEngineMonitor subject, ResultSetType arg)  {
                final String string = AutoCompleteJComboBox.this.getEditor().getItem().toString().trim();
                if (string.isEmpty() || false == string.equalsIgnoreCase(arg.Query)) {
                    return;
                }

                // We are no longer busy.
                busySubject.notify(AutoCompleteJComboBox.this, false);

                // During _update operation, there will be a lot of ListDataListeners
                // trying to modify the content of our text field. We will not allow
                // them to do so.
                //
                // Without setReadOnly(true), when we type the first character "w", IME
                // will suggest "我". However, when we call removeAllItems and addItem,
                // JComboBox will "commit" this suggestion to JComboBox's text field.
                // Hence, if we continue to type second character "m", the string displayed
                // at JComboBox's text field will be "我我我".
                //
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(true);

                // Must hide popup. If not, the pop up windows will not be
                // resized. But this causes flickering. :(
                AutoCompleteJComboBox.this.hidePopup();
                AutoCompleteJComboBox.this.removeAllItems();

                if (arg.Result.isEmpty() == false) {
                    // Change to online mode before adding any item.
                    changeMode(Mode.YahooOnline);
                }

                boolean shouldShowPopup = false;
                for (ResultType result : arg.Result) {
                    AutoCompleteJComboBox.this.addItem(result);
                    shouldShowPopup = true;
                }
                if (shouldShowPopup) {
                    AutoCompleteJComboBox.this.showPopup();
                }

                // Restore.
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(false);
            }
        };
    }

    private final SubjectEx<AutoCompleteJComboBox, ResultType> resultSubject = new SubjectEx<AutoCompleteJComboBox, ResultType>();
    private final SubjectEx<AutoCompleteJComboBox, MatchType> matchSubject = new SubjectEx<AutoCompleteJComboBox, MatchType>();
    private final SubjectEx<AutoCompleteJComboBox, StockInfo> stockInfoSubject = new SubjectEx<AutoCompleteJComboBox, StockInfo>();
    private final SubjectEx<AutoCompleteJComboBox, Boolean> busySubject = new SubjectEx<AutoCompleteJComboBox, Boolean>();

    /**
     * Attach an observer to listen to stock info available event.
     *
     * @param observer an observer to listen to stock info available event
     */
    public void attachStockInfoObserver(Observer<AutoCompleteJComboBox, StockInfo> observer) {
        stockInfoSubject.attach(observer);
    }

    /**
     * Attach an observer to listen to ResultType available event.
     *
     * @param observer an observer to listen to ResultType available event
     */
    public void attachResultObserver(Observer<AutoCompleteJComboBox, ResultType> observer) {
        resultSubject.attach(observer);
    }

    public void attachMatchObserver(Observer<AutoCompleteJComboBox, MatchType> observer) {
        matchSubject.attach(observer);
    }
    
    /**
     * Attach an observer to listen to busy state event.
     *
     * @param observer an observer to listen to busy state event
     */
    public void attachBusyObserver(Observer<AutoCompleteJComboBox, Boolean> observer) {
        busySubject.attach(observer);
    }

    /**
     * Removes all observers for this combo box.
     */
    public void dettachAll() {
        // For offline database feature.
        stockInfoSubject.dettachAll();
        // For online database feature.
        resultSubject.dettachAll();
        matchSubject.dettachAll();
        busySubject.dettachAll();
    }
    
    /**
     * Stop Ajax threading activity in this combo box. Once stop, this combo box
     * can no longer be reused.
     */
    public void stop() {
        ajaxYahooSearchEngineMonitor.stop();
        ajaxGoogleSearchEngineMonitor.stop();
    }
    
    private void sortStockInfosIfPossible(List<StockInfo> stockInfos) {
        if (!greedyEnabled) {
            return;
        }
        
        final Map<String, Integer> m = new HashMap<String, Integer>();
        for (int i = 0, ei = codeExtensionSortingOption.size(); i < ei; i++) {
            m.put(codeExtensionSortingOption.get(i), i);
        }
        
        Collections.sort(stockInfos, new Comparator<StockInfo>() {

            @Override
            public int compare(StockInfo o1, StockInfo o2) {
                String str1 = o1.code.toString();
                String str2 = o2.code.toString();
                String extension1 = null;
                String extension2 = null;
                int index1 = str1.lastIndexOf(".");
                int index2 = str2.lastIndexOf(".");
                if (index1 >= 0) {
                    extension1 = str1.substring(index1 + 1);
                }
                if (index2 >= 0) {
                    extension2 = str2.substring(index2 + 1);
                }
                
                Integer order1 = m.get(extension1);
                Integer order2 = m.get(extension2);
                
                if (Objects.equals(order1, order2)) {
                    return str1.compareTo(str2);
                }
                
                // With extension comes first.
                if (order1 != null && order2 == null) {
                    return -1;
                }
                
                if (order1 == null && order2 != null) {
                    return 1;
                } 
                
                return order1 - order2;
            }      
        });
    }
    
    private boolean greedyEnabled = false;
    private List<String> codeExtensionSortingOption = java.util.Collections.emptyList();
    
    private final ListCellRenderer offlineModeCellRenderer = new StockInfoCellRenderer();
    private final ListCellRenderer yahooOnlineModeCellRenderer = new ResultSetCellRenderer();
    private final ListCellRenderer googleOnlineModeCellRenderer = new MatchSetCellRenderer();
    private final ListCellRenderer oldListCellRenderer;
    private ListCellRenderer currentListCellRenderer;

    // Online database.
    private AjaxServiceProvider ajaxServiceProvider;
    private final AjaxYahooSearchEngineMonitor ajaxYahooSearchEngineMonitor = new AjaxYahooSearchEngineMonitor();
    private final AjaxGoogleSearchEngineMonitor ajaxGoogleSearchEngineMonitor = new AjaxGoogleSearchEngineMonitor();
    
    /***************************************************************************
     * END OF ONLINE DATABASE FEATURE
     **************************************************************************/

    // Offline database.
    private StockInfoDatabase stockInfoDatabase;
    private final KeyAdapter keyAdapter;
    private final MyJComboBoxEditor jComboBoxEditor;

    private static final Log log = LogFactory.getLog(AutoCompleteJComboBox.class);
    
    /*
     * =========================================================================
     * START >>
     * 
     * Hacking code picked from
     * http://javabyexample.wisdomplug.com/java-concepts/34-core-java/59-tips-and-tricks-for-jtree-jlist-and-jcombobox-part-i.html
     *
     */
    
    /**
     * Set the combo box popup width.
     * 
     * @param popupWidth the combo box popup width
     */
    @Override
    public void setPopupWidth(int popupWidth) {
        this.popupWidth = popupWidth;
    }

    /**
    * Override to handle the popup Size.
    */
    @Override
    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        } finally {
            layingOut = false;
        }
    }

    /**
    * Overriden to handle the popup Size
    */
    @Override
    public Dimension getSize()
    {
        Dimension dim = super.getSize();
        if (!layingOut && popupWidth != 0) {
            // Ensure the popup size must be a least equal or larger than combo
            // box size.
            if (dim.width < popupWidth) {
                dim.width = popupWidth;
            }
        }
        return dim;
    }
    
    /**
     * Set the popup Width.
     */
    private int popupWidth = 0;

    /**
     * Keep track of whether layout is happening.
     */
    private boolean layingOut = false;
    
    /*
     * 
     * Hacking code picked from
     * http://javabyexample.wisdomplug.com/java-concepts/34-core-java/59-tips-and-tricks-for-jtree-jlist-and-jcombobox-part-i.html
     * 
     * << END
     * =========================================================================
     */    
}
