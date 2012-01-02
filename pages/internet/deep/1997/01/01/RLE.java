//
// RLE - RLE compression algorithm
//
// $Id: RLE.java,v 1.0 1996/12/16 09:07:07 wfk Exp $

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Class: RLE
 * 
 * Displays live RLE compression/decompression of strings.  The current
 * string, either compressed or decompressed, is stored in _workString.
 * Initially, data is displayed and stored in _workString uncompressed.
 * When the user clicks on the applet, the data is compressed and the
 * progress of the compression is displayed.  Clicking again will decompress
 * the data.  The user may compress/decompress to their heart's content
 * (or until their mouse button wears out.)
 *
 * Assumptions for code simplicity:
 *     - Length of runs of character sequences are <= 9.
 *     - Alphabet used in input strings consists of only {'A', 'B'}.
 */
public class RLE extends BaseApplet
{
    /* RLE public member functions */

    /**
     * run
     *
     * Main body of the RLE applet.  Toggles between compressing and 
     * decompressing the string.
     */
    public void run ()
    {
        Dimension d = size();
	boolean comp = false;
	Rectangle dispRect = new Rectangle(5, (10 + _ascent + _height),
				   d.width - 10, d.height - (15 + _ascent +
				   (3 * _height)));

        while (_running) {
	    blank();

	    drawDataString(_workString.toString(), dispRect);

	    if(comp == true) {
                message("Click to uncompress.", 
		  "",
		  "String = " + _workString.toString(),
		  "Length = " + (_workString.toString()).length() +  
		  ", Savings = " + 
		  (1.0 - (float) _workString.length() / 
		  (float) _srcString.length()) * 100.0 + "%");
	    } else {
                message("Click to compress.", 
		  "",
		  "String = " + _workString.toString(),
		  "Length = " + (_workString.toString()).length());
	    }

            repaint();
            waitForClick();
	    blank();

	    if (comp == true) {
		message("Decompressing...", "", "", "");
		decompress(dispRect);
	    } else {
		message("Compressing...", "", "", "");
		compress(dispRect);
	    }

	    comp = !comp;
	}
    }

    /**
     * drawDataString
     *
     * Draws the data string in the applet, with 'A' = COLOR_A, 'B' = COLOR_B.
     * Numbers in the data string (representing the quantity of compression) 
     * are drawn as separate blocks of yellow.  Also calculates appropriate 
     * values for _hblocks, _vblocks, _block_width, _block_height based on the 
     * rect we draw into and the length of the data string.
     */
    public void drawDataString(String data, Rectangle dispRect) {
	int width, height, x, y, i;
	Color oldColor, fillColor, textColor;

	_hblocks = (int) Math.ceil(Math.sqrt((float) data.length()));
	_vblocks = _hblocks;
	_block_width = dispRect.width / _hblocks;
	_block_height = dispRect.height / _vblocks;

	x = dispRect.x;
	y = dispRect.y;

	oldColor = _offGraphics.getColor();

	for(i = 0; i < data.length(); i++) {
	    if(i % _hblocks == 0) {
		x = dispRect.x;
		y = dispRect.y + ((i / _hblocks) * _block_height);
	    } else {
		x += _block_width;
	    }

	    switch(data.charAt(i)) {
	      case 'A':
		fillColor = COLOR_A;
		textColor = COLOR_A_TEXT;
		break;

	      case 'B':
		fillColor = COLOR_B;
		textColor = COLOR_B_TEXT;
		break;

	      default: /* number */
		fillColor = COLOR_COMP;
		textColor = COLOR_NUM_TEXT;
		break;
	    }

	    _offGraphics.setColor(fillColor);
	    _offGraphics.fillRect(x + 1, y + 1, _block_width - 2, 
	      _block_height - 2);
	    _offGraphics.setColor(Color.black);
	    _offGraphics.drawRect(x + 1, y + 1, _block_width - 2, 
	      _block_height - 2);

	    _offGraphics.setColor(textColor);
	    _offGraphics.drawString(data.charAt(i) + "", 
	      x + (_block_width / 2),
	      y + (_block_height / 2) + (_height / 2));
	}

	_offGraphics.setColor(oldColor);
    }

