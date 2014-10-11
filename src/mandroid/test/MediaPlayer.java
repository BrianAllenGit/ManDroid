package mandroid.test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MediaPlayer {

	private final static int NOTSTARTED = 0;
	private final static int PLAYING = 1;
	private final static int PAUSED = 2;
	private final static int FINISHED = 3;
	private AudioInputStream audioInputStream;
	private int currentStatus = NOTSTARTED;
	private AudioInputStream din = null;
	private AudioFormat baseFormat;
	private AudioFormat decodedFormat;
	private byte[] data;
	private SourceDataLine playSong;
	private volatile Thread t;
	public MediaPlayer() {
	}

	public MediaPlayer(final File inputSong)throws LineUnavailableException, UnsupportedAudioFileException,IOException {
		this.audioInputStream = AudioSystem.getAudioInputStream(inputSong);
		this.baseFormat = audioInputStream.getFormat();
		this.decodedFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
				16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(), false);
		this.din = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
	}

	public void playSong() throws LineUnavailableException, IOException {
	
			switch (currentStatus) {
			case NOTSTARTED:
				data = new byte[4096];
				playSong = getLine(this.decodedFormat);
				if (playSong != null) {
					final Runnable r = new Runnable() {
				         public void run()
				         {
				        	 playSong.start();
				              try {
								loop();
							} catch (LineUnavailableException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
				         }
						};
					t = new Thread(r);
					t.setDaemon(true);
                    t.setPriority(Thread.MAX_PRIORITY);
                    currentStatus = PLAYING;
                    t.start();
				}
				break;
			case PAUSED:
				resumeSong();
				break;
			case FINISHED:
				loop();
				break;
			}

	}

	public void pauseSong() throws LineUnavailableException, IOException, InterruptedException {
        synchronized (t) {
            if (currentStatus == PLAYING) {
            	playSong.stop();
            	currentStatus = PAUSED;
            }
        }
	}
	
	public void resumeSong(){
		synchronized (t){
			playSong.start();
			currentStatus = PLAYING;
		}
	}

	public void loop() throws LineUnavailableException, IOException {
		int nBytesRead = 0, nBytesWritten = 0;
		while (nBytesRead != -1) {
			nBytesRead = din.read(data, 0, data.length);
			if (nBytesRead != -1)
				nBytesWritten = playSong.write(data, 0, nBytesRead);
		}
		currentStatus = PLAYING;
	}

	private static SourceDataLine getLine(AudioFormat audioFormat)
			throws LineUnavailableException {
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	}
}
