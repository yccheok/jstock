/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2012 Yan Cheng CHEOK <yccheok@yahoo.com>
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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.SimpleDate;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.StockHistoryServer;
import org.yccheok.jstock.file.GUIBundleWrapper.Language;
import org.yccheok.jstock.gui.POIUtils;
import org.yccheok.jstock.gui.treetable.AbstractPortfolioTreeTableModelEx;
import org.yccheok.jstock.gui.treetable.BuyPortfolioTreeTableModelEx;
import org.yccheok.jstock.gui.treetable.SellPortfolioTreeTableModelEx;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;

/**
 *
 * @author yccheok
 */
public class Statements {
    public static final Statements UNKNOWN_STATEMENTS = new Statements(Statement.Type.Unknown, GUIBundleWrapper.newInstance(Language.DEFAULT));
    
    public static class StatementsEx {
        public final Statements statements;
        public final String title;
        public StatementsEx(Statements statements, String title) {
            if (statements == null || title == null) {
                throw new java.lang.IllegalArgumentException();
            }
            this.statements = statements;
            this.title = title;
        }
    }
    /**
     * Prevent client from constructing Statements other than static factory
     * method.
     */
    private Statements(Statement.Type type, GUIBundleWrapper guiBundleWrapper) {
        this.type = type;
        this.guiBundleWrapper = guiBundleWrapper;
    }

    public static Statements newInstanceFromBuyPortfolioTreeTableModel(BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel, boolean languageIndependent) {
        Statements statements = newInstanceFromAbstractPortfolioTreeTableModel(buyPortfolioTreeTableModel, languageIndependent);
        
        // (metadata is already reserved for TransactionSummary's comment)
        // Preparing for metadata.
        //Map<Code, Double> stockPrices = buyPortfolioTreeTableModel.getStockPrices();
        //for (Map.Entry<Code, Double> stockPrice : stockPrices.entrySet()) {
        //    Code key = stockPrice.getKey();
        //    Double value = stockPrice.getValue();
        //    statements.metadatas.put(key.toString(), value.toString());
        //}
        
        return statements;
    }

