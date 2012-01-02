public class Note {
	public static final int C = 0;
	public static final int D = 2;
	public static final int E = 4;
	public static final int F = 5;
	public static final int G = 7;
	public static final int A = 9;
	public static final int B = 11;

	public static final int FLAT = -1;
	public static final int SHARP = 1;
	public static final int NOTHING = 0;


	private int note;

	public Note(int octave, int basicnote, int modifiers) {
		note = ((octave - 1) * 12) + basicnote + modifiers;
	}

	public Note(int octave, int basicnote) {
		this(octave, basicnote, NOTHING);
	}

	public boolean equals(Note othernote) {
		return othernote.note == note;
	}

	public int getCode() {
		return note;
	}

	//print the note as a (semi-) human readable string.
	public String toString() {
		String oct = String.valueOf((note / 12) + 1);
		switch (note % 12) {
			case 0:
				return oct + "C";
			case 1:
				return oct + "C#";
			case 2:
				return oct + "D";
			case 3:
				return oct + "Eb";
			case 4:
				return oct + "E";
			case 5:
				return oct + "F";
			case 6:
				return oct + "F#";
			case 7:
				return oct + "G";
			case 8:
				return oct + "G#";
			case 9:
				return oct + "A";
			case 10:
				return oct + "Bb";
			case 11:
				return oct + "B";
		}
		return "";
	}
}
