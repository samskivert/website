#!/usr/local/bin/perl -w

require LWP::UserAgent;
use HTML::Element;
use Fcntl;
use POSIX;
use NDBM_File;

my $db_file = "/var/preserve/words";
my $lookup_log = "/var/preserve/lookup.log";

my @unknowns = ();
# grab all the words from the command line
push @questionables, @ARGV;

open(LOG, ">>$lookup_log") or die "Can't open $lookup_log: $!\n";
select(LOG); $| = 1; select(STDOUT); # stealth-a-hontas

my %words = {};
tie(%words, "NDBM_File", $db_file, O_CREAT|O_RDONLY, 0664) or
    die "Couldn't tie $db_file: $!\n";

foreach $word (@questionables) {
    my $type = $words{$word};

    if (!defined $type) {
        print "unknown $word\n";
        push @unknowns, $word;
    } elsif ($type == 2) {
        print "$word\n";
    }
}

untie(%words);

my @newwords = ();
my @types = ();

foreach $word (@unknowns) {
    my $type = webster_word($word);

    if ($type == 2) {
        print "$word\n";
    }
    push @newwords, $word;
    push @types, $type;
}

write_words(\@newwords, \@types);

close(LOG);

sub write_words {
    my ($wordref, $typeref) = @_;

    my %worddb = {};
    my @words = @$wordref;
    my @types = @$typeref;

    if (open(DBF, ">$db_file.lck")) {
        if (flock(DBF, 2)) {
            tie(%worddb, "NDBM_File", $db_file, O_CREAT|O_RDWR, 0664) or
                die "Couldn't tie worddb: $!\n";

            for ($i = 0; $i <= $#words; $i++) {
                $worddb{$words[$i]} = $types[$i];
            }

            untie(%worddb);
            flock(DBF, 8);
            close(DBF);
        } else {
            print LOG "Couldn't flock $db_file.lck: $!\n";
        }
        
    } else {
        print LOG "Couldn't open $db_file.lck: $!\n";
    }
}

sub webster_word {
    my ($word) = @_;

    my $webster = 'http://www.m-w.com/cgi-bin/netdict';
    my $headers =
        new HTTP::Headers('Content-type' => 'application/x-www-form-urlencoded',
                          'Referer' => 'http://www.m-w.com/netdict.htm');

    my $ua = new LWP::UserAgent;
    my $request = new HTTP::Request('POST', $webster, $headers);
    my $querystring = "book=Dictionary&va=$word\r\n";

    print LOG "looking up: $word\n";
    
    $request->add_content($querystring);
    $response = $ua->request($request);

    my $content = $response->content();

    if ($content =~ /No entries found that match your query/) {
        print LOG "$word invalid\n";
        return 2;
    }

    my ($entry_line) = ($content =~ /(Main Entry:[^\n]*)/);
    my ($foo, $entry) =
        ($content =~ /Main Entry:\s?<b>\s?(<sup>\s?[^<]*\s?<\/sup>\s?)?([^<]*)</);

    print LOG "entry line: $entry_line\n";
    print LOG "entry: $entry\n";

    # make sure it's not capital letter or a prefix or suffix
    if (!($entry =~ /^[-A-Z]/) && !($entry =~ /-$/)) {
        print LOG "$word valid\n";
        return 1;
    }

    print LOG "$word invalid\n";
    return 2;
}
