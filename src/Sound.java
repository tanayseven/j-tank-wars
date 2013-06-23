import sun.audio.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
public class Sound
{
	InputStream in;
	AudioStream as;
	
	InputStream inPlayer;
	AudioStream asPlayer;
	
	InputStream inEnemy;
	AudioStream asEnemy;
	
	Sound() throws IOException
	{
		inPlayer = new FileInputStream("sounds/gunshot2.wav");
		asPlayer = new AudioStream(inPlayer);
		
		inEnemy = new FileInputStream("sounds/gunshot1.wav");
		asEnemy = new AudioStream(inEnemy);
	}
	
	public void gunshotPlayer() throws IOException
	{
		AudioPlayer.player.start(asPlayer);
	}
	public void gunshotEnemy() throws IOException
	{
		AudioPlayer.player.start(asEnemy);
	}
}
