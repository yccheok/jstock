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

# Ensure there will not be runtime error for ResourceBundle.getString
use strict;
use File::Find;

my %gui_bundle = ();
my %messages_bundle = ();
my $init_counter = 0;
my $process_counter = 0;
my $dir = "c:/Projects/jstock/src";

if ($ARGV[0])
{
    $dir = $ARGV[0];
}

####################
# Default Language #
####################
reset_global_variables();
find(\&init, $dir);
find(\&process, $dir);
print "\n$init_counter resource keys being initiazlied.\n";
print "$process_counter files being processed.\n";
print "Validation on (default) files done!\n\n\n";

####################
# English Language #
####################
reset_global_variables();
find(\&init_en, $dir);
find(\&process, $dir);
print "\n$init_counter resource keys being initiazlied.\n";
print "$process_counter files being processed.\n";
print "Validation on (en) files done!\n\n\n";

####################
# Chinese Language #
####################
reset_global_variables();
find(\&init_zh, $dir);
find(\&process, $dir);
print "\n$init_counter resource keys being initiazlied.\n";
print "$process_counter files being processed.\n";
print "Validation on (zh) files done!\n\n\n";

################################
# Traditional Chinese Language #
################################
reset_global_variables();
find(\&init_zh_tw, $dir);
find(\&process, $dir);
print "\n$init_counter resource keys being initiazlied.\n";
print "$process_counter files being processed.\n";
print "Validation on (zh_tw) files done!\n\n\n";

####################
# Germany Language #
####################
reset_global_variables();
find(\&init_de, $dir);
find(\&process, $dir);
print "\n$init_counter resource keys being initiazlied.\n";
print "$process_counter files being processed.\n";
print "Validation on (de) files done!\n\n\n";

sub reset_global_variables
{
    %gui_bundle = ();
    %messages_bundle = ();
    $init_counter = 0;
    $process_counter = 0;
}

sub init_de
{
    my $name = $_;
    my $dir = $File::Find::dir;
    my $file = $File::Find::name;
    # Till we have messages_de.properties, we will use default messages.properties
    # at current moment.
    #if ($file !~ /(gui_de\.properties$)|(messages_de\.properties$)/) {
    if ($file !~ /(gui_de\.properties$)|(messages\.properties$)/) {
        return;
    }

    my $hash = \%gui_bundle;
    # Till we have messages_de.properties, we will use default messages.properties
    # at current moment.
    #if ($file =~ /(messages_de\.properties$)/) {
    if ($file =~ /(messages\.properties$)/) {
        $hash = \%messages_bundle;
    }
    open FILE, $file or die $!;
    while (my $line = <FILE>) {
        if ($line =~ /^#/) {
            next;
        }
        chomp $line;

        if ($line =~ /([^=]+)=(.+)/) {
            my $key = $1;
            my $value = $2;
            if ($hash->{$key}) {
                print "WARNING : conflict occurs for key '$key'\n";
            }
            $hash->{$key} = $value;
            $init_counter++;
        }
    }
    close(FILE);
}

sub init_zh
{
    my $name = $_;
    my $dir = $File::Find::dir;
    my $file = $File::Find::name;
    if ($file !~ /(gui_zh\.properties$)|(messages_zh\.properties$)/) {
        return;
    }

    my $hash = \%gui_bundle;
    if ($file =~ /(messages_zh\.properties$)/) {
        $hash = \%messages_bundle;
    }
    open FILE, $file or die $!;
    while (my $line = <FILE>) {
        if ($line =~ /^#/) {
            next;
        }
        chomp $line;

        if ($line =~ /([^=]+)=(.+)/) {
            my $key = $1;
            my $value = $2;
            if ($hash->{$key}) {
                print "WARNING : conflict occurs for key '$key'\n";
            }
            $hash->{$key} = $value;
            $init_counter++;
        }
    }
    close(FILE);
}

