#!/usr/bin/perl

# JStock - Free Stock Market Software
# Copyright (C) 2010 Shuwn Yuan TEE <s_yuan31tee@yahoo.com>
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

# Extract Shanghai Stock Exchange data.

use WWW::Mechanize;
use utf8;
use Data::Dumper;


my $mech = WWW::Mechanize->new();
my $url = "http://www.sse.com.cn/sseportal/webapp/datapresent/SSEQueryStockInfoAct?keyword=&reportName=BizCompStockInfoRpt&PRODUCTID=&PRODUCTJP=&PRODUCTNAME=&CURSOR=1";

my $is_first_time = 1;

while ($url)
{
	$mech->get( $url );
	my $content = $mech->content();

	open FILE, ">:utf8", "a.txt" or die $!;
	print FILE $content;
	close FILE;

	my $new_content;
	if ($content =~ /证券简称<\/td>.+?<\/tr>(.+?)<\/table>/s)
	{
		$new_content = $1;
		print "match! \n";
	}

	open FILE, ">:utf8", "b.txt" or die $!;
	print FILE $new_content;
	close FILE;

	# extract stock code, stock name & write to csv
	extract_stock_info($new_content, $is_first_time);

	# dinamically grep for next page URL
	if ($content =~ /<a href="(.+?)">下一页<\/a>/)
	{
		$url = 'http://www.sse.com.cn'. $1;
	}
	else
	{
		$url = undef;
	}

	if ($is_first_time == 1)
	{
		$is_first_time = 0;
	}
}



sub extract_stock_info
{
	my $new_content = shift;
	my $is_first_time = shift;

	my @stocks = split("</tr>", $new_content);
	#print "stocks  [" . Data::Dumper::Dumper(\@stocks) . "] \n";


	my $main_url = "http://www.sse.com.cn";
	# grep stock code, name for each stock
	my @stocks_info;
	foreach my $stock (@stocks)
	{
		my ($stock_name, $stock_code, $stock_url, $stock_long_name);
		if ($stock =~ /<td class="table3" bgcolor=".+?">([^<]+)?<\/td>/)
		{
			$stock_name = $1;
		}

		if ($stock =~ /&COMPANY_CODE=\d+"\s*>(\d+)<\/a>/)
		{
			$stock_code = $1;
		}

		if ($stock =~ /<a href="(.+?&COMPANY_CODE=$stock_code)"\s*>$stock_code<\/a>/)
		{
			$stock_url = $main_url . $1;
			$mech->get( $stock_url );
			my $content = $mech->content();

			if ($content =~ /<span class="pagetitle"\s*>(.+)?\s+$stock_code<br>/)
			{
				$stock_long_name = $1;
			}
		}

		if ($stock_code and $stock_name)
		{
			push(@stocks_info, $stock_code.'.SS'.',"'.$stock_name.'","'.$stock_long_name.'","","","","","","","","","","","","","","","","",""' );
		}
	}


	if ($is_first_time)
	{
		open FILE, ">:utf8", "stocks.csv" or die $!;
	}
	else
	{
		open FILE, ">>:utf8", "stocks.csv" or die $!;
	}

	print "amount: " . scalar @stocks_info . "\n";

	foreach my $stock_info (@stocks_info)
	{
		#print "$stock_info \n";
		print FILE "$stock_info\n";
	}
	close FILE;
}





