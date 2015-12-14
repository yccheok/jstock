package org.yccheok.jstock.engine;

public enum PriceSource {
    // It is important that the used name shall be tally with StockServerFactory
    // concrete class name.
    Yahoo,
    Google,
    @Deprecated
    KLSEInfo;   // Still here for xstream backward compatible. Shall be removed
                // after a while.
}
