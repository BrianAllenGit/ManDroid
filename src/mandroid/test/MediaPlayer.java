package mandroid.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MediaPlayer {

	private final static int NOTSTARTED = 0;
	private final static int PLAYING = 1;
	private final static int PAUSED = 2;
	private final static int FINISHED = 3;
	private InputStream inputSong;
	private Clip songClip;
	private MediaPlayer stuff;
	private AudioInputStream audioInputStream;
	private int currentStatus = NOTSTARTED;
	private final Object mediaLock = new Object();

	public MediaPlayer() {
	}

	public MediaPlayer(final InputStream inputSong)
			throws LineUnavailableException, UnsupportedAudioFileException,
			IOException {
		this.inputSong = inputSong;
		this.songClip = AudioSystem.getClip();
		this.audioInputStream = AudioSystem.getAudioInputStream(inputSong);
	}

	public void playSong() throws LineUnavailableException, IOException {

			switch (currentStatus) {
			case NOTSTARTED:
				songClip.open(audioInputStream);
        				songClip.start();
        				songClip.loop(Clip.LOOP_CONTINUOUSLY);

                currentStatus = PLAYING;
				break;
			case PAUSED:
				songClip.start();
				songClip.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case FINISHED:
				loop();
				break;
			}

	}

	public void pauseSong() throws LineUnavailableException, IOException {
		if (currentStatus == PLAYING) {
			songClip.stop();
			currentStatus = PAUSED;
		}
	}

	public void loop() throws LineUnavailableException, IOException {
		songClip.loop(Clip.LOOP_CONTINUOUSLY);
		songClip.open(audioInputStream);
		songClip.start();
		currentStatus = PLAYING;
	}

	public void run() {
		// TODO Auto-generated method stub

	}
}