sub init_zh_tw
{
    my $name = $_;
    my $dir = $File::Find::dir;
    my $file = $File::Find::name;
    if ($file !~ /(gui_zh_TW\.properties$)|(messages_zh_TW\.properties$)/) {
        return;
    }

    my $hash = \%gui_bundle;
    if ($file =~ /(messages_zh_TW\.properties$)/) {
        $hash = \%messages_bundle;
    }
    open FILE, $file or die $!;
    while (my $line = <FILE>) {
        if ($line =~ /^#/) {
            next;
        }
        chomp $line;

        if ($line =~ /([^=]+)=(.+)/) {
            my $key = $1;
            my $value = $2;
            if ($hash->{$key}) {
                print "WARNING : conflict occurs for key '$key'\n";
            }
            $hash->{$key} = $value;
            $init_counter++;
        }
    }
    close(FILE);
}

sub init_en
{
    my $name = $_;
    my $dir = $File::Find::dir;
    my $file = $File::Find::name;
    if ($file !~ /(gui_en\.properties$)|(messages_en\.properties$)/) {
        return;
    }

    my $hash = \%gui_bundle;
    if ($file =~ /(messages_en\.properties$)/) {
        $hash = \%messages_bundle;
    }
    open FILE, $file or die $!;
    while (my $line = <FILE>) {
        if ($line =~ /^#/) {
            next;
        }
        chomp $line;

        if ($line =~ /([^=]+)=(.+)/) {
            my $key = $1;
            my $value = $2;
            if ($hash->{$key}) {
                print "WARNING : conflict occurs for key '$key'\n";
            }
            $hash->{$key} = $value;
            $init_counter++;
        }
    }
    close(FILE);
}

sub init
{
    my $name = $_;
    my $dir = $File::Find::dir;
    my $file = $File::Find::name;
    if ($file !~ /(gui\.properties$)|(messages\.properties$)/) {
        return;
    }

    my $hash = \%gui_bundle;
    if ($file =~ /(messages\.properties$)/) {
        $hash = \%messages_bundle;
    }
    open FILE, $file or die $!;
    while (my $line = <FILE>) {
        if ($line =~ /^#/) {
            next;
        }
        chomp $line;

        if ($line =~ /([^=]+)=(.+)/) {
            my $key = $1;
            my $value = $2;
            if ($hash->{$key}) {
                print "WARNING : conflict occurs for key '$key'\n";
            }
            $hash->{$key} = $value;
            $init_counter++;
        }
    }
    close(FILE);
}

sub process
{
    my $name = $_;
    my $dir = $File::Find::dir;
    my $file = $File::Find::name;
    if ($file !~ /\.java$/) {
        return;
    }
    open FILE, $file or die $!;
    while (my $line = <FILE>) {
        chomp $line;

        while ($line =~ /GUIBundle\.getString\("([^"]+)"\)/g) {
            my $key = $1;
            if (not $gui_bundle{$key}) {
                print "WARNING : key '$key' not found ($name $.)\n";
            }
        }
        while ($line =~ /MessagesBundle\.getString\("([^"]+)"\)/g) {
            my $key = $1;
            if (not $messages_bundle{$key}) {
                print "WARNING : key '$key' not found ($name $.)\n";
            }
        }
        while ($line =~ /bundle\.getString\("([^"]+)"\)/g) {
            my $key = $1;
            if (not $messages_bundle{$key} and not $gui_bundle{$key}) {
                print "WARNING : key '$key' not found ($name $.)\n";
            }            
        }
        while ($line =~ /getBundle\("org\/yccheok\/jstock\/data\/gui"\)\.getString\("([^"]+)"\)/g) {
            my $key = $1;
            if (not $gui_bundle{$key}) {
                print "WARNING : key '$key' not found ($name $.)\n";
            }        
        }
        while ($line =~ /getBundle\("org\/yccheok\/jstock\/data\/messages"\)\.getString\("([^"]+)"\)/g) {
            my $key = $1;
            if (not $messages_bundle{$key}) {
                print "WARNING : key '$key' not found ($name $.)\n";
            }        
        }        
    }
    close(FILE);
    $process_counter++;
}