    /**
     * markChar
     *
     * Marks character i's graphics block with markColor.  Delays for delay 
     * milliseconds.
     */
    public synchronized void markChar(int i, Rectangle dispRect, 
				Color markColor, long delay) {
	Color oldColor;

	oldColor = _offGraphics.getColor();
	_offGraphics.setColor(markColor);
	_offGraphics.fillRect(dispRect.x + (i % _hblocks * _block_width) + 1,
	  dispRect.y + ((i / _hblocks) * _block_height) + 1,
	  _block_width - 1, _block_height - 1);
	_offGraphics.setColor(oldColor);

	repaint();

	/* Wait briefly, else repaint event isn't received */
	if(delay > 0) {
	    try {
	        this.wait(delay);
	    } catch(InterruptedException e) { }
	}
    }

    /**
     * markChunk
     *
     * Marks chunks of chars with MARK_COMP; used to show character chunks which
     * can be compressed.
     */
    public synchronized void markChunk(int from, int to, Rectangle dispRect) {
	int i;

	/* Mark all chars in chunk at once */
	for(i = from; i <= to; i++)
	    markChar(i, dispRect, COLOR_COMP, 0);

	/* Wait briefly for repaint event; slightly longer wait here since
	   we mark a whole chunk */
	try {
	    this.wait(CHUNK_DELAY);
	} catch(InterruptedException e) { }
    }

    /**
     * compress
     *
     * Perform RLE compression.  Highlights each character in
     * the graphical display of the source string as it is examined.
     */
    public void compress(Rectangle dispRect) {
	int i, j, count, len;
	char c;
	String startString;
	StringBuffer outString;

	/* Perform RLE compression */

	startString = _workString.toString();
	outString = new StringBuffer();
	len = _workString.length();
	i = 0;

	while(i < len) {
	    drawDataString(startString, dispRect);
	    markChar(i, dispRect, COLOR_MARK, CHAR_DELAY);

	    count = 0;
	    c = _workString.charAt(i);

	    if(i < len - 1) {
	        j = i;
	        while(j < len - 1 && _workString.charAt(j + 1) == c) {
		    count++;
		    j++;
		}

		if(count > 1) {
		    /* Show char compression by marking char chunk */
		    markChunk(i, j, dispRect);
			
		    i += count;
		    outString.append(count + 1);
		}
	    }

	    outString.append(c);
	    i++;
	}

	/* Replace old uncompressed string with the new compressed string */
	_workString = outString;
    }


    /**
     * decompress
     *
     * Perform RLE decompression.  Highlights each character in
     * the graphical display of the source string as it is examined.
     */
    public void decompress(Rectangle dispRect) {
	int i, j, count, len;
	char c;
	String startString;
	StringBuffer outString;

	/* Perform RLE decompression */

	startString = _workString.toString();
	outString = new StringBuffer();
	len = _workString.length();
	i = 0;

	while(i < len) {
	    drawDataString(startString, dispRect);
	    markChar(i, dispRect, COLOR_MARK, CHAR_DELAY);

	    count = 0;
	    c = _workString.charAt(i);

	    if(c == 'A' || c == 'B') {
	        outString.append(c);
	    } else {
		/* Show a compressed (num, char) pair by marking char chunk */
		markChunk(i, i+1, dispRect);

		i++;
		for(j = 0; j < (c - '0'); j++)
		    outString.append(_workString.charAt(i));
	    } 
	    i++;
	}

	/* Replace old uncompressed string with the new compressed string */
	_workString = outString;
    }

    /**
     * blank
     *
     * Clears the offscreen graphics image.
     */
    public void blank ()
    {
        Dimension d = size();
        // clear out the background
        _offGraphics.setColor(Color.white);
        _offGraphics.fillRect(0, 0, d.width, d.height);
        _offGraphics.setColor(Color.black);
        title("RLE", "Compression");
    }

    /* RLE data members */

    int _hblocks, _vblocks;          // # of h/v blocks to represent _srcString
    int _block_width, _block_height; // width/height of blocks in pixels

    /* RLE constants */

    static final int CHUNK_DELAY = 400; // ms delay when displaying marked chunk
    static final int CHAR_DELAY = 200;  // ms delay when displaying marked char
    static final Color COLOR_A = Color.black;   // Color of A char blocks
    static final Color COLOR_B = Color.blue;    // Color of B char blocks
    static final Color COLOR_A_TEXT = Color.white;   // Color of A char text
    static final Color COLOR_B_TEXT = Color.white;   // Color of B char text
    static final Color COLOR_NUM_TEXT = Color.black; // Color of num char text
    static final Color COLOR_MARK = Color.red;  // Color of char being examined
    static final Color COLOR_COMP = Color.yellow;  // Color of chars being compd
}

