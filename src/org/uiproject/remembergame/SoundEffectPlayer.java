package org.uiproject.remembergame;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundEffectPlayer {

	SoundPool soundEffect;
	Context context;

	public SoundEffectPlayer(Context context) {
		this.context = context;
	}

	void startSound() {
		try {
			soundEffect = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
			int sound = soundEffect.load(context, R.raw.effect1, 1);
			soundEffect.play(sound, 1, 1, 1, 0, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
