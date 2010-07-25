# Ensure (1) Correct code format for ShenZhen stock market. (2) There is no space in between symbol.
use strict;
sub strip_spaces { my $str = shift; $str =~ y/ //d; $str }
open INPUT, "<C:/Projects/guestbook/war/stocks_information/china/stocks.csv" or die $!;
open OUTPUT, ">C:/Projects/guestbook/war/stocks_information/china/stocks-new.csv" or die $!;
while (my $line = <INPUT>) {
    $line =~ s/(\d+)/ sprintf '%.6d'.'.sz', $1 /e;
    $line =~ s/("[^"]+",)/ strip_spaces($1) /e;
    print OUTPUT $line; 
}
close OUTPUT;
close INPUT;