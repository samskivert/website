#!/usr/local/bin/perl -w

use Fcntl;
use POSIX;
use NDBM_File;

my $db_file = "/var/preserve/words";

my %words = {};
tie(%words, "NDBM_File", $db_file, O_CREAT|O_RDWR, 0664) or
    die "Couldn't tie $db_file: $!\n";

my $input = shift or die "Usage: create_dict.pl word_list\n";

open(INP, $input) or die "Can't open $input: $!\n";

while (<INP>) {
    chomp($_);
    if (/^[A-Z].*/) {
        $words{$_} = 2;
    } else {
        $words{$_} = 1;
    }
}

untie(%words);