    /**
     * Construct Statements based on given stock history server.
     *
     * @param server stock history server
     * @param languageIndependent should the returned statements be language
     * independent?
     * @return the constructed Statements. UNKNOWN_STATEMENTS if fail
     */
    public static Statements newInstanceFromStockHistoryServer(StockHistoryServer server, boolean languageIndependent) {        
        final GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(languageIndependent ? GUIBundleWrapper.Language.INDEPENDENT : GUIBundleWrapper.Language.DEFAULT);

        final Statements s = new Statements(Statement.Type.StockHistory, guiBundleWrapper);
        
        final int size = server.getNumOfCalendar();
        
        final DateFormat dateFormat = org.yccheok.jstock.gui.Utils.getCommonDateFormat();
        
        Stock stock = null;
        for (int i = 0; i < size; i++) {
            final Calendar calendar = server.getCalendar(i);
            stock = server.getStock(calendar);
            assert(calendar != null && stock != null);
            final List<Atom> atoms = new ArrayList<Atom>();
            final Atom atom0 = new Atom(dateFormat.format(calendar.getTime()), guiBundleWrapper.getString("StockHistory_Date"));
            final Atom atom1 = new Atom(new Double(stock.getOpenPrice()), guiBundleWrapper.getString("StockHistory_Open"));
            final Atom atom2 = new Atom(new Double(stock.getHighPrice()), guiBundleWrapper.getString("StockHistory_High"));
            final Atom atom3 = new Atom(new Double(stock.getLowPrice()), guiBundleWrapper.getString("StockHistory_Low"));
            final Atom atom4 = new Atom(new Double(stock.getLastPrice()), guiBundleWrapper.getString("StockHistory_Close"));
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            final Atom atom5 = new Atom(new Long(stock.getVolume()), guiBundleWrapper.getString("StockHistory_Volume"));
            atoms.add(atom0);
            atoms.add(atom1);
            atoms.add(atom2);
            atoms.add(atom3);
            atoms.add(atom4);
            atoms.add(atom5);

            Statement statement = new Statement(atoms);
            // They should be the same type. The checking just act as paranoid.
            if (s.getType() != statement.getType()) {
                throw new java.lang.RuntimeException("" + statement.getType());
            }
            s.statements.add(statement);
        }
        
        if (stock != null) {
            // Metadata. Oh yeah...
            s.metadatas.put("code", stock.getCode().toString());
            s.metadatas.put("symbol", stock.getSymbol().toString());
            s.metadatas.put("name", stock.getName());
            s.metadatas.put("board", stock.getBoard().name());
            s.metadatas.put("industry", stock.getIndustry().name());
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

                final Statement.What what = Statement.what(types);
                Statements s = new Statements(what.type, what.guiBundleWrapper);
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

                    if (s.getType() != statement.getType()) {
                        // Give up.
                        s = null;
                        break;
                    }
                    s.statements.add(statement);
                }   // for (int i = startRow + 1; i <= endRow; i++)

                if (s != null) {
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
     * @return the constructed Statements. UNKNOWN_STATEMENTS if fail
     */
    public static Statements newInstanceFromCSVFile(File file) {
        boolean status = false;

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        CSVReader csvreader = null;
        Statements s = null;
        
        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream,  Charset.forName("UTF-8"));
            csvreader = new CSVReader(inputStreamReader);
            final List<String> types = new ArrayList<String>();

            String [] nextLine;
            Map<String, String> metadatas = new LinkedHashMap<String, String>();
            if ((nextLine = csvreader.readNext()) != null) {
                
                // Metadata handling.
                while (nextLine != null && nextLine.length == 1) {
                    String[] tokens = nextLine[0].split("=", 2);
                    if (tokens.length == 2) {
                        String key = tokens[0].trim();
                        String value = tokens[1].trim();
                        if (key.length() > 0) {
                            // Is OK for value to be empty.
                            metadatas.put(key, value);
                            nextLine = csvreader.readNext();
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                
                if (nextLine != null) {
                    types.addAll(Arrays.asList(nextLine));
                }
            }   /* if ((nextLine = csvreader.readNext()) != null) */
            
            if (types.isEmpty()) {
                return UNKNOWN_STATEMENTS;
            } else {
                Statement.What what = Statement.what(types);
                s = new Statements(what.type, what.guiBundleWrapper);
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
                if (s.getType() != statement.getType()) {
                    // Doesn't not match.
                    return UNKNOWN_STATEMENTS;
                }

                s.statements.add(statement);
            }

            // Pump in metadata.
            for (Map.Entry<String, String> entry : metadatas.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                s.metadatas.put(key, value);
            }
            
            status = true;
        } catch (IOException ex) {
            log.error(null, ex);
        } finally {
            if (csvreader != null) {
                try {
                    csvreader.close();
                } catch (IOException ex) {
                    log.error(null, ex);
                }
            }
            org.yccheok.jstock.gui.Utils.close(inputStreamReader);
            org.yccheok.jstock.gui.Utils.close(fileInputStream);
        }

        if (status) {
            return s;
        }

        return UNKNOWN_STATEMENTS;
    }
    
    public static Statements newInstanceFromStockPrices(Map<Code, Double> stockPrices) {
        GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(GUIBundleWrapper.Language.INDEPENDENT);
        Statements s = new Statements(Statement.Type.StockPrice, guiBundleWrapper);
        
        final String code_string = guiBundleWrapper.getString("MainFrame_Code");
        final String last_string = guiBundleWrapper.getString("MainFrame_Last");
        for (Map.Entry<Code, Double> stockPrice : stockPrices.entrySet()) {
            Code key = stockPrice.getKey();
            Double value = stockPrice.getValue();
            final List<Atom> atoms = new ArrayList<Atom>();
            atoms.add(new Atom(key.toString(), code_string));
            atoms.add(new Atom(value.toString(), last_string));
            Statement statement = new Statement(atoms);
            // They should be the same type. The checking just act as paranoid.
            if (s.getType() != statement.getType()) {
                throw new java.lang.RuntimeException("" + statement.getType());
            }
            s.statements.add(statement);
        }        
        return s;
    }
    
    /**
     * Construct Statements based on given TableModel.
     *
     * @param tableModel Given TableModel
     * @return the constructed Statements. UNKNOWN_STATEMENTS if fail
     */
    public static Statements newInstanceFromTableModel(TableModel tableModel, boolean languageIndependent) {
        final CSVHelper csvHelper = (CSVHelper)tableModel;
        final GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(languageIndependent ? GUIBundleWrapper.Language.INDEPENDENT : GUIBundleWrapper.Language.DEFAULT);
        
        final int column = tableModel.getColumnCount();
        final int row = tableModel.getRowCount();

        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < column; i++) {
            final String type = languageIndependent ? csvHelper.getLanguageIndependentColumnName(i) : tableModel.getColumnName(i);
            if (tableModel.getColumnClass(i).equals(Stock.class)) {                    
                final String code_string = guiBundleWrapper.getString("MainFrame_Code");
                final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");
                strings.add(code_string);
                strings.add(symbol_string);
            } else {
                strings.add(type);
            }

        }
        Statement.What what = Statement.what(strings);
        final Statements s = new Statements(what.type, what.guiBundleWrapper);
        
        for (int i = 0; i < row; i++) {
            final List<Atom> atoms = new ArrayList<Atom>();
            for (int j = 0; j < column; j++) {
                final String type = languageIndependent ? csvHelper.getLanguageIndependentColumnName(j) : tableModel.getColumnName(j);
                final Object object = tableModel.getValueAt(i, j);
                if (tableModel.getColumnClass(j).equals(Stock.class)) {
                    final Stock stock = (Stock)object;
                    // There are no way to represent Stock in text form. We
                    // will represent them in Code and Symbol.
                    // Code first. Follow by symbol.
                    
                    final String code_string = guiBundleWrapper.getString("MainFrame_Code");
                    final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");

                    atoms.add(new Atom(stock.getCode().toString(), code_string));
                    atoms.add(new Atom(stock.getSymbol().toString(), symbol_string));
                }
                else if (tableModel.getColumnClass(j).equals(Date.class)) {
                    DateFormat dateFormat = org.yccheok.jstock.gui.Utils.getCommonDateFormat();
                    atoms.add(new Atom(object != null ? dateFormat.format(((Date)object).getTime()) : "", type));
                } else {
                    // For fall below and rise above, null value is permitted.
                    // Use empty string to represent null value.
                    atoms.add(new Atom(object != null ? object : "", type));
                }
            }
            final Statement statement = new Statement(atoms);

            if (s.getType() != statement.getType()) {
                // Doesn't not match. Return UNKNOWN_STATEMENTS to indicate we fail to
                // construct Statements from TableModel.
                return UNKNOWN_STATEMENTS;
            }

            s.statements.add(statement);
        }

        return s;
    }

    public static Statements newInstanceFromSellPortfolioTreeTableModel(SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel, boolean languageIndependent) {
        return newInstanceFromAbstractPortfolioTreeTableModel(sellPortfolioTreeTableModel, languageIndependent);
    }
    
    /**
     * Construct Statements based on given AbstractPortfolioTreeTableModel.
     *
     * @param abstractPortfolioTreeTableModel Given AbstractPortfolioTreeTableModel
     * @return the constructed Statements. UNKNOWN_STATEMENTS if fail
     */
    private static Statements newInstanceFromAbstractPortfolioTreeTableModel(AbstractPortfolioTreeTableModelEx abstractPortfolioTreeTableModel, boolean languageIndependent) {
        final GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(languageIndependent ? GUIBundleWrapper.Language.INDEPENDENT : GUIBundleWrapper.Language.DEFAULT);

        final int column = abstractPortfolioTreeTableModel.getColumnCount();
        final Portfolio portfolio = (Portfolio)abstractPortfolioTreeTableModel.getRoot();
        final int summaryCount = portfolio.getChildCount();
        
        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < column; i++) {
            if (abstractPortfolioTreeTableModel.getColumnClass(i).equals(TreeTableModel.class)) {
                final String code_string = guiBundleWrapper.getString("MainFrame_Code");
                final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");
                final String reference_date_string = guiBundleWrapper.getString("PortfolioManagementJPanel_ReferenceDate");                            
                strings.add(code_string);
                strings.add(symbol_string);

                // OK. I know. This breaks generalization.
                if (abstractPortfolioTreeTableModel instanceof SellPortfolioTreeTableModelEx) {
                    strings.add(reference_date_string);
                }
            }
            else {
                final String type = languageIndependent ? abstractPortfolioTreeTableModel.getLanguageIndependentColumnName(i) : abstractPortfolioTreeTableModel.getColumnName(i);
                strings.add(type);
            }
        }
        Statement.What what = Statement.what(strings);
        final Statements s = new Statements(what.type, what.guiBundleWrapper);
        
        for (int i = 0; i < summaryCount; i++) {
            Object o = portfolio.getChildAt(i);
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            
            // Metadatas will be used to store TransactionSummary's comment.
            final String comment = transactionSummary.getComment().trim();
            if (comment.isEmpty() == false) {
                final Stock stock = ((Transaction)transactionSummary.getChildAt(0)).getContract().getStock();
                s.metadatas.put(stock.getCode().toString(), comment);
            }
            
            final int transactionCount = transactionSummary.getChildCount();
            for (int j = 0; j < transactionCount; j++)
            {
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(j);
                final List<Atom> atoms = new ArrayList<Atom>();
                for (int k = 0; k < column; k++) {
                    final String type = languageIndependent ? abstractPortfolioTreeTableModel.getLanguageIndependentColumnName(k) : abstractPortfolioTreeTableModel.getColumnName(k);
                    final Object object = abstractPortfolioTreeTableModel.getValueAt(transaction, k);
                    if (abstractPortfolioTreeTableModel.getColumnClass(k).equals(TreeTableModel.class)) {
                        final Stock stock = transaction.getContract().getStock();
                        // There are no way to represent Stock in text form. We
                        // will represent them in Code and Symbol.
                        // Code first. Follow by symbol.

                        final String code_string = guiBundleWrapper.getString("MainFrame_Code");
                        final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");
                        final String reference_date_string = guiBundleWrapper.getString("PortfolioManagementJPanel_ReferenceDate");                            
                        
                        atoms.add(new Atom(stock.getCode().toString(), code_string));
                        atoms.add(new Atom(stock.getSymbol().toString(), symbol_string));

                        // OK. I know. This breaks generalization.
                        if (abstractPortfolioTreeTableModel instanceof SellPortfolioTreeTableModelEx) {
                            final SimpleDate simpleDate = transaction.getContract().getReferenceDate();
                            DateFormat dateFormat = org.yccheok.jstock.gui.Utils.getCommonDateFormat();
                            atoms.add(new Atom(object != null ? dateFormat.format(simpleDate.getTime()) : "", reference_date_string));
                        }
                    }
                    else if (abstractPortfolioTreeTableModel.getColumnClass(k).equals(SimpleDate.class)) {
                        DateFormat dateFormat = org.yccheok.jstock.gui.Utils.getCommonDateFormat();
                        SimpleDate simpleDate = (SimpleDate)object;
                        atoms.add(new Atom(object != null ? dateFormat.format(simpleDate.getTime()) : "", type));
                    } else {
                        // For fall below and rise above, null value is permitted.
                        // Use empty string to represent null value.
                        atoms.add(new Atom(object != null ? object : "", type));
                    }
                }
                final Statement statement = new Statement(atoms);
               
                if (s.getType() != statement.getType()) {
                    // Doesn't not match. Return UNKNOWN_STATEMENTS to indicate we fail to
                    // construct Statements from AbstractPortfolioTreeTableModelEx.
                    return UNKNOWN_STATEMENTS;
                }
                s.statements.add(statement);
            }   // for (int j = 0; j < transactionCount; j++)
        }   //  for (int i = 0; i < summaryCount; i++)

        return s;
    }

    public boolean saveAsCSVFile(File file) {
        return saveAsCSVFile(file, false);
    }
    
    public boolean saveAsCSVFile(File file, boolean metadataEnabled) {
        if (this.type == Statement.Type.Unknown) {
            return false;
        }
        
        boolean status = false;

        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        CSVWriter csvwriter = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream,  Charset.forName("UTF-8"));
            csvwriter = new CSVWriter(outputStreamWriter);
            
            if (metadataEnabled) {
                for (Map.Entry<String, String> metadata : metadatas.entrySet()) {
                    String key = metadata.getKey();
                    String value = metadata.getValue();
                    String output = key + "=" + value;
                    csvwriter.writeNext(new String[]{output});
                }                
            }
            
            // Do not obtain "type" through statements, as there is possible that 
            // statements is empty.
            final List<String> strings = Statement.typeToStrings(this.getType(), this.getGUIBundleWrapper());
            final int columnCount = strings.size();
            String[] datas = new String[columnCount];

            // First row. Print out table header.
            for (int i = 0; i < columnCount; i++) {
                datas[i] = strings.get(i);
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
            
            status = true;
        }  catch (IOException ex) {
            log.error(null, ex);
        } finally {
            if (csvwriter != null) {
                try {
                    csvwriter.close();
                } catch (IOException ex) {
                    log.error(null, ex);
                }
            }
            org.yccheok.jstock.gui.Utils.close(outputStreamWriter);
            org.yccheok.jstock.gui.Utils.close(fileOutputStream);
        }

        return status;
    }

