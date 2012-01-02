/*
 * This code is Copyright (C) 1997, go2net Inc.
 * Permissions is granted for any use so long as this header
 * remains intact.
 * Originally published in Deep Magic:
 *   <URL:http://www.go2net.com/internet/deep/>
 *
 * The code herein is provided to you as is, without any warranty
 * of any kind, including express or implied warranties, the
 * warranties of merchantability and fitness for a particular
 * purpose, and non-infringement of proprietary rights.  The risk
 * of using this code remains with you.
 */

/*
 * Implements Boyer-Moore string search
 * paulp@go2net.com 5/3/97
 */

import java.awt.*;
import java.applet.*;

public class BM extends SearchBase {
	static final int ASCII_SIZE = 256;

	public BM(String text, String pattern) {
		super(text, pattern);
		algorithm_name = "Boyer-Moore";
	}

	public void run() {
		BM(text, pattern);
	}

	public void BM(String text, String pattern) {
		int[] skip = getBMSkip(p);
		int i = M - 1, j = M - 1;
		int min_pos = 0;

		do {
			/* test current (rightmost) char */
			if(a[i] == p[j]) {
				compare(i, j, 1, true);
				if(j == M - 1)		/* reset first untested position */
					min_pos = i + 1;
				i--;				/* move yon pointer back */
				j--;
			}
			else {					/* no match */
				compare(i, j, 1, false);
				i += skip[(int)a[i]];	/* skip forward far as we can */
				if(i < min_pos)			/* at least move to untested */
					i = min_pos;
				j = M - 1;				/* set pattern pointer to end */
				advancePattern(i - j);
			}
		} while(j >= 0 && i < N);

		/* match */
		if(j < 0) {
			hit = i + 1;
			showCompare(hit, 0, M, true);
			return;
		}

		/* no match */
		hit = -1;
		return;
	}

	/*
	 * builds the BM skip[] array.  It presets all the possible
	 * alphabet letters to the length of the pattern, then shrinks
	 * all those that actually occur in the pattern to M minus
	 * one minus the position in which they occur.  This is as far
	 * forward as we can skip without potentially missing a match.
	 */

	public int[] getBMSkip(char[] p) {
		int M = p.length;
		int[] skip = new int[ASCII_SIZE];
		int i;

		/* initialize all entries to length of pattern */
		for(i = 0; i < ASCII_SIZE; i++)
			skip[i] = M;
	
		/* now reset characters actually in the pattern to M - i - 1 */
		for(i = 0; i < M; i++)
			skip[(int)p[i]] = M - i - 1;

		return skip;
	}
}


