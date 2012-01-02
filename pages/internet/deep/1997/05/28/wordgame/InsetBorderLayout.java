//
// InsetBorderLayout - a border layout that lays stuff out a little in
//                     from the border of the container
//
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

package wordgame;

import java.awt.*;

public class InsetBorderLayout extends BorderLayout
{
    //
    // InsetBorderLayout public constructor

    public InsetBorderLayout (int inset)
    {
        _inset = inset;
    }

    //
    // InsetBorderLayout public member functions

    public void layoutContainer (Container parent)
    {
    }

    public Dimension minimumLayoutSize (Container parent)
    {
    }

    public Dimension preferredLayoutSize (Container parent)
    {
    }

    //
    // InsetBorderLayout protected data members

    int _inset;
}
