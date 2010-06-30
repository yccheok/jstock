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

find(\&init, $dir);
find(\&process, $dir);

print "\n$init_counter resource keys being initiazlied.\n";
print "$process_counter files being processed.\n";

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
	my $line_number = 0;
	while (my $line = <FILE>) {
		$line_number++;
		if ($line =~ /^#/) {
			next;
		}
		chomp $line;

		if ($line =~ /GUIBundle\.getString\("([^"]+)"\)/) {
			my $key = $1;
			if (not $gui_bundle{$key}) {
				print "WARNING : key '$key' not found ($name $line_number)\n";
			}
		}
		elsif ($line =~ /MessagesBundle\.getString\("([^"]+)"\)/) {
			my $key = $1;
			if (not $messages_bundle{$key}) {
				print "WARNING : key '$key' not found ($name $line_number)\n";
			}		
		}
        elsif ($line =~ /bundle\.getString\("([^"]+)"\)/) {
            my $key = $1;
			if (not $messages_bundle{$key} and not $gui_bundle{$key}) {
				print "WARNING : key '$key' not found ($name $line_number)\n";
			}            
        }
        elsif ($line =~ /getBundle\("org\/yccheok\/jstock\/data\/gui"\)\.getString\("([^"]+)"\)/) {
			my $key = $1;
			if (not $gui_bundle{$key}) {
				print "WARNING : key '$key' not found ($name $line_number)\n";
			}        
        }
        elsif ($line =~ /getBundle\("org\/yccheok\/jstock\/data\/messages"\)\.getString\("([^"]+)"\)/) {
			my $key = $1;
			if (not $messages_bundle{$key}) {
				print "WARNING : key '$key' not found ($name $line_number)\n";
			}        
        }        
	}
	close(FILE);
	$process_counter++;
}
