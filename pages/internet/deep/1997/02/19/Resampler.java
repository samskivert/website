import sun.audio.*;
import java.applet.Applet;
import java.applet.AudioClip;

import java.net.*;
import java.io.*;
import java.awt.*;

public class Resampler {
	byte[] raw;

	int sample_rate;

	public Resampler(URL url) {
		InputStream in = null;
		try {
			URLConnection uc = url.openConnection();
			uc.setAllowUserInteraction(true);
			in = uc.getInputStream();

			DataInputStream dis = new DataInputStream(in);


			//read in special header info.
			dis.readInt(); //magic number
			int hdr_size = dis.readInt();
			int length = dis.readInt();
			dis.readInt(); //encoding
			sample_rate = dis.readInt();
			dis.readInt(); //channels

			//skip the rest of the header.
			dis.skip((long) hdr_size - 24);

			//OUR header size will be 24.
			hdr_size = 24;

			raw = new byte[length];
			int read = 0;
			do {
				int i = dis.read(raw, read, length);
				if (i == -1) {
					//hm the length field lied or something.
					byte[] newraw = new byte[read];
					System.arraycopy(raw, 0, newraw, 0, read);
					raw = newraw;
					length = read; 
					break;
				} else {
					read += i;
				}
			} while (read < length);
			
			dis.close();
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	//multiply the clip by factor.
	public AudioClip makeClip(double factor) {
	try {

		double newlength = (factor * raw.length * 8000) /((double) sample_rate);
		byte[] resample = new byte[(int) Math.round(newlength)];

		double inc = ((double) raw.length) / newlength;
		double dex = 0;
		for (int ii=0; ii < resample.length; ii++) {
			resample[ii] = raw[Math.min(raw.length - 1,
							(int) Math.round(dex))];
			dex += inc;
		}

		return new MyAudioClip(resample);

	} catch (Exception e) {
		e.printStackTrace(System.out);
		return null;
	}
	}
}

class MyAudioClip implements AudioClip {
	AudioData data;
	InputStream stream;

	public MyAudioClip(InputStream in) {
		try {
			data = new AudioStream(in).getData();
		} catch (IOException e) {
			e.printStackTrace(System.out);
			return;
		}
	}

	public MyAudioClip(byte[] b) {
		data = new AudioData(b);
	}

	public synchronized void play() {
		stop();
		if (data != null) {
			stream = new AudioDataStream(data);
			AudioPlayer.player.start(stream);
		}
	}

	public synchronized void loop() {
		stop();
		if (data != null) {
			stream = new ContinuousAudioDataStream(data);
			AudioPlayer.player.start(stream);
		}
	}

	public synchronized void stop() {
		if (stream != null) {
			AudioPlayer.player.stop(stream);
			try {
				stream.close();
			} catch (IOException e) {
			e.printStackTrace(System.out);
			}
		}
		return;
	}
}
