/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2014 Yan Cheng Cheok <yccheok@yahoo.com>
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

package org.yccheok.jstock.engine;

import au.com.bytecode.opencsv.CSVReader;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.engine.Stock.Board;
import org.yccheok.jstock.engine.Stock.Industry;
import org.yccheok.jstock.file.Statements;
import org.yccheok.jstock.gui.MainFrame;
import org.yccheok.jstock.gui.Pair;

/**
 *
 * @author yccheok
 */
public class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {
    }   

    /**
     * Returns empty stock based on given stock info.
     *
     * @param stockInfo the stock info
     * @return empty stock based on given stock info
     */
    public static Stock getEmptyStock(StockInfo stockInfo) {
        return getEmptyStock(stockInfo.code, stockInfo.symbol);
    }

    /**
     * Returns empty stock based on given code and symbol.
     *
     * @param code the code
     * @param symbol the symbol
     * @return empty stock based on given code and symbol
     */
    public static Stock getEmptyStock(Code code, Symbol symbol) {
        return new Stock(   code,
                            symbol,
                            "",
                            Stock.Board.Unknown,
                            Stock.Industry.Unknown,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            0,
                            0.0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            0.0,
                            0,
                            System.currentTimeMillis()                              
                            );                
    } 
    
    public static Country toCountry(Code code) {
        assert(countries.keySet().size() == 42);
        
        String string = code.toString();
        int index = string.lastIndexOf(".");
        if (index == -1) {
            if (isYahooIndexSubset(code)) {
                Country country = indices.get(string.toUpperCase());
                if (country == null) {
                    return Country.UnitedState;
                }
                return country;
            }
            
            return Country.UnitedState;
        }
        String key = string.substring(index + 1, string.length());
        Country country = countries.get(key.toUpperCase());
        if (country == null) {
            return Country.UnitedState;
        }
        return country;
    }
    
    /**
     * Generate the best online database result if possible so that it is
     * acceptable by JStock application.
     *
     * @param result result from online database
     * @return best result after rectified. null if result cannot be rectified
     */
    public static ResultType rectifyResult(ResultType result) {
        String symbolStr = result.symbol;
        String nameStr = result.name;
        if (symbolStr == null) {
            return null;
        }
        if (symbolStr.trim().isEmpty()) {
            return null;
        }
        symbolStr = symbolStr.trim().toUpperCase();
        if (nameStr == null) {
            // If name is not available, we will make it same as symbol.
            nameStr = symbolStr;
        }
        if (nameStr.trim().isEmpty()) {
            // If name is not available, we will make it same as symbol.
            nameStr = symbolStr;
        }
        nameStr = nameStr.trim();
        return result.deriveWithSymbol(symbolStr).deriveWithName(nameStr);
    }

    /**
     * Initialize HttpClient with information from system properties.
     *
     * @param httpClient HttpClient to be initialized
     */
    public static void setHttpClientProxyFromSystemProperties(HttpClient httpClient) {
        final String httpproxyHost = System.getProperties().getProperty("http.proxyHost");
        final String httpproxyPort = System.getProperties().getProperty("http.proxyPort");
        
        if (null == httpproxyHost || null == httpproxyPort) {
            HostConfiguration hostConfiguration = httpClient.getHostConfiguration();
            hostConfiguration.setProxyHost(null);
        }
        else {
            int port = -1;
            try {
                port = Integer.parseInt(httpproxyPort);
            }
            catch (NumberFormatException exp) {
            }     
            
            if (isValidPortNumber(port)) {
                HostConfiguration hostConfiguration = httpClient.getHostConfiguration();
                hostConfiguration.setProxy(httpproxyHost, port);                
            }
            else {
                HostConfiguration hostConfiguration = httpClient.getHostConfiguration();
                hostConfiguration.setProxyHost(null);
            }
        }
    } 
    
    // Refer to http://www.exampledepot.com/egs/java.util/CompDates.html
    public static long getDifferenceInDays(long timeInMillis0, long timeInMillis1) {
        long diffMillis = Math.abs(timeInMillis0 - timeInMillis1);
        // Get difference in days
        long diffDays = diffMillis/(24*60*60*1000);
        return diffDays;        
    }
    
    // Refer to http://www.exampledepot.com/egs/java.util/CompDates.html
    public static long getDifferenceInDays(Date date0, Date date1) {
        return getDifferenceInDays(date0.getTime(), date1.getTime());
    }

    // Refer to http://www.exampledepot.com/egs/java.util/CompDates.html
    public static long getDifferenceInDays(Calendar calendar0, Calendar calendar1) {
        return getDifferenceInDays(calendar0.getTimeInMillis(), calendar1.getTimeInMillis());
    }
    
    public static void resetCalendarTime(Calendar calendar) {
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int date = calendar.get(Calendar.DATE);
        calendar.set(year, month, date, 0, 0, 0);
        // Reset milli second as well.
        calendar.set(Calendar.MILLISECOND, 0);        
    }
    
    public static boolean isValidPortNumber(int portNumber) {
        return (portNumber >= 0) && (portNumber <= 65534);
    }
    
    public static boolean isValidPortNumber(String portNumber) {
        int port = -1;
        try {
            port = Integer.parseInt(portNumber);
        }
        catch(NumberFormatException exp) {
        }
        
        return isValidPortNumber(port);
    }
    
    public static File getStockInfoDatabaseFile(Country country) {
        return new File(org.yccheok.jstock.gui.Utils.getUserDataDirectory() + country + File.separator + "database" + File.separator + "stock-info-database.csv");
    }
    
    /**
     * Gets the CSV file, which will be used to construct 
     * {@code StockCodeAndSymbolDatabase} object.
     *
     * @param country The country of the stock market
     * @return Location of the stocks CSV file.
     */
    public static String getStocksCSVZipFileLocation(Country country) {
        // Must use lower case, as Google App Engine only support URL in lower
        // case.
        return org.yccheok.jstock.network.Utils.getJStockStaticServer() + "stocks_information/" + country.toString().toLowerCase() + "/" + "stocks.zip";
    }

    /**
     * One of the shortcomings of JStock is that, it is very difficult to get a
     * complete list of available stocks in a market. Most stock servers do not
     * provide information on complete list of available stocks. We can overcome
     * this, by reading the stock list information from a CSV file.
     *
     * @param file The CSV file
     * @return List of stocks carried by the CSV file.
     */
    public static List<Stock> getStocksFromCSVFile(File file) {
        List<Stock> stocks = new ArrayList<Stock>();
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        CSVReader csvreader = null;
        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream,  Charset.forName("UTF-8"));
            csvreader = new CSVReader(inputStreamReader);
            final String[] types = csvreader.readNext();
            if (types == null) {
                // Fail. Returns empty stock list.
                return stocks;
            }
            int code_index = -1;
            int symbol_index = -1;
            // Name, board and industry information is optional.
            int name_index = -1;            
            int board_index = -1;
            int industry_index = -1;
            
            boolean success_index = false;
            // Search for the indecies for code, symbol and name.
            for (int index = 0; index < types.length; index++) {
                final String type = types[index];
                if (0 == type.compareToIgnoreCase("code")) {
                    code_index = index;
                } else if (0 == type.compareToIgnoreCase("symbol")) {
                    symbol_index = index;
                } else if (0 == type.compareToIgnoreCase("name")) {
                    name_index = index;
                } else if (0 == type.compareToIgnoreCase("board")) {
                    board_index = index;
                } else if (0 == type.compareToIgnoreCase("industry")) {
                    industry_index = index;
                }

                if (code_index != -1 && symbol_index != -1 && name_index != -1 && board_index != -1 && industry_index != -1) {
                    // All found. Early quit.
                    break;
                }
            }

            // Ignore board_index, as it is optional.
            success_index = (code_index != -1 && symbol_index != -1);

            // Are we having all the indecies?
            if (false == success_index) {
                // Nope. Returns empty stock list.
                return stocks;
            }

            String [] nextLine;
            while ((nextLine = csvreader.readNext()) != null) {
                // Shall we continue to ignore, or shall we just return early to
                // flag an error?
                if (nextLine.length != types.length) {
                    // Give a warning message.
                    log.error("Incorrect CSV format. There should be exactly " + types.length + " item(s)");
                    continue;
                }
                final String code = nextLine[code_index];
                final String symbol = nextLine[symbol_index];
                final String name = name_index == -1 ? "" : nextLine[name_index];
                final String _board = board_index == -1 ? "Unknown" : nextLine[board_index];
                final String _industry = industry_index == -1 ? "Unknown" : nextLine[industry_index];
                Board board;
                Industry industry;
                try {
                    board = Board.valueOf(_board);
                } catch (IllegalArgumentException exp) {
                    log.error(null, exp);
                    board = Board.Unknown;
                }
                try {
                    industry = Industry.valueOf(_industry);
                } catch (IllegalArgumentException exp) {
                    log.error(null, exp);
                    industry = Industry.Unknown;
                }
                
                final Stock stock = new Stock.Builder(Code.newInstance(code), Symbol.newInstance(symbol)).name(name).board(board).industry(industry).build();
                stocks.add(stock);
            }
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
        return stocks;
    }
    
    public static Pair<StockInfoDatabase, StockNameDatabase> toStockDatabase(List<Stock> stocks, Country country) {
        assert(false == stocks.isEmpty());
        
        // Let's make our database since we get a list of good stocks.
        StockInfoDatabase tmp_stock_info_database = new StockInfoDatabase(stocks);
        
        // StockNameDatabase is an optional item.
        StockNameDatabase tmp_name_database = null;
        if (org.yccheok.jstock.engine.Utils.isNameImmutable(country)) {
            tmp_name_database = new StockNameDatabase(stocks);
        }
        
        return Pair.create(tmp_stock_info_database, tmp_name_database);
    }
    
    public static boolean migrateXMLToCSVDatabases(String srcBaseDirectory, String destBaseDirectory) {
        boolean result = true;
        for (Country country : Country.values()) {
            final File userDefinedDatabaseXMLFile = new File(srcBaseDirectory + country + File.separator + "database" + File.separator + "user-defined-database.xml");
            final File userDefinedDatabaseCSVFile = new File(destBaseDirectory + country + File.separator + "database" + File.separator + "user-defined-database.csv");
            
            final java.util.List<Pair<Code, Symbol>> pairs = org.yccheok.jstock.gui.Utils.fromXML(java.util.List.class, userDefinedDatabaseXMLFile);            
            if (pairs != null && !pairs.isEmpty()) {
                final Statements statements = Statements.newInstanceFromUserDefinedDatabase(pairs);
                boolean r = statements.saveAsCSVFile(userDefinedDatabaseCSVFile);
                if (r) {
                    userDefinedDatabaseXMLFile.delete();
                }  
                result = r & result;
            } else {
                userDefinedDatabaseXMLFile.delete();
            }

            // Delete these old XML files. We can re-generate new CSV from database.zip.
            new File(srcBaseDirectory + country + File.separator + "database" + File.separator + "stock-name-database.xml").delete();
            new File(destBaseDirectory + country + File.separator + "database" + File.separator + "stock-info-database.xml").delete();
            new File(destBaseDirectory + country + File.separator + "database" + File.separator + "stockcodeandsymboldatabase.xml").delete();
        }
        return result;
    }
    
    private static final Map<Country, List<Index>> country2Indices = new EnumMap<Country, List<Index>>(Country.class);

    static
    {
        for (Index index : Index.values()) {
            List<Index> indices = country2Indices.get(index.country);
            if (indices == null) {
                indices = new ArrayList<Index>();
                country2Indices.put(index.country, indices);
            }
            indices.add(index);
        }
    }

    /**
     * Returns code in Google's format.
     * 
     * @param code the code
     * @return code in Google's format
     */
    public static String toGoogleFormat(Code code) {
        if (isYahooIndexSubset(code)) {
            return toGoogleIndex(code);
        } else if (isYahooCurrency(code)) {
            return toGoogleCurrency(code);
        }

        String string = code.toString().trim().toUpperCase();

        // WTF?! Handle case for RDS-B (Yahoo Finance) & RDS.B (Google Finance)
        if (toCountry(code) == Country.UnitedState) {
            String googleFormat = UnitedStatesGoogleFormatCodeLookup.INSTANCE.get(code);
            if (googleFormat != null) {
                return googleFormat;
            }
            return string.replaceAll("-", ".");
        }
        
        final int string_length = string.length();
        if (string.endsWith(".N") && string_length > ".N".length()) {
            return "NSE:" + string.substring(0, string_length - ".N".length());
        } else if (string.endsWith(".B") && string_length > ".B".length()) {
            return "BOM:" + string.substring(0, string_length - ".B".length());
        } else if (string.endsWith(".NS") && string_length > ".NS".length()) {
            // Resolving Yahoo server down for India NSE stock market. Note, we
            // do not support Bombay stock market at this moment, due to the
            // difficulty in converting "TATACHEM.BO" (Yahoo Finance) to 
            // "BOM:500770" (Google Finance)
            string = string.substring(0, string_length - ".NS".length());
            String googleFormat = toGoogleFormatThroughAutoComplete(string, "NSE");
            if (googleFormat != null) {
                return "NSE:" + googleFormat;
            }
        } else if (string.endsWith(".SS") && string_length > ".SS".length()) {
            return "SHA:" + string.substring(0, string_length - ".SS".length());
        } else if (string.endsWith(".SZ") && string_length > ".SZ".length()) {
            return "SHE:" + string.substring(0, string_length - ".SZ".length());
        } else if (string.endsWith(".SA") && string_length > ".SA".length()) {
            return "BVMF:" + string.substring(0, string_length - ".SA".length());
        } else if (string.endsWith(".VI") && string_length > ".VI".length()) {
            return "VIE:" + string.substring(0, string_length - ".VI".length());
        } else if (string.endsWith(".L") && string_length > ".L".length()) {
            return "LON:" + string.substring(0, string_length - ".L".length());
        } else if (string.endsWith(".SI") && string_length > ".SI".length()) {
            return "SGX:" + string.substring(0, string_length - ".SI".length());
        } else if (string.endsWith(".TW") && string_length > ".TW".length()) {
            return "TPE:" + string.substring(0, string_length - ".TW".length());
        }
        
        return string;
    }
    
    private static boolean isYahooIndexSubset(Code code) {
        return code.toString().startsWith("^");
    }
    
    public static boolean isUSStock(Code code) {
        return Utils.toCountry(code) == Country.UnitedState && !Utils.isYahooCurrency(code) && !Utils.isYahooIndexSubset(code);
    }
    
    public static boolean isYahooCurrency(Code code) {
        return code.toString().toUpperCase().endsWith("=X");
    }
    
    private static String toGoogleIndex(Code code) {
        String string = code.toString().trim().toUpperCase();
        String googleIndex = toGoogleIndex.get(string);
        if (googleIndex != null) {
            return googleIndex;
        }
        return string;
    }
    
    private static String toGoogleCurrency(Code code) {
        String string = code.toString().trim().toUpperCase();
        int index = string.indexOf("=X");
        if (index > 0) {
            return string.substring(0, index);
        }
        return string;
    }
    
    // FIXME : Make it private.
    public static String toGoogleFormatThroughAutoComplete(String code, String exchange) {
        final StringBuilder builder = new StringBuilder("https://www.google.com/finance/match?matchtype=matchall&q=");
        try {
            // Exception will be thrown from apache httpclient, if we do not
            // perform URL encoding.
            builder.append(java.net.URLEncoder.encode(code, "UTF-8"));

            final String location = builder.toString();
            final String _respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);
            if (_respond == null) {
                return null;
            }
            final String respond = Utils.GoogleRespondToJSON(_respond);
            // Google returns "// [ { "id": ... } ]".
            // We need to turn them into "[ { "id": ... } ]".
            final List<Map> jsonArray = gson.fromJson(respond, List.class);
            
            if (jsonArray == null) {
                return null;
            }
            
            for (int i = 0, size = jsonArray.size(); i < size; i++) {
                final Map<String, String> jsonObject = jsonArray.get(i);
                if (jsonObject.containsKey("e") && jsonObject.get("e").equalsIgnoreCase(exchange)) {
                    if (jsonObject.containsKey("t")) {
                        return jsonObject.get("t");
                    }
                }
            }           
        } catch (UnsupportedEncodingException ex) {
            log.error(null, ex);
        } catch (Exception ex) {
            // Jackson library may cause runtime exception if there is error
            // in the JSON string.
            log.error(null, ex);
        }        
        return null;
    }
    
    /**
     * Returns code in non Yahoo! format, by stripping off ".KL" suffix.
     * 
     * @param code the code
     * @return code in non Yahoo! format, by stripping off ".KL" suffix.
     */
    public static Code toNonYahooFormat(Code code)
    {
        final String tmp = code.toString();
        final String TMP = tmp.toUpperCase();
        int endIndex = TMP.lastIndexOf(".KL");
        if (endIndex < 0) {
            return code;
        }
        return Code.newInstance(tmp.substring(0, endIndex));
    }
    
    /**
     * Returns best search engine based on current selected country.
     * 
     * @param list List of elements, to be inserted into search engine
     * @return Best search engine based on current selected country.
     */
    public static boolean isPinyinTSTSearchEngineRequiredForSymbol() {
        final Country country = MainFrame.getInstance().getJStockOptions().getCountry();
        return (country == Country.China || country == Country.Taiwan);
    }

    /**
     * Returns <code>true</code> if we should maintain the symbol as database's,
     * even the symbol provided by stock server is different from our database.
     * This happens when our symbol in database is Chinese, but the symbol
     * returned by stock server is in English.
     * 
     * @return <code>true</code> if we should maintain the symbol as database's.
     */
    public static boolean isSymbolImmutable() {
        final Country country = MainFrame.getInstance().getJStockOptions().getCountry();
        return (country == Country.China || country == Country.Taiwan);
    }

    /**
     * Returns <code>true</code> if we should maintain the name as database's,
     * even the name provided by stock server is different from our database.
     * This happens when our name in database is Chinese, but the name returned
     * by stock server is in English.
     *
     * @return <code>true</code> if we should maintain the name as database's.
     */
    public static boolean isNameImmutable() {
        final Country country = MainFrame.getInstance().getJStockOptions().getCountry();
        return isNameImmutable(country);
    }

    private static boolean isNameImmutable(Country country) {
        return (country == Country.China || country == Country.Taiwan);
    }
    
    /**
     * Returns <code>true</code> if we need to use red color to indicate "rise
     * above". Green color to indicate "fall below".
     * 
     * @return <code>true</code> if we need to use red color to indicate "rise
     * above". Green color to indicate "fall below".
     */
    public static boolean isFallBelowAndRiseAboveColorReverse() {
        final Country country = MainFrame.getInstance().getJStockOptions().getCountry();
        return (country == Country.China || country == Country.Taiwan);
    }

    public static List<Index> getStockIndices(Country country) {
        List<Index> indices = country2Indices.get(country);
        if (indices != null) {
            return java.util.Collections.unmodifiableList(indices);
        }
        return java.util.Collections.emptyList();
    }

    /**
     * Returns JSON string, by parsing respond from Google server.
     *
     * @param respond string returned from Google server directly
     * @return JSON string, by parsing respond from Google server
     */
    public static String GoogleRespondToJSON(String respond) {
        final int beginIndex = respond.indexOf("[");
        final int endIndex = respond.lastIndexOf("]");
        if (beginIndex < 0) {
            return "";
        }
        if (beginIndex > endIndex) {
            return "";
        }
        String string = respond.substring(beginIndex, endIndex + 1);
        // http://stackoverflow.com/questions/6067673/urldecoder-illegal-hex-characters-in-escape-pattern-for-input-string
        string = string.replaceAll("%", "%25");
        
        // http://stackoverflow.com/questions/15518340/json-returned-by-google-maps-query-contains-encoded-characters-like-x26-how-to
        // JSON returned by Google Maps Query contains encoded characters like \x26 (how to decode?)
        try {
            string = URLDecoder.decode(string.replace("\\x", "%"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.error(null, ex);
        }
        
        return string;
    }

    /**
     * Returns JSON string, by parsing respond from Yahoo server.
     *
     * @param respond string returned from Yahoo server directly
     * @return JSON string, by parsing respond from Yahoo server
     */
    public static String YahooRespondToJSON(String respond) {
        final int beginIndex = respond.indexOf("{");
        final int endIndex = respond.lastIndexOf("}");
        if (beginIndex < 0) {
            return "";
        }
        if (beginIndex > endIndex) {
            return "";
        }
        return respond.substring(beginIndex, endIndex + 1);
    }

    /**
     * Returns a new double initialized to the value represented by the
     * specified String, as performed by the valueOf method of class Double.
     * If failed, 0.0 will be returned.
     *
     * @return the double value represented by the string argument.
     */
    public static double parseDouble(String value) {
        if (value == null) {
            // This is an invalid value.
            return 0.0;
        }

        try {
            // Use String.replace, in order to turn "1,234,567%" into "1234567".
            return Double.parseDouble(value.replace(",", "").replace("%", ""));
        } catch (NumberFormatException ex) {
            log.error(null, ex);
        }
        // This is an invalid value.
        return 0.0;
    }

    /**
     * Returns a new long initialized to the value represented by the
     * specified String, as performed by the valueOf method of class Long.
     * If failed, 0L will be returned.
     *
     * @return the long value represented by the string argument.
     */
    public static long parseLong(String value) {
        if (value == null) {
            // This is an invalid value.
            return 0L;
        }
        
        try {
            // Use String.replace, in order to turn "1,234,567%" into "1234567".
            return Long.parseLong(value.replace(",", "").replace("%", ""));
        } catch (NumberFormatException ex) {
            log.error(null, ex);
        }
        // This is an invalid value.
        return 0L;
    }
    
    public static int getGoogleUnitedStatesStockExchangePriority(String e) {
        Integer priority = googleUnitedStatesStockExchanges.get(e);
        if (priority == null) {
            return Integer.MAX_VALUE;
        }
        return priority;
    }
    
    public static String toCompleteUnitedStatesGoogleFormat(Code code) {
        if (false == Utils.isUSStock(code)) {
            return null;
        }
        
        // Use toGoogleFormat, as it will handle case for RDS-B (Yahoo Finance) 
        // & RDS.B (Google Finance)
        final String googleFormat = toGoogleFormat(code);
        if (googleFormat.contains(":")) {
            return googleFormat;
        }
        
        String[] exchanges = {"NYSE:", "NASDAQ:", "NYSEARCA:", "NYSEMKT:", "OPRA:", "OTCBB:", "OTCMKTS:"};
        final StringBuilder builder = new StringBuilder("https://www.google.com/finance/info?infotype=infoquoteall&q=");
        
        try {
            
            builder.append(java.net.URLEncoder.encode(exchanges[0] + code, "UTF-8"));
            for (int i = 1, ei = exchanges.length; i < ei; i++) {
                builder.append(",");
                builder.append(java.net.URLEncoder.encode(exchanges[i] + code, "UTF-8"));
            }
        } catch (UnsupportedEncodingException ex) {
            log.error(null, ex);
            return null;
        } 
        
        final String location = builder.toString();
        final String _respond = org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location);
        if (_respond == null) {
            return null;
        }

        final String respond = Utils.GoogleRespondToJSON(_respond);
        // Google returns "// [ { "id": ... } ]".
        // We need to turn them into "[ { "id": ... } ]".
        final List<Map> jsonArray = gson.fromJson(respond, List.class);

        if (jsonArray == null) {
            return null;
        }
        
        List<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();
        
        for (Map<String, String> jsonObject : jsonArray) {
            try {
                String ticker = jsonObject.get("t").toUpperCase();
                String exchange = jsonObject.get("e").toUpperCase();
                pairs.add(Pair.create(ticker, exchange));
            } catch (Exception ex) {
                log.error(null, ex);
            }
        }
        
        if (pairs.isEmpty()) {
            return null;
        }
        
        Collections.sort(pairs, new Comparator<Pair<String, String>>() {

            @Override
            public int compare(Pair<String, String> o0, Pair<String, String> o1) {
                String e0 = o0.second;
                String e1 = o1.second;                
                return Integer.compare(getGoogleUnitedStatesStockExchangePriority(e0), getGoogleUnitedStatesStockExchangePriority(e1));
            }            
        });
        
        Pair<String, String> pair = pairs.get(0);
        final String result = pair.second + ":" + pair.first;
        UnitedStatesGoogleFormatCodeLookup.INSTANCE.put(code, result);
        
        return result; 
    }
    
    // https://www.google.com/intl/en/googlefinance/disclaimer/
    public static boolean isGoogleUnitedStatesStockExchange(String e) {
        return googleUnitedStatesStockExchanges.containsKey(e);
    }
    
    public static PriceSource getDefaultPriceSource(Country country) {
        assert(defaultPriceSources.containsKey(country));
        return defaultPriceSources.get(country);
    }
    
    public static Set<PriceSource> getSupportedPriceSources(Country country) {
        List<StockServerFactory> stockServerFactories = Factories.INSTANCE.getStockServerFactories(country);
        Set<PriceSource> set = EnumSet.noneOf(PriceSource.class);
        for (StockServerFactory stockServerFactory : stockServerFactories) {
            PriceSource priceSource = classToPriceSourceMap.get(stockServerFactory.getClass());
            if (priceSource != null) {
                set.add(priceSource);
            }
        }
        return set;
    }
    
    private static final Map<String, Country> countries = new HashMap<String, Country>();
    private static final Map<String, Country> indices = new HashMap<String, Country>();
    private static final Map<String, String> toGoogleIndex = new HashMap<String, String>();
    private static final Map<Country, PriceSource> defaultPriceSources = new HashMap<Country, PriceSource>();
    private static final Map<Class<? extends StockServerFactory>, PriceSource> classToPriceSourceMap = new HashMap<Class<? extends StockServerFactory>, PriceSource>();
    private static final Map<String, Integer> googleUnitedStatesStockExchanges = new HashMap<String, Integer>();
    
    static {
        countries.put("AX", Country.Australia);
        countries.put("VI", Country.Austria);
        countries.put("SA", Country.Brazil);
        countries.put("TO", Country.Canada);
        countries.put("V", Country.Canada); // TSXV
        
        countries.put("SS", Country.China);
        countries.put("SZ", Country.China);
        
        countries.put("CO", Country.Denmark);
        countries.put("PA", Country.France);

        countries.put("BE", Country.Germany);
        countries.put("DE", Country.Germany);
        countries.put("DU", Country.Germany);
        countries.put("EX", Country.Germany);
        countries.put("F", Country.Germany);
        countries.put("HA", Country.Germany);
        countries.put("HM", Country.Germany);
        countries.put("MU", Country.Germany);
        countries.put("SG", Country.Germany);
        
        countries.put("HK", Country.HongKong);
        
        countries.put("NS", Country.India);
        countries.put("N", Country.India);
        countries.put("B", Country.India);
        
        countries.put("JK", Country.Indonesia);
        countries.put("TA", Country.Israel);
        countries.put("MI", Country.Italy);
        countries.put("KQ", Country.Korea);
        countries.put("KL", Country.Malaysia);
        countries.put("AS", Country.Netherlands);
        countries.put("NZ", Country.NewZealand);
        countries.put("OL", Country.Norway);
        countries.put("LS", Country.Portugal);
        countries.put("SI", Country.Singapore);
        
        countries.put("BI", Country.Spain);
        countries.put("BC", Country.Spain);
        countries.put("MA", Country.Spain);
        countries.put("MC", Country.Spain);
        countries.put("VA", Country.Spain);
        
        countries.put("SW", Country.Sweden);
        countries.put("VX", Country.Sweden);
        
        countries.put("TW", Country.Taiwan);
        countries.put("TWO", Country.Taiwan);
        
        countries.put("L", Country.UnitedKingdom);
        
        for (Index index : Index.values()) {
            indices.put(index.code.toString(), index.country);
        }
        
        toGoogleIndex.put("^DJI", "INDEXDJX:.DJI");
        toGoogleIndex.put("^IXIC", "INDEXNASDAQ:.IXIC");
        toGoogleIndex.put("^BSESN", "INDEXBOM:SENSEX");
        toGoogleIndex.put("^NSEI", "NSE:NIFTY");
        toGoogleIndex.put("^NSEBANK", "NSE:BANKNIFTY");
        toGoogleIndex.put("^BVSP", "INDEXBVMF:IBOV");
        toGoogleIndex.put("^ATX", "INDEXVIE:ATX");
        toGoogleIndex.put("^FTSE", "INDEXFTSE:UKX");
        toGoogleIndex.put("^TWII", "TPE:TAIEX");
        
        // TODO : Need revision. We no longer have primaryStockServerFactoryClasses
        // concept. Going to replace with PriceSource.
        defaultPriceSources.put(Country.Australia, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Austria, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Belgium, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Brazil, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Canada, PriceSource.Yahoo);
        defaultPriceSources.put(Country.China, PriceSource.Google);
        defaultPriceSources.put(Country.Czech, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Denmark, PriceSource.Yahoo);
        defaultPriceSources.put(Country.France, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Germany, PriceSource.Yahoo);
        defaultPriceSources.put(Country.HongKong, PriceSource.Yahoo);
        defaultPriceSources.put(Country.India, PriceSource.Google);
        defaultPriceSources.put(Country.Indonesia, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Israel, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Italy, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Korea, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Malaysia, PriceSource.KLSEInfo);
        defaultPriceSources.put(Country.Netherlands, PriceSource.Yahoo);
        defaultPriceSources.put(Country.NewZealand, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Norway, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Portugal, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Singapore, PriceSource.Google);
        defaultPriceSources.put(Country.Spain, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Sweden, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Switzerland, PriceSource.Yahoo);
        defaultPriceSources.put(Country.Taiwan, PriceSource.Google);
        defaultPriceSources.put(Country.UnitedKingdom, PriceSource.Google);
        defaultPriceSources.put(Country.UnitedState, PriceSource.Google);
        
        classToPriceSourceMap.put(GoogleStockServerFactory.class, PriceSource.Google);
        classToPriceSourceMap.put(YahooStockServerFactory.class, PriceSource.Yahoo);
        classToPriceSourceMap.put(BrazilYahooStockServerFactory.class, PriceSource.Yahoo);
        classToPriceSourceMap.put(KLSEInfoStockServerFactory.class, PriceSource.KLSEInfo);
        
        googleUnitedStatesStockExchanges.put("NYSE", 0);
        googleUnitedStatesStockExchanges.put("NASDAQ", 1);
        googleUnitedStatesStockExchanges.put("NYSEARCA", 2);
        googleUnitedStatesStockExchanges.put("NYSEMKT", 3);
        googleUnitedStatesStockExchanges.put("OPRA", 4);
        googleUnitedStatesStockExchanges.put("OTCBB", 5);
        googleUnitedStatesStockExchanges.put("OTCMKTS", 6);
    }
    
    private static final Gson gson = new Gson();
    private static final Log log = LogFactory.getLog(Utils.class);
}
