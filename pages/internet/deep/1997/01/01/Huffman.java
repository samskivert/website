//
// Huffman - Huffman compression algorithm
//
// $Id: Huffman.java,v 1.0 1996/12/16 09:07:07 wfk Exp $

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Vector;

/**
 * Class: Huffman
 * 
 * Displays live Huffman compression/decompression of strings.  The current
 * string, either compressed or decompressed, is stored in _workString.
 * When the user clicks on the applet, the data is compressed and the
 * progress of the compression is displayed.  Clicking again will decompress
 * the data.  The user may compress/decompress to their heart's content
 * (or until their mouse button wears out.)
 *
 * Assumptions for code simplicity:
 *     - Alphabet used in input strings consists of {' ', 'A'..'Z'} only.
 *     - Chars are 1 byte (8-bits), even though Java really uses Unicode
 *       (2-byte) chars.  Shame on us!
 *
 * Huffman compression code is based on the code in R. Sedgewick's
 * _Algorithms in C++_.
 */
public class Huffman extends BaseApplet
{
    /* Huffman public member functions */

    /**
     * run
     *
     * Main body of the Huffman applet.  Toggles between compressing and 
     * decompressing the string.
     */
    public void run ()
    {
        Dimension d = size();
	boolean comp = false;
	Rectangle dispRect = new Rectangle(5, 2 * (_height + _ascent),
				   d.width - 10, 
				   d.height - (4 * (_height + _ascent)));

        while (_running) {
            blank();

	    if(comp == false) {
                message("Click to compress.", "", 
		  "String (chars) = " + _workString.toString(),
		  "Length = " + 
		  (_workString.toString()).length() * BITS_PER_CHAR + " bits");
	    } else {
		message("Click to decompress.", "", 
		  "String (bits) = " + _workString.toString(),
		  "Length = " + 
		  (_workString.toString()).length() + " bits, Savings = " +
	  	  ((1.0 - ((float) (_workString.toString()).length() / 
	  	  (_srcString.length() * BITS_PER_CHAR))) * 100.0) + "%");
	    }

            refresh();
            waitForClick();

	    clearMessage();

	    if(comp == false) {
		compress(dispRect);
	    } else {
		decompress(dispRect);
	    }

	    comp = !comp;
        }
    }

    /**
     * index
     *
     * Returns the index value of our character in our internal alphabet
     * storage arrays.  ' ' = 0, 'A' = 1, ..., 'Z' = 26.
     */
    public int index(char c) {
	if(c == ' ')
	    return 0;
	else
	    return c - 'A' + 1;
    }

    /**
     * drawCountGraph
     *
     * Display bar graph representing the current percentage of each character's
     * appearance in the source data.
     */
    public void drawCountGraph(Rectangle dispRect) {
	int i, curbar_height, len, hloc, char_width, bar_bottom, bar_top;
	float str_frac;
	Color oldColor;

	oldColor = _offGraphics.getColor();

	/* Draw legend of alphabet characters at bottom of graph */
	_offGraphics.drawString(ALPH_STRING, dispRect.x, 
	  dispRect.y + dispRect.height - 3);

	len = (_workString.toString()).length();

	_offGraphics.setColor(COLOR_COUNT_BAR);
	hloc = 5;

	bar_bottom = dispRect.y + dispRect.height - _height;
	bar_top = dispRect.y + 2;

	/* Draw bars of frequency for each character */
	for(i = 0; i < ALPH_SIZE; i++) {
	    char_width = _metrics.charWidth(ALPH_STRING.charAt(i));
	    str_frac = (float) _count[i] / (float) len;
	    curbar_height = (int) (((float) (bar_bottom - bar_top)) * str_frac);

	    _offGraphics.fillRect(hloc + 1, bar_bottom - curbar_height, 
	      char_width - 2, curbar_height);

	    hloc += char_width;
	}

	refresh();

	_offGraphics.setColor(oldColor);
    }

    /**
     * drawTrie
     *
     * Draws a graphical representation of the entire encoding trie.
     */ 
    public synchronized void drawTrie(Rectangle dispRect, Vector path) {
	Color oldColor;

	oldColor = _offGraphics.getColor();

	_offGraphics.setColor(COLOR_TRIE);

	/* Draw entire subtree rooted at our root */
	drawNode(dispRect.width, -1, -1, dispRect.width / 2, dispRect.y + 5, 
	  path, _root);

	_offGraphics.setColor(oldColor);

	refresh();

	if(path != null) {
	    try {
	        this.wait(DRAWTRIE_DELAY);
	    } catch(InterruptedException e) { }
	}
    }

