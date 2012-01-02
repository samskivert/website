import java.applet.*;

public class Instrument {
	AudioClip[] ac = new AudioClip[25];

	public Instrument(Resampler r) {
		//the frequency multiplier between successive notes.
		double step = Math.pow(2, 1.0 / 12.0);

		double rate = 1.0;
		for (int ii=12; ii >= 0; ii--) {
			ac[ii] = r.makeClip(rate);
			rate *= step;
		}

		rate = 1.0;
		rate /= step;
		for (int ii=13; ii < 25; ii++) {
			ac[ii] = r.makeClip(rate);
			rate /= step;
		}
	}

	//
	public void play(Note note) {
		ac[note.getCode()].play();
	}

	public void stopAll() {
		for (int ii=0; ii < ac.length; ii++) {
			ac[ii].stop();
		}
	}
}
