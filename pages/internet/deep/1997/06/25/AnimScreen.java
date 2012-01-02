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

/**
 * Author: Walter Korman
 *         http://www.cerfnet.com/~shaper/index.html
 *
 * Manages a group of sprites representing a "screen" of animation.
 * Painting the screen draws a current snapshot of the animation.
 */
class AnimScreen {
    int num_sprites;
    Sprite sprites[];

    AnimScreen() {
	reset();
    }
    
    void reset() {
	num_sprites = 0;
	sprites = null;
    }

    void addSprite(Sprite s) {
	Sprite new_sprites[];
	int i;

	num_sprites++;
	new_sprites = new Sprite[num_sprites];
	if(num_sprites > 1) {
	    for(i = 0; i < num_sprites - 1; i++) {
		new_sprites[i] = sprites[i];
	    }
	}
	
	new_sprites[num_sprites - 1] = s;

	sprites = new_sprites;
    }

    void paint(Graphics g) {
	int i;

	if(num_sprites <= 0) {
	    return;
	}

	for(i = 0; i < num_sprites; i++) {
	    sprites[i].paint(g);
	}
    }
}