    /**
     * drawNode
     *
     * Draws a graphical representation of the specified node and all its 
     * children.  Hilights the character, nodes and edges along the
     * specified path in the tree.
     */
    public void drawNode(int width, int old_h, int old_v, int hloc, int vloc, 
		  Vector path, int cur_node) {
	int i;
	boolean found;

	/* Choose appropriate color for this node */
	found = false;
	if(path != null) {
	    i = 0;
	    while(found == false && i < path.size()) {
		found = (((Integer) path.elementAt(i)).intValue() == cur_node);
		i++;
	    }
	    
	    if(found == true)
		_offGraphics.setColor(COLOR_NODE_HILIGHT);
	    else
		_offGraphics.setColor(COLOR_NODE_NORM);
	}

	/* Draw self */
	if(cur_node < ALPH_SIZE) {
	    _offGraphics.drawString(ALPH_STRING.charAt(cur_node) + "",
	      hloc, vloc + SQUARE_WIDTH + _height);
	    if(found == false)
                _offGraphics.drawRect(hloc, vloc, SQUARE_WIDTH, SQUARE_WIDTH);
	    else {
                _offGraphics.fillRect(hloc, vloc, SQUARE_WIDTH, SQUARE_WIDTH);
		_offGraphics.setColor(Color.black);
                _offGraphics.drawRect(hloc, vloc, SQUARE_WIDTH, SQUARE_WIDTH);
		_offGraphics.setColor(COLOR_NODE_HILIGHT);
	    }
	} else {
	    if(found == false)
                _offGraphics.drawOval(hloc, vloc, SQUARE_WIDTH, SQUARE_WIDTH);
	    else {
                _offGraphics.fillOval(hloc, vloc, SQUARE_WIDTH, SQUARE_WIDTH);
		_offGraphics.setColor(Color.black);
                _offGraphics.drawOval(hloc, vloc, SQUARE_WIDTH, SQUARE_WIDTH);
		_offGraphics.setColor(COLOR_NODE_HILIGHT);
	    }
	}

	/* Draw line from current node to parent */
	if(_dad[cur_node] != 0) {
	    if(old_h < hloc) { /* parent was to our left */
	        _offGraphics.drawLine(old_h + SQUARE_WIDTH - 1, 
	          old_v + SQUARE_WIDTH - 1, hloc + (SQUARE_WIDTH / 2),
	          vloc);
	    } else { /* parent was to our right */
	        _offGraphics.drawLine(old_h + 1, old_v + SQUARE_WIDTH - 1, 
		  hloc + (SQUARE_WIDTH / 2), vloc);
	    }
	}

	/* Find children, if any */
	for(i = 0; i < ALPH_SIZE * 2; i++) {
	    if(_dad[i] == cur_node && _dad[i] != 0) {   

		/* Found left child */

	        drawNode(width / 2, hloc, vloc, hloc - (width / 4), 
	          vloc + SQUARE_WIDTH + SQUARE_VSPACE, path, i);

	    } else if(_dad[i] == -cur_node && _dad[i] != 0) { 

		/* Found right child */

	        drawNode(width / 2, hloc, vloc, hloc + (width / 4), 
	          vloc + SQUARE_WIDTH + SQUARE_VSPACE, path, i);

	    }
	}
    }

    /**
     * charCode
     *
     * Returns a string containing the (compressed) bit representation of 
     * the specified character in our alphabet.
     */
    public String charCode(int char_num) {
	StringBuffer outString = new StringBuffer();
	int i;
	char c;

	c = ALPH_STRING.charAt(char_num);

	for(i = _len[index(c)]; i > 0; i--)
	    outString.append((_code[index(c)] >> (i - 1)) & 1);

	return outString.toString();
    }

    /**
     * updatePath
     *
     * Adds a new element to our path, and redraws the trie to display the 
     * new path.  Used to display progress as we find the code for each 
     * character.  If which_char < 0, we display message for decoding chars, 
     * else we display message for encoding chars.
     */
    public void updatePath(Rectangle dispRect, Vector path, int cur_node,
		  int which_char, String curString) {
	path.addElement(new Integer(cur_node));
	blank();

	if(which_char < 0) {
	    message("Decoding bit " + -which_char + "...", "", 
	      "Decomp. string (chars) = " + curString, 
	      "String (bits) = " + _workString.toString());
	} else {
	    message("Finding code for", 
	      "character '" + ALPH_STRING.charAt(which_char) + "'...", "", "");
	}

	drawTrie(dispRect, path);
    }

