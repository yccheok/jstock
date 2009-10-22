/*
 * Copyright (c) 2009 Nanne Baars
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.yccheok.jstock.network;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * I am responsible for detecting and selecting a proxy.
 *
 * @author nb
 */
public class ProxyDetector {

    private static final String PROXY_PROPERTY = "java.net.useSystemProxies";
    private final List<Proxy> proxies;
    private final Proxy proxyToUse;

    /**
     * No instances
     */
    private ProxyDetector() {
        this.proxies = initProxies();
        this.proxyToUse = determineProxy();
    }

    /**
     * ProxyDetectorHolder is loaded on the first execution of ProxyDetector.getInstance()
     * or the first access to ProxyDetectorHolder.INSTANCE, not before.
     */
    private static class ProxyDetectorHolder {
        private static final ProxyDetector INSTANCE = new ProxyDetector();
    }

    /**
     * @return the instance
     */
    public static ProxyDetector getInstance() {
        return ProxyDetectorHolder.INSTANCE;
    }

    /**
     * Find the proxy, use the property <code>java.net.useSystemProxies</code> to force
     * the usage of the system proxy. The value of this setting is restored afterwards.
     *
     * @return a list of found proxies
     */
    private List<Proxy> initProxies() {
        final String valuePropertyBefore = System.getProperty(PROXY_PROPERTY);
        try {
            System.setProperty(PROXY_PROPERTY, "true");
            return ProxySelector.getDefault().select(new java.net.URI("http://www.google.com"));
        } catch (Exception e) {
            // As ProxyDetector is the initial code being executed in main,
            // we cannot afford any failure. This will make our entire JStock
            // application crash.
            // throw new RuntimeException(e);
            log.error(null, e);
        } finally {
            if (valuePropertyBefore != null) {
                System.setProperty(PROXY_PROPERTY, valuePropertyBefore);
            }
        }

        // Use emptyList instead of EMPTY_LIST, to avoid compiler warning.
        return java.util.Collections.emptyList();
    }

    /**
     * Is there a direct connection available? If I return <tt>true</tt> it is not
     * necessary to detect a proxy address.
     *
     * @return <tt>true</tt> if the is a direct connection to the internet
     */
    public boolean directConnectionAvailable() {
        for (Proxy proxy : this.proxies) {
            if (Proxy.NO_PROXY.equals(proxy)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return did we detect a proxy?
     */
    public boolean proxyDetected() {
        return this.proxyToUse != null;
    }

    /**
     * I will determine the right proxy, there might be several proxies
     * available, but some might not support the HTTP protocol.
     *
     * @return a proxy which can be used to access the given url, <tt>null</tt>
     * if there is no proxy which supports HTTP.
     */
    private Proxy determineProxy() {
        if (!directConnectionAvailable()) {
            for (Proxy proxy : this.proxies) {
                if (proxy.type().equals(Proxy.Type.HTTP)) {
                    return proxy;
                }
            }
        }
        return null;
    }

    /**
     * @return a String representing the hostname of the proxy, <tt>null</tt> if there is no proxy
     */
    public String getHostname() {
        if (this.proxyToUse != null) {
            final SocketAddress socketAddress = this.proxyToUse.address();
            if (socketAddress instanceof InetSocketAddress) {
                InetSocketAddress address = (InetSocketAddress) socketAddress;
                return address.getHostName();
            }
        }
        return null;
    }

    /**
     * @return the port of the proxy, <tt>-1</tt> if there is no proxy
     */
    public int getPort() {
        if (this.proxyToUse != null) {
            final SocketAddress socketAddress = this.proxyToUse.address();
            if (socketAddress instanceof InetSocketAddress) {
                InetSocketAddress address = (InetSocketAddress) socketAddress;
                return address.getPort();
            }
        }
        return -1;
    }

    private static final Log log = LogFactory.getLog(ProxyDetector.class);
}
