/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.engine;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.file.Statement;
import org.yccheok.jstock.file.Statements;

/**
 *
 * @author yccheok
 */
public class StatementsStockHistoryServer implements StockHistoryServer {
    private final java.util.Map<SimpleDate, Stock> historyDatabase = new HashMap<SimpleDate, Stock>();
    private final java.util.List<SimpleDate> simpleDates = new ArrayList<SimpleDate>();
    
    private StatementsStockHistoryServer(Statements statements) throws ParseException {
        assert(statements.getType() == Statement.Type.StockHistory);
        
        DateFormat dateFormat = org.yccheok.jstock.gui.Utils.getCommonDateFormat();
        Map<String, String> metadatas = statements.getMetadatas();
        double previousClosePrice = Double.MAX_VALUE;
        
        for (int i = 0, ei = statements.size(); i < ei; i++) {
            Statement statement = statements.get(i);
            assert(statement.getType() == Statement.Type.StockHistory);
            SimpleDate simpleDate = new SimpleDate(dateFormat.parse(statement.getAtom(0).getValue().toString()));
            double openPrice = Double.parseDouble(statement.getAtom(1).getValue().toString());
            double highPrice = Double.parseDouble(statement.getAtom(2).getValue().toString());
            double lowPrice = Double.parseDouble(statement.getAtom(3).getValue().toString());
            double closePrice = Double.parseDouble(statement.getAtom(4).getValue().toString());
            double prevPrice = (previousClosePrice == Double.MAX_VALUE) ? 0 : previousClosePrice;
            long volume = Long.parseLong(statement.getAtom(5).getValue().toString());
            double changePrice = (previousClosePrice == Double.MAX_VALUE) ? 0 : closePrice - previousClosePrice;
            double changePricePercentage = ((previousClosePrice == Double.MAX_VALUE) || (previousClosePrice == 0.0)) ? 0 : changePrice / previousClosePrice * 100.0;
            
            Code code = Code.newInstance(metadatas.get("code"));
            Symbol symbol = Symbol.newInstance(metadatas.get("symbol"));
            String name = metadatas.get("name");
            Stock.Board board = Stock.Board.valueOf(metadatas.get("board"));
            Stock.Industry industry = Stock.Industry.valueOf(metadatas.get("industry"));
            
            Stock stock = new Stock(
                    code,
                    symbol,
                    name,
                    board,
                    industry,
                    prevPrice,
                    openPrice,
                    closePrice, /* Last Price. */
                    highPrice,
                    lowPrice,
                    volume,
                    changePrice,
                    changePricePercentage,
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
                    simpleDate.getCalendar()
                    );

            historyDatabase.put(simpleDate, stock);
            simpleDates.add(simpleDate);
            previousClosePrice = closePrice;
        }
    }
    
    public static StatementsStockHistoryServer newInstance(Statements statements) {
        assert(statements != null);
        
        if (statements.getType() != Statement.Type.StockHistory) {
            return null;
        }
        
        try {
            return new StatementsStockHistoryServer(statements);
        } catch (Exception ex) {
            log.error("", ex);
            return null;
        }
    }
    
    @Override
    public Stock getStock(Calendar calendar) {
        SimpleDate simpleDate = new SimpleDate(calendar);
        return historyDatabase.get(simpleDate);
    }

    @Override
    public Calendar getCalendar(int index) {
        return simpleDates.get(index).getCalendar();
    }

    @Override
    public int getNumOfCalendar() {
        return simpleDates.size();
    }

    @Override
    public long getSharesIssued() {
        return 0;
    }

    @Override
    public long getMarketCapital() {
        return 0;
    }    
    
    private static final Log log = LogFactory.getLog(StatementsStockHistoryServer.class);
}
