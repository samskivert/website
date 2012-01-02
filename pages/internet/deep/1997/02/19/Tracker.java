import java.awt.*;

import java.net.URL;
import java.net.MalformedURLException;

public class Tracker extends Panel {
	PianoCanvas		piano;
	String[]		names;
	Instrument[]	instrument;

	Button[][]		buttons = new Button[64][4];
	Song			song;
	Pattern			editpat;

	Choice			insSelect, speedSelect;
	Button			clearBtn, playBtn;
	int				curIns;

	final static int[] speeds = {50, 100, 150, 200, 250, 300, 500, 750, 1000};

	//just one pattern for now.
	int curpattern = 0;

	public Tracker(URL base, String[] samples) {
		setLayout(new BorderLayout());
		Panel top = new Panel();
		top.setLayout(new BorderLayout());
		try {
			top.add("North",
				piano = new PianoCanvas(new URL(base, "piano.gif")));
		} catch (MalformedURLException e) {
			System.out.println("could not find image of piano.");
		}

		Panel controls = new Panel();
		controls.setLayout(new GridLayout(3, 2));

		//set up controls here.
		insSelect = new Choice();
		names = new String[samples.length + 1];
		System.arraycopy(samples, 0, names, 1, samples.length);
		names[0] = "Clear sample";
		insSelect.addItem("     " + names[0]);

		instrument = new Instrument[names.length];
		for (int ii=1; ii < names.length; ii++) {
			try {
				insSelect.addItem(((ii < 10) ? "0" : "") + ii + " - " +
					names[ii]);
				Resampler r = new Resampler(new URL(base, names[ii]));
				instrument[ii] = new Instrument(r);
			} catch (Exception e) {
				System.out.println("could not load " + names[ii]+", check URL");
			}
		}

		song = new Song(instrument);

		speedSelect = new Choice();
		for (int ii=0; ii < speeds.length; ii++) {
			speedSelect.addItem(String.valueOf(speeds[ii]));
		}
		speedSelect.select(3);
		song.setSpeed(speeds[3]);
		insSelect.select(1);

		controls.add(new Label("Instrument:", Label.RIGHT));
		controls.add(insSelect);

		controls.add(new Label("Play Speed:", Label.RIGHT));
		controls.add(speedSelect);

		controls.add(clearBtn = new Button("Clear Pattern"));
		controls.add(playBtn = new Button());
		stop();
		top.add("Center", controls);
		
		add("North", top);

		editpat = song.getPattern(0);

		ScrollPanel sp = new ScrollPanel();
		sp.setLayout(new GridLayout(64, 5));
		for (int ii=0; ii < 64; ii++) {
			sp.add(new Label(String.valueOf(ii), 2));
			for (int jj=0; jj < 4; jj++) {
				sp.add(buttons[ii][jj] = new Button("   -  "));
			}
		}
		add("Center", sp);

		piano.setInstrument(instrument[1]);
	}

	public void clearPattern() {
		for (int ii=0; ii < 64; ii++) {
			for (int jj=0; jj < 4; jj++) {
				editpat.ins[ii][jj] = 0;
				editpat.notes[ii][jj] = null;
			}
		}
		setAllButtons();
	}

	//make all the buttons have the right labels for the current pattern.
	public void setAllButtons() {
		for (int ii=0; ii < 64; ii++) {
			for (int jj=0; jj < 4; jj++) {
				setButton(buttons[ii][jj], editpat.notes[ii][jj],
					editpat.ins[ii][jj]); 
			}
		}
	}

	//give a button nullthe correct label for its corresponding note/ins.
	public void setButton(Button b, Note n, int ins) {
		if (ins == 0) {
			b.setLabel("   -  ");
		} else {
			b.setLabel(n.toString() + "-" + ((ins < 10) ? "0" : "") + ins);
		}
	}

	public void stop() {
		song.stop();
		playBtn.setLabel("Play");
		playBtn.setBackground(Color.green);
	}

	public void play() {
		song.play();
		playBtn.setLabel("Stop");
		playBtn.setBackground(Color.red);
	}

	public boolean handleEvent(Event evt) {
		if (evt.id == Event.ACTION_EVENT) {
			if (evt.target == clearBtn) {
				clearPattern();
				return true;
			}
			if (evt.target == insSelect) {
				curIns = insSelect.getSelectedIndex();
				piano.setInstrument(instrument[curIns]);
				return true;
			}
			if (evt.target == speedSelect) {
				song.setSpeed(speeds[speedSelect.getSelectedIndex()]);
				return true;
			}
			if (evt.target == playBtn) {
				if (song.playing()) {
					stop();
				} else {
					play();
				}
				return true;
			}
			for (int ii=0; ii < 64; ii++) {
				for (int jj=0; jj < 4; jj++) {
					if (evt.target == buttons[ii][jj]) {
						editpat.ins[ii][jj] = curIns;
						if (curIns == 0) {
							editpat.notes[ii][jj] = null;
						} else {
							editpat.notes[ii][jj] = piano.getLastNote();
						}
						
						setButton(buttons[ii][jj], editpat.notes[ii][jj],
							editpat.ins[ii][jj]);
						return true;
					}
				}
			}
		}
		return false;
	}
}
