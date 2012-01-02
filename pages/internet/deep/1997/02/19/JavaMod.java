import java.applet.Applet;
import java.awt.*;
import java.util.StringTokenizer;

public class JavaMod extends Applet {
	Tracker t;

	public void init() {
		String samps = getParameter("samples");
		if (samps == null) {
			System.out.println("No samples specified. <param name=samples " +
					"value=\"samp1.au samp2.au ...\">");
			return;
		}

		StringTokenizer st = new StringTokenizer(samps);
		String[] samples = new String[st.countTokens()];
		for (int ii=0; ii < samples.length; ii++) {
			samples[ii] = st.nextToken();
		}

		int bgcolor = 0xFFFFFF;
		try {
			bgcolor = Integer.parseInt(getParameter("bgcolor"), 16);
		} catch (Exception e) {}
		setBackground(new Color(bgcolor));

		setLayout(new BorderLayout());
		add("Center", t = new Tracker(getDocumentBase(), samples));
	}

	public void stop() {
		t.stop();
	}
}
