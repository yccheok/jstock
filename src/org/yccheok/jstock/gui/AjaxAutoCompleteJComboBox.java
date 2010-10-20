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
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxEditor;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.AjaxYahooSearchEngine;
import org.yccheok.jstock.engine.AjaxYahooSearchEngine.ResultSetType;
import org.yccheok.jstock.engine.AjaxYahooSearchEngine.ResultType;
import org.yccheok.jstock.engine.AjaxYahooSearchEngineMonitor;
import org.yccheok.jstock.engine.Observer;
import org.yccheok.jstock.engine.Subject;

/**
 * This is a combo box, which will provides a list of suggested stocks, by
 * getting asynchronous Ajax search result from server.
 *
 * @author yccheok
 */
public class AjaxAutoCompleteJComboBox extends JComboBox {

    // Use SubjectEx, in order to make notify method public.
    private static class SubjectEx<S, A> extends Subject<S, A> {
        @Override
        public void notify(S subject, A arg) {
            super.notify(subject, arg);
        }
    }

    private final SubjectEx<AjaxAutoCompleteJComboBox, String> subject = new SubjectEx<AjaxAutoCompleteJComboBox, String>();

    public void attach(Observer<AjaxAutoCompleteJComboBox, String> observer) {
        subject.attach(observer);
    }

    public void dettach(Observer<AjaxAutoCompleteJComboBox, String> observer) {
        subject.dettach(observer);
    }

    public void dettachAll() {
        subject.dettachAll();
    }

    /**
     * Create an instance of AjaxAutoCompleteJComboBox.
     */
    public AjaxAutoCompleteJComboBox() {
        super();

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

        ajaxYahooSearchEngineMonitor.attach(getMonitorObserver());

        this.setRenderer(new ResultSetCellRenderer());
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

                ajaxYahooSearchEngineMonitor.clearAndPut(string);
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
                    if (AjaxAutoCompleteJComboBox.this.getItemCount() > 0) {
                        int index = AjaxAutoCompleteJComboBox.this.getSelectedIndex();
                        if (index == -1) {
                            lastEnteredString = AjaxAutoCompleteJComboBox.this.getItemAt(0).toString();
                        }
                        else {
                            lastEnteredString = AjaxAutoCompleteJComboBox.this.getItemAt(index).toString();
                        }
                    }
                    else {
                        final Object object = AjaxAutoCompleteJComboBox.this.getEditor().getItem();

                        if (object instanceof String) {
                            lastEnteredString = ((String)object).trim();
                        }
                        else {
                            lastEnteredString = "";
                        }
                    }

                    AjaxAutoCompleteJComboBox.this.removeAllItems();
                    AjaxAutoCompleteJComboBox.this.subject.notify(AjaxAutoCompleteJComboBox.this, lastEnteredString);
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

    private Observer<AjaxYahooSearchEngineMonitor, AjaxYahooSearchEngine.ResultSetType> getMonitorObserver() {
        return new Observer<AjaxYahooSearchEngineMonitor, AjaxYahooSearchEngine.ResultSetType>() {
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
                final String string = AjaxAutoCompleteJComboBox.this.getEditor().getItem().toString().trim();
                if (string.isEmpty() || false == string.equalsIgnoreCase(arg.Query)) {
                    return;
                }

                // During _update operation, there will be a lot of ListDataListeners
                // trying to modify the content of our text field. We will not allow
                // them to do so.
                //
                // Without setReadOnly(true), when we type the first character "w", IME
                // will suggest "我". However, when we call removeAllItems and addItem,
                // JComboBox will "commit" this suggestion to JComboBox's text field.
                // Hence, if we continue to type second character "m", the string displayed
                // at JComboBox's text field will be "我我们".
                //
                AjaxAutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(true);

                AjaxAutoCompleteJComboBox.this.hidePopup();
                AjaxAutoCompleteJComboBox.this.removeAllItems();

                boolean shouldShowPopup = false;
                for (ResultType result : arg.Result) {
                    AjaxAutoCompleteJComboBox.this.addItem(result);
                    shouldShowPopup = true;
                }
                if (shouldShowPopup) {
                    AjaxAutoCompleteJComboBox.this.showPopup();
                }

                // Restore.
                AjaxAutoCompleteJComboBox.this.jComboBoxEditor.setReadOnly(false);
            }
        };
    }

    @Override
    public void setUI(ComboBoxUI ui)
    {
        if (ui != null)
        {
            // Let's try our own customized UI.
            Class c = ui.getClass();
            final String myClass = "org.yccheok.jstock.gui.AjaxAutoCompleteJComboBox$My" + c.getSimpleName();

            try {
                ComboBoxUI myUI = (ComboBoxUI) Class.forName(myClass).newInstance();
                super.setUI(myUI);
                return;
            } catch (ClassNotFoundException ex) {
                log.error(null, ex);
            } catch (InstantiationException ex) {
                log.error(null, ex);
            } catch (IllegalAccessException ex) {
                log.error(null, ex);
            }
        }

        // Either null, or we fail to use our own customized UI.
        // Fall back to default.
        super.setUI(ui);
    }

    // This is a non-portable method to make combo box horizontal scroll bar.
    // Whenever there is a new look-n-feel, we need to manually provide the ComboBoxUI.
    // Any idea on how to make this portable?
    //
    protected static class MyWindowsComboBoxUI extends com.sun.java.swing.plaf.windows.WindowsComboBoxUI
    {
        @Override
        protected ComboPopup createPopup()
        {
            return new MyComboPopup(comboBox);
        }
    }

    protected static class MyMotifComboBoxUI extends com.sun.java.swing.plaf.motif.MotifComboBoxUI
    {
        @Override
        protected ComboPopup createPopup()
        {
            return new MyComboPopup(comboBox);
        }
    }

    protected static class MyMetalComboBoxUI extends javax.swing.plaf.metal.MetalComboBoxUI
    {
        @Override
        protected ComboPopup createPopup()
        {
            return new MyComboPopup(comboBox);
        }
    }

    private static class MyComboPopup extends BasicComboPopup
    {
        public MyComboPopup(JComboBox combo)
        {
            super(combo);
        }

        @Override
        public JScrollPane createScroller()
        {
            return new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

        @Override
        public Rectangle computePopupBounds(int px, int py, int pw, int ph)
        {
            int w = 418;
            return super.computePopupBounds(px, py, w, ph);
        }
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
     * Stop any threading activities.
     */
    public void stop() {
        ajaxYahooSearchEngineMonitor.stop();
    }

    private final AjaxYahooSearchEngineMonitor ajaxYahooSearchEngineMonitor = new AjaxYahooSearchEngineMonitor();
    private final MyJComboBoxEditor jComboBoxEditor;
    private final KeyAdapter keyAdapter;
    private static final Log log = LogFactory.getLog(AjaxAutoCompleteJComboBox.class);
}
