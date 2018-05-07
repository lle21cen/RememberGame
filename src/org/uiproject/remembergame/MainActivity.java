package org.uiproject.remembergame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	final int CODE_INGAME = 1000, CODE_SETTINGS = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		Button start = (Button) findViewById(R.id.game_start);
		Button settings = (Button) findViewById(R.id.settings);
		Button rankingBtn = (Button) findViewById(R.id.ranking);

		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						IngameActivity.class);
				startActivityForResult(intent, CODE_INGAME);
			}
		});

		settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						SettingActivity.class);
				startActivityForResult(intent, CODE_SETTINGS);
			}
		});
		
		rankingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), RankingActivity.class);
				startActivity(i);				
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case (CODE_INGAME):
			break;
		case (CODE_SETTINGS):
//			getSettings();
			break;
		}
	}
//	
//	private void getSettings(){
//		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//		
//	}
}
