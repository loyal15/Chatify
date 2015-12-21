package com.gpit.android.audio;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class AudioUtility {
	private static AudioUtility utility;
	private MediaPlayer mMediaPlayer;
	
	public static AudioUtility getInstance(Context context) {
		if (utility == null)
			utility = new AudioUtility(context);

		return utility;
	}

	private Context mContext;

	private AudioUtility(Context context) {
		mContext = context;
		mMediaPlayer = new MediaPlayer();
	}

	public void playSound() throws IllegalArgumentException,
	SecurityException, IllegalStateException, IOException {
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setDataSource(mContext, soundUri);
		final AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mMediaPlayer.setLooping(false);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		}
	}

	public void playSound(AssetFileDescriptor fd, boolean looping) throws IllegalArgumentException,
	SecurityException, IllegalStateException, IOException {
		
		mMediaPlayer.reset();
		mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
		fd.close();
		final AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//		if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setLooping(looping);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
//		}
	}
	
	public void stopSound() {
		mMediaPlayer.stop();
	}
}
