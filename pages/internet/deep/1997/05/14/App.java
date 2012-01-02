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
 * App.java
 * paulp@go2net.com 5/3/97
 */

import java.awt.*;
import java.applet.*;

public class App extends Applet {
	SearchBase sb;

	public void init() {
		String text, pattern, algorithm;

		text = getParameter("text");
		pattern = getParameter("pattern");
		algorithm = getParameter("algorithm");

		if(text == null) { text = "some lame search text"; }
		if(pattern == null) { pattern = "earc"; }
		if(algorithm == null) { algorithm = "BruteForce"; }

		if(algorithm.compareTo("BruteForce") == 0) {
			sb = new BruteForce(text, pattern);
		}
		else if(algorithm.compareTo("KMP") == 0) {
			sb = new KMP(text, pattern);
		}
		else if(algorithm.compareTo("BM") == 0) {
			sb = new BM(text, pattern);
		}
		else if(algorithm.compareTo("RK") == 0) {
			sb = new RK(text, pattern);
		}
		else {
			sb = new BruteForce(text, pattern);
		}

		setLayout(new BorderLayout());
		add("Center", sb);
	}
}

