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

import java.awt.Graphics;
import java.awt.Image;

/**
 * Author: Walter Korman
 *         http://www.cerfnet.com/~shaper/index.html
 *
 * A simple class to manage positioning, animation frames, and painting images.
 */
class Sprite {
    static final int NO_DELAY = -1;
    
    static Graphics spriteGraphics;
    static Image    spriteImage;
    
    int width, height;
    int x, y;
    int num_images;
    int img_offset_x[];
    int img_offset_y[];
    long frame_delay;

    int cur_image;
    long last_frame;
    
    Sprite(int width, int height, int x, int y, int num_images,
	   int img_offset_x[], int img_offset_y[], long frame_delay) {
	this.width = width;
	this.height = height;
	this.x = x;
	this.y = y;
	this.num_images = num_images;
	this.img_offset_x = img_offset_x;
	this.img_offset_y = img_offset_y;
	this.frame_delay = frame_delay;

	cur_image = 0;
	last_frame = System.currentTimeMillis();
    }

    void paint(Graphics g) {
	int i_x, i_y;
	long cur_time = System.currentTimeMillis();

	if(frame_delay != NO_DELAY && cur_time >= last_frame + frame_delay) {
	    last_frame = cur_time;
	    cur_image++;
	    if(cur_image > num_images - 1) {
		cur_image = 0;
	    }
	}
	
	i_x = img_offset_x[cur_image];
	i_y = img_offset_y[cur_image];

	spriteGraphics = g.create(x, y, width, height);
	spriteGraphics.drawImage(spriteImage, -i_x, -i_y, null);
    }
}
