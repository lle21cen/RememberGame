package org.uiproject.remembergame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

/**
 * 음악파일을 재생하는 방법에 대해 알 수 있습니다.
 * 
 * @author Mike
 *
 */
public class BGMPlayer {

	private MediaPlayer BGM;
	private int playbackPosition = 0;
	Context context;
	boolean isEnd = false;

	public BGMPlayer(Context context, boolean isEnd) {
		this.context = context;
		this.isEnd = isEnd;
	}

	// BGM player
	void playBGM() throws Exception {
		killBGM();

		try {
			BGM = new MediaPlayer();
			AssetFileDescriptor afd;
			if (SettingActivity.musicIndex == 0)
				afd = context.getAssets().openFd("background.mp3");
			else if (SettingActivity.musicIndex == 1)
				afd = context.getAssets().openFd("hunter.mp3");
			else
				afd = context.getAssets().openFd("interstellar.mp3");

			if (isEnd)
				afd = context.getAssets().openFd("death.mp3");
			BGM.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());

			afd.close();
			BGM.prepare();
			BGM.start();
		} catch (Exception ex) {

		}
	}

	protected void StopBGM() {
		killBGM();
	}

	protected void PauseBGM() {
		if (BGM != null) {
			playbackPosition = BGM.getCurrentPosition();
			BGM.pause();
		}
	}

	private void killBGM() {
		if (BGM != null) {
			try {
				BGM.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void RestartBGM() {
		if (BGM != null && !BGM.isPlaying()) {
			BGM.start();
			BGM.seekTo(playbackPosition);
		}
	}
}
