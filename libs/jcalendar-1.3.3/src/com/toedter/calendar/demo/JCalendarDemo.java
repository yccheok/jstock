/*
 *  JCalendarDemo.java - Demonstration of JCalendar Java Bean
 *  Copyright (C) 2004 Kai Toedter
 *  kai@toedter.com
 *  www.toedter.com
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.toedter.calendar.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JDayChooser;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;
import com.toedter.components.JLocaleChooser;
import com.toedter.components.JSpinField;
import com.toedter.components.JTitlePanel;

/**
 * A demonstration Applet for the JCalendar bean. The demo can also be started
 * as Java application.
 * 
 * @author Kai Toedter
 * @version $LastChangedRevision: 103 $
 * @version $LastChangedDate: 2006-06-04 14:57:02 +0200 (So, 04 Jun 2006) $
 */
public class JCalendarDemo extends JApplet implements PropertyChangeListener {
	private static final long serialVersionUID = 6739986412544494316L;
	private JSplitPane splitPane;
	private JPanel calendarPanel;
	private JComponent[] beans;
	private JPanel propertyPanel;
	private JTitlePanel propertyTitlePanel;
	private JTitlePanel componentTitlePanel;
	private JPanel componentPanel;
	private JToolBar toolBar;

	/**
	 * Initializes the applet.
	 */
	public void init() {
		// Set the JGoodies Plastic 3D look and feel
		initializeLookAndFeels();

		// initialize all beans to demo
		beans = new JComponent[6];
		beans[0] = new DateChooserPanel();
		beans[1] = new JCalendar();
		beans[2] = new JDayChooser();
		beans[3] = new JMonthChooser();
		beans[4] = new JYearChooser();
		beans[5] = new JSpinField();

		((JSpinField) beans[5]).adjustWidthToMaximumValue();
		((JYearChooser) beans[4]).setMaximum(((JSpinField) beans[5]).getMaximum());
		((JYearChooser) beans[4]).adjustWidthToMaximumValue();

		getContentPane().setLayout(new BorderLayout());
		setJMenuBar(createMenuBar());

		toolBar = createToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		splitPane.setDividerSize(4);
		splitPane.setDividerLocation(190);

		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) splitPane.getUI()).getDivider();

		if (divider != null) {
			divider.setBorder(null);
		}

		propertyPanel = new JPanel();
		componentPanel = new JPanel();

		URL iconURL = beans[0].getClass().getResource(
				"images/" + beans[0].getName() + "Color16.gif");
		ImageIcon icon = new ImageIcon(iconURL);

		propertyTitlePanel = new JTitlePanel("Properties", null, propertyPanel, BorderFactory
				.createEmptyBorder(4, 4, 4, 4));

		componentTitlePanel = new JTitlePanel("Component", icon, componentPanel, BorderFactory
				.createEmptyBorder(4, 4, 0, 4));

		splitPane.setBottomComponent(propertyTitlePanel);
		splitPane.setTopComponent(componentTitlePanel);
		installBean(beans[0]);

		getContentPane().add(splitPane, BorderLayout.CENTER);
	}

	/**
	 * Installs the JGoodies Look & Feels, if available, in classpath.
	 */
	public final void initializeLookAndFeels() {
		// if in classpath thry to load JGoodies Plastic Look & Feel
		try {
			LookAndFeelInfo[] lnfs = UIManager.getInstalledLookAndFeels();
			boolean found = false;
			for (int i = 0; i < lnfs.length; i++) {
				if (lnfs[i].getName().equals("JGoodies Plastic 3D")) {
					found = true;
				}
			}
			if (!found) {
				UIManager.installLookAndFeel("JGoodies Plastic 3D",
						"com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
			}
			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		} catch (Throwable t) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the menu bar
	 * 
	 * @return Description of the Return Value
	 */
	public JToolBar createToolBar() {
		// Create the tool bar
		toolBar = new JToolBar();
		toolBar.putClientProperty("jgoodies.headerStyle", "Both");
		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		for (int i = 0; i < beans.length; i++) {
			Icon icon;
			JButton button;

			try {
				final JComponent bean = beans[i];
				URL iconURL = bean.getClass().getResource(
						"images/" + bean.getName() + "Color16.gif");
				icon = new ImageIcon(iconURL);

				button = new JButton(icon);

				ActionListener actionListener = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						installBean(bean);
					}
				};

				button.addActionListener(actionListener);
			} catch (Exception e) {
				System.out.println("JCalendarDemo.createToolBar(): " + e);
				button = new JButton(beans[i].getName());
			}

			button.setFocusPainted(false);
			toolBar.add(button);
		}

		return toolBar;
	}

	/**
	 * Creates the menu bar
	 * 
	 * @return Description of the Return Value
	 */
	public JMenuBar createMenuBar() {
		// Create the menu bar
		final JMenuBar menuBar = new JMenuBar();

		// Menu for all beans to demo
		JMenu componentsMenu = new JMenu("Components");
		componentsMenu.setMnemonic('C');

		menuBar.add(componentsMenu);

		for (int i = 0; i < beans.length; i++) {
			Icon icon;
			JMenuItem menuItem;

			try {
				URL iconURL = beans[i].getClass().getResource(
						"images/" + beans[i].getName() + "Color16.gif");
				icon = new ImageIcon(iconURL);
				menuItem = new JMenuItem(beans[i].getName(), icon);
			} catch (Exception e) {
				System.out.println("JCalendarDemo.createMenuBar(): " + e + " for URL: " + "images/"
						+ beans[i].getName() + "Color16.gif");
				menuItem = new JMenuItem(beans[i].getName());
			}

			componentsMenu.add(menuItem);

			final JComponent bean = beans[i];
			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					installBean(bean);
				}
			};

			menuItem.addActionListener(actionListener);
		}

		// Menu for the look and feels (lnfs).
		UIManager.LookAndFeelInfo[] lnfs = UIManager.getInstalledLookAndFeels();

		ButtonGroup lnfGroup = new ButtonGroup();
		JMenu lnfMenu = new JMenu("Look&Feel");
		lnfMenu.setMnemonic('L');

		menuBar.add(lnfMenu);

		for (int i = 0; i < lnfs.length; i++) {
			if (!lnfs[i].getName().equals("CDE/Motif")) {
				JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(lnfs[i].getName());
				lnfMenu.add(rbmi);

				// preselect the current Look & feel
				rbmi.setSelected(UIManager.getLookAndFeel().getName().equals(lnfs[i].getName()));

				// store lool & feel info as client property
				rbmi.putClientProperty("lnf name", lnfs[i]);

				// create and add the item listener
				rbmi.addItemListener(
				// inlining
						new ItemListener() {
							public void itemStateChanged(ItemEvent ie) {
								JRadioButtonMenuItem rbmi2 = (JRadioButtonMenuItem) ie.getSource();

								if (rbmi2.isSelected()) {
									// get the stored look & feel info
									UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo) rbmi2
											.getClientProperty("lnf name");

									try {
										menuBar.putClientProperty("jgoodies.headerStyle", "Both");
										UIManager.setLookAndFeel(info.getClassName());

										// update the complete application's
										// look & feel
										SwingUtilities.updateComponentTreeUI(JCalendarDemo.this);
										for (int i = 0; i < beans.length; i++) {
											SwingUtilities.updateComponentTreeUI(beans[i]);
										}
										// set the split pane devider border to
										// null
										BasicSplitPaneDivider divider = ((BasicSplitPaneUI) splitPane
												.getUI()).getDivider();

										if (divider != null) {
											divider.setBorder(null);
										}
									} catch (Exception e) {
										e.printStackTrace();

										System.err.println("Unable to set UI " + e.getMessage());
									}
								}
							}
						});
				lnfGroup.add(rbmi);
			}
		}

		// the help menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		JMenuItem aboutItem = helpMenu.add(new AboutAction(this));
		aboutItem.setMnemonic('A');
		aboutItem.setAccelerator(KeyStroke.getKeyStroke('A', java.awt.Event.CTRL_MASK));

		menuBar.add(helpMenu);

		return menuBar;
	}

	/**
	 * The applet is a PropertyChangeListener for "locale" and "calendar".
	 * 
	 * @param evt
	 *            Description of the Parameter
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (calendarPanel != null) {
			if (evt.getPropertyName().equals("calendar")) {
				// calendar = (Calendar) evt.getNewValue();
				// DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
				// jcalendar.getLocale());
				// dateField.setText(df.format(calendar.getTime()));
			}
		}
	}

	/**
	 * Creates a JFrame with a JCalendarDemo inside and can be used for testing.
	 * 
	 * @param s
	 *            The command line arguments
	 */
	public static void main(String[] s) {
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};

		JFrame frame = new JFrame("JCalendar Demo");
		frame.addWindowListener(l);

		JCalendarDemo demo = new JCalendarDemo();
		demo.init();
		frame.getContentPane().add(demo);
		frame.pack();
		frame.setBounds(200, 200, (int) frame.getPreferredSize().getWidth() + 20, (int) frame
				.getPreferredSize().getHeight() + 180);
		frame.setVisible(true);
	}

	/**
	 * Installes a demo bean.
	 * 
	 * @param bean
	 *            the demo bean
	 */
	private void installBean(JComponent bean) {
		try {
			componentPanel.removeAll();
			componentPanel.add(bean);

			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), bean.getClass()
					.getSuperclass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

			propertyPanel.removeAll();

			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;

			propertyPanel.setLayout(gridbag);

			int count = 0;

			String[] types = new String[] { "class java.util.Locale", "boolean", "int",
					"class java.awt.Color", "class java.util.Date", "class java.lang.String" };

			for (int t = 0; t < types.length; t++) {
				for (int i = 0; i < propertyDescriptors.length; i++) {
					if (propertyDescriptors[i].getWriteMethod() != null) {
						String type = propertyDescriptors[i].getPropertyType().toString();

						final PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
						final JComponent currentBean = bean;
						final Method readMethod = propertyDescriptor.getReadMethod();
						final Method writeMethod = propertyDescriptor.getWriteMethod();

						if (type.equals(types[t])
								&& (((readMethod != null) && (writeMethod != null)) || ("class java.util.Locale"
										.equals(type)))) {
							if ("boolean".equals(type)) {
								boolean isSelected = false;

								try {
									Boolean booleanObj = ((Boolean) readMethod.invoke(bean, null));
									isSelected = booleanObj.booleanValue();
								} catch (Exception e) {
									e.printStackTrace();
								}

								final JCheckBox checkBox = new JCheckBox("", isSelected);
								checkBox.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent event) {
										try {
											if (checkBox.isSelected()) {
												writeMethod.invoke(currentBean,
														new Object[] { new Boolean(true) });
											} else {
												writeMethod.invoke(currentBean,
														new Object[] { new Boolean(false) });
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
								addProperty(propertyDescriptors[i], checkBox, gridbag);
								count += 1;
							} else if ("int".equals(type)) {
								JSpinField spinField = new JSpinField();
								spinField.addPropertyChangeListener(new PropertyChangeListener() {
									public void propertyChange(PropertyChangeEvent evt) {
										try {
											if (evt.getPropertyName().equals("value")) {
												writeMethod.invoke(currentBean, new Object[] { evt
														.getNewValue() });
											}
										} catch (Exception e) {
										}
									}
								});

								try {
									Integer integerObj = ((Integer) readMethod.invoke(bean, null));
									spinField.setValue(integerObj.intValue());
								} catch (Exception e) {
									e.printStackTrace();
								}

								addProperty(propertyDescriptors[i], spinField, gridbag);
								count += 1;
							} else if ("class java.lang.String".equals(type)) {
								String string = "";

								try {
									string = ((String) readMethod.invoke(bean, null));
								} catch (Exception e) {
									e.printStackTrace();
								}

								JTextField textField = new JTextField(string);
								ActionListener actionListener = new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										try {
											writeMethod.invoke(currentBean, new Object[] { e
													.getActionCommand() });
										} catch (Exception ex) {
										}
									}
								};

								textField.addActionListener(actionListener);

								addProperty(propertyDescriptors[i], textField, gridbag);
								count += 1;
							} else if ("class java.util.Locale".equals(type)) {
								JLocaleChooser localeChooser = new JLocaleChooser(bean);
								localeChooser.setPreferredSize(new Dimension(200, localeChooser
										.getPreferredSize().height));
								addProperty(propertyDescriptors[i], localeChooser, gridbag);
								count += 1;
							} else if ("class java.util.Date".equals(type)) {
								Date date = null;

								try {
									date = ((Date) readMethod.invoke(bean, null));
								} catch (Exception e) {
									e.printStackTrace();
								}

								JDateChooser dateChooser = new JDateChooser(date);

								dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
									public void propertyChange(PropertyChangeEvent evt) {
										try {
											if (evt.getPropertyName().equals("date")) {
												writeMethod.invoke(currentBean, new Object[] { evt
														.getNewValue() });
											}
										} catch (Exception e) {
										}
									}
								});

								addProperty(propertyDescriptors[i], dateChooser, gridbag);
								count += 1;
							} else if ("class java.awt.Color".equals(type)) {
								final JButton button = new JButton();

								try {
									final Color colorObj = ((Color) readMethod.invoke(bean, null));
									button.setText("...");
									button.setBackground(colorObj);

									ActionListener actionListener = new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											Color newColor = JColorChooser.showDialog(
													JCalendarDemo.this, "Choose Color", colorObj);
											button.setBackground(newColor);

											try {
												writeMethod.invoke(currentBean,
														new Object[] { newColor });
											} catch (Exception e1) {
												e1.printStackTrace();
											}
										}
									};

									button.addActionListener(actionListener);
								} catch (Exception e) {
									e.printStackTrace();
								}

								addProperty(propertyDescriptors[i], button, gridbag);
								count += 1;
							}
						}
					}
				}
			}

			URL iconURL = bean.getClass().getResource("images/" + bean.getName() + "Color16.gif");
			ImageIcon icon = new ImageIcon(iconURL);

			componentTitlePanel.setTitle(bean.getName(), icon);
			bean.invalidate();
			propertyPanel.invalidate();
			componentPanel.invalidate();
			componentPanel.repaint();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}

	private void addProperty(PropertyDescriptor propertyDescriptor, JComponent editor,
			GridBagLayout grid) {
		String text = propertyDescriptor.getDisplayName();
		String newText = "";

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (((c >= 'A') && (c <= 'Z')) || (i == 0)) {
				if (i == 0) {
					c += ('A' - 'a');
				}

				newText += (" " + c);
			} else {
				newText += c;
			}
		}

		JLabel label = new JLabel(newText + ": ", null, JLabel.RIGHT);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		grid.setConstraints(label, c);
		propertyPanel.add(label);
		c.gridwidth = GridBagConstraints.REMAINDER;
		grid.setConstraints(editor, c);
		propertyPanel.add(editor);

		JPanel blankLine = new JPanel() {
			private static final long serialVersionUID = 4514530330521503732L;

			public Dimension getPreferredSize() {
				return new Dimension(10, 2);
			}
		};
		grid.setConstraints(blankLine, c);
		propertyPanel.add(blankLine);
	}

	/**
	 * Action to show the About dialog
	 * 
	 * @author toedter_k
	 */
	class AboutAction extends AbstractAction {
		private static final long serialVersionUID = -5204865941545323214L;
		private JCalendarDemo demo;

		/**
		 * Constructor for the AboutAction object
		 * 
		 * @param demo
		 *            Description of the Parameter
		 */
		AboutAction(JCalendarDemo demo) {
			super("About...");
			this.demo = demo;
		}

		/**
		 * Description of the Method
		 * 
		 * @param event
		 *            Description of the Parameter
		 */
		public void actionPerformed(ActionEvent event) {
			JOptionPane
					.showMessageDialog(
							demo,
							"JCalendar Demo\nVersion 1.3.2\n\nKai Toedter\nkai@toedter.com\nwww.toedter.com",
							"About...", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
