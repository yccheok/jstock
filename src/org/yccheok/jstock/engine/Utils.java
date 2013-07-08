/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2013 Yan Cheng CHEOK <yccheok@yahoo.com>
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
import org.codehaus.jackson.map.ObjectMapper;
import org.yccheok.jstock.engine.AjaxYahooSearchEngine.ResultType;
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

    /**
     * Gets the CSV file, which will be used to construct 
     * {@code StockCodeAndSymbolDatabase} object.
     *
     * @param country The country of the stock market
     * @return Location of the stocks CSV file.
     */
    public static String getStocksCSVFileLocation(Country country) {
        // Must use lower case, as Google App Engine only support URL in lower
        // case.
        return org.yccheok.jstock.network.Utils.getJStockStaticServer() + "stocks_information/" + country.toString().toLowerCase() + "/" + "stocks.csv";
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
                    industry = Industry.valueOf(_board);
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
    
    private static final List<Index> australiaIndices = new ArrayList<Index>();
    private static final List<Index> austriaIndices = new ArrayList<Index>();
    private static final List<Index> belgiumIndices = new ArrayList<Index>();
    private static final List<Index> brazilIndices = new ArrayList<Index>();
    private static final List<Index> canadaIndices = new ArrayList<Index>();
    private static final List<Index> chinaIndices = new ArrayList<Index>();
    private static final List<Index> denmarkIndices = new ArrayList<Index>();
    private static final List<Index> franceIndices = new ArrayList<Index>();
    private static final List<Index> germanyIndices = new ArrayList<Index>();
    private static final List<Index> hongkongIndices = new ArrayList<Index>();
    private static final List<Index> indiaIndices = new ArrayList<Index>();
    private static final List<Index> indonesiaIndices = new ArrayList<Index>();
    private static final List<Index> israelIndices = new ArrayList<Index>();
    private static final List<Index> italyIndices = new ArrayList<Index>();
    private static final List<Index> koreaIndices = new ArrayList<Index>();
    private static final List<Index> malaysiaIndices = new ArrayList<Index>();
    private static final List<Index> netherlandsIndices = new ArrayList<Index>();
    private static final List<Index> newZealandIndices = new ArrayList<Index>();
    private static final List<Index> norwayIndices = new ArrayList<Index>();
    private static final List<Index> portugalIndices = new ArrayList<Index>();
    private static final List<Index> singaporeIndices = new ArrayList<Index>();
    private static final List<Index> spainIndices = new ArrayList<Index>();
    private static final List<Index> swedenIndices = new ArrayList<Index>();
    private static final List<Index> switzerlandIndices = new ArrayList<Index>();
    private static final List<Index> taiwanIndices = new ArrayList<Index>();
    private static final List<Index> unitedKingdomIndices = new ArrayList<Index>();
    private static final List<Index> unitedStateIndices = new ArrayList<Index>();
    
    static
    {
        austriaIndices.add(Index.ATX);
        australiaIndices.add(Index.AORD);
        belgiumIndices.add(Index.BFX);
        brazilIndices.add(Index.BVSP);
        canadaIndices.add(Index.GSPTSE);
        chinaIndices.add(Index.SSEC);
        denmarkIndices.add(Index.OMXC20CO);
        franceIndices.add(Index.FCHI);  
        germanyIndices.add(Index.DAX);
        hongkongIndices.add(Index.HSI);
        indiaIndices.add(Index.BSESN);
        indiaIndices.add(Index.NSEI);
        indonesiaIndices.add(Index.JKSE);
        israelIndices.add(Index.TA100);
        italyIndices.add(Index.FTSEMIB);
        koreaIndices.add(Index.KS11);
        malaysiaIndices.add(Index.KLSE);
        netherlandsIndices.add(Index.AEX);
        newZealandIndices.add(Index.NZSX50);
        norwayIndices.add(Index.OSEAX);
        portugalIndices.add(Index.PSI20);
        singaporeIndices.add(Index.STI);
        spainIndices.add(Index.SMSI);
        swedenIndices.add(Index.OMX30);
        switzerlandIndices.add(Index.SSMI);
        taiwanIndices.add(Index.TWII);
        unitedKingdomIndices.add(Index.FTSE);
        unitedStateIndices.add(Index.DJI);        
        unitedStateIndices.add(Index.IXIC);        
    }

    /**
     * Returns code in Google's format.
     * 
     * @param code the code
     * @return code in Google's format
     */
    public static Code toGoogleFormat(Code code) {
        String string = code.toString().trim().toUpperCase();
        if (string.equals("^KLSE")) {
            //return Code.newInstance("INDEXFTSE:FBMKLCI");
        } else if (string.equals("^DJI")) {
            return Code.newInstance("INDEXDJX:.DJI");
        } else if (string.equals("^IXIC")) {
            return Code.newInstance("INDEXNASDAQ:.IXIC");
        } else if (string.equals("^BSESN")) {
            return Code.newInstance("INDEXBOM:SENSEX");
        } else if (string.equals("^NSEI")) {
            return Code.newInstance("NSE:NIFTY");
        } else if (string.endsWith(".NS") && string.length() > ".NS".length()) {
            // Resolving Yahoo server down for India NSE stock market. Note, we
            // do not support Bombay stock market at this moment, due to the
            // difficulty in converting "TATACHEM.BO" (Yahoo Finance) to 
            // "BOM:500770" (Google Finance)
            string = string.substring(0, string.length() - ".NS".length());
            return Code.newInstance("NSE:" + toGoogleFormatThroughAutoComplete(string, "NSE"));
        }
        return Code.newInstance(string);
    }
    
    private static String toGoogleFormatThroughAutoComplete(String code, String exchange) {
        final StringBuilder builder = new StringBuilder("https://www.google.com/finance/match?matchtype=matchall&q=");
        try {
            // Exception will be thrown from apache httpclient, if we do not
            // perform URL encoding.
            builder.append(java.net.URLEncoder.encode(code, "UTF-8"));

            final String location = builder.toString();
            final String respond = Utils.GoogleRespondToJSON(org.yccheok.jstock.gui.Utils.getResponseBodyAsStringBasedOnProxyAuthOption(location));
            // Google returns "// [ { "id": ... } ]".
            // We need to turn them into "[ { "id": ... } ]".
            final List<Map> jsonArray = mapper.readValue(respond, List.class);
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
        } catch (IOException ex) {
            log.error(null, ex);
        } catch (Exception ex) {
            // Jackson library may cause runtime exception if there is error
            // in the JSON string.
            log.error(null, ex);
        }        
        return code;
    }
    
    public static Code toYahooFormat(Code code, Country country)
    {
        if (code == null || country == null)
        {
            throw new java.lang.IllegalArgumentException("Method parameters cannot be null in toYahooFormat");
        }

        Code result = code;
        
        return result;
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
        switch (country)
        {
            case Australia:
                return java.util.Collections.unmodifiableList(Utils.australiaIndices);
            case Austria:
                return java.util.Collections.unmodifiableList(Utils.austriaIndices);
            case Belgium:
                return java.util.Collections.unmodifiableList(Utils.belgiumIndices);
            case Brazil:
                return java.util.Collections.unmodifiableList(Utils.brazilIndices);
            case Canada:
                return java.util.Collections.unmodifiableList(Utils.canadaIndices);
            case China:
                return java.util.Collections.unmodifiableList(Utils.chinaIndices);
            case Denmark:
                return java.util.Collections.unmodifiableList(Utils.denmarkIndices);
            case France:
                return java.util.Collections.unmodifiableList(Utils.franceIndices);
            case Germany:
                return java.util.Collections.unmodifiableList(Utils.germanyIndices);
            case HongKong:
                return java.util.Collections.unmodifiableList(Utils.hongkongIndices);
            case India:
                return java.util.Collections.unmodifiableList(Utils.indiaIndices);
            case Indonesia:
                return java.util.Collections.unmodifiableList(Utils.indonesiaIndices);
            case Israel:
                return java.util.Collections.unmodifiableList(Utils.israelIndices);
            case Italy:
                return java.util.Collections.unmodifiableList(Utils.italyIndices);
            case Korea:
                return java.util.Collections.unmodifiableList(Utils.koreaIndices);
            case Malaysia:
                return java.util.Collections.unmodifiableList(Utils.malaysiaIndices);
            case Netherlands:
                return java.util.Collections.unmodifiableList(Utils.netherlandsIndices);
            case NewZealand:
                return java.util.Collections.unmodifiableList(Utils.newZealandIndices);
            case Norway:
                return java.util.Collections.unmodifiableList(Utils.norwayIndices);
            case Portugal:
                return java.util.Collections.unmodifiableList(Utils.portugalIndices);
            case Singapore:
                return java.util.Collections.unmodifiableList(Utils.singaporeIndices);
            case Spain:
                return java.util.Collections.unmodifiableList(Utils.spainIndices);
            case Sweden:
                return java.util.Collections.unmodifiableList(Utils.swedenIndices);
            case Switzerland:
                return java.util.Collections.unmodifiableList(Utils.switzerlandIndices);
            case Taiwan:
                return java.util.Collections.unmodifiableList(Utils.taiwanIndices);
            case UnitedKingdom:
                return java.util.Collections.unmodifiableList(Utils.unitedKingdomIndices);                
            case UnitedState:
                return java.util.Collections.unmodifiableList(Utils.unitedStateIndices);
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

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Log log = LogFactory.getLog(Utils.class);
}
