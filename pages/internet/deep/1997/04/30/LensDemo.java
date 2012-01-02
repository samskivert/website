import java.awt.*;
import java.applet.*;

import java.util.*;

import java.net.*;

public class LensDemo extends Applet {

	PaintCanvas	canv;
	Button[]	buttons;
	String[]	classnames;
	String[]	params;


	public void init() {
		setLayout(new BorderLayout());
		ScrollPanel p = new ScrollPanel();

		int numlenses = Integer.parseInt(getParameter("lenses"));

		p.setLayout(new GridLayout(numlenses, 1));
		buttons = new Button[numlenses];
		classnames = new String[numlenses];
		params = new String[numlenses];

		for (int ii=0; ii < numlenses; ii++) {
			StringTokenizer st = new StringTokenizer(
								getParameter("lens" + (ii+1)), ";");
			buttons[ii] = new Button(st.nextToken().trim());
			p.add(buttons[ii]);
			classnames[ii] = st.nextToken().trim();
			if (st.hasMoreTokens()) {
				params[ii] = st.nextToken().trim();
			} else {
				params[ii] = "";
			}
		}
	
		add("West", p);
		add("Center", canv = new PaintCanvas());
		layout();
	}

	public boolean handleEvent(Event evt) {
		if (evt.id == Event.ACTION_EVENT) {
			for (int ii=0; ii < buttons.length; ii++) {
				if (evt.target == buttons[ii]) {
					canv.addLens(classnames[ii], params[ii]);
					return true;
				}
			}
		} else if (evt.id == Event.KEY_PRESS) {
			return canv.handleEvent(evt);
		}

		return false;
	}
}
