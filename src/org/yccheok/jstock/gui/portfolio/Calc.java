// calc-ui-model/Calc.java -- Fred Swartz
//     Level     : Intermediate.
//     Structure : Three files: main, GUI (subclass of JFrame), logic.
//     Components: JButton, JTextField (right justified).
//     Containers: JFrame, several JPanels.
//     Layouts   : BorderLayout to put the other panels together.
//                 Two GridLayout panels for the buttons.
//     Listeners : One ActionListener which is shared by all
//                 numeric key buttons.  Similarly share
//                 an ActionListener for all operator buttons.
//                 ActionListener for Clear button.
//     Other     : Use Font to enlarge font for components.
//               : try...catch for NumberFormatExceptions.

// Possible enhancements:
//               Check for zero before division.
//               Additional operations: mod, square root, sign change, ...
//               Make this work with doubles, BigInteger, or ...
//               Format double results with DecimalFormat
//               Add keyboard listener.
//               Change to RPN (Reverse Polish Notation)

/** calc-ui-model/CalcGUI.java - A GUI for the calculator.
 * @author Fred Swartz
 * @version 2004-04-20 Rodenbach, 2007-02-11 minor changes.
 */

package org.yccheok.jstock.gui.portfolio;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.*;
import org.yccheok.jstock.internationalization.GUIBundle;

///////////////////////////////////////////////////////////////////// class Calc
public class Calc extends javax.swing.JDialog {
    //================================================================ constants
    private static final Font BIGGER_FONT = new Font("monspaced", Font.PLAIN, 20);

    //=================================================================== fields
    //... Component referenced during execution
    private JTextField _displayField;       // display result / input.

    //... Variables representing state of the calculator
    private boolean   _startNumber = true;      // true: num key next
    private String    _previousOp  = "=";       // previous operation
    private CalcLogic _logic = new CalcLogic(); // The internal calculator.

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00######");

    //============================================================== method main
//    public static void main(String[] args) {
//        //... Set the Look and Feel to that of system we're running on.
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception unused) {
//            ; // Ignore exception because we can't do anything.  Will use default.
//        }
//
//        //... Create the window.
//        Calc window = new Calc();
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        window.setVisible(true);
//    }

    //============================================================== constructor
    public Calc(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        //... Set attributes of the display field
        _displayField = new JTextField("0", 12);
        _displayField.setEditable(false);
        _displayField.setHorizontalAlignment(JTextField.RIGHT);
        _displayField.setFont(BIGGER_FONT);

        //... Create and set attributes of clear button
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(BIGGER_FONT);
        clearButton.addActionListener(new ClearListener());

        //... Use one listener for all numeric keys.
        ActionListener numListener = new NumListener();

        //... Layout numeric keys in a grid.  Generate the buttons
        //    in a loop from the chars in a string.
        String buttonOrder = "7894561230 .";
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 3, 2, 2));
        for (int i = 0; i < buttonOrder.length(); i++) {
            String keyTop = buttonOrder.substring(i, i+1);
            JButton b = new JButton(keyTop);
            if (keyTop.equals(" ")) {
                //... Put a dummy button in this position.
                b.setEnabled(false);
            } else {
                //... Put a digit button in the interface.
                b.addActionListener(numListener);
                b.setFont(BIGGER_FONT);
            }
            buttonPanel.add(b);
        }

        //... One ActionListener to use for all operator buttons.
        ActionListener opListener = new OpListener();

        //... Create panel with gridlayout to hold operator buttons.
        //    Use array of button names to create buttons in a loop.
        JPanel opPanel = new JPanel();
        opPanel.setLayout(new GridLayout(5, 1, 2, 2));
        String[] opOrder = {"+", "-", "*", "/", "="};
        for (int i = 0; i < opOrder.length; i++) {
            JButton b = new JButton(opOrder[i]);
            b.addActionListener(opListener);
            b.setFont(BIGGER_FONT);
            opPanel.add(b);
        }

        //... Put Clear button in flow layout to keep from expanding.
        JPanel clearPanel = new JPanel();
        clearPanel.setLayout(new FlowLayout());
        clearPanel.add(clearButton);

        //... Layout the top-level content panel.
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(5, 5));
        content.add(_displayField, BorderLayout.NORTH );
        content.add(buttonPanel   , BorderLayout.CENTER);
        content.add(opPanel       , BorderLayout.EAST  );
        content.add(clearPanel    , BorderLayout.SOUTH );

        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //... Finish building the window (JFrame)
        this.setContentPane(content);
        this.pack();
        this.setTitle(GUIBundle.getString("MainFrame_Calculator"));
        this.setResizable(false);
        this.setLocationRelativeTo(parent);

        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }//end constructor


    //============================================================== actionClear
    /** Called by Clear btn action listener and elsewhere.*/
    private void actionClear() {
        _startNumber = true;         // Expecting number, not op.
        _displayField.setText("0");
        _previousOp  = "=";
        _logic.setTotal("0");
    }

    //////////////////////////////////////////// inner listener class OpListener
    /** Listener for all op buttons. */
    class OpListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // The calculator is always in one of two states.
            // 1. A number must be entered -- an operator is wrong.
            // 2. An operator must be entered.
            if (_startNumber) { // Error: needed number, not operator
                //... In this state we're expecting a number, but got an operator.
//                actionClear();
//                _displayField.setText("ERROR - No operator");
                
                // Overwrite previous operator.
                _previousOp = e.getActionCommand();
                return;
            } else {
                //... We're expecting an operator.
                _startNumber = true;  // Next thing must be a number
                try {
                    // Get value from display field, convert, do prev op
                    // If this is the first op, _previousOp will be =.
                    String displayText = _displayField.getText();

                    if (_previousOp.equals("=")) {
                        _logic.setTotal(displayText);
                    } else if (_previousOp.equals("+")) {
                        _logic.add(displayText);
                    } else if (_previousOp.equals("-")) {
                        _logic.subtract(displayText);
                    } else if (_previousOp.equals("*")) {
                        _logic.multiply(displayText);
                    } else if (_previousOp.equals("/")) {
                        _logic.divide(displayText);
                    }

                    _displayField.setText("" + decimalFormat.format(_logic.getCurrentTotal()));

                } catch (NumberFormatException ex) {
                    actionClear();
                    _displayField.setText("Error");
                }

                //... set _previousOp for the next operator.
                _previousOp = e.getActionCommand();
            }//endif _startNumber
        }//endmethod
    }//end class


    //////////////////////////////////// inner listener class ClearListener
    /** Action listener for numeric keys */
    class NumListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String digit = e.getActionCommand(); // Get text from button
            // We do not want to have more than one dot.
            if (digit.equals(".") && _displayField.getText().contains(digit)) {
                return;
            }
            if (_startNumber) {
                //... This is the first digit, clear field and set
                _displayField.setText(digit);
                _startNumber = false;
            } else {
                //... Add this digit to the end of the display field
                _displayField.setText(_displayField.getText() + digit);
            }
        }
    }


    //////////////////////////////////// inner listener class ClearListener
    class ClearListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            actionClear();
        }
    }
}
