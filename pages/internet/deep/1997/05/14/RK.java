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
 * Implements Rabin-Karp string search
 * paulp@go2net.com 5/3/97
 */

import java.awt.*;
import java.applet.*;

public class RK extends SearchBase {

	public RK(String text, String pattern) {
		super(text, pattern);
		algorithm_name = "Rabin-Karp";
	}

	public void run() {
		RK(text, pattern);
	}

	public void RK(String text, String pattern) {
		/* "q, what big size you have!" */
		/* "The better to hash you with, my dear." */
		long q = 2113929255l, d = 32, d_bits = 5;

		char[] a = text.toCharArray();
		char[] p = pattern.toCharArray();
		int M = p.length, N = a.length;
		long h1 = 0, h2 = 0, dM = 1;
		int i;

		for(i = 0; i < M - 1; i++)
			dM = (dM << d_bits) % q;

		/* Whoa, pass the hash, man */
		for(i = 0; i < M; i++) {
			h1 = ((h1 << d_bits) + (long)p[i]) % q;
			h2 = ((h2 << d_bits) + (long)a[i]) % q;
		}

		/* There's got to be a string in here somewhere */
		i = 0;
		while(h1 != h2 && i < N - M) {
			h2 = (h2 + (q << d_bits) - (long)a[i] * dM) % q;
			h2 = ((h2 << d_bits) + (long)a[i + M]) % q;
			compare(i, 0, M, false);
			i++;
			advancePattern(i);
		}

		/* no match */
		if(i >= N - M) {
			showCompare(i, 0, M, false);
			hit = -1;
			return;
		}

		/* match */
		incCompare();
		showCompare(i, 0, M, true);
		hit = i;
		return;
	}
}


