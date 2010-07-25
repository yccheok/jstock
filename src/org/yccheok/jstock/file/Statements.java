/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.file;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.table.TableModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.StockHistoryServer;
import org.yccheok.jstock.gui.AbstractPortfolioTreeTableModel;
import org.yccheok.jstock.gui.POIUtils;
import org.yccheok.jstock.gui.SellPortfolioTreeTableModel;
import org.yccheok.jstock.internationalization.GUIBundle;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;

/**
 *
 * @author yccheok
 */
public class Statements {
    public static class StatementsEx {
        // Possible null for statements.
        public final Statements statements;
        public final String title;
        public StatementsEx(Statements statements, String title) {
            this.statements = statements;
            this.title = title;
        }
    }
    /**
     * Prevent client from constructing Statements other than static factory
     * method.
     */
    private Statements() {}

    public static Statements newInstanceFromStockHistoryServer(StockHistoryServer server) {
        final Statements s = new Statements();
        final int size = server.getNumOfCalendar();
        final DateFormat dateFormat = DateFormat.getDateInstance();
        for (int i = 0; i < size; i++) {
            final Calendar calendar = server.getCalendar(i);
            final Stock stock = server.getStock(calendar);
            assert(calendar != null && stock != null);
            final List<Atom> atoms = new ArrayList<Atom>();
            final Atom atom0 = new Atom(dateFormat.format(calendar.getTime()), GUIBundle.getString("StockHistory_Date"));
            final Atom atom1 = new Atom(new Double(stock.getOpenPrice()), GUIBundle.getString("StockHistory_Open"));
            final Atom atom2 = new Atom(new Double(stock.getHighPrice()), GUIBundle.getString("StockHistory_High"));
            final Atom atom3 = new Atom(new Double(stock.getLowPrice()), GUIBundle.getString("StockHistory_Low"));
            final Atom atom4 = new Atom(new Double(stock.getLastPrice()), GUIBundle.getString("StockHistory_Close"));
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            final Atom atom5 = new Atom(new Long(stock.getVolume()), GUIBundle.getString("StockHistory_Volume"));
            atoms.add(atom0);
            atoms.add(atom1);
            atoms.add(atom2);
            atoms.add(atom3);
            atoms.add(atom4);
            atoms.add(atom5);
            s.statements.add(new Statement(atoms));
        }
        return s;
    }

