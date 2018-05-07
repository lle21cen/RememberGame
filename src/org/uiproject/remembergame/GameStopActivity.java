package org.uiproject.remembergame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class GameStopActivity extends Activity {

	final int CODE_FINISH = 2;

	boolean isBgmOn, isEffectOn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gamestop);

		// 소리 on off 체크
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		isBgmOn = sharedPrefs.getBoolean("playBGM", false);
		isEffectOn = sharedPrefs.getBoolean("playEffect", false);

		final Button restart = (Button) findViewById(R.id.restart);
		restart.setOnTouchListener(new Button.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Animation rotationAnim = AnimationUtils.loadAnimation(
							getBaseContext(), R.anim.rotation_button);
					restart.setAnimation(rotationAnim);
					break;

				case MotionEvent.ACTION_UP:
					setResult(3);
					finish();
					break;
				}

				return false;
			}
		});

		final Button home = (Button) findViewById(R.id.out);
		home.setOnTouchListener(new Button.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					home.setBackgroundResource(R.drawable.home_clicked);
					break;

				case MotionEvent.ACTION_UP:
					home.setBackgroundResource(R.drawable.home_origin);
					break;
				}
				return false;
			}
		});
		home.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(CODE_FINISH);
				finish();
			}
		});

		final Button background = (Button) findViewById(R.id.background);
		if (isBgmOn) {
			background.setBackgroundResource(R.drawable.button_on);
		} else {
			background.setBackgroundResource(R.drawable.button_off);
		}
		background.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sharedPrefs = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPrefs.edit();

				if (isBgmOn) {
					editor.putBoolean("playBGM", false);
					isBgmOn = false;
					background.setBackgroundResource(R.drawable.button_off);
				} else {
					editor.putBoolean("playBGM", true);
					isBgmOn = true;
					background.setBackgroundResource(R.drawable.button_on);
				}

				editor.commit();
			}
		});

		final Button effect = (Button) findViewById(R.id.sound);
		if (isEffectOn) {
			effect.setBackgroundResource(R.drawable.button_on);
		} else {
			effect.setBackgroundResource(R.drawable.button_off);
		}
		effect.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedPrefs = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPrefs.edit();
				if (isEffectOn) {
					editor.putBoolean("playEffect", false);
					effect.setBackgroundResource(R.drawable.button_off);
					isEffectOn = false;
				} else {
					editor.putBoolean("playEffect", true);
					isEffectOn = true;
					effect.setBackgroundResource(R.drawable.button_on);
				}
				editor.commit();
			}
		});
	}

	// 영역 외 터치시에도 닫히지 않도록 함.
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);
		if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY()))
			return false;

		return super.dispatchTouchEvent(ev);
	}

	// 뒤로가기 버튼을 눌러도 닫히지 않도록 함.
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