    /**
     * compress
     *
     * Perform Huffman compression.  Displays the compression progress
     * graphically.
     */
    public void compress(Rectangle dispRect) {
	StringBuffer outString;
	PriorityQueue pq = new PriorityQueue();
	Vector path;
	int i, x, j, k, t, len;
	PairElement t1, t2;
	char c;

	/* Initialize data storage arrays */

	for(i = 0; i < ALPH_SIZE; i++) {
	    _dad[i] = 0;
	    _dad[ALPH_SIZE + i] = 0;
	    _count[i] = 0;
	    _count[ALPH_SIZE + i] = 0;
	    _code[i] = 0;
	    _len[i] = 0;
	}

	/* Build array of character frequency */

	message("Calculating character", "frequency...", "", "");
	refresh();

	len = _workString.length();
	for(i = 0; i < len; i++) {
	    _count[index(_workString.charAt(i))]++;
	    drawCountGraph(dispRect);
	}

	/* Push all non-zero items onto priority queue */

	for(i = 0; i < ALPH_SIZE; i++)
	    if(_count[i] > 0) {
		pq.insert(new PairElement(i, _count[i]));
	    }

	blank();
	message("Done calculating", "character frequency.", 
	  "", "Click to continue.");
	drawCountGraph(dispRect);
	refresh();
	waitForClick();

	/* Build trie representing characters in the string */

	blank();
	message("Building encoding trie...", "", "", "");
	refresh();

	i = ALPH_SIZE;
	while(pq.size() > 1) {
	    t1 = pq.removeMin();
	    t2 = pq.removeMin();
	    _dad[i] = 0;
	    _dad[t1.key] = i;
	    _dad[t2.key] = -i;
	    _count[i] = _count[t1.key] + _count[t2.key];
	    if(!pq.isEmpty())
		pq.insert(new PairElement(i, _count[i]));

	    i++;
	}

	/* Handle possible remaining entry in pq */

	if(pq.size() == 1) {
	    t1 = pq.removeMin();
	    _dad[i] = 0;
	    _dad[t1.key] = i;
	    _count[i] = _count[t1.key];
	    i++;
	}

	/* Store root node for later decompression */
	_root = i - 1;

	blank();
	message("Done building", "encoding trie.", "", "Click to continue.");
	drawTrie(dispRect, null);
	refresh();
	waitForClick();

	/* Convert trie representation into char codes */

	/* We traverse up the tree from each leaf node, storing the
         * path we take along the way. */

	for(k = 0; k < ALPH_SIZE; k++) {
	    path = new Vector();
	    i = 0; 
	    x = 0;
	    j = 1;

	    if(_count[k] != 0) {
		updatePath(dispRect, path, k, k, null);

		t = _dad[k];
		updatePath(dispRect, path, t, k, null);

		while(t != 0) {
		    if(t < 0) {
			x += j;
			t = -t;
		    }
		    updatePath(dispRect, path, t, k, null);

		    t = _dad[t];
		    j += j;
		    i++;
		}
	    }

	    _code[k] = x;
	    _len[k] = i;
	}

	/* Draw final codes for all chars */

	blank();
	i = dispRect.y + _height;
	for(j = 0; j < ALPH_SIZE; j++) {
	    if(_count[j] > 0) {
		_offGraphics.drawString(ALPH_STRING.charAt(j) + " = ",
		  dispRect.x + 5, i);

		_offGraphics.drawString(charCode(j), dispRect.x + 25, i);
		i += _height;
	    }
	}

	message("Done finding", "character codes.", "", "Click to continue.");
	refresh();
	waitForClick();

	/* Encode the string */

	blank();
	message("Encoding the string...", "", "", "");
	refresh();

	outString = new StringBuffer();
	for(j = 0; j < len; j++)
	    outString.append(charCode(index(_workString.charAt(j))));

	_workString = outString;

	blank();
	message("Done encoding", "string.", "", "Click to continue.");

	i = dispRect.y + _height + 7;
	_offGraphics.drawString("Orig. string (chars) = " + _srcString, 
	  dispRect.x + 5, i);
	i += _height;
	_offGraphics.drawString("Orig. string length = " + 
	  (_srcString.length() * BITS_PER_CHAR) + " bits", dispRect.x + 5, i);
	i += _height;
	_offGraphics.drawString("Comp. string (bits) = \n" + 
	  _workString.toString(), dispRect.x + 5, i);
	i += _height;
	_offGraphics.drawString("Comp. string length = " + 
	  (_workString.toString()).length() + " bits", dispRect.x + 5, i);
	i += _height;
	_offGraphics.drawString("Compression Savings = " + 
	  ((1.0 - ((float) (_workString.toString()).length() / 
	  (_srcString.length() * BITS_PER_CHAR))) * 100.0) + "%",
	  dispRect.x + 5, i);

	refresh();
	waitForClick();
    }

