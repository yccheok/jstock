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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
import org.yccheok.jstock.engine.Board;
import org.yccheok.jstock.engine.Code;
import org.yccheok.jstock.engine.Stock;
import org.yccheok.jstock.engine.Industry;
import org.yccheok.jstock.engine.Intraday;
import org.yccheok.jstock.engine.StockHistoryServer;
import org.yccheok.jstock.engine.StockInfo;
import org.yccheok.jstock.engine.StockInfoDatabase;
import org.yccheok.jstock.engine.StockNameDatabase;
import org.yccheok.jstock.engine.Symbol;
import org.yccheok.jstock.file.GUIBundleWrapper.Language;
import org.yccheok.jstock.gui.BackwardCompatible;
import org.yccheok.jstock.gui.POIUtils;
import org.yccheok.jstock.engine.Pair;
import org.yccheok.jstock.gui.StockTableModel;
import org.yccheok.jstock.gui.portfolio.CommentableContainer;
import org.yccheok.jstock.gui.treetable.BuyPortfolioTreeTableModelEx;
import org.yccheok.jstock.gui.treetable.SellPortfolioTreeTableModelEx;
import org.yccheok.jstock.portfolio.Portfolio;
import org.yccheok.jstock.portfolio.PortfolioInfo;
import org.yccheok.jstock.portfolio.PortfolioRealTimeInfo;
import org.yccheok.jstock.portfolio.Transaction;
import org.yccheok.jstock.portfolio.TransactionSummary;
import org.yccheok.jstock.watchlist.WatchlistInfo;

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

    public static Statements newInstanceFromBuyPortfolioTreeTableModel(BuyPortfolioTreeTableModelEx buyPortfolioTreeTableModel, PortfolioRealTimeInfo portfolioRealTimeInfo, boolean languageIndependent) {
        final GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(languageIndependent ? GUIBundleWrapper.Language.INDEPENDENT : GUIBundleWrapper.Language.DEFAULT);
        
        final String[] tmp = {
            guiBundleWrapper.getString("MainFrame_Code"),
            guiBundleWrapper.getString("MainFrame_Symbol"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Date"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Units"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_CurrentPrice"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_CurrentValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPrice"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPercentage"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Broker"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_NetPurchaseValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossPercentage"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Comment")
        };
        
        Statement.What what = Statement.what(Arrays.asList(tmp));
        final Statements statements = new Statements(what.type, what.guiBundleWrapper);

        final Portfolio portfolio = (Portfolio)buyPortfolioTreeTableModel.getRoot();
        final int summaryCount = portfolio.getChildCount();

        for (int i = 0; i < summaryCount; i++) {
            Object o = portfolio.getChildAt(i);
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            
            // Metadatas will be used to store TransactionSummary's comment.
            final String comment = transactionSummary.getComment().trim();
            if (comment.isEmpty() == false) {
                final StockInfo stockInfo = ((Transaction)transactionSummary.getChildAt(0)).getStockInfo();
                statements.metadatas.put(stockInfo.code.toString(), comment);
            }
            
            final int transactionCount = transactionSummary.getChildCount();
            for (int j = 0; j < transactionCount; j++)
            {
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(j);
                final StockInfo stockInfo = transaction.getStockInfo();
                final boolean shouldConvertPenceToPound = org.yccheok.jstock.portfolio.Utils.shouldConvertPenceToPound(portfolioRealTimeInfo, stockInfo.code);
                
                final List<Atom> atoms = new ArrayList<>();
                atoms.add(new Atom(stockInfo.code.toString(), tmp[0]));
                atoms.add(new Atom(stockInfo.symbol.toString(), tmp[1]));
                
                final String dateString = transaction.getDate() != null ? org.yccheok.jstock.gui.Utils.commonDateFormat(transaction.getDate().getTime()) : "";                        
                atoms.add(new Atom(dateString, tmp[2]));
                atoms.add(new Atom(transaction.getQuantity(), tmp[3]));
                atoms.add(new Atom(transaction.getPrice(), tmp[4]));
                atoms.add(new Atom(buyPortfolioTreeTableModel.getCurrentPrice(transaction), tmp[5]));
                if (shouldConvertPenceToPound == false) {
                    atoms.add(new Atom(transaction.getTotal(), tmp[6]));
                } else {
                    atoms.add(new Atom(transaction.getTotal() / 100.0, tmp[6]));
                }
                atoms.add(new Atom(buyPortfolioTreeTableModel.getCurrentValue(transaction), tmp[7]));
                atoms.add(new Atom(buyPortfolioTreeTableModel.getGainLossPrice(transaction), tmp[8]));
                if (shouldConvertPenceToPound == false) {
                    atoms.add(new Atom(buyPortfolioTreeTableModel.getGainLossValue(transaction), tmp[9]));
                } else {
                    atoms.add(new Atom(buyPortfolioTreeTableModel.getGainLossValue(transaction) / 100.0, tmp[9]));
                }                
                atoms.add(new Atom(buyPortfolioTreeTableModel.getGainLossPercentage(transaction), tmp[10]));
                atoms.add(new Atom(transaction.getBroker(), tmp[11]));
                atoms.add(new Atom(transaction.getClearingFee(), tmp[12]));
                atoms.add(new Atom(transaction.getStampDuty(), tmp[13]));
                if (shouldConvertPenceToPound == false) {
                    atoms.add(new Atom(transaction.getNetTotal(), tmp[14]));
                    atoms.add(new Atom(buyPortfolioTreeTableModel.getNetGainLossValue(transaction), tmp[15]));
                } else {
                    atoms.add(new Atom(transaction.getNetTotal() / 100.0, tmp[14]));
                    atoms.add(new Atom(buyPortfolioTreeTableModel.getNetGainLossValue(transaction) / 100.0, tmp[15]));
                }
                atoms.add(new Atom(buyPortfolioTreeTableModel.getNetGainLossPercentage(transaction), tmp[16]));
                atoms.add(new Atom(transaction.getComment(), tmp[17]));
                
                final Statement statement = new Statement(atoms);
               
                if (statements.getType() != statement.getType()) {
                    return UNKNOWN_STATEMENTS;
                }
                statements.statements.add(statement);                
            }
        }   
        
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
        
        final int size = server.size();
        
        Stock stock = null;
        for (int i = 0; i < size; i++) {
            final long timestamp = server.getTimestamp(i);
            stock = server.getStock(timestamp);
            assert(timestamp != 0 && stock != null);
            final List<Atom> atoms = new ArrayList<Atom>();
            final Atom atom0 = new Atom(org.yccheok.jstock.gui.Utils.commonDateFormat(timestamp), guiBundleWrapper.getString("StockHistory_Date"));
            final Atom atom1 = new Atom(Double.valueOf(stock.getOpenPrice()), guiBundleWrapper.getString("StockHistory_Open"));
            final Atom atom2 = new Atom(Double.valueOf(stock.getHighPrice()), guiBundleWrapper.getString("StockHistory_High"));
            final Atom atom3 = new Atom(Double.valueOf(stock.getLowPrice()), guiBundleWrapper.getString("StockHistory_Low"));
            final Atom atom4 = new Atom(Double.valueOf(stock.getLastPrice()), guiBundleWrapper.getString("StockHistory_Close"));
            // TODO: CRITICAL LONG BUG REVISED NEEDED.
            final Atom atom5 = new Atom(Long.valueOf(stock.getVolume()), guiBundleWrapper.getString("StockHistory_Volume"));
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
            s.metadatas.put("code", stock.code.toString());
            s.metadatas.put("symbol", stock.symbol.toString());
            s.metadatas.put("name", stock.getName());
            s.metadatas.put("board", stock.getBoard().name());
            s.metadatas.put("industry", stock.getIndustry().name());

            // for History Summary Chart
            s.metadatas.put("timeZone", server.getTimeZone().getID());

            // for Intraday Chart, represent Open & Close time in epoch ms
            String openTs = "";
            String closeTs = "";
            Intraday intraday = server.getIntraday();

            if (intraday != null) {
                openTs = Long.toString(intraday.open);
                closeTs = Long.toString(intraday.close);
            }

            s.metadatas.put("intradayOpen", openTs);
            s.metadatas.put("intradayClose", closeTs);
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
        final List<Statements> statementsList = new ArrayList<>();
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
            Utils.close(fileInputStream);
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
        // FIXME :
        final boolean needToPerformBackwardCompatible = BackwardCompatible.needToPerformBackwardCompatible(file);
        final boolean needToHandleMetadata = BackwardCompatible.needToHandleMetadata(file);
                
        boolean status = false;

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        CSVReader csvreader = null;
        Statements s = null;
        
        final ThreadSafeFileLock.Lock lock = ThreadSafeFileLock.getLock(file);
        if (lock == null) {
            return UNKNOWN_STATEMENTS;
        }
        // http://stackoverflow.com/questions/10868423/lock-lock-before-try
        ThreadSafeFileLock.lockRead(lock);
        
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
                        
                        // FIXME :
                        if (needToHandleMetadata) {
                            key = BackwardCompatible.toGoogleCodeIfPossible(key);
                        }                       
                        
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
                    
                    // FIXME :
                    if (needToPerformBackwardCompatible && type.equals("Code")) {
                        value = BackwardCompatible.toGoogleCodeIfPossible(value);
                    }
                    
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
            s.metadatas.putAll(metadatas);

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
            Utils.close(inputStreamReader);
            Utils.close(fileInputStream);
            
            ThreadSafeFileLock.unlockRead(lock);
            ThreadSafeFileLock.releaseLock(lock);
        }

        if (status) {
            return s;
        }

        return UNKNOWN_STATEMENTS;
    }
    
    /**
     * Construct Statements based on given stock pairs.
     *
     * @param tableModel give stock pairs
     * @return the constructed Statements. UNKNOWN_STATEMENTS if fail
     */        
    public static Statements newInstanceFromUserDefinedDatabase(java.util.List<Pair<Code, Symbol>> pairs) {
        GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(GUIBundleWrapper.Language.INDEPENDENT);
        Statements s = new Statements(Statement.Type.UserDefinedDatabase, guiBundleWrapper);
        
        final String code_string = guiBundleWrapper.getString("MainFrame_Code");
        final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");
        
        for (Pair<Code, Symbol> pair : pairs) {
            final List<Atom> atoms = new ArrayList<Atom>();
            atoms.add(new Atom(pair.first, code_string));
            atoms.add(new Atom(pair.second, symbol_string));
            Statement statement = new Statement(atoms);
            
            // They should be the same type. The checking just act as paranoid.
            if (s.getType() != statement.getType()) {
                throw new java.lang.RuntimeException("" + statement.getType());
            }
            s.statements.add(statement);            
        }
        return s;
    }
    
    public static Statements newInstanceFromStockNameDatabase(StockNameDatabase stockNameDatabase) {
        GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(GUIBundleWrapper.Language.INDEPENDENT);
        Statements s = new Statements(Statement.Type.StockNameDatabase, guiBundleWrapper);
        
        final String code_string = guiBundleWrapper.getString("MainFrame_Code");
        final String name_string = guiBundleWrapper.getString("MainFrame_Name");
        
        for (Map.Entry<Code, String> entry : stockNameDatabase.getCodeToName().entrySet()) {
            final Code code = entry.getKey();
            final String name = entry.getValue();
            final List<Atom> atoms = new ArrayList<Atom>();
            atoms.add(new Atom(code, code_string));
            atoms.add(new Atom(name, name_string));
            Statement statement = new Statement(atoms);
            
            // They should be the same type. The checking just act as paranoid.
            if (s.getType() != statement.getType()) {
                throw new java.lang.RuntimeException("" + statement.getType());
            }
            s.statements.add(statement);            
        }
        return s;
    }

    public static Statements newInstanceFromWatchlistInfos(List<WatchlistInfo> wathclistInfos) {
        GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(GUIBundleWrapper.Language.INDEPENDENT);
        Statements s = new Statements(Statement.Type.WatchlistInfos, guiBundleWrapper);
        
        final String country_string = guiBundleWrapper.getString("WatchlistInfo_Country");
        final String name_string = guiBundleWrapper.getString("WatchlistInfo_Name");
        final String size_string = guiBundleWrapper.getString("WatchlistInfo_Size");        
        
        for (WatchlistInfo watchlistInfo : wathclistInfos) {
            final List<Atom> atoms = new ArrayList<Atom>();
            atoms.add(new Atom(watchlistInfo.country, country_string));
            atoms.add(new Atom(watchlistInfo.name, name_string));
            atoms.add(new Atom(watchlistInfo.size, size_string)); 
            Statement statement = new Statement(atoms);
            // They should be the same type. The checking just act as paranoid.
            if (s.getType() != statement.getType()) {
                throw new java.lang.RuntimeException("" + statement.getType());
            }
            s.statements.add(statement);            
        }
        return s;
    }
    
    public static Statements newInstanceFromPortfolioInfos(List<PortfolioInfo> portfolioInfos) {
        GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(GUIBundleWrapper.Language.INDEPENDENT);
        Statements s = new Statements(Statement.Type.PortfolioInfos, guiBundleWrapper);
        
        final String country_string = guiBundleWrapper.getString("PortfolioInfo_Country");
        final String name_string = guiBundleWrapper.getString("PortfolioInfo_Name");
        final String size_string = guiBundleWrapper.getString("PortfolioInfo_Size");        
        
        for (PortfolioInfo portfolioInfo : portfolioInfos) {
            final List<Atom> atoms = new ArrayList<Atom>();
            atoms.add(new Atom(portfolioInfo.country, country_string));
            atoms.add(new Atom(portfolioInfo.name, name_string));
            atoms.add(new Atom(portfolioInfo.size, size_string)); 
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
     * Construct Statements based on given stock info database.
     *
     * @param tableModel give stock info database
     * @return the constructed Statements. UNKNOWN_STATEMENTS if fail
     */    
    public static Statements newInstanceFromStockInfoDatabase(StockInfoDatabase stockInfoDatabase) {
        GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(GUIBundleWrapper.Language.INDEPENDENT);
        Statements s = new Statements(Statement.Type.StockInfoDatabase, guiBundleWrapper);
        
        // Build mechanism, to retrieve StockInfo's Board and Industry.
        Map<StockInfo, Industry> stockInfo2Industry = new HashMap<StockInfo, Industry>();
        Map<StockInfo, Board> stockInfo2Board = new HashMap<StockInfo, Board>();
        
        List<Industry> industries = stockInfoDatabase.getIndustries();
        List<Board> boards = stockInfoDatabase.getBoards();
        
        for (Industry industry : industries) {
            List<StockInfo> stockInfos = stockInfoDatabase.getStockInfos(industry);
            for (StockInfo stockInfo : stockInfos) {
                stockInfo2Industry.put(stockInfo, industry);
            }
        }
        
        for (Board board : boards) {
            List<StockInfo> stockInfos = stockInfoDatabase.getStockInfos(board);
            for (StockInfo stockInfo : stockInfos) {
                stockInfo2Board.put(stockInfo, board);
            }
        }   
        
        final String code_string = guiBundleWrapper.getString("MainFrame_Code");
        final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");
        final String industry_string = guiBundleWrapper.getString("MainFrame_Industry");
        final String board_string = guiBundleWrapper.getString("MainFrame_Board");
        
        List<StockInfo> stockInfos = stockInfoDatabase.getStockInfos();
        for (StockInfo stockInfo : stockInfos) {
            Industry industry = stockInfo2Industry.get(stockInfo);
            Board board = stockInfo2Board.get(stockInfo);
            if (industry == null) {
                // Shouldn't happen.
                industry = Industry.Unknown;
            }
            if (board == null) {
                // Shouldn't happen.
                board = Board.Unknown;
            }            
            final List<Atom> atoms = new ArrayList<Atom>();
            atoms.add(new Atom(stockInfo.code, code_string));
            atoms.add(new Atom(stockInfo.symbol, symbol_string));
            // Do not use toString, as we had overridden toString.
            atoms.add(new Atom(industry.name(), industry_string));
            atoms.add(new Atom(board.name(), board_string)); 
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
     * Construct Statements based on given stock price.
     *
     * @param tableModel give stock price
     * @return the constructed Statements. UNKNOWN_STATEMENTS if fail
     */
    public static Statements newInstanceFromStockPrices(Map<Code, Double> stockPrices, long timestamp) {
        GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(GUIBundleWrapper.Language.INDEPENDENT);
        Statements s = new Statements(Statement.Type.StockPrice, guiBundleWrapper);
        s.metadatas.put("timestamp", Long.toString(timestamp));
        
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
     * @param tableModel given TableModel
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
            final Class c = tableModel.getColumnClass(i);
            if (c.equals(Stock.class)) {                    
                final String code_string = guiBundleWrapper.getString("MainFrame_Code");
                final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");
                strings.add(code_string);
                strings.add(symbol_string);
            } if (c.equals(StockInfo.class)) {
                final String code_string = guiBundleWrapper.getString("MainFrame_Code");
                final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");
                strings.add(code_string);
                strings.add(symbol_string);                
            } else {
                strings.add(type);
            }

        }
        
        // Comment handling.
        CommentableContainer commentableContainer = null;
        if (tableModel instanceof CommentableContainer) {
            commentableContainer = (CommentableContainer)tableModel;
        }
        
        Statement.What what = Statement.what(strings);
        final Statements s = new Statements(what.type, what.guiBundleWrapper);
        
        for (int i = 0; i < row; i++) {
            final List<Atom> atoms = new ArrayList<Atom>();
            for (int j = 0; j < column; j++) {
                final String type = languageIndependent ? csvHelper.getLanguageIndependentColumnName(j) : tableModel.getColumnName(j);
                final Object object = tableModel.getValueAt(i, j);
                final Class c = tableModel.getColumnClass(j);
                if (c.equals(Stock.class)) {
                    final Stock stock = (Stock)object;
                    // There are no way to represent Stock in text form. We
                    // will represent them in Code and Symbol.
                    // Code first. Follow by symbol.
                    
                    final String code_string = guiBundleWrapper.getString("MainFrame_Code");
                    final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");

                    atoms.add(new Atom(stock.code.toString(), code_string));
                    atoms.add(new Atom(stock.symbol.toString(), symbol_string));
                }
                else if (c.equals(StockInfo.class)) {
                    final StockInfo stockInfo = (StockInfo)object;
                    
                    final String code_string = guiBundleWrapper.getString("MainFrame_Code");
                    final String symbol_string = guiBundleWrapper.getString("MainFrame_Symbol");

                    atoms.add(new Atom(stockInfo.code.toString(), code_string));
                    atoms.add(new Atom(stockInfo.symbol.toString(), symbol_string));                    
                }
                else if (c.equals(Date.class)) {
                    atoms.add(new Atom(object != null ? org.yccheok.jstock.gui.Utils.commonDateFormat(((Date)object).getTime()) : "", type));
                } else {
                    // For fall below and rise above, null value is permitted.
                    // Use empty string to represent null value.
                    atoms.add(new Atom(object != null ? object : "", type));
                }
            }
            
            // Comment handling.
            if (commentableContainer != null) {
                atoms.add(new Atom(commentableContainer.getCommentable(i).getComment(), guiBundleWrapper.getString("PortfolioManagementJPanel_Comment")));
            }
            
            final Statement statement = new Statement(atoms);

            if (s.getType() != statement.getType()) {
                // Doesn't not match. Return UNKNOWN_STATEMENTS to indicate we fail to
                // construct Statements from TableModel.
                return UNKNOWN_STATEMENTS;
            }

            s.statements.add(statement);
        }

        // Any metadata? This is special hack since Android introduction.
        if (tableModel instanceof StockTableModel) {
            s.metadatas.put("timestamp", Long.toString(((StockTableModel)tableModel).getTimestamp()));
        }
        
        return s;
    }

    public static Statements newInstanceFromSellPortfolioTreeTableModel(SellPortfolioTreeTableModelEx sellPortfolioTreeTableModel, PortfolioRealTimeInfo portfolioRealTimeInfo, boolean languageIndependent) {
        final GUIBundleWrapper guiBundleWrapper = GUIBundleWrapper.newInstance(languageIndependent ? GUIBundleWrapper.Language.INDEPENDENT : GUIBundleWrapper.Language.DEFAULT);
        
        final String[] tmp = {            
            guiBundleWrapper.getString("MainFrame_Code"),
            guiBundleWrapper.getString("MainFrame_Symbol"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_ReferenceDate"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Date"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Units"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_SellingPrice"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_PurchasePrice"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_SellingValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseBroker"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseClearingFee"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_PurchaseStampDuty"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPrice"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_GainLossPercentage"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Broker"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_ClearingFee"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_StampDuty"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_NetSellingValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossValue"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_NetGainLossPercentage"),
            guiBundleWrapper.getString("PortfolioManagementJPanel_Comment")
        };
        
        Statement.What what = Statement.what(Arrays.asList(tmp));
        final Statements statements = new Statements(what.type, what.guiBundleWrapper);

        final Portfolio portfolio = (Portfolio)sellPortfolioTreeTableModel.getRoot();
        final int summaryCount = portfolio.getChildCount();

        for (int i = 0; i < summaryCount; i++) {
            Object o = portfolio.getChildAt(i);
            final TransactionSummary transactionSummary = (TransactionSummary)o;
            
            // Metadatas will be used to store TransactionSummary's comment.
            final String comment = transactionSummary.getComment().trim();
            if (comment.isEmpty() == false) {
                final StockInfo stockInfo = ((Transaction)transactionSummary.getChildAt(0)).getStockInfo();
                statements.metadatas.put(stockInfo.code.toString(), comment);
            }

            final int transactionCount = transactionSummary.getChildCount();
            for (int j = 0; j < transactionCount; j++)
            {
                final Transaction transaction = (Transaction)transactionSummary.getChildAt(j);
                final StockInfo stockInfo = transaction.getStockInfo();
                final boolean shouldConvertPenceToPound = org.yccheok.jstock.portfolio.Utils.shouldConvertPenceToPound(portfolioRealTimeInfo, stockInfo.code);
                final List<Atom> atoms = new ArrayList<>();
                atoms.add(new Atom(stockInfo.code.toString(), tmp[0]));
                atoms.add(new Atom(stockInfo.symbol.toString(), tmp[1]));
                
                final String referenceDateString = transaction.getReferenceDate() != null ? org.yccheok.jstock.gui.Utils.commonDateFormat(transaction.getReferenceDate().getTime()) : "";
                atoms.add(new Atom(referenceDateString, tmp[2]));
                final String dateString = transaction.getDate() != null ? org.yccheok.jstock.gui.Utils.commonDateFormat(transaction.getDate().getTime()) : "";
                atoms.add(new Atom(dateString, tmp[3]));
                atoms.add(new Atom(transaction.getQuantity(), tmp[4]));
                atoms.add(new Atom(transaction.getPrice(), tmp[5]));
                atoms.add(new Atom(transaction.getReferencePrice(), tmp[6]));
                
                if (shouldConvertPenceToPound == false) {
                    atoms.add(new Atom(transaction.getTotal(), tmp[7]));                
                    atoms.add(new Atom(transaction.getReferenceTotal(), tmp[8]));
                } else {
                    atoms.add(new Atom(transaction.getTotal() / 100.0, tmp[7]));                
                    atoms.add(new Atom(transaction.getReferenceTotal() / 100.0, tmp[8]));                    
                }
                
                atoms.add(new Atom(transaction.getReferenceBroker(), tmp[9]));
                atoms.add(new Atom(transaction.getReferenceClearingFee(), tmp[10]));
                atoms.add(new Atom(transaction.getReferenceStampDuty(), tmp[11]));
                
                atoms.add(new Atom(sellPortfolioTreeTableModel.getGainLossPrice(transaction), tmp[12]));
                
                if (shouldConvertPenceToPound == false) {
                    atoms.add(new Atom(sellPortfolioTreeTableModel.getGainLossValue(transaction), tmp[13]));
                } else {
                    atoms.add(new Atom(sellPortfolioTreeTableModel.getGainLossValue(transaction) / 100.0, tmp[13]));
                }
                
                atoms.add(new Atom(sellPortfolioTreeTableModel.getGainLossPercentage(transaction), tmp[14]));
                atoms.add(new Atom(transaction.getBroker(), tmp[15]));
                atoms.add(new Atom(transaction.getClearingFee(), tmp[16]));
                atoms.add(new Atom(transaction.getStampDuty(), tmp[17]));
                
                if (shouldConvertPenceToPound == false) {
                    atoms.add(new Atom(transaction.getNetTotal(), tmp[18]));                
                    atoms.add(new Atom(sellPortfolioTreeTableModel.getNetGainLossValue(transaction), tmp[19]));
                } else {
                    atoms.add(new Atom(transaction.getNetTotal() / 100.0, tmp[18]));
                    atoms.add(new Atom(sellPortfolioTreeTableModel.getNetGainLossValue(transaction) / 100.0, tmp[19]));
                }
                
                atoms.add(new Atom(sellPortfolioTreeTableModel.getNetGainLossPercentage(transaction), tmp[20]));
                atoms.add(new Atom(transaction.getComment(), tmp[21]));
                
                final Statement statement = new Statement(atoms);
               
                if (statements.getType() != statement.getType()) {
                    return UNKNOWN_STATEMENTS;
                }
                statements.statements.add(statement);                
            }
        }
        return statements;
    }
    
    public boolean saveAsCSVFile(File file) {
        if (this.type == Statement.Type.Unknown) {
            return false;
        }
        
        boolean status = false;

        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        CSVWriter csvwriter = null;
        
        final ThreadSafeFileLock.Lock lock = ThreadSafeFileLock.getLock(file);
        if (lock == null) {
            return false;
        }
        // http://stackoverflow.com/questions/10868423/lock-lock-before-try
        ThreadSafeFileLock.lockWrite(lock);
        
        try {
            fileOutputStream = new FileOutputStream(file);

            outputStreamWriter = new OutputStreamWriter(fileOutputStream,  Charset.forName("UTF-8"));
            csvwriter = new CSVWriter(outputStreamWriter);

            for (Map.Entry<String, String> metadata : metadatas.entrySet()) {
                String key = metadata.getKey();
                String value = metadata.getValue();
                String output = key + "=" + value;
                csvwriter.writeNext(new String[]{output});
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
            Utils.close(outputStreamWriter);
            Utils.close(fileOutputStream);
            
            ThreadSafeFileLock.unlockWrite(lock);
            ThreadSafeFileLock.releaseLock(lock);
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
        
        final ThreadSafeFileLock.Lock lock = ThreadSafeFileLock.getLock(file);
        if (lock == null) {
            return false;
        }
        // http://stackoverflow.com/questions/10868423/lock-lock-before-try
        ThreadSafeFileLock.lockWrite(lock);
        
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
            Utils.close(fileOut);
            
            ThreadSafeFileLock.unlockWrite(lock);
            ThreadSafeFileLock.releaseLock(lock);
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
        
        final ThreadSafeFileLock.Lock lock = ThreadSafeFileLock.getLock(file);
        if (lock == null) {
            return false;
        }
        // http://stackoverflow.com/questions/10868423/lock-lock-before-try
        ThreadSafeFileLock.lockWrite(lock);
        
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
            Utils.close(fileOut);
            
            ThreadSafeFileLock.unlockWrite(lock);
            ThreadSafeFileLock.releaseLock(lock);
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
