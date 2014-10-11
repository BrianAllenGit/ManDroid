package mandroid.test;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.JavaLayerException;

import com.gtranslate.Audio;
import com.gtranslate.Language;


public class Testing {
    @SuppressWarnings("unused")
	public static void main(String[] args) throws JavaLayerException, IOException, InterruptedException, LineUnavailableException, UnsupportedAudioFileException {
//	     try{
//	         FileInputStream f = new FileInputStream("resources/mysong.wav");
//	         Clip clip = AudioSystem.getClip();
//	         AudioInputStream ais = AudioSystem.getAudioInputStream(f);
//	         clip.open(ais);
//
//	         clip.loop(Clip.LOOP_CONTINUOUSLY);
//
//	     }catch(Exception exception){System.out.println("Failed To Play The WAV File!");}
//    	
    	 FileInputStream input1 = new FileInputStream("resources/mysong.wav");  
    	 InputStream bufferedIn = new BufferedInputStream(input1);
    	 MediaPlayer media = new MediaPlayer(bufferedIn);
    	 media.playSong();
    	
    	Thread.sleep(600);
    	media.pauseSong();
    	Audio audio = Audio.getInstance();     
     //   FileInputStream input = new FileInputStream("resources/myfile.mp3");    
     //   PausablePlayer player = new PausablePlayer(input);
    	try{
        // start playing
     //   player.play();

    	InputStream sound  = audio.getAudio("do i work yet", Language.ENGLISH);
    	audio.play(sound);
        // after 5 secs, pause
        Thread.sleep(200);
    //    player.pause();     
    } catch (final Exception e) {
        throw new RuntimeException(e);
    }
    	
        try {
            URL url;
            if (args.length > 0) {
                url = new File(args[0]).toURI().toURL();
            } else {
                url = Testing.class.getResource("helloworld.config.xml");
            }
            try{
                InputStream sound  = audio.getAudio("Currently Loading", Language.ENGLISH);
            	audio.play(sound);
            }catch (final Exception e) {
            	throw new RuntimeException(e);
            }

            System.out.println("Loading...");

            ConfigurationManager cm = new ConfigurationManager(url);

            Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
            Microphone microphone = (Microphone) cm.lookup("microphone");



            recognizer.allocate();


	    if (microphone.startRecording()) {
            try{
                InputStream sound  = audio.getAudio("Loaded. Start saying chit.", Language.ENGLISH);
            	audio.play(sound);
            }catch (final Exception e) {
            	throw new RuntimeException(e);
            }
		System.out.println("Say: (Good morning | Hello) " + "( Brian )");

		while (true) {
	//    	player.pause();
	    	media.playSong();
		    System.out.println
			("Start speaking. Press Ctrl-C to quit.\n");


		    Result result = recognizer.recognize();
	//	    player.resume();
		    Thread.sleep(3000);
		    result.getActiveTokens();
		    result.getBestFinalToken();
		    result.getReferenceText();
		    
		    if (result != null) {
			String resultText = result.getBestFinalResultNoFiller();
			try{
				InputStream sound = audio.getAudio("You said " + resultText, Language.ENGLISH);
				audio.play(sound);
			}catch(final Exception e) {
				
			}
			System.out.println("You said: " + resultText + "\n");
		    } 
		    else 
		    {
		    	System.out.println("I can't hear what you said.\n");
		    }
		}
	    } else {
		System.out.println("Cannot start microphone.");
		recognizer.deallocate();
		System.exit(1);
	    }
        } catch (IOException e) {
            System.err.println("Problem when loading HelloWorld: " + e);
            e.printStackTrace();
        } catch (PropertyException e) {
            System.err.println("Problem configuring HelloWorld: " + e);
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.err.println("Problem creating HelloWorld: " + e);
            e.printStackTrace();
        }
    }
}
