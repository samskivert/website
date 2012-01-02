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
 * Implements Knuth-Morris-Pratt string search
 * paulp@go2net.com 5/3/97
 */

import java.awt.*;
import java.applet.*;

public class KMP extends SearchBase {

	public KMP(String text, String pattern) {
		super(text, pattern);
		algorithm_name = "Knuth-Morris-Pratt";
	}

	public void run() {
		KMP(text, pattern);
	}

	public void KMP(String text, String pattern) {
		int[] next = getKMPNext();
		int i = 0, j = 0;

		while(i < N && j < M) {
			if(j == -1 || a[i] == p[j]) {
				if(j >= 0)
					compare(i, j, 1, true);
				i++; 
				j++;
			}
			else {
				compare(i, j, 1, false);
				j = next[j];
				advancePattern(i - j);
			}
		}

		/* match */
		if(j == M) {
			hit = i - j;
			showCompare(hit, 0, M, true);
			return;
		}

		/* no match */
		hit = -1;
		return;
	}


	/*
	 * builds the KMP next[] array.  This is fairly confusing.
	 * It "slides" the first j-1 characters over the pattern
	 * itself, starting at the array location [1] and stopping
	 * when all overlapping characters match.  This is the
	 * >next< place the pattern could match in such a text
	 * string, and it is as far forward as we can jump the
	 * pointer after a mismatch.
	 */

	public int[] getKMPNext() {
		int[] next = new int[M];
		int i = 0, j = -1;

		next[0] = -1;
		do {
			if(j == -1 || p[i] == p[j]) {
				i++;
				j++;
				if(p[i] != p[j]) {
					next[i] = j;
				}
				else {
					next[i] = next[j];
				}
			}
			else {
				j = next[j];
			}
		} while(i < M - 1);

		return next; 
	}
}


