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
 * Implements brute force string search
 * paulp@go2net.com 5/3/97
 */

import java.awt.*;
import java.applet.*;

public class BruteForce extends SearchBase {

	public BruteForce(String text, String pattern) {
		super(text, pattern);
		algorithm_name = "Brute Force";
	}

	public void run() {
		bruteForce(text, pattern);
	}

	void bruteForce(String text, String pattern) {
		int i = 0, j = 0;

		while(i < N) {
			while(a[i] == p[j]) {
				compare(i, j, 1, true);
				i++;
				j++;

				/* match */
				if(j == M) {
					hit = i - j;
					showCompare(hit, 0, M, true);
					return;
				}

				/* end of string, no match */
				if(i == N) {
					hit = -1;
					return;
				}
			}
			compare(i, j, 1, false);
			
			/* set i back to 1 past current start and j to 0 */
			i -= (j - 1);
			advancePattern(i);
			j = 0;
		}

		/* no match */
		hit = -1;
		return;
	}
}

