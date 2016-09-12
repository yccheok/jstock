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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.metal.MetalComboBoxEditor;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.AjaxGoogleSearchEngineMonitor;
import org.yccheok.jstock.engine.AjaxStockInfoSearchEngine;
import org.yccheok.jstock.engine.ResultSetType;
import org.yccheok.jstock.engine.ResultType;
import org.yccheok.jstock.engine.AjaxYahooSearchEngineMonitor;
import org.yccheok.jstock.engine.DispType;
import org.yccheok.jstock.engine.MatchSetType;
import org.yccheok.jstock.engine.MatchType;
import org.yccheok.jstock.engine.Observer;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.Subject;

/**
 * This is a combo box, which will provides a list of suggested stocks, by
 * getting asynchronous Ajax search result from server.
 *
 * @author yccheok
 */
public class AjaxAutoCompleteJComboBox extends JComboBox implements JComboBoxPopupAdjustable {

    // Use SubjectEx, in order to make notify method public.
    private static class SubjectEx<S, A> extends Subject<S, A> {
        @Override
        public void notify(S subject, A arg) {
            super.notify(subject, arg);
        }
    }

    private final SubjectEx<AjaxAutoCompleteJComboBox, DispType> dispSubject = new SubjectEx<AjaxAutoCompleteJComboBox, DispType>();
    private final SubjectEx<AjaxAutoCompleteJComboBox, Boolean> busySubject = new SubjectEx<AjaxAutoCompleteJComboBox, Boolean>();

    /**
     * Attach an observer to listen to ResultType available event.
     *
     * @param observer An observer to listen to ResultType available event
     */
    public void attachDispObserver(Observer<AjaxAutoCompleteJComboBox, DispType> observer) {
        dispSubject.attach(observer);
    }

    
    /**
     * Attach an observer to listen to busy state event.
     *
     * @param observer An observer to listen to busy state event
     */
    public void attachBusyObserver(Observer<AjaxAutoCompleteJComboBox, Boolean> observer) {
        busySubject.attach(observer);
    }

    /**
     * Dettach all observers.
     */
    public void dettachAll() {
        dispSubject.dettachAll();
        busySubject.dettachAll();
    }

    /**
     * Create an instance of AjaxAutoCompleteJComboBox.
     */
    public AjaxAutoCompleteJComboBox() {
        super();

        this.setModel(new SortedComboBoxModel());
        
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
            log.error("Unable to attach DocumentListener to AjaxAutoCompleteJComboBox.");
        }

        this.setRenderer(new DispTypeCellRenderer());
        
        ajaxYahooSearchEngineMonitor.attach(getYahooMonitorObserver());
        ajaxGoogleSearchEngineMonitor.attach(getGoogleMonitorObserver());

        this.addActionListener(getActionListener());

        // Have a wide enough drop down list.
        this.addPopupMenuListener(this.getPopupMenuListener());

