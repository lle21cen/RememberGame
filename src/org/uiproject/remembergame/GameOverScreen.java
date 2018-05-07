package org.uiproject.remembergame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.KakaoLink;
import com.kakao.KakaoTalkLinkMessageBuilder;

public class GameOverScreen extends Activity {

	private int highestScore, score;
	private boolean autoRank, reCall;
	private String userName;
	private Context context = this;

	final int RESTART_RESULT = 1, GOTOMAIN_RESULT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gameover);

		// intent로 넘겨 받은 데이터 저장
		Intent i = getIntent();
		highestScore = i.getIntExtra("highestScore", 0);
		score = i.getIntExtra("score", 0);
		reCall = i.getBooleanExtra("reCall", false);

		// 화면에 게임 기록 표시, 글씨체 변경
		TextView scoreBoard = (TextView) findViewById(R.id.user_score);
		TextView highestBoard = (TextView) findViewById(R.id.highest_score);

		scoreBoard.setTypeface(Typeface
				.createFromAsset(getAssets(), "pado.ttf"));
		highestBoard.setTypeface(Typeface.createFromAsset(getAssets(),
				"pado.ttf"));

		scoreBoard.setText("Score : " + score);
		highestBoard.setText("최고점수 : " + highestScore);

		if (score >= highestScore) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
			layout.setVisibility(View.VISIBLE);
			getPrefs();

			Button regist = (Button) findViewById(R.id.regist);
			regist.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDFragment fragment = new AlertDFragment();
					fragment.setScore(highestScore);
					fragment.show(getFragmentManager(), "regist");
				}
			});

			if (autoRank) {
				if (!reCall) {
					phpDown2 down = new phpDown2();
					down.execute("http://ljs93kr.cafe24.com/reminput.php?name="
							+ userName + "&score=" + score);
					Toast.makeText(getApplicationContext(), "랭킹에 등록되었습니다",
							Toast.LENGTH_LONG).show();
				}
				regist.setBackgroundResource(R.drawable.checkrecord);
				regist.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(getApplicationContext(),
								RankingActivity.class);
						startActivity(i);
					}
				});
			}
			final Button kakao = (Button) findViewById(R.id.kakao);
			kakao.setOnTouchListener(new Button.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						kakao.setBackgroundResource(R.drawable.kakaolink_btn_medium_ov);
						break;
					case MotionEvent.ACTION_UP:
						kakao.setBackgroundResource(R.drawable.kakaolink_btn_medium);
						break;
					}

					return false;
				}
			});
			kakao.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						KakaoLink kakaoLink = KakaoLink
								.getKakaoLink(getApplicationContext());
						KakaoTalkLinkMessageBuilder builder = kakaoLink
								.createKakaoTalkLinkMessageBuilder();
						builder.addAppButton(
								getString(R.string.kakaolink_appbutton))
								.addText(
										"최고 기록 " + highestScore
												+ "점 달성! 당신의 순간기억력을 테스트 해 보세요.");

						kakaoLink.sendMessage(builder.build(), context);

					} catch (Exception ex) {
						Toast.makeText(getApplicationContext(),
								ex.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			});
		}

		// Button
		final Button main = (Button) findViewById(R.id.gotomain);
		final Button restart = (Button) findViewById(R.id.restart);

		main.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(GOTOMAIN_RESULT);
				finish();
			}
		});

		main.setOnTouchListener(new Button.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					main.setBackgroundResource(R.drawable.home_clicked);
					break;

				case MotionEvent.ACTION_UP:
					main.setBackgroundResource(R.drawable.home_origin);
					break;
				}
				return false;
			}
		});
		restart.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESTART_RESULT);
				finish();
			}
		});
	}

	public void getPrefs() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		autoRank = sharedPrefs.getBoolean("setRanking", false);
		userName = sharedPrefs.getString("userName", "user");
	}
}
