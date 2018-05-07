package org.uiproject.remembergame;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class IngameActivity extends Activity {

	static int highestScore;

	// xml
	TextView timeBoard, ScoreBoard;
	Button stopOrStartGame;

	int TimeLimit = 10;
	int gameCount = 1;
	Handler handler, gameHandler;
	GameBoardAdapter adapter;
	GridView gameBoard;
	int GameDelay = 3000, TimerDelay = 1000;
	boolean isStart, isMore = true, isMore100 = true;
	BGMPlayer BackgroundPlayer;

	// Thread를 돌리는데 필요한 변수
	Thread gameThread, timerThread;
	boolean isRunning = true, isntGameOver = true;

	// Image, Animation
	ImageView GameOverImage;;
	Animation GameOverAnim;
	boolean[] isSelected = new boolean[49];

	// 정답 여부 판단
	boolean isCorrect;
	private int ChangedPosition;
	ImageView lifeImage1, lifeImage2, lifeImage3;

	// Player's Life, Score
	int life = 3, score = 0;

	// 설정화면에서 효과음 설정 값
	boolean effectOn, bgmOn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ingame);

		stopOrStartGame = (Button) findViewById(R.id.stopStart);

		lifeImage1 = (ImageView) findViewById(R.id.life1);
		lifeImage2 = (ImageView) findViewById(R.id.life2);
		lifeImage3 = (ImageView) findViewById(R.id.life3);

		timeBoard = (TextView) findViewById(R.id.timeBoard);
		ScoreBoard = (TextView) findViewById(R.id.scoreBoard);

		startTimer();

		final Resources res = getResources();
		gameBoard = (GridView) findViewById(R.id.gameBoard);

		adapter = new GameBoardAdapter(this);
		for (int i = 0; i < 49; i++)
			adapter.addIcon(res.getDrawable(R.drawable.front));

		gameBoard.setAdapter(adapter);

		RunGameLv1();

		itemClickListener listener = new itemClickListener();
		gameBoard.setOnItemClickListener(listener);

		stopOrStartGame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRunning) {
					isRunning = false;
					timerThread.interrupt();
					if (bgmOn)
						BackgroundPlayer.PauseBGM();
					Intent i = new Intent(getApplicationContext(),
							GameStopActivity.class);
					startActivityForResult(i, 1003);
				} else {
					// 추후 삭제(result에서 구현)
					isRunning = true;
					startTimer();
					BackgroundPlayer.RestartBGM();
				}
			}
		});
	}

	class itemClickListener implements OnItemClickListener {

		boolean[] isItemChecked = new boolean[49];

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Resources res = getResources();
			if (isRunning && isntGameOver) {
				if (isCorrect = (ChangedPosition == id)
						&& !isItemChecked[position]) {
					// 맞춘 경우 점수올리기, 타이머 리셋, 가리개 내리기, 그림뒤집기 효과음 100점 이상인 경우 목숨
					// 추가, 중복선택불가
					isItemChecked[position] = true;
					score += TimeLimit;
					ScoreBoard.setText("Score : " + score);
					ImageView blindImage = (ImageView) findViewById(R.id.blind);
					blindImage.setImageDrawable(res
							.getDrawable(R.drawable.blind));
					blindImage.setVisibility(View.VISIBLE);
					Animation slideAnim = AnimationUtils.loadAnimation(
							getBaseContext(), R.anim.slide_down);

					if (score >= 60 && isMore) {
						// 점수가 60점이 넘으면 목숨을 하나 추가한다.
						blindImage.setImageDrawable(res
								.getDrawable(R.drawable.lifeup));
						if (life == 1)
							lifeImage2.setImageDrawable(res
									.getDrawable(R.drawable.life));
						else if (life == 2)
							lifeImage3.setImageDrawable(res
									.getDrawable(R.drawable.life));
						life++;
						isMore = false;
					}

					if (score >= 100 && isMore100) {
						// 점수가 100점이 넘으면 목숨하나 더 추가
						blindImage.setImageDrawable(res
								.getDrawable(R.drawable.lifeup));
						if (life == 1)
							lifeImage2.setImageDrawable(res
									.getDrawable(R.drawable.life));
						else if (life == 2)
							lifeImage3.setImageDrawable(res
									.getDrawable(R.drawable.life));
						life++;
						isMore100 = false;
					}

					blindImage.setAnimation(slideAnim);

					slideAnim.setAnimationListener(new AnimationAdapter());
					SoundEffectPlayer sound = new SoundEffectPlayer(
							IngameActivity.this);
					if (effectOn)
						sound.startSound();

				} else {
					if (life == 3) {
						lifeImage3.setImageDrawable(res
								.getDrawable(R.drawable.lifezero));
					} else if (life == 2) {
						lifeImage2.setImageDrawable(res
								.getDrawable(R.drawable.lifezero));
					} else if (life == 1) {
						gameOver();
						lifeImage1.setImageDrawable(res
								.getDrawable(R.drawable.lifezero));
						try {
							BackgroundPlayer.StopBGM();
							BackgroundPlayer = new BGMPlayer(
									getApplicationContext(), true);
							BackgroundPlayer.playBGM();
						} catch (Exception ex) {

						}
					}
					life--;
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case 1:
			finish();
			startActivity(new Intent(this, IngameActivity.class));
			break;
		case 2:
			finish();
			break;
		case 3:
			isRunning = true;
			startTimer();
			break;
		default:
			// 그냥 화면으로 돌아왔을 경우
			Resources res = getResources();
			adapter.icons.set(ChangedPosition,
					res.getDrawable(R.drawable.stupid)); // 뒤집힌 곳의 그림뒤집기
			gameBoard.setAdapter(adapter);
			stopOrStartGame.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getApplicationContext(),GameOverScreen.class);
					i.putExtra("highestScore", highestScore);
					i.putExtra("score", score);
					i.putExtra("reCall", true);
					startActivityForResult(i, 1001);
				}
			});
			break;
		}
	}

	private final class AnimationAdapter implements Animation.AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {

			// GameOver Animation
			if (animation.equals(GameOverAnim)) {
				try {
					Thread.sleep(1000);
					Intent intent = new Intent(IngameActivity.this,
							GameOverScreen.class);
					intent.putExtra("highestScore", highestScore);
					intent.putExtra("score", score);
					GameOverImage.setVisibility(View.INVISIBLE);
					startActivityForResult(intent, 1001);
				} catch (Exception e) {
					e.getStackTrace();
				}
			} else {
				// GridView Item Clicked
				if (score >= 0 && score < 50)
					RunGameLv1();
				else if (score >= 50)
					RunGameLv1_2();

				ImageView blindImage = (ImageView) findViewById(R.id.blind);
				Animation slideAnim_up = AnimationUtils.loadAnimation(
						getBaseContext(), R.anim.slide_up);
				blindImage.setAnimation(slideAnim_up);
				blindImage.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

	}

	public void onPause() {
		super.onPause();

		// 최고점수 저장, BGM 정지
		saveScoreInPref();
		if (bgmOn)
			BackgroundPlayer.StopBGM();
	}

	public void onResume() {
		super.onResume();

		// 최고점수 불러오기, BGM 세팅, 시작
		getScoreInPref();
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		bgmOn = sharedPrefs.getBoolean("playBGM", true);
		effectOn = sharedPrefs.getBoolean("playEffect", true);
		try {
			if (bgmOn) {
				BackgroundPlayer = new BGMPlayer(getApplicationContext(), false);
				BackgroundPlayer.playBGM();
			}
		} catch (Exception ex) {
			Toast.makeText(getApplicationContext(), ex.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	// 값 불러오기
	private void getScoreInPref() {
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		highestScore = pref.getInt("score", 0);
	}

	// 값 저장하기
	private void saveScoreInPref() {
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("score", highestScore);
		editor.commit();
	}
	
	public void RunGameLv1() {

		Resources res = getResources();
		Random rand = new Random();
		int randomIndex = rand.nextInt(49);

		// 중복 제거
		while (isSelected[randomIndex]) {
			randomIndex = rand.nextInt(49);
		}
		isSelected[randomIndex] = true;

		ChangedPosition = randomIndex; // 정답여부 판명에 사용
		adapter.icons.set(randomIndex, res.getDrawable(R.drawable.back)); // 그림뒤집기
		gameBoard.setAdapter(adapter);

		TimeLimit = 10;
		gameCount++;
	}

	public void RunGameLv1_2() {
		RunGameLv1();
		TimeLimit = 7;
		
		if(gameCount == 49){
			gameOver();
		}
	}

	public void RunGameLv2(){
		/*
		 * level 2 게임
		 * 모두 뒤집한 상태에서 시작. 역으로 2개씩 뒤집기
		 */
		
	}
	public void startTimer() {
		final Handler timerHandler = new Handler();
		timerThread = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted() && isntGameOver) {
					SystemClock.sleep(TimerDelay);
					timerHandler.post(new Runnable() {
						public void run() {
							TimeLimit--;
							if (TimeLimit == 1) {
								gameOver();
								// gameThread.interrupt();

							}
							timeBoard.setText("" + TimeLimit);
						}
					});
				}
			}
		});
		timerThread.start();
	}

	public void gameOver() {

		isntGameOver = false;
		GameOverImage = (ImageView) findViewById(R.id.gameover);
		GameOverImage.setVisibility(View.VISIBLE);
		GameOverAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.gameover);
		GameOverImage.setAnimation(GameOverAnim);

		if (score > highestScore)
			highestScore = score; // 최고기록 갱신

		GameOverAnim.setAnimationListener(new AnimationAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Adapter Class 
	public class GameBoardAdapter extends BaseAdapter {

		Context context;
		int screenWidth, screenHeight;
		ArrayList<Drawable> icons = new ArrayList<Drawable>();

		public GameBoardAdapter(Context context) {
			this.context = context;
			
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			screenWidth = metrics.widthPixels;
		}

		public void addIcon(Drawable icon) {
			icons.add(icon);
		}

		@Override
		public int getCount() {
			return icons.size();
		}

		@Override
		public Object getItem(int position) {
			return icons.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;

			if (convertView != null) {
				image = (ImageView) convertView;
			} else {
				image = new ImageView(context);
				GridView.LayoutParams params = new GridView.LayoutParams(85, 85);
				if(screenWidth >= 1000)
					params = new GridView.LayoutParams(125, 125);
				image.setScaleType(ImageView.ScaleType.CENTER_CROP);
				image.setLayoutParams(params);
			}
			image.setImageDrawable(icons.get(position));
			return image;
		}
	}
}
