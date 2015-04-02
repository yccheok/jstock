/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yccheok.jstock.engine;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
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
    private final java.util.Map<Long, Stock> historyDatabase = new HashMap<Long, Stock>();
    private final java.util.List<Long> timestamps = new ArrayList<Long>();
    
    private StatementsStockHistoryServer(Statements statements) throws ParseException {
        assert(statements.getType() == Statement.Type.StockHistory);
        
        DateFormat dateFormat = org.yccheok.jstock.gui.Utils.getCommonDateFormat();
        Map<String, String> metadatas = statements.getMetadatas();
        double previousClosePrice = Double.MAX_VALUE;
        
        for (int i = 0, ei = statements.size(); i < ei; i++) {
            Statement statement = statements.get(i);
            assert(statement.getType() == Statement.Type.StockHistory);
            final long timestamp = dateFormat.parse(statement.getAtom(0).getValue().toString()).getTime();
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
            Board board = Board.valueOf(metadatas.get("board"));
            Stock.Industry industry = Stock.Industry.valueOf(metadatas.get("industry"));
            
            Stock stock = new Stock(
                    code,
                    symbol,
                    name,
                    null,
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
                    timestamp
                    );

            historyDatabase.put(timestamp, stock);
            timestamps.add(timestamp);
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
            log.error(null, ex);
            return null;
        }
    }
    
    @Override
    public Stock getStock(long timestamp) {
        return historyDatabase.get(timestamp);
    }

    @Override
    public long getTimestamp(int index) {
        return timestamps.get(index);
    }

    @Override
    public int size() {
        return timestamps.size();
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
