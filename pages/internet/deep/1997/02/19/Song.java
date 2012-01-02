import java.util.Vector;


public class Song implements Runnable {
	Vector			patterns;
	long			speed;
	Vector			order;
	Instrument[]	ins;

	Thread	runner;
	
	public Song(Instrument[] ins) {
		this.ins = ins;

		order = new Vector();
		order.addElement(new Integer(0));
		patterns = new Vector();
		patterns.addElement(new Pattern());
		speed = 200;
	}

	public void play() {
		runner = new Thread(this);
		runner.start();
	}

	public void stop() {
		runner = null;
		for (int ii=0; ii < ins.length; ii++) {
			if (ins[ii] != null) {
				ins[ii].stopAll();
			}
		} 
	}

	public Pattern getPattern(int i) {
		return (Pattern) patterns.elementAt(i);
	}

	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long newspeed) {
		speed = newspeed;
	}

	public boolean playing() {
		return (runner != null);
	}

	public void run() {
		//some values to get the ball rolling.
		int curord = order.size() - 1;
		int row = 64;
		Pattern curpat = null;

		while (runner == Thread.currentThread()) {
			if (row == 64) {
				row = 0;
				curord = (curord + 1) % order.size();
				curpat = (Pattern) patterns.elementAt(
					((Integer) order.elementAt(curord)).intValue());
				row = 0;
			}
			
			for (int ii=0; ii < 4; ii++) {
				if (curpat.notes[row][ii] != null) {
					ins[curpat.ins[row][ii]].play(curpat.notes[row][ii]);
				}
			}

			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
			}
			row++;
		}
	}

}
