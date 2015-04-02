# JStock - Free Stock Market Software
# Copyright (C) 2010 Yan Cheng CHEOK <yccheok@yahoo.com>
# 
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

# Fix Shenzhen's CSV file downloaded from http://www.szse.cn/main/marketdata/jypz/colist/
#
# 1)Ensure correct code format for Shenzhen Stock Exchange.
# 2)Ensure there is no space in between symbol.

use strict;
sub strip_spaces { my $str = shift; $str =~ y/ //d; $str }
open INPUT, "<C:/Projects/guestbook/war/stocks_information/china/stocks.csv" or die $!;
open OUTPUT, ">C:/Projects/guestbook/war/stocks_information/china/tmp.csv" or die $!;
while (my $line = <INPUT>) {
    $line =~ s/(\d+)/ sprintf '%.6d'.'.SZ', $1 /e;
    $line =~ s/("[^"]+",)/ strip_spaces($1) /e;
    print OUTPUT $line; 
}
close OUTPUT;
close INPUT;
rename("C:/Projects/guestbook/war/stocks_information/china/tmp.csv", "C:/Projects/guestbook/war/stocks_information/china/stocks.csv");