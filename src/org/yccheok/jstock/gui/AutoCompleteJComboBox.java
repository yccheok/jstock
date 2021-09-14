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

package org.yccheok.jstock.gui;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
                    if (object instanceof DispType) {
                        DispType lastEnteredDisp = (DispType)object;
                        AutoCompleteJComboBox.this.dispSubject.notify(AutoCompleteJComboBox.this, lastEnteredDisp);
                    } else if (object instanceof StockInfo) {
                        // From our offline database.
                        StockInfo lastEnteredStockInfo = (StockInfo)object;
                        AutoCompleteJComboBox.this.stockInfoSubject.notify(AutoCompleteJComboBox.this, lastEnteredStockInfo);
                    } else {
                        assert(false);
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

     private void injectWithGreedyIEXSQLiteResultIfPossible(List<StockInfo> stockInfos, Set<Code> codes, String string) {
        if (JStock.instance().getJStockOptions().getCountry() != Country.UnitedState) {
            return;
        }

        final int size = stockInfos.size();

        if (size >= TRANSACTION_MAX_SIZE) {
            return;
        }

        //
        // Can we enhance search using local SQLite from IEX, by searching via Symbol.
        //
        if (stockInfos.isEmpty()) {
            // Perhaps we should consider escape percentage sign in string.
            // https://stackoverflow.com/questions/23318708/how-to-escape-a-sign-in-sqlite/45861206

            // Name in IEX means symbol in JStock. We use double percentage sign for greedy purpose.
            List<StockInfo> tmpStockInfos = IEXStockInfoSQLiteOpenHelper.INSTANCE.findViaName("%" + string + "%", TRANSACTION_MAX_SIZE);
            for (StockInfo tmpStockInfo : tmpStockInfos) {
                if (!codes.contains(tmpStockInfo.code)) {
                    stockInfos.add(tmpStockInfo);
                    codes.add(tmpStockInfo.code);
                }
            }
        }
    }

    private void injectWithIEXSQLiteResultIfPossible(List<StockInfo> stockInfos, Set<Code> codes, String string) {
        if (JStock.instance().getJStockOptions().getCountry() != Country.UnitedState) {
            return;
        }

        final int size = stockInfos.size();

        if (size >= TRANSACTION_MAX_SIZE) {
            return;
        }

        //
        // Can we enhance search using local SQLite from IEX, by searching via Code.
        //
        {
            // Perhaps we should consider escape percentage sign in string.
            // https://stackoverflow.com/questions/23318708/how-to-escape-a-sign-in-sqlite/45861206

            // Symbol in IEX means code in JStock.
            List<StockInfo> tmpStockInfos = IEXStockInfoSQLiteOpenHelper.INSTANCE.findViaSymbol(string + "%", TRANSACTION_MAX_SIZE - size);
            for (StockInfo tmpStockInfo : tmpStockInfos) {
                if (!codes.contains(tmpStockInfo.code)) {
                    stockInfos.add(tmpStockInfo);
                    codes.add(tmpStockInfo.code);
                }
            }
        }

        //
        // Can we enhance search using local SQLite from IEX, by searching via Symbol.
        //
        if (stockInfos.isEmpty()) {
            // Perhaps we should consider escape percentage sign in string.
            // https://stackoverflow.com/questions/23318708/how-to-escape-a-sign-in-sqlite/45861206

            // Name in IEX means symbol in JStock.
            List<StockInfo> tmpStockInfos = IEXStockInfoSQLiteOpenHelper.INSTANCE.findViaName(string + "%", TRANSACTION_MAX_SIZE);
            for (StockInfo tmpStockInfo : tmpStockInfos) {
                if (!codes.contains(tmpStockInfo.code)) {
                    stockInfos.add(tmpStockInfo);
                    codes.add(tmpStockInfo.code);
                }
            }
        }
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
                // will suggest ... However, when we call removeAllItems and addItem,
                // JComboBox will "commit" this suggestion to JComboBox's text field.
                // Hence, if we continue to type second character "m", the string displayed
                // at JComboBox's text field will be ...
                //
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(true);

                // Must hide popup. If not, the pop up windows will not be
                // resized.
                AutoCompleteJComboBox.this.hidePopup();
                AutoCompleteJComboBox.this.removeAllItems();
                
                final List<StockInfo> stockInfos = new ArrayList<>();
                final Set<Code> codes = new HashSet<>();
                
                // STEP 1: Look for local SQLITE.

                // For US only.
                injectWithIEXSQLiteResultIfPossible(stockInfos, codes, string);

                // STEP 2: Look for more stocks from local CSV.
                
                if (AutoCompleteJComboBox.this.stockInfoDatabase != null) {
                    final List<StockInfo> tmpStockInfos = 
                            greedyEnabled ?
                            stockInfoDatabase.greedySearchStockInfos(string) :
                            stockInfoDatabase.searchStockInfos(string);

                    for (StockInfo tmpStockInfo : tmpStockInfos) {
                        if (!codes.contains(tmpStockInfo.code)) {
                            stockInfos.add(tmpStockInfo);
                            codes.add(tmpStockInfo.code);
                        }
                    }
                    
                    // For India only.
                    sortStockInfosIfPossible(stockInfos);

                }   // if (AutoCompleteJComboBox.this.stockInfoDatabase != null)

                
                // STEP 3: Perform greedy search from local SQLite if no result.

                if (stockInfos.isEmpty()) {
                    injectWithGreedyIEXSQLiteResultIfPossible(stockInfos, codes, string);
                }
                    
                if (stockInfos.isEmpty()) {
                    // OK. We found nothing from offline database. Let's
                    // ask help from online database.
                    // We are busy contacting server right now.

                    // TODO
                    // Only enable ajaxYahooSearchEngineMonitor, till we solve
                    // http://sourceforge.net/apps/mediawiki/jstock/index.php?title=TechnicalDisability
                    busySubject.notify(AutoCompleteJComboBox.this, true);
                    
                    canRemoveAllItems = true;
                    ajaxYahooSearchEngineMonitor.clearAndPut(string);
                } else {
                     Collections.sort(stockInfos, (s0, s1) -> s0.code.toString().compareTo(s1.code.toString()));

                    //
                    // Control the size of list.
                    //
                    final int size = stockInfos.size();
                    if (size > TRANSACTION_MAX_SIZE) {
                        // Avoid android.os.TransactionTooLargeException.
                        stockInfos.subList(TRANSACTION_MAX_SIZE, size).clear();
                    }
                    
                    // Change to offline mode before adding any item.
                    changeMode(Mode.Offline);
                        
                    for (StockInfo stockInfo : stockInfos) {
                        AutoCompleteJComboBox.this.addItem(stockInfo);
                    }
                    
                    AutoCompleteJComboBox.this.showPopup();
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
                    DispType lastEnteredDispType = null;
                    
                    if (AutoCompleteJComboBox.this.getItemCount() > 0) {
                        int index = AutoCompleteJComboBox.this.getSelectedIndex();
                        if (index == -1) {
                            Object object = AutoCompleteJComboBox.this.getItemAt(0);
                            if (object instanceof StockInfo) {
                                lastEnteredStockInfo = (StockInfo)object;
                            } else if (object instanceof DispType) {
                                lastEnteredDispType = (DispType)object;
                            }
                        }
                        else {
                            Object object = AutoCompleteJComboBox.this.getItemAt(index);
                            if (object instanceof StockInfo) {
                                lastEnteredStockInfo = (StockInfo)object;
                            } else if (object instanceof DispType) {
                                lastEnteredDispType = (DispType)object;
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
                    } else if (lastEnteredDispType != null) {
                        AutoCompleteJComboBox.this.dispSubject.notify(AutoCompleteJComboBox.this, lastEnteredDispType);
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
        Online          // Suggestion will be getting through online database.
    }

    private void changeMode(Mode mode) {
        ListCellRenderer me = null;

        if (mode == Mode.Offline) {
            if (this.getModel() instanceof SortedComboBoxModel) {
                this.setModel(new DefaultComboBoxModel());
            }
            
            // Check through JStockOptions, to determine which renderer to be
            // applied.
            if (JStock.instance().getJStockOptions().getStockInputSuggestionListOption() == JStockOptions.StockInputSuggestionListOption.OneColumn) {
                me = oldListCellRenderer;
            } else {
                assert(JStock.instance().getJStockOptions().getStockInputSuggestionListOption() == JStockOptions.StockInputSuggestionListOption.TwoColumns);
                me = offlineModeCellRenderer;
            }
        } else if (mode == Mode.Online) {
            if (!(this.getModel() instanceof SortedComboBoxModel)) {
                this.setModel(new SortedComboBoxModel());
            }
            
            me = onlineModeCellRenderer;
        }

        if (this.currentListCellRenderer != me) {
            this.currentListCellRenderer = me;
            this.setRenderer(this.currentListCellRenderer);
            // When we change mode, the previous inserted item(s) no longer valid.
            // Let's clear them up first, before we switch to a new renderer.
            this.removeAllItems();
        }
    }
   
    private Observer<AjaxYahooSearchEngineMonitor, ResultSetType> getYahooMonitorObserver() {
        return new Observer<AjaxYahooSearchEngineMonitor, ResultSetType>() {
            @Override
            public void update(final AjaxYahooSearchEngineMonitor subject, ResultSetType arg) {
                // Can we further enhance our search result?
                if (arg.Result.isEmpty()) {
                    StockInfo stockInfo = ajaxStockInfoSearchEngine.search(arg.Query);
                    if (stockInfo != null) {
                        ResultType resultType = new ResultType(stockInfo.code.toString().toUpperCase(), stockInfo.symbol.toString());
                        List<ResultType> resultTypes = new ArrayList<>();
                        resultTypes.add(resultType);
                        // Overwrite!
                        arg = ResultSetType.newInstance(arg.Query, resultTypes);
                    }
                }


                // Make it a mutable data structure so that we can sort and sublist.
                final List<ResultType> resultTypes = new ArrayList<>();
                resultTypes.addAll(arg.Result);
                arg = ResultSetType.newInstance(arg.Query, resultTypes);

                Collections.sort(arg.Result, (r0, r1) -> r0.symbol.toString().compareTo(r1.symbol.toString()));
                
                final int size = arg.Result.size();
                if (size > TRANSACTION_MAX_SIZE) {
                    // Avoid android.os.TransactionTooLargeException.
                    arg.Result.subList(TRANSACTION_MAX_SIZE, size).clear();
                }

                final ResultSetType _arg = arg;
                
                if (SwingUtilities.isEventDispatchThread()) {
                    _update(subject, _arg);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            _update(subject, _arg);
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
                // will suggest ... However, when we call removeAllItems and addItem,
                // JComboBox will "commit" this suggestion to JComboBox's text field.
                // Hence, if we continue to type second character "m", the string displayed
                // at JComboBox's text field will be ...
                //
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(true);

                // Must hide popup. If not, the pop up windows will not be
                // resized. But this causes flickering. :(
                boolean isPopupHide = false;
                if (canRemoveAllItems) {
                    canRemoveAllItems = false;
                    
                    isPopupHide = true;
                    AutoCompleteJComboBox.this.hidePopup();
                    AutoCompleteJComboBox.this.removeAllItems();
                    
                    codes.clear();
                }                

                if (arg.Result.isEmpty() == false) {
                    // Change to online mode before adding any item.
                    changeMode(Mode.Online);
                }

                for (ResultType result : arg.Result) {
                    if (codes.contains(result.symbol)) {
                        continue;
                    }
                    
                    if (!isPopupHide) {
                        isPopupHide = true;
                        AutoCompleteJComboBox.this.hidePopup();
                    }
                    
                    codes.add(result.symbol);
                    AutoCompleteJComboBox.this.addItem(result);
                }
                if (isPopupHide && AutoCompleteJComboBox.this.getItemCount() > 0) {
                    AutoCompleteJComboBox.this.showPopup();
                }

                // Restore.
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(false);
            }
        };
    }

    private final SubjectEx<AutoCompleteJComboBox, DispType> dispSubject = new SubjectEx<AutoCompleteJComboBox, DispType>();
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
    public void attachDispObserver(Observer<AutoCompleteJComboBox, DispType> observer) {
        dispSubject.attach(observer);
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
        dispSubject.dettachAll();
        busySubject.dettachAll();
    }
    
    /**
     * Stop Ajax threading activity in this combo box. Once stop, this combo box
     * can no longer be reused.
     */
    public void stop() {
        ajaxYahooSearchEngineMonitor.stop();
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
    private final ListCellRenderer onlineModeCellRenderer = new DispTypeCellRenderer();
    private final ListCellRenderer oldListCellRenderer;
    private ListCellRenderer currentListCellRenderer;

    // Online database.
    private final AjaxYahooSearchEngineMonitor ajaxYahooSearchEngineMonitor = new AjaxYahooSearchEngineMonitor();
    private final AjaxStockInfoSearchEngine ajaxStockInfoSearchEngine = new AjaxStockInfoSearchEngine();
    
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
    
    private static final int TRANSACTION_MAX_SIZE = 100;
    
    /**
     * Set the popup Width.
     */
    private int popupWidth = 0;

    /**
     * Keep track of whether layout is happening.
     */
    private boolean layingOut = false;
    
    private volatile boolean canRemoveAllItems = false;
    private final Set<String> codes = new HashSet<>();
    
    /*
     * 
     * Hacking code picked from
     * http://javabyexample.wisdomplug.com/java-concepts/34-core-java/59-tips-and-tricks-for-jtree-jlist-and-jcombobox-part-i.html
     * 
     * << END
     * =========================================================================
     */    
}