    /**
     * decompress
     *
     * Perform Huffman decompression.  
     */
    public void decompress(Rectangle dispRect) {
	int i, len, dest, tree_loc;
	char c;
	StringBuffer outString = new StringBuffer();
	Vector path;

	/* As we still have the encoding trie from our original compression
	 * we can simply walk down the trie using the encoded bits until
	 * we hit a leaf node, representing the decoded character. */

	tree_loc = _root;
	path = new Vector();
	updatePath(dispRect, path, tree_loc, -1, outString.toString());

	len = (_workString.toString()).length();
	for(i = 0; i < len; i++) {
	    c = _workString.charAt(i);

	    if(c == '0') {
		dest = tree_loc;
	    } else { /* c == '1' */
		dest = -tree_loc;
	    }
	    tree_loc = 0;
	    while(_dad[tree_loc] != dest)
	        tree_loc++;

	    updatePath(dispRect, path, tree_loc, -(i + 1), 
	      outString.toString());

	    if(tree_loc < ALPH_SIZE) {
		outString.append(ALPH_STRING.charAt(tree_loc));
		tree_loc = _root;
		path = new Vector();

	        updatePath(dispRect, path, tree_loc, -(i + 1), 
	          outString.toString());
	    }
	}

	/* Store final decompressed string into _workString */
	_workString = outString;

	blank();
	message("Done decoding bits.", "", "Decomp. string (chars) = " 
	  + outString, "Click to continue.");
	refresh();
	waitForClick();
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
        title("Huffman", "Compression");
	_offGraphics.drawRect(5, 2 * (_height + _ascent),
	  d.width - 10, d.height - (4 * (_height + _ascent)));
    }

    /**
     * refresh
     *
     * Force repaint event and pause briefly for event to be received by applet.
     */
    public synchronized void refresh() {
	repaint();
	try {
	    this.wait(REFRESH_DELAY);
	} catch (InterruptedException e) { }
    }

    /* Huffman data members */

    int _count[] = new int[2 * ALPH_SIZE]; // freq of ALPH_STRING[i] in source
    int _dad[] = new int[2 * ALPH_SIZE];   // parent of count[i]
    int _code[] = new int[ALPH_SIZE];      // code data for ALPH_STRING[i]
    int _len[] = new int[ALPH_SIZE];       // bit length of code data for ALPH_STRING[i]
    int _root;                             // root node of encoding trie

    /* Huffman constants */

    static final int ALPH_SIZE = 27;       // # chars in alphabet
    static final int REFRESH_DELAY = 5;    // min ms delay for repaint
    static final int DRAWTRIE_DELAY = 400; // ms delay for repaint in drawTrie
    static final int BITS_PER_CHAR = 8;    // standard char = 1 byte = 8 bits
    static final int SQUARE_WIDTH = 6;     // width of node drawing in pixels
    static final int SQUARE_VSPACE = 10;   // vert. space between node drawings
    static final String ALPH_STRING = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final Color COLOR_COUNT_BAR = Color.blue;
    static final Color COLOR_TRIE = Color.black;
    static final Color COLOR_NODE_HILIGHT = Color.red;
    static final Color COLOR_NODE_NORM = Color.black;
}

/**
 * Class: PriorityQueue
 * 
 * Very simple implementation of a Priority Queue.  Inefficient but effective.
 * Relies on the java.util.Vector class for most of its functionality.
 */
class PriorityQueue extends Vector {
    /**
     * PriorityQueue
     *
     * Default constructor.
     */
    public PriorityQueue() {
	super();
    }

    /**
     * insert
     *
     * Insert an element into priority queue.  We insert the element into
     * the proper location so as to maintain the priority queue's order in
     * a min..max sequence (ordered by frequency.)
     */
    public void insert(PairElement elem) {
	int i;

	i = 0;
	while(i < size() && 
	  ((PairElement) elementAt(i)).freq < elem.freq) {
	    i++;
	}
	
	insertElementAt(elem, i);
    }

    /**
     * removeMin
     *
     * Remove minimum (frequency) element from priority queue, and
     * return a reference to it.
     */
    public PairElement removeMin() {
	PairElement e;

	if(size() > 0) {
	    e = (PairElement) firstElement();
	    removeElementAt(0);

	    return e;
	} else {
	    return null;
	}
    }
}

/**
 * Class: PairElement
 * 
 * Used as a tuple data structure for storing values in PriorityQueue.
 */
class PairElement extends Object {
    int key;  // char index in ALPH_STRING
    int freq; // # recurrences of this char in source data

    /**
     * PairElement
     *
     * Simple constructor.
     */
    public PairElement(int key, int freq) {
	super();

	this.key = key;
	this.freq = freq;
    }
}