    public boolean saveAsExcelFile(File file, String title) {
        if (this.type == Statement.Type.Unknown) {
            return false;
        }
        
        final HSSFWorkbook wb = new HSSFWorkbook();
        final HSSFSheet sheet = wb.createSheet(title);

        // Do not obtain "type" through statements, as there is possible that 
        // statements is empty.
        final List<String> strings = Statement.typeToStrings(this.getType(), this.getGUIBundleWrapper());
        final int columnCount = strings.size();
        // First row. Print out table header.
        {
            final HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < columnCount; i++) {
                row.createCell(i).setCellValue(new HSSFRichTextString(strings.get(i)));
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
            assert(statements != null);
            if (statements.getType() == Statement.Type.Unknown) {
                continue;
            }
            needToWrite = true;
            final HSSFSheet sheet = wb.createSheet(title);
            // Do not obtain "type" through statements, as there is possible that 
            // statements is empty.
            final List<String> strings = Statement.typeToStrings(statements.getType(), statements.getGUIBundleWrapper());
            final int columnCount = strings.size();
            // First row. Print out table header.
            {
                final HSSFRow row = sheet.createRow(0);
                for (int i = 0; i < columnCount; i++) {
                    row.createCell(i).setCellValue(new HSSFRichTextString(strings.get(i)));
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
        return type;
    }

    /**
     * @return resource language file used by this statements.
     */
    public GUIBundleWrapper getGUIBundleWrapper() {
        return guiBundleWrapper;
    }

    public int size() {
        return statements.size();
    }
    
    public Map<String, String> getMetadatas() {
        return Collections.unmodifiableMap(metadatas);
    }
    
    public Statement get(int index) {
        return statements.get(index);
    }
    
    private final Statement.Type type;
    private final GUIBundleWrapper guiBundleWrapper;
    private final List<Statement> statements = new ArrayList<Statement>();
    // Use LinkedHashMap to ensure insertion order is maintained.
    private final Map<String, String> metadatas = new LinkedHashMap<String, String>();
    private static final Log log = LogFactory.getLog(Statements.class);
    
}
