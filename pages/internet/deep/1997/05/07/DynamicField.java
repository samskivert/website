import java.awt.Event;
import java.awt.TextField;

class DynamicField extends TextField {
    DynamicApplet ap;

    public DynamicField(int cols, DynamicApplet ap) {
	super(cols);

	this.ap = ap;
    }

    public boolean keyDown(Event e, int key) {
	if(key == '\n') {
	    ap.addString();
	    setText("");
	    repaint();

	    return true;
	}

	return false;
    }
};