        // Create horizontal scroll bar if needed.
        // (I am not sure I still need this one as I already have adjustPopupWidth)
        this.adjustScrollBar();
    }

    private ActionListener getActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Handle mouse clicked. */
                if ((e.getModifiers() & java.awt.event.InputEvent.BUTTON1_MASK) == java.awt.event.InputEvent.BUTTON1_MASK) {
                    // Not sure why during debug mode, we cannot enter this block during mouse click?

                    final Object object = AjaxAutoCompleteJComboBox.this.getEditor().getItem();
                    
                    // The object can be either String or AjaxYahooSearchEngine.ResultType.
                    // If user keys in the item, editor's item will be String.
                    // If user clicks on the drop down list, editor's item will be
                    // AjaxYahooSearchEngine.ResultType.
                    if (object instanceof DispType) {
                        DispType lastEnteredResult = (DispType)object;
                        AjaxAutoCompleteJComboBox.this.dispSubject.notify(AjaxAutoCompleteJComboBox.this, lastEnteredResult);
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // We schedule the below actions in GUI event queue,
                            // so that DocumentListener will not be triggered.
                            // But I am not sure why.
                            AjaxAutoCompleteJComboBox.this.getEditor().setItem(null);
                            AjaxAutoCompleteJComboBox.this.hidePopup();
                            AjaxAutoCompleteJComboBox.this.removeAllItems();
                        }
                    });

                    return;
                }
            }
        };
    }

    private PopupMenuListener getPopupMenuListener() {
        return new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // We will have a much wider drop down list.
                Utils.adjustPopupWidth(AjaxAutoCompleteJComboBox.this);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Reset popup width.
                AjaxAutoCompleteJComboBox.this.setPopupWidth(-1);                
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Reset popup width.
                AjaxAutoCompleteJComboBox.this.setPopupWidth(-1);                
            }
        };
    }

    private DocumentListener getDocumentListener() {
       return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    final String string = e.getDocument().getText(0, e.getDocument().getLength()).trim();
                    _handle(string);
                } catch (BadLocationException ex) {
                    log.error(null, ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    final String string = e.getDocument().getText(0, e.getDocument().getLength()).trim();
                    _handle(string);
                } catch (BadLocationException ex) {
                    log.error(null, ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    final String string = e.getDocument().getText(0, e.getDocument().getLength()).trim();
                    _handle(string);
                } catch (BadLocationException ex) {
                    log.error(null, ex);
                }
            }

            private void _handle(final String string) {
                // We are no longer busy.
                busySubject.notify(AjaxAutoCompleteJComboBox.this, false);

                if (AjaxAutoCompleteJComboBox.this.getSelectedItem() != null) {
                    if (AjaxAutoCompleteJComboBox.this.getSelectedItem().toString().equals(string)) {
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

                // We are busy contacting server right now.
                busySubject.notify(AjaxAutoCompleteJComboBox.this, true);
                
                canRemoveAllItems = true;
                ajaxYahooSearchEngineMonitor.clearAndPut(string);
                ajaxGoogleSearchEngineMonitor.clearAndPut(string);
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
                    busySubject.notify(AjaxAutoCompleteJComboBox.this, false);

                    DispType lastEnteredDispType = null;

                    if (AjaxAutoCompleteJComboBox.this.getItemCount() > 0) {
                        int index = AjaxAutoCompleteJComboBox.this.getSelectedIndex();

                        if (index == -1) {
                            Object object = AjaxAutoCompleteJComboBox.this.getItemAt(0);
                            if (object instanceof DispType) {
                                lastEnteredDispType = (DispType)object;
                            }
                        }
                        else {
                            Object object = AjaxAutoCompleteJComboBox.this.getItemAt(index);
                            if (object instanceof DispType) {
                                lastEnteredDispType = (DispType)object;
                            }                         
                        }
                    }
                    else {
                        final Object object = AjaxAutoCompleteJComboBox.this.getEditor().getItem();
                        
                        if (object instanceof String) {
                            // All upper-case, if the result is not coming from server.
                            final String string = ((String)object).trim().toUpperCase();
                            if (string.length() > 0) {
                                lastEnteredDispType = new ResultType(string, string);
                            }
                        }
                    }

                    AjaxAutoCompleteJComboBox.this.removeAllItems();
                    if (lastEnteredDispType != null) {
                        AjaxAutoCompleteJComboBox.this.dispSubject.notify(AjaxAutoCompleteJComboBox.this, lastEnteredDispType);
                    }
                    return;
                }   /* if(KeyEvent.VK_ENTER == e.getKeyCode()) */

                // If user removes item from text field, we will hidePopup and
                // removeAllItems. Please refer DocumentListener.handle, on why
                // don't we handle hidePopup and removeAllItems there.
                final Object object = AjaxAutoCompleteJComboBox.this.getEditor().getItem();
                if (object == null || object.toString().length() <= 0) {
                    AjaxAutoCompleteJComboBox.this.hidePopup();
                    AjaxAutoCompleteJComboBox.this.removeAllItems();
                }
            }   /* public void keyReleased(KeyEvent e) */
        };
    }

    private Observer<AjaxGoogleSearchEngineMonitor, MatchSetType> getGoogleMonitorObserver() {
        return new Observer<AjaxGoogleSearchEngineMonitor, MatchSetType>() {

            @Override
            public void update(final AjaxGoogleSearchEngineMonitor subject, MatchSetType arg) {
                // Can we further enhance our search result?
                if (arg.Match.isEmpty()) {
                    StockInfo stockInfo = ajaxStockInfoSearchEngine.search(arg.Query);
                    if (stockInfo != null) {
                        MatchType matchType = new MatchType(stockInfo.code.toString().toUpperCase(), stockInfo.symbol.toString(), null, null);
                        List<MatchType> matchTypes = new ArrayList<>();
                        matchTypes.add(matchType);
                        MatchSetType matchSetType = MatchSetType.newInstance(arg.Query, matchTypes);
                        // Overwrite!
                        arg = matchSetType;
                    }
                }

                final MatchSetType _arg = arg;

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
            
            public void _update(AjaxGoogleSearchEngineMonitor subject, MatchSetType arg)  {
                final String string = AjaxAutoCompleteJComboBox.this.getEditor().getItem().toString().trim();
                if (string.isEmpty() || false == string.equalsIgnoreCase(arg.Query)) {
                    return;
                }

                // We are no longer busy.
                busySubject.notify(AjaxAutoCompleteJComboBox.this, false);

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
                AjaxAutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(true);

                // Must hide popup. If not, the pop up windows will not be
                // resized. But this causes flickering. :(
                boolean isPopupHide = false;
                if (canRemoveAllItems) {
                    canRemoveAllItems = false;
                    
                    isPopupHide = true;
                    AjaxAutoCompleteJComboBox.this.hidePopup();
                    AjaxAutoCompleteJComboBox.this.removeAllItems();
                    
                    codes.clear();
                }

                for (MatchType match : arg.Match) {
                    if (codes.contains(match.getCode().toString())) {
                        continue;
                    }
                    
                    if (!isPopupHide) {
                        isPopupHide = true;
                        AjaxAutoCompleteJComboBox.this.hidePopup();
                    }
                    
                    codes.add(match.getCode().toString());
                    AjaxAutoCompleteJComboBox.this.addItem(match);
                }
                if (isPopupHide && AjaxAutoCompleteJComboBox.this.getItemCount() > 0) {
                    AjaxAutoCompleteJComboBox.this.showPopup();
                }

                // Restore.
                AjaxAutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(false);
            }            
        };
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
                final String string = AjaxAutoCompleteJComboBox.this.getEditor().getItem().toString().trim();
                if (string.isEmpty() || false == string.equalsIgnoreCase(arg.Query)) {
                    return;
                }

                // We are no longer busy.
                busySubject.notify(AjaxAutoCompleteJComboBox.this, false);

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
                AjaxAutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(true);

                // Must hide popup. If not, the pop up windows will not be
                // resized. But this causes flickering. :(
                boolean isPopupHide = false;
                
                if (canRemoveAllItems) {
                    canRemoveAllItems = false;
                    
                    isPopupHide = true;
                    AjaxAutoCompleteJComboBox.this.hidePopup();
                    AjaxAutoCompleteJComboBox.this.removeAllItems();
                    
                    codes.clear();
                }

                boolean shouldShowPopup = false;
                for (ResultType result : arg.Result) {
                    if (codes.contains(result.symbol)) {
                        continue;
                    }
                    
                    if (!isPopupHide) {
                        isPopupHide = true;
                        AjaxAutoCompleteJComboBox.this.hidePopup();
                    }
                    
                    codes.add(result.symbol);
                    AjaxAutoCompleteJComboBox.this.addItem(result);
                }
                if (isPopupHide && AjaxAutoCompleteJComboBox.this.getItemCount() > 0) {
                    AjaxAutoCompleteJComboBox.this.showPopup();
                }

                // Restore.
                AjaxAutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(false);
            }
        };
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

    /**
     * Stop Ajax threading activity in this combo box. Once stop, this combo box
     * can no longer be reused.
     */
    public void stop() {
        ajaxYahooSearchEngineMonitor.stop();
        ajaxGoogleSearchEngineMonitor.stop();
    }

    // Online database. 
    private final AjaxYahooSearchEngineMonitor ajaxYahooSearchEngineMonitor = new AjaxYahooSearchEngineMonitor();
    private final AjaxGoogleSearchEngineMonitor ajaxGoogleSearchEngineMonitor = new AjaxGoogleSearchEngineMonitor();
    private final AjaxStockInfoSearchEngine ajaxStockInfoSearchEngine = new AjaxStockInfoSearchEngine();
    
    private final MyJComboBoxEditor jComboBoxEditor;
    private final KeyAdapter keyAdapter;
    private static final Log log = LogFactory.getLog(AjaxAutoCompleteJComboBox.class);
    
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
