import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;

/**
 * Class:  PangText
 * Author: Walter Korman
 * Date:   1/26/97
 *
 * Title string for bouncing and displaying in Demo Mode.
 * Block text is 18 blocks wide and 7 blocks high.
 * To see the block layout, run the applet!  I absolutely refuse to
 * enter the layout in ASCII after spending so much time typing in the
 * code to generate the picture below.
 */
class PangText extends Bouncer {
    static final int MAX_VELOCITY = 5;/* max velocity of string h/v */
    static final int BLOCK_WIDTH = 10; /* width of one block, in pixels */
    static final int BLOCK_HEIGHT = 10;/* height of one block, in pixels */

    /**
     * PangText
     *
     * Constructor, allows setting initial location.
     */
    public PangText(int h, int v, Dimension d) {
	super(h, v, 18 * BLOCK_WIDTH, 7 * BLOCK_HEIGHT, MAX_VELOCITY, d);
    }

    /**
     * paint
     *
     * Draws the title text in blocky chunks.  Thanks to mdb for suggestion.
     */
    public void paint(Graphics g) {
	int x, y;

	g.setXORMode(Color.black);

	/* Draw first row of blocks */
	x = h;
	y = v;

	/* Top of 'P' */
	g.fillRect(x, y, 3 * BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Top of 'a' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, 3 * BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Top of 'n' */
	x += (5 * BLOCK_WIDTH);
	g.fillRect(x, y, 2 * BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Top of 'g' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, 3 * BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Top of excl. mark */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Draw second row of blocks */
	x = h;
	y += BLOCK_HEIGHT;

	/* Row 2 of 'P' */
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 2 of 'a' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 2 of 'n' */
	x += (5 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 2 of 'g' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 2 of excl. mark */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Draw third row of blocks */
	x = h;
	y += BLOCK_HEIGHT;

	/* Row 3 of 'P' */
	g.fillRect(x, y, (3 * BLOCK_WIDTH), BLOCK_HEIGHT);
	
	/* Row 3 of 'a' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, (3 * BLOCK_WIDTH), BLOCK_HEIGHT);

	/* Row 3 of 'n' */
	x += (5 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 3 of 'g' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 3 of excl. mark */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Draw fourth row of blocks */
	x = h;
	y += BLOCK_HEIGHT;

	/* Row 4 of 'P' */
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 4 of 'a' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 4 of 'n' */
	x += (5 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 4 of 'g' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Draw fifth row of blocks */
	x = h;
	y += BLOCK_HEIGHT;

	/* Row 5 of 'P' */
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 5 of 'a' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, 4 * BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 5 of 'n' */
	x += (5 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 5 of 'g' */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, 3 * BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Row 5 of excl. mark */
	x += (4 * BLOCK_WIDTH);
	g.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Draw sixth row of blocks */
	x = h + (13 * BLOCK_WIDTH);
	y += BLOCK_HEIGHT;

	/* Row 6 of 'g' */
	g.fillRect(x + (2 * BLOCK_WIDTH), y, BLOCK_WIDTH, BLOCK_HEIGHT);

	/* Draw seventh row of blocks */
	x = h;
	y += BLOCK_HEIGHT;

	/* Row 7 of 'g' */
	g.fillRect(x, y, 16 * BLOCK_WIDTH, BLOCK_HEIGHT);

	g.setPaintMode();
    }
};
