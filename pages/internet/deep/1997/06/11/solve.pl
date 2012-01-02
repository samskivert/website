#!/usr/local/bin/perl -w
#
# solve.pl -- code for cheating at an NxN word grid
#
# This code is Copyright (C) 1997, go2net Inc. Permission is granted for
# any use so long as this header remains intact.
#
# Originally published in Deep Magic:
#     <URL:http://www.go2net.com/internet/deep/>
#
# The code herein is provided to you as is, without any warranty of any
# kind, including express or implied warranties, the warranties of
# merchantability and fitness for a particular purpose, and
# non-infringement of proprietary rights.  The risk of using this code
# remains with you.
# 
#
$DICT = "crack_dict.txt";
%SCORES = (4 => 1, 5 => 2, 6 => 3, 7 => 5);

$SIDE = shift;
$MINLENGTH = shift;
$fields = shift;

unless ($SIDE and $MINLENGTH and $fields) {
	print <<EOL

args:
	<number of letters on a side> <minlength> <board letters, row by row>

	A typical command line might be:
	solve.pl 5 4 alxtsasioftahrohtninnague

	The program will first print out the board to verify that you entered
	it correctly, then it will proceed to read in the dictionary and check
	words.
EOL
}


# build the hash, and print out the board to verify
foreach $jj (0..$SIDE - 1) {
	foreach $ii (0..$SIDE - 1) {
		my $num = $jj * $SIDE + $ii;
		my $singleletter = substr($fields, $num, 1);
		my $listref = $positions{$singleletter};
		unless ($listref) {
			$listref = [ ];
			$positions{$singleletter} = $listref;
		}
		push (@$listref, $num);
		print "$singleletter ";
	}
	print "\n";
}
print "--"x$SIDE . "\n";

open (IN, "<$DICT");

my ($bla, $word, $score, $wordlen);
#read in each word, and put it in the wordhash
while (defined($word = <IN>)) {

	chomp($word);
	if ((length($word) >= $MINLENGTH) and ($word !~ /q[^u]/)) {

		# sly q-substitution
		$word =~ s/qu/q/g;
		$wordlist{$word} = 1;
	}
}

#the wordhash helps us randomize the results, so they don't look computer
#generated.
$score = 0;
while (($word, $bla) = each %wordlist) {

	my @path = ();
	if (findit($word, @path)) {
		$word =~ s/q/qu/g;

		$wordlen = length($word);

		if ($wordlen > 7) {
			$score += 11;
		} else {
			$score += $SCORES{$wordlen};
		} 
		print "$word\n";
	}
}
close (IN);

print "--"x$SIDE . "\n";
print "Score: $score\n";

#done!



# returns 1 if the rest of the letters can be mapped out from the current
# path
sub findit {
	my ($letters, @path) = @_;
	my ($listref, $pos);

	# pull out a list of the positions of the first letter.
	$listref = $positions{substr($letters, 0, 1)};

	# step through each position
	foreach $pos (@$listref) {

		# if that position would be legal to add to this path..
		if (legalpath($pos, @path)) {

			# then if we are on the last letter, we are done!
			if (length($letters) == 1) {
				return 1;
			}

			# otherwise, add that position to our path,
			push(@path, $pos);

			# and recurse with the rest of the letters.
			if (findit(substr($letters, 1), @path)) {
				return 1;
			}

			# if we didn't find it, take that position off and try the rest
			pop(@path);
		}
	}

	# if none of the positions were any good
	return 0;
}


# check to see if a path is legal
sub legalpath {
	my ($curpos, @path) = @_;
	my ($item, $lastpos, $lastx, $lasty, $curx, $cury);

	#if we are just starting, it is legal.
	if (scalar @path == 0) {
		return 1;
	}

	#make sure the new position is next to the last one.
	$lastpos = $path[(scalar @path) - 1];
	$lastx = $lastpos % $SIDE;
	$lasty = int($lastpos / $SIDE);

	$curx = $curpos % $SIDE;
	$cury = int($curpos / $SIDE);
	if ((abs($curx - $lastx) > 1) or (abs($cury - $lasty) > 1)) {
		return 0;
	}
	

	# now check each old position to make sure its not the same as the new one
	foreach $item (@path) {
		if ($item == $curpos) {
			return 0;
		}
	}

	#all is good
	return 1;
}
