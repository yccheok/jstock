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

package org.yccheok.jstock.portfolio;

import org.yccheok.jstock.engine.Country;

/**
 *
 * @author yccheok
 */
public class PortfolioInfo {
    public final Country country;
    public final String name;
    public final int size;
    
    private PortfolioInfo(Country country, String name, int size) {
        this.country = country;
        this.name = name;
        this.size = size;
    }
    
    public static PortfolioInfo newInstance(Country country, String name, int size) {
        return new PortfolioInfo(country, name, size);
    }
    
    public PortfolioInfo setCountry(Country country) {
        return new PortfolioInfo(country, this.name, this.size);
    }
    
    public PortfolioInfo setName(String name) {
        return new PortfolioInfo(this.country, name, this.size);
    }
    
    public PortfolioInfo setSize(int size) {
        return new PortfolioInfo(this.country, this.name, size);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + country.hashCode();
        result = 31 * result + name.toLowerCase().hashCode();
        // Do not compare size.
        
        return result;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof PortfolioInfo)) {
            return false;
        }
        
        PortfolioInfo portfolioInfo = (PortfolioInfo)o;
        // Do not compare size.
        return this.country.equals(portfolioInfo.country) && this.name.toLowerCase().equals(portfolioInfo.name.toLowerCase());
    }
    
    @Override
    public String toString() {
        return PortfolioInfo.class.getSimpleName() + "[" + country + ", " + name + ", " + size + "]";
    }    
}
