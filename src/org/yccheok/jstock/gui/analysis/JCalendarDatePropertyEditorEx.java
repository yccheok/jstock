/**
 * L2FProd.com Common Components 7.3 License.
 *
 * Copyright 2005-2007 L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Modified from l2fprod-commo's JCalendarDatePropertyEditor.java, to suit the
// need of JStock.

package org.yccheok.jstock.gui.analysis;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.util.Locale;
import org.yccheok.jstock.gui.Utils;

/**
 * Date Property Editor based on <a
 * href="http://www.toedter.com/en/jcalendar/index.html">toedter
 * JCalendar </a> component. <br>
 */
public class JCalendarDatePropertyEditorEx extends AbstractPropertyEditor {

  /**
   * Constructor for JCalendarDatePropertyEditor
   */
  public JCalendarDatePropertyEditorEx() {
    editor = Utils.getDefaultJDateChooser();
  }

  /**
   * Constructor for JCalendarDatePropertyEditor
   * 
   * @param dateFormatString string used to format the Date object,
   *          see: <b>java.text.SimpleDateFormat </b>
   * 
   * @param locale Locale used to display the Date object
   */
  public JCalendarDatePropertyEditorEx(String dateFormatString, Locale locale) {
    editor = Utils.getDefaultJDateChooser();
    ((JDateChooser)editor).setDateFormatString(dateFormatString);
    ((JDateChooser)editor).setLocale(locale);
  }

  /**
   * Constructor for JCalendarDatePropertyEditor
   * 
   * @param locale Locale used to display the Date object
   */
  public JCalendarDatePropertyEditorEx(Locale locale) {
    editor = Utils.getDefaultJDateChooser();
    ((JDateChooser)editor).setLocale(locale);
  }

  /**
   * Returns the Date of the Calendar
   * 
   * @return the date choosed as a <b>java.util.Date </b>b> object or
   *         null is the date is not set
   */
    @Override
  public Object getValue() {
    return ((JDateChooser)editor).getDate();
  }

  /**
   * Sets the Date of the Calendar
   * 
   * @param value the Date object
   */
    @Override
  public void setValue(Object value) {
    if (value != null) {
      ((JDateChooser)editor).setDate((Date)value);
    }
  }

  /**
   * Returns the Date formated with the locale and formatString set.
   * 
   * @return the choosen Date as String
   */
    @Override
  public String getAsText() {
    Date date = (Date)getValue();
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
      getDateFormatString());
    String s = formatter.format(date);
    return s;
  }

  /**
   * Sets the date format string. E.g "MMMMM d, yyyy" will result in
   * "July 21, 2004" if this is the selected date and locale is
   * English.
   * 
   * @param dateFormatString The dateFormatString to set.
   */
  public void setDateFormatString(String dateFormatString) {
    ((JDateChooser)editor).setDateFormatString(dateFormatString);
  }

  /**
   * Gets the date format string.
   * 
   * @return Returns the dateFormatString.
   */
  public String getDateFormatString() {
    return ((JDateChooser)editor).getDateFormatString();
  }

  /**
   * Sets the locale.
   * 
   * @param l The new locale value
   */
  public void setLocale(Locale l) {
    ((JDateChooser)editor).setLocale(l);
  }

  /**
   * Returns the Locale used.
   * 
   * @return the Locale object
   */
  public Locale getLocale() {
    return ((JDateChooser)editor).getLocale();
  }
  
}