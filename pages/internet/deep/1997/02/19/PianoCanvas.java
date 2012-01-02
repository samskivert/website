import java.awt.*;
import java.net.URL;

public class PianoCanvas extends Canvas {
	Image		piano;
	Instrument	i;
	Note		lastnote;
	
	//hard coded for increased mutual stimulation
	private static final int WIDTH = 301;
	private static final int HEIGHT = 91;

	public PianoCanvas(URL img) {
		piano = Toolkit.getDefaultToolkit().getImage(img);
	}

	public void setInstrument(Instrument i) {
		this.i = i;
	}

	public Note getLastNote() {
		return lastnote;
	}

	public Dimension preferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}

	public void paint(Graphics g) {
		g.drawImage(piano, 0, 0, this);
	}

	private Note detectNote(int x, int y) {
		x = Math.max(Math.min(x, WIDTH), 0);
		y = Math.max(Math.min(y, HEIGHT), 0);

		if (y > 55) {
			//no modifiers
			x = x / 20;
			int octave = (x / 7) + 1 ;
			switch (x % 7) {
				case 0:
					return new Note(octave, Note.C);
				case 1:
					return new Note(octave, Note.D);
				case 2:
					return new Note(octave, Note.E);
				case 3:
					return new Note(octave, Note.F);
				case 4:
					return new Note(octave, Note.G);
				case 5:
					return new Note(octave, Note.A);
				case 6:
					return new Note(octave, Note.B);
			}
		}
		//we could be in black-key zone.
		int myx = (x - 5) / 10;
		int octave = (myx / 14) + 1;
		if (octave == 3) {
			return detectNote(x, y + 55);
		}
		switch (myx % 14) {
			case 1:
				return new Note(octave, Note.C, Note.SHARP);
			case 3:
				return new Note(octave, Note.E, Note.FLAT);
			case 7:
				return new Note(octave, Note.F, Note.SHARP);
			case 9:
				return new Note(octave, Note.G, Note.SHARP);
			case 11:
				return new Note(octave, Note.B, Note.FLAT);
			default:
				//not on a black key, handle it the other way.
				return detectNote(x, y + 55);
		} 
	}

	public boolean mouseDrag(Event evt, int x, int y) {
		Note note = detectNote(x, y);
		if (!note.equals(lastnote)) {
			lastnote = note;
			if (i != null) {
				i.play(lastnote);
			}
		}
		return true;
	}

	public boolean mouseDown(Event evt, int x, int y) {
		lastnote = detectNote(x, y);
		if (i != null) {
			i.play(lastnote);
		}
		return true;
	}
}
