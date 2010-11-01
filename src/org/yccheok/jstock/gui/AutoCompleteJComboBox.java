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
import java.awt.Dimension;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.metal.MetalComboBoxEditor;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.yccheok.jstock.engine.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class AutoCompleteJComboBox extends JComboBox {

    // Use SubjectEx, in order to make notify method public.
    private static class SubjectEx<S, A> extends Subject<S, A> {
        @Override
        public void notify(S subject, A arg) {
            super.notify(subject, arg);
        }
    }

    private final SubjectEx<AutoCompleteJComboBox, String> subject = new SubjectEx<AutoCompleteJComboBox, String>();

    public void attach(Observer<AutoCompleteJComboBox, String> observer) {
        subject.attach(observer);
    }

    public void dettach(Observer<AutoCompleteJComboBox, String> observer) {
        subject.dettach(observer);
    }

    public void dettachAll() {
        subject.dettachAll();
    }

    /** Creates a new instance of AutoCompleteJComboBox */
    public AutoCompleteJComboBox() {
        this.stockCodeAndSymbolDatabase = null;
        
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

        this.addActionListener(getActionListener());

        // Have a wide enough drop down list.
        this.addPopupMenuListener(this.getPopupMenuListener());
        
        // Create horizontal scroll bar if needed.
        // (I am not sure I still need this one as I already have adjustPopupWidth)
        adjustScrollBar();
    }

    // http://forums.oracle.com/forums/thread.jspa?messageID=8037483&#8037483
    private void adjustPopupWidth() {
        if (this.getItemCount() == 0) return;
        Object comp = this.getUI().getAccessibleChild(this, 0);
        if (!(comp instanceof JPopupMenu)) {
            return;
        }
        JPopupMenu popup = (JPopupMenu) comp;
        JScrollPane scrollPane = (JScrollPane) popup.getComponent(0);
        //Object value = this.getItemAt(0);
        //Component rendererComp = this.getRenderer().getListCellRendererComponent(null, value, 0, false, false);
        //if (rendererComp instanceof JXTable) {
        //    scrollPane.setColumnHeaderView(((JTable) rendererComp).getTableHeader());
        //}
        BasicComboPopup basic = (BasicComboPopup)comp;
        Dimension prefSize = basic.getList().getPreferredSize();
        Dimension size = scrollPane.getPreferredSize();
        size.width = Math.max(size.width, prefSize.width);
        scrollPane.setPreferredSize(size);
        scrollPane.setMaximumSize(size);
        // Do we need to call revalidate?
        //scrollPane.revalidate();
    }

    private PopupMenuListener getPopupMenuListener() {
        return new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // We will have a much wider drop down list.
                adjustPopupWidth();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
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
                    String lastEnteredString = "";
                    /* Let us be extra paranoid. */
                    if (object instanceof String) {
                        lastEnteredString = (String)object;
                    }
                    else {
                        lastEnteredString = "";
                    }
                    
                    AutoCompleteJComboBox.this.subject.notify(AutoCompleteJComboBox.this, lastEnteredString);

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

                    return;
                }
            }
        };
    }

    public void setStockCodeAndSymbolDatabase(StockCodeAndSymbolDatabase stockCodeAndSymbolDatabase) {
        this.stockCodeAndSymbolDatabase = stockCodeAndSymbolDatabase;
        
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
                if (AutoCompleteJComboBox.this.getSelectedItem() != null) {
                    if (AutoCompleteJComboBox.this.getSelectedItem().equals(string)) {
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
                // at JComboBox's text field will be "我我们".
                //
                AutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(true);

                // Must hide popup. If not, the pop up windows will not be
                // resized.
                AutoCompleteJComboBox.this.hidePopup();
                AutoCompleteJComboBox.this.removeAllItems();
                if (AutoCompleteJComboBox.this.stockCodeAndSymbolDatabase != null) {
                    java.util.List<Code> codes = codes = stockCodeAndSymbolDatabase.searchStockCodes(string);

                    boolean shouldShowPopup = false;

                    // Here is our user friendly rule.
                    // (1) User will first like to search for their prefer stock by code. Hence, we only list
                    // out stock code to them. No more, no less.
                    // (2) If we cannot find any stock based on user given stock code, we will search by using
                    // stock symbol.
                    // (3) Do not search using both code and symbol at the same time. There are too much information,
                    // which will make user unhappy.
                    for (Code c : codes) {
                        AutoCompleteJComboBox.this.addItem(c.toString());
                        shouldShowPopup = true;
                    }

                    if (shouldShowPopup) {
                        AutoCompleteJComboBox.this.showPopup();
                    }
                    else {
                        java.util.List<Symbol> symbols = stockCodeAndSymbolDatabase.searchStockSymbols(string);

                        for (Symbol s : symbols) {
                            AutoCompleteJComboBox.this.addItem(s.toString());
                            shouldShowPopup = true;
                        }

                        if (shouldShowPopup) {
                            AutoCompleteJComboBox.this.showPopup();
                        }   // if (shouldShowPopup)
                    }   // if (shouldShowPopup)
                }   // if (AutoCompleteJComboBox.this.stockCodeAndSymbolDatabase != null)

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
                    String lastEnteredString = "";
                    if (AutoCompleteJComboBox.this.getItemCount() > 0) {
                        int index = AutoCompleteJComboBox.this.getSelectedIndex();
                        if (index == -1) {
                            lastEnteredString = (String)AutoCompleteJComboBox.this.getItemAt(0);
                        }
                        else {
                            lastEnteredString = (String)AutoCompleteJComboBox.this.getItemAt(index);
                        }
                    }
                    else {
                        final Object object = AutoCompleteJComboBox.this.getEditor().getItem();

                        if (object instanceof String) {
                            lastEnteredString = ((String)object).trim();
                        }
                        else {
                            lastEnteredString = "";
                        }
                    }

                    AutoCompleteJComboBox.this.removeAllItems();
                    AutoCompleteJComboBox.this.subject.notify(AutoCompleteJComboBox.this, lastEnteredString);
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

    private void adjustScrollBar() {
        //if (this.getItemCount() == 0) return;
        Object comp = this.getUI().getAccessibleChild(this, 0);
        if (!(comp instanceof JPopupMenu)) {
            return;
        }
        JPopupMenu popup = (JPopupMenu) comp;
        JScrollPane scrollPane = (JScrollPane) popup.getComponent(0);
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

    private StockCodeAndSymbolDatabase stockCodeAndSymbolDatabase;
    private final KeyAdapter keyAdapter;
    private final MyJComboBoxEditor jComboBoxEditor;

    private static final Log log = LogFactory.getLog(AutoCompleteJComboBox.class);
}
