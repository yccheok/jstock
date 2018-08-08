package org.yccheok.jstock.gui;

/**
 * Created by yccheok on 29/12/2017.
 */

public class SQLite {
    private SQLite() {
    }

    @Deprecated
    public static final String DATABASE_GOOGLE_CODE = "google_code.db";
    @Deprecated
    public static final String DATABASE_IEX_STOCK_INFO = "iex_stock_info.db";

    public static final String DATABASE_STOCK_INFO = "stock_info.db";

    public static final String DATABASE_WIDGETS_BUY = "widgets_buy.db";

    public static final String DATABASE_WIDGETS_INDEX = "widgets_index.db";

    public static final String DATABASE_WIDGETS = "widgets.db";

    public static final String DATABASE_JSTOCK = "jstock.db";

    public static final String TABLE_GOOGLE_CODE_TEMPLATE = "google_code_";

    public static final String TABLE_IEX_STOCK_INFO = "iex_stock_info";

    public static final String TABLE_DRIVE_WEALTH_STOCK_INFO = "drive_wealth_stock_info";

    public static final String TABLE_STOCK_PRICE_ALERT = "stock_price_alert";

    public static final String TABLE_NEWS_ALERT = "news_alert";

    public static final String TABLE_NOTE = "note";

    public static final String TABLE_NEWS_NOTIFICATION = "news_notification";
}