    /**
     * Construct Statements based on given Excel File.
     *
     * @param file Given Excel File
     * @return the List of constructed Statements. Empty list if fail.
     */
    public static List<Statements> newInstanceFromExcelFile(File file) {
        FileInputStream fileInputStream = null;
        final List<Statements> statementsList = new ArrayList<Statements>();
        try
        {
            fileInputStream = new FileInputStream(file);
            final POIFSFileSystem fs = new POIFSFileSystem(fileInputStream);
            final HSSFWorkbook wb = new HSSFWorkbook(fs);
            final int numberOfSheets = wb.getNumberOfSheets();
            for (int k = 0; k < numberOfSheets; k++)
            {
                final HSSFSheet sheet = wb.getSheetAt(k);
                final int startRow = sheet.getFirstRowNum();
                final int endRow = sheet.getLastRowNum();
                // If there are 3 rows, endRow will be 2.
                // We must have at least 2 rows. (endRow = 1)
                if (startRow != 0 || endRow <= startRow)
                {
                    continue;
                }

                final HSSFRow row = sheet.getRow(startRow);
                if (row == null) {
                    continue;
                }

                final int startCell = row.getFirstCellNum();
                final int endCell = row.getLastCellNum();
                // If there are 2 cols, endCell will be 2.
                // We must have at least 1 col. (endCell = 1)
                if (startCell != 0 || endCell <= startCell) {
                    continue;
                }

                final List<String> types = new ArrayList<String>();
                for (int i = startCell; i < endCell; i++) {
                    final HSSFCell cell = row.getCell(i);
                    if (cell == null) {
                        continue;
                    }

                    // Exception may be thrown here, as cell may be numerical value.
                    final String type = cell.getRichStringCellValue().getString();
                    if (type != null) {
                        types.add(type);
                    }
                }

                if (types.isEmpty()) {
                    continue;
                }

                if (types.size() != (endCell - startCell))
                {
                    continue;
                }

                final Statements s = new Statements();
                for (int i = startRow + 1; i <= endRow; i++) {
                    final HSSFRow r = sheet.getRow(i);
                    if (r == null) {
                        continue;
                    }
                    final List<Atom> atoms = new ArrayList<Atom>();
                    for (int j = startCell; j < endCell; j++) {
                        final HSSFCell cell = r.getCell(j);
                        if (cell == null) {
                            continue;
                        }
                        Object value = null;
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            final HSSFRichTextString richString = cell.getRichStringCellValue();
                            if (richString != null) {
                                value = richString.getString();
                            }
                            else {
                                value = "";
                            }
                        }
                        else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            try {
                                value = new Double(cell.getNumericCellValue());
                            }
                            catch (NumberFormatException ex) {
                                log.error(null, ex);
                                value = new Double(0.0);
                            }
                        }
                        else {
                        }
                        
                        if (null == value) {
                            continue;
                        }
                        atoms.add(new Atom(value, types.get(j - startCell)));
                    }
                    final Statement statement = new Statement(atoms);
                    if (!s.statements.isEmpty()) {
                        if (s.statements.get(0).getType() != statement.getType()) {
							// Give up.
                            s.statements.clear();
                            break;
                        }
                    }
                    s.statements.add(statement);
                }   // for (int i = startRow + 1; i <= endRow; i++)

                if (s.statements.size() > 0) {
                    statementsList.add(s);
                }

            }   /* for(int k = 0; k < numberOfSheets; k++) */
        }
        catch (Exception ex)
        {
            log.error(null, ex);
        }
        finally
        {
            org.yccheok.jstock.gui.Utils.close(fileInputStream);
        }
        return statementsList;
    }

    /**
     * Construct Statements based on given CSV File.
     *
     * @param file Given CSV File
     * @return the constructed Statements. null if fail
     */
    public static Statements newInstanceFromCSVFile(File file) {
        java.io.Reader reader = null;
        try {
            reader = new java.io.FileReader(file);
        } catch (IOException ex) {
            log.error(null, ex);
            return null;
        }
        final CSVReader csvreader = new CSVReader(reader);
        final List<String> types = new ArrayList<String>();
        final Statements s = new Statements();
        try {
            String [] nextLine;
            if ((nextLine = csvreader.readNext()) != null) {
                types.addAll(Arrays.asList(nextLine));
            }   /* if ((nextLine = csvreader.readNext()) != null) */

            if (types.isEmpty()) {
                return null;
            }

            while ((nextLine = csvreader.readNext()) != null) {
                // Shall we continue to ignore, or shall we just return null to
                // flag an error?
                if (nextLine.length != types.size()) {
                    // Give a warning message.
                    log.error("Incorrect CSV format. There should be exactly " + types.size() + " item(s)");
                    continue;
                }

                int i = 0;
                final List<Atom> atoms = new ArrayList<Atom>();
                for (String value : nextLine) {
                    final String type = types.get(i++);
                    final Atom atom = new Atom(value, type);
                    atoms.add(atom);
                }
                final Statement statement = new Statement(atoms);
                if (!s.statements.isEmpty()) {
                    if (s.statements.get(0).getType() != statement.getType()) {
                        // Doesn't not match. Return null to indicate we fail to
                        // construct Statements from TableModel.
                        return null;
                    }
                }
                s.statements.add(statement);
            }
        }
        catch (IOException ex) {
            log.error(null, ex);
        }
        finally {
            try {
                csvreader.close();
            } catch (IOException ex) {
                log.error(null, ex);
            }
            org.yccheok.jstock.gui.Utils.close(reader);
        }
        if (s.statements.isEmpty()) {
            // No statement being found. Returns null.
            return null;
        }
        return s;
    }
    
    /**
     * Construct Statements based on given TableModel.
     *
     * @param tableModel Given TableModel
     * @return the constructed Statements. null if fail
     */
    public static Statements newInstanceFromTableModel(TableModel tableModel) {        
        final int column = tableModel.getColumnCount();
        final int row = tableModel.getRowCount();
        final Statements s = new Statements();
        for (int i = 0; i < row; i++) {
            final List<Atom> atoms = new ArrayList<Atom>();
            for (int j = 0; j < column; j++) {
                final String type = tableModel.getColumnName(j);
                final Object object = tableModel.getValueAt(i, j);
                if (tableModel.getColumnClass(j).equals(Stock.class)) {
                    final Stock stock = (Stock)object;
                    // There are no way to represent Stock in text form. We
                    // will represent them in Code and Symbol.
                    // Code first. Follow by symbol.
                    atoms.add(new Atom(stock.getCode().toString(), GUIBundle.getString("MainFrame_Code")));
                    atoms.add(new Atom(stock.getSymbol().toString(), GUIBundle.getString("MainFrame_Symbol")));
                }
                else if (tableModel.getColumnClass(j).equals(Date.class)) {
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    atoms.add(new Atom(object != null ? dateFormat.format(((Date)object).getTime()) : "", type));
                }
                else {
                    // For fall below and rise above, null value is permitted.
                    // Use empty string to represent null value.
                    atoms.add(new Atom(object != null ? object : "", type));
                }
            }
            final Statement statement = new Statement(atoms);
            if (!s.statements.isEmpty()) {
                if (s.statements.get(0).getType() != statement.getType()) {
                    // Doesn't not match. Return null to indicate we fail to
                    // construct Statements from TableModel.
                    return null;
                }
            }
            s.statements.add(statement);
        }
        if (s.statements.isEmpty()) {
            // No statement being found. Returns null.
            return null;
        }
        return s;
    }

    /**
     * Construct Statements based on given AbstractPortfolioTreeTableModel.
     *
     * @param abstractPortfolioTreeTableModel Given AbstractPortfolioTreeTableModel
     * @return the constructed Statements. null if fail
     */
    public static Statements newInstanceFromAbstractPortfolioTreeTableModel(AbstractPortfolioTreeTableModel abstractPortfolioTreeTableModel) {
        final int column = abstractPortfolioTreeTableModel.getColumnCount();
        final Portfolio portfolio = (Portfolio)abstractPortfolioTreeTableModel.getRoot();
        final int summaryCount = portfolio.getChildCount();
        final Statements s = new Statements();
        for (int i = 0; i < summaryCount; i++) {
            Object o = portfolio.getChildAt(i);
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            final int transactionCount = transactionSummary.getChildCount();
            for (int j = 0; j < transactionCount; j++)
            {
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(j);
                final List<Atom> atoms = new ArrayList<Atom>();
                for (int k = 0; k < column; k++) {
                    final String type = abstractPortfolioTreeTableModel.getColumnName(k);
                    final Object object = abstractPortfolioTreeTableModel.getValueAt(transaction, k);
                    if (abstractPortfolioTreeTableModel.getColumnClass(k).equals(TreeTableModel.class)) {
                        final Stock stock = transaction.getContract().getStock();
                        // There are no way to represent Stock in text form. We
                        // will represent them in Code and Symbol.
                        // Code first. Follow by symbol.
                        atoms.add(new Atom(stock.getCode().toString(), GUIBundle.getString("MainFrame_Code")));
                        atoms.add(new Atom(stock.getSymbol().toString(), GUIBundle.getString("MainFrame_Symbol")));
						// OK. I know. This breaks generalization.
                        if (abstractPortfolioTreeTableModel instanceof SellPortfolioTreeTableModel) {
                            final SimpleDate simpleDate = transaction.getContract().getReferenceDate();
                            DateFormat dateFormat = DateFormat.getDateInstance();
                            atoms.add(new Atom(object != null ? dateFormat.format(simpleDate.getTime().getTime()) : "", GUIBundle.getString("PortfolioManagementJPanel_ReferenceDate")));
                        }
                    }
                    else if (abstractPortfolioTreeTableModel.getColumnClass(k).equals(Date.class)) {
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        atoms.add(new Atom(object != null ? dateFormat.format(((Date)object).getTime()) : "", type));
                    }
                    else {
                        // For fall below and rise above, null value is permitted.
                        // Use empty string to represent null value.
                        atoms.add(new Atom(object != null ? object : "", type));
                    }
                }
                final Statement statement = new Statement(atoms);
                if (!s.statements.isEmpty()) {
                    if (s.statements.get(0).getType() != statement.getType()) {
                        // Doesn't not match. Return null to indicate we fail to
                        // construct Statements from TableModel.
                        return null;
                    }
                }
                s.statements.add(statement);
            }   // for (int j = 0; j < transactionCount; j++)
        }   //  for (int i = 0; i < summaryCount; i++)
        if (s.statements.isEmpty()) {
            // No statement being found. Returns null.
            return null;
        }
        return s;
    }

    public boolean saveAsCSVFile(File file) {
        java.io.Writer writer = null;
        try {
            writer = new java.io.FileWriter(file);
        } catch (IOException ex) {
            log.error(null, ex);
            return false;
        }
        final CSVWriter csvwriter = new CSVWriter(writer);
        // statements.size() can never be 0.
        final int columnCount = statements.get(0).size();
        String[] datas = new String[columnCount];

        // First row. Print out table header.
        for (int i = 0; i < columnCount; i++) {
            datas[i] = statements.get(0).getAtom(i).getType();
        }

        csvwriter.writeNext(datas);

        final int rowCount = statements.size();
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                // Value shouldn't be null, as we prevent atom with null value.
                final String value = statements.get(i).getAtom(j).getValue().toString();
                datas[j] = value;
            }
            csvwriter.writeNext(datas);
        }
        try {
            csvwriter.close();
        } catch (IOException ex) {
            log.error(null, ex);
        }
        org.yccheok.jstock.gui.Utils.close(writer);
        return true;
    }

    public boolean saveAsExcelFile(File file, String title) {
        final HSSFWorkbook wb = new HSSFWorkbook();
        final HSSFSheet sheet = wb.createSheet(title);
        // statements.size() can never be 0.
        final int columnCount = statements.get(0).size();
        // First row. Print out table header.
        {
            final HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < columnCount; i++) {
                row.createCell(i).setCellValue(new HSSFRichTextString(statements.get(0).getAtom(i).getType()));
            }
        }

        final int rowCount = statements.size();
        for (int i = 0; i < rowCount; i++) {
            final HSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < columnCount; j++) {
                // Value shouldn't be null, as we prevent atom with null value.
                final Object value = statements.get(i).getAtom(j).getValue();
                final HSSFCell cell = row.createCell(j);
                POIUtils.invokeSetCellValue(cell, value);
            }
        }
        boolean status = false;
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            status = true;
        } catch (FileNotFoundException ex) {
            log.error(null, ex);
        }
        catch (IOException ex) {
            log.error(null, ex);
        }
        finally {
            org.yccheok.jstock.gui.Utils.close(fileOut);
        }
        return status;
    }

    public static boolean saveAsExcelFile(File file, List<StatementsEx> statementsExs) {
        final HSSFWorkbook wb = new HSSFWorkbook();
        boolean needToWrite = false;
        for (StatementsEx statementsEx : statementsExs) {
            final String title = statementsEx.title;
            final Statements statements = statementsEx.statements;
            if (statements == null) {
                continue;
            }
            needToWrite = true;
            final HSSFSheet sheet = wb.createSheet(title);
            // statements.size() can never be 0.
            final int columnCount = statements.get(0).size();
            // First row. Print out table header.
            {
                final HSSFRow row = sheet.createRow(0);
                for (int i = 0; i < columnCount; i++) {
                    row.createCell(i).setCellValue(new HSSFRichTextString(statements.get(0).getAtom(i).getType()));
                }
            }

            final int rowCount = statements.size();
            for (int i = 0; i < rowCount; i++) {
                final HSSFRow row = sheet.createRow(i + 1);
                for (int j = 0; j < columnCount; j++) {
                    // Value shouldn't be null, as we prevent atom with null value.
                    final Object value = statements.get(i).getAtom(j).getValue();
                    final HSSFCell cell = row.createCell(j);
                    POIUtils.invokeSetCellValue(cell, value);
                }
            }
        }
        if (needToWrite == false) {
            return needToWrite;
        }
        boolean status = false;
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            status = true;
        } catch (FileNotFoundException ex) {
            log.error(null, ex);
        }
        catch (IOException ex) {
            log.error(null, ex);
        }
        finally {
            org.yccheok.jstock.gui.Utils.close(fileOut);
        }
        return status;
    }

    public Statement.Type getType() {
        // statements.size() can never be 0.
        return statements.get(0).getType();
    }

    public int size() {
        return statements.size();
    }
    
    public Statement get(int index) {
        return statements.get(index);
    }
    
    private final List<Statement> statements = new ArrayList<Statement>();
    private static final Log log = LogFactory.getLog(Statements.class);
}
