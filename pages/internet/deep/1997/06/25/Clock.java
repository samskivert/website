// This code is Copyright (C) 1997, go2net Inc. Permission is granted for
// any use so long as this header remains intact.
//
// Originally published in Deep Magic:
//     <URL:http://www.go2net.com/internet/deep/>
//
// The code herein is provided to you as is, without any warranty of any
// kind, including express or implied warranties, the warranties of
// merchantability and fitness for a particular purpose, and
// non-infringement of proprietary rights.  The risk of using this code
// remains with you.

import java.awt.*;
import java.applet.*;
import java.util.Date;

//
// SettableClock
//
// Ray Greenwell
//
// Adapted and hacked by Walter Korman for the mystical Hamachi applet
// Please forgive unused, leftover code remnants.  I feel validated in leaving
// things a mess, as I've managed some actual code-reuse, and the
// deadline calls.  Hallelujah.

class Clock implements Runnable {

    private boolean     flash = false;
    private boolean     pm = false;
    private boolean     black = false;

    private int		hour = 12;
    private int		minute = 0;
    private Thread	runny = null;
    private boolean     running = false;
    private long	minExpire;

    private static Color	GREEN = new Color(0x336600);  
    private static long		MINUTE = 1000L * 60L;

    private Rectangle box;
    private int off_x, off_y;
    
    public Clock(Rectangle box) {
	this.box = box;
	
	off_x = (box.width - 100) / 2;
	off_y = (box.height - 20) / 2;
		
	minExpire = System.currentTimeMillis() + MINUTE;

	Date d = new Date();
	hour = d.getHours();
	if(hour > 11) {
	    pm = true;
	}
	if(hour >= 13) {
	    hour -= 12;
	} else if(hour == 0) {
	    hour = 12;
	}
	
	minute = d.getMinutes();
    }

    public void start() {
	running = true;
	if (runny == null) {
	    runny = new Thread(this);
	    runny.start();
	}
    }

    public void stop() {
	running = false;
    }
		
    public void run() {
	while (running) {
	    try {
		// repaint();
		if (flash) {
		    black = !black;
		    Thread.sleep(500); //half a second
		} else {
		    Thread.sleep(Math.max(0, minExpire -
					  System.currentTimeMillis()));
		}
		
		//check time
		if (System.currentTimeMillis() >= minExpire) {
		    incMinute(true);
		    minExpire = System.currentTimeMillis() + MINUTE -
			(System.currentTimeMillis() - minExpire);
		}
	    } catch (InterruptedException e) {
	    }
	}
	runny = null;
    }
    

    private void incMinute(boolean sideeffects) {
	minute++;
	if (minute == 60) {
	    minute = 0;
	    if (sideeffects) {
		incHour();
	    }
	}
	//repaint();
    }
    
    private void incHour() {
	hour++;
	if (hour == 12) {
	    pm = !pm;
	} else if (hour >= 13) {
	    hour -= 12;
	}
	//repaint();
    }
    
    public boolean mouseDown(Event evt, int x, int y) {
	flash = black = false;
	if (x > 36) {
	    incMinute(false);
	} else {
	    incHour();
	}
	return true;
    }
    
    public void paint(Graphics g) {
	if (!black) {
	    if (hour / 10 != 0) {
		drawNum(hour / 10, g, 0, 0);
	    }
	    drawNum(hour % 10, g, 18, 0);
	    drawNum(minute / 10, g, 43, 0); 
	    drawNum(minute % 10, g, 61, 0);
	}
	if (pm) {
	    drawCirc(g, 79, 22);
	}
	drawCirc(g, 36, 7);
	drawCirc(g, 36, 15);
    }
	
    private void drawNum(int num, Graphics g, int x, int y) {
	//top horiz
	switch (num) {
	case 0: case 2: case 3: case 5: case 6: case 7: case 8: case 9:
	    drawHoriz(g, x + 3, y);
	default:
	} 

	//mid horiz
	switch (num) {
	case 2: case 3: case 4: case 5: case 6: case 8: case 9:
	    drawHoriz(g, x + 3, y + 11);
	default:
	}

	//bot horiz
	switch (num) {
	case 0: case 2: case 3: case 5: case 6: case 8: case 9:
	    drawHoriz(g, x + 3, y + 22);
	default:
	}

	//upperleft vert
	switch (num) {
	case 0: case 4: case 5: case 6: case 8: case 9:
	    drawVert(g, x, y +2);
	default:
	}

	//lowerleft vert
	switch (num) {
	case 0: case 2: case 6: case 8:
	    drawVert(g, x, y + 13);
	default:
	}

	//upperright vert
	switch (num) {
	case 0: case 1: case 2: case 3: case 4: case 7: case 8: case 9:
	    drawVert(g, x + 12, y + 2);
	default:
	}

	//lowerright vert
	switch (num) {
	case 0: case 1: case 3: case 4: case 5: case 6: case 7: case 8:
	case 9:
	    drawVert(g, x + 12, y + 13);
	default:
	}
    }

    private void drawVert(Graphics g, int x, int y) {
	x += box.x + off_x;
	y += box.y + off_y;
	
	g.setColor(GREEN);
	g.drawLine(x, y + 1, x, y + 8);
	g.drawLine(x+1, y, x + 1, y + 9);
	g.drawLine(x+2, y + 1, x + 2, y + 8); 
    }
    
    private void drawHoriz(Graphics g, int x, int y) {
	x += box.x + off_x;
	y += box.y + off_y;
	
	g.setColor(GREEN);
	g.drawLine(x + 1, y, x + 7, y);
	g.drawLine(x, y + 1, x + 8, y + 1);
	g.drawLine(x + 1, y + 2, x + 7, y + 2);
    }
    
    private void drawCirc(Graphics g, int x, int y) {
	x += box.x + off_x;
	y += box.y + off_y;
	
	g.setColor(GREEN);
	g.drawLine(x + 1, y, x + 2, y);
	g.drawLine(x, y + 1, x + 3, y + 1);
	g.drawLine(x + 1, y + 2, x + 2, y + 2);
    }
}


