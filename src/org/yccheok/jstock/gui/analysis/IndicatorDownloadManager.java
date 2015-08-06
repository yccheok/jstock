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

package org.yccheok.jstock.gui.analysis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.analysis.OperatorIndicator;
import org.yccheok.jstock.network.Utils.Type;

/**
 *
 * @author yccheok
 */
public class IndicatorDownloadManager {
    public static final class Info {
        public final String projectName;
        public final OperatorIndicator.Type type;
        public final long checksum;
        public final URL fileURL;
        public final URL descriptionURL;

        private Info(String projectName, OperatorIndicator.Type type, long checksum, URL fileURL, URL descriptionURL) {
            if (descriptionURL == null || fileURL == null || projectName == null) {
                throw new IllegalArgumentException("Method arguments cannot be null");
            }
            this.projectName = projectName;
            this.type = type;
            this.checksum = checksum;
            this.fileURL = fileURL;
            this.descriptionURL = descriptionURL;
        }

        public static Info newInstance(String projectName, OperatorIndicator.Type type, long checksum, URL fileURL, URL descriptionURL) {
            return new Info(projectName, type, checksum, fileURL, descriptionURL);
        }

        public static Info newInstance(String projectName, OperatorIndicator.Type type, long checksum, String fileLocation, String descriptionLocation) throws MalformedURLException {
            return new Info(projectName, type, checksum, new URL(fileLocation), new URL(descriptionLocation));
        }
    }

    public boolean add(Info indicatorDownloadInfo) {
        return this.indicatorDownloadInfos.add(indicatorDownloadInfo);
    }

    public IndicatorDownloadManager.Info get(int index) {
        return this.indicatorDownloadInfos.get(index);
    }
    
    public int size() {
        return this.indicatorDownloadInfos.size();
    }

    public static String getIndicatorDownloadManagerDescriptionFileLocation(OperatorIndicator.Type operatorIndicatorType) {
        if (operatorIndicatorType == OperatorIndicator.Type.AlertIndicator) {
            return org.yccheok.jstock.network.Utils.getURL(Type.ALERT_INDICATOR_DOWNLOAD_MANAGER_XML);
        }
        else if (operatorIndicatorType == OperatorIndicator.Type.ModuleIndicator) {
            return org.yccheok.jstock.network.Utils.getURL(Type.MODULE_INDICATOR_DOWNLOAD_MANAGER_XML);
        }
        else {
            throw new java.lang.IllegalArgumentException();
        }
    }

    public static void main(String[] args) {
        IndicatorDownloadManager.deploy();
    }
    
    /* WARNING : As a tool for deployment purpose only. Not for code development usage. */
    public static void deploy() {
        try {
            IndicatorDownloadManager me = new IndicatorDownloadManager();
            me.add(Info.newInstance("MACD Down Trend Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\MACDDownTrendSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/MACDDownTrendSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/MACDDownTrendSignal.html"));
            me.add(Info.newInstance("MACD Up Trend Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\MACDUpTrendSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/MACDUpTrendSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/MACDUpTrendSignal.html"));
            me.add(Info.newInstance("MFI Down Trend Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\MFIDownTrendSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/MFIDownTrendSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/MFIDownTrendSignal.html"));
            me.add(Info.newInstance("MFI Up Trend Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\MFIUpTrendSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/MFIUpTrendSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/MFIUpTrendSignal.html"));
            me.add(Info.newInstance("RSI Sell Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\RSISellSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/RSISellSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/RSISellSignal.html"));
            me.add(Info.newInstance("RSI Buy Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\RSIBuySignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/RSIBuySignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/RSIBuySignal.html"));
            me.add(Info.newInstance("CCI Sell Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\CCISellSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/CCISellSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/CCISellSignal.html"));
            me.add(Info.newInstance("CCI Buy Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\CCIBuySignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/CCIBuySignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/CCIBuySignal.html"));
            me.add(Info.newInstance("Doji", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\Doji.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/Doji.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/Doji.html"));
            me.add(Info.newInstance("Golden Cross", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\GoldenCross.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/GoldenCross.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/GoldenCross.html"));
            me.add(Info.newInstance("Death Cross", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\DeathCross.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/DeathCross.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/DeathCross.html"));
            me.add(Info.newInstance("Top Gainers", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\TopGainers.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/TopGainers.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/TopGainers.html"));
            me.add(Info.newInstance("Top Losers", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\TopLosers.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/TopLosers.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/TopLosers.html"));
            
            org.yccheok.jstock.gui.Utils.toXML(me, "C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\indicator_download_manager.xml");
        } catch (MalformedURLException ex) {
            Logger.getLogger(IndicatorDownloadManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            IndicatorDownloadManager me = new IndicatorDownloadManager();
            me.add(Info.newInstance("MACD Down Trend Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\MACDDownTrendSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/MACDDownTrendSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/MACDDownTrendSignal.html"));
            me.add(Info.newInstance("MACD Up Trend Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\MACDUpTrendSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/MACDUpTrendSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/MACDUpTrendSignal.html"));
            me.add(Info.newInstance("MFI Down Trend Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\MFIDownTrendSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/MFIDownTrendSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/MFIDownTrendSignal.html"));
            me.add(Info.newInstance("MFI Up Trend Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\MFIUpTrendSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/MFIUpTrendSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/MFIUpTrendSignal.html"));
            me.add(Info.newInstance("RSI Sell Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\RSISellSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/RSISellSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/RSISellSignal.html"));
            me.add(Info.newInstance("RSI Buy Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\RSIBuySignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/RSIBuySignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/RSIBuySignal.html"));
            me.add(Info.newInstance("CCI Sell Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\CCISellSignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/CCISellSignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/CCISellSignal.html"));
            me.add(Info.newInstance("CCI Buy Signal", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\CCIBuySignal.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/CCIBuySignal.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/CCIBuySignal.html"));
            me.add(Info.newInstance("Doji", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\Doji.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/Doji.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/Doji.html"));
            me.add(Info.newInstance("Golden Cross", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\GoldenCross.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/GoldenCross.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/GoldenCross.html"));
            me.add(Info.newInstance("Death Cross", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\DeathCross.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/DeathCross.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/DeathCross.html"));
            me.add(Info.newInstance("Top Gainers", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\TopGainers.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/TopGainers.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/TopGainers.html"));
            me.add(Info.newInstance("Top Losers", OperatorIndicator.Type.AlertIndicator, org.yccheok.jstock.analysis.Utils.getChecksum("C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\TopLosers.zip"), org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/TopLosers.zip", org.yccheok.jstock.network.Utils.getJStockStaticServer() + "alert_indicators/zh/TopLosers.html"));

            org.yccheok.jstock.gui.Utils.toXML(me, "C:\\yocto\\jstock\\appengine\\jstock-static\\war\\alert_indicators\\zh\\indicator_download_manager.xml");
        } catch (MalformedURLException ex) {
            Logger.getLogger(IndicatorDownloadManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static final Log log = LogFactory.getLog(IndicatorDownloadManager.class);
    private final List<Info> indicatorDownloadInfos = new ArrayList<Info>();
}
