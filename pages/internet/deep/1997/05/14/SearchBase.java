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
 * Base class for string searching applets
 * paulp@go2net.com 5/3/97
 */

import java.awt.*;

public abstract class SearchBase extends Canvas implements Runnable {
	Thread t;
	String text;
	String pattern;
	String algorithm_name;

	char a[], p[];		/* text and pattern arrays */
	int M, N;			/* length of arrays */

	int compare_count = 0;	/* comparisons to reach answer */
	int hit = 0;			/* position of found text (-1 for none) */

	int t_highlight_start, t_highlight_end;
	int p_highlight_start, p_highlight_end;
	boolean match = false;		/* last comparison a match? */
	boolean started = false;	/* started yet? */

	int pattern_start = 0;

	static final Color BACKGROUND_COLOR = Color.white;
	static final Color TEXT_COLOR = Color.black;
	static final Color SUCCESS_COLOR = Color.green;
	static final Color FAILURE_COLOR = Color.red;
	static final Color COUNT_COLOR = Color.gray;

	static final Font DEFAULT_FONT = new Font("Helvetica", Font.PLAIN, 12);

	static final int WIDTH = 195;
	static final int HEIGHT = 145;

	static int COUNT_X, COUNT_Y, NAME_Y, START_Y;
	static int TEXT_Y, PATTERN_Y;
	static int SLEEP_TIME = 200;

	public SearchBase(String text, String pattern) {
		this.text = text;
		this.pattern = pattern;

		this.a = text.toCharArray();
		this.p = pattern.toCharArray();
		this.M = p.length;
		this.N = a.length;
	}

	public void update(Graphics g) {
		Image offscreen;
		Graphics offgraphics;

		g.setFont(DEFAULT_FONT);

		/* Set up offscreen buffer */
		offscreen = createImage(WIDTH, HEIGHT);
		offgraphics = offscreen.getGraphics();
		offgraphics.setFont(getFont());

		offgraphics.setColor(BACKGROUND_COLOR);
		offgraphics.fillRect(0, 0, WIDTH, HEIGHT);

		offgraphics.setFont(DEFAULT_FONT);
		FontMetrics fm = offgraphics.getFontMetrics();
		offgraphics.setColor(COUNT_COLOR);

		/* Do annoying static stuff */
		NAME_Y = fm.getAscent();
		offgraphics.drawString(algorithm_name, 0, NAME_Y);

		COUNT_X = fm.stringWidth("Comparisons: ");
		COUNT_Y = NAME_Y + fm.getMaxAscent() + fm.getMaxDescent();
		offgraphics.drawString("Comparisons: ", 0, COUNT_Y);

		TEXT_Y = COUNT_Y + fm.getMaxAscent() + fm.getMaxDescent();
		PATTERN_Y = TEXT_Y + fm.getMaxAscent() + fm.getMaxDescent();
		START_Y = PATTERN_Y + fm.getMaxAscent() + fm.getMaxDescent() + 5;

		if(!started) {
			offgraphics.drawString("Click to start", 0, START_Y);
		}

		/* draw the pattern and text */
		drawStrings(offgraphics);
		g.setColor(COUNT_COLOR);
		offgraphics.drawString(Integer.toString(compare_count), 
		  COUNT_X, COUNT_Y);

		/* swap buffers */
		g.drawImage(offscreen, 0, 0, null);
	}

	public void drawStrings(Graphics g) {
		FontMetrics fm = g.getFontMetrics();

		/* First the text */
		g.setColor(TEXT_COLOR);
		g.drawChars(a, 0, t_highlight_start, 0, TEXT_Y);

		g.setColor(match ? SUCCESS_COLOR : FAILURE_COLOR);
		g.drawChars(a, t_highlight_start, 
		  t_highlight_end - t_highlight_start,
		  fm.charsWidth(a, 0, t_highlight_start), TEXT_Y);

		g.setColor(TEXT_COLOR);
		g.drawChars(a, t_highlight_end, N - t_highlight_end,
			  fm.charsWidth(a, 0, t_highlight_end), TEXT_Y);

		/* Now the pattern */
		g.drawChars(p, 0, p_highlight_start,
		  fm.charsWidth(a, 0, pattern_start), PATTERN_Y);

		g.setColor(match ? SUCCESS_COLOR : FAILURE_COLOR);
		g.drawChars(p, p_highlight_start, 
		  p_highlight_end - p_highlight_start,
		  fm.charsWidth(a, 0, pattern_start + p_highlight_start), PATTERN_Y);

		g.setColor(TEXT_COLOR);
		g.drawChars(p, p_highlight_end, M - p_highlight_end,
		  fm.charsWidth(a, 0, pattern_start + p_highlight_end),
		  PATTERN_Y);
	}

	public void paint(Graphics g) {
		update(g);
	}

	void compare(int i, int j, int length, boolean match) {
		incCompare();
		showCompare(i, j, length, match); 
	}

	void showCompare(int i, int j, int length, boolean match) {
		this.match = match;

		t_highlight_start = i;
		t_highlight_end = i + length;

		p_highlight_start = (j >= 0 ? j : 0);
		p_highlight_end = j + length;

		repaint();
		sleep(SLEEP_TIME);
	}

	void incCompare() {
		compare_count++;
		repaint();
	}

	void advancePattern() {
		t_highlight_start = t_highlight_end = 0;
		p_highlight_start = p_highlight_end = 0;

		pattern_start++;
		repaint();
		sleep(SLEEP_TIME);
	}

	void advancePattern(int pos) {
		pattern_start = pos;
		repaint();
		sleep(SLEEP_TIME);
	}

	public void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch(InterruptedException e) {}
	}

	public boolean mouseDown(Event evt, int x, int y) {
		started = true;
		compare_count = 0;
		repaint();

		t = new Thread(this);
		t.start();
		return true;
	}

	public void print(String s) { 
		System.out.println(s);
	}
}


