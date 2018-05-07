package org.uiproject.remembergame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RankingActivity extends Activity {

	phpDown down;
	ArrayList<rankingItem> items = new ArrayList<rankingItem>();
	ListView rankingList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranking);

		rankingList = (ListView) findViewById(R.id.ranking_list);
		down = new phpDown();
		down.execute("http://ljs93kr.cafe24.com/remoutput.php");
	}

	class RankingAdapter extends BaseAdapter {

		Context context;

		public RankingAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {

			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout layout = new LinearLayout(getApplicationContext());

			TextView name = new TextView(getApplicationContext());
			TextView score = new TextView(getApplicationContext());

			layout.setOrientation(LinearLayout.HORIZONTAL);

			name.setTextSize(30);
			name.setTypeface(Typeface
					.createFromAsset(getAssets(), "makdae.ttf"));
			name.setTextColor(Color.BLACK);
			name.setText((position + 1) + "등 " + items.get(position).getName());
			score.setTextSize(40);
			score.setTypeface(Typeface.createFromAsset(getAssets(),
					"makdae.ttf"));
			score.setTextColor(Color.BLACK);
			score.setText("   " + items.get(position).getScore() + "점");

			layout.addView(name);
			layout.addView(score);

			return layout;
		}
	}

	private class phpDown extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... urls) {
			StringBuilder jsonHtml = new StringBuilder();
			try {
				// 연결 url 설정
				URL url = new URL(urls[0]);
				// 커넥션 객체 생성
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				// 연결되었으면.
				if (conn != null) {
					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					// 연결되었음 코드가 리턴되면.
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(conn.getInputStream(),
										"UTF-8"));
						for (;;) {
							// 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
							String line = br.readLine();
							Log.d("line", line);
							if (line == null)
								break;
							// 저장된 텍스트 라인을 jsonHtml에 붙여넣음
							jsonHtml.append(line + "\n");
						}
						br.close();
					}

					conn.disconnect();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return jsonHtml.toString();

		}

		protected void onPostExecute(String str) {

			StringTokenizer st = new StringTokenizer(str, ":");
			int total = Integer.parseInt(st.nextToken());

			for (int i = 0; i < total; i++) {
				String name = st.nextToken();
				int score = Integer.parseInt(st.nextToken().toString());
				items.add(new rankingItem(name, score));
			}

			// 랭킹을 점수 내림차순으로 정렬
			Collections.sort(items, new Comparator<rankingItem>() {

				@Override
				public int compare(rankingItem lhs, rankingItem rhs) {
					return lhs.getScore() > rhs.getScore() ? -1 : lhs
							.getScore() < rhs.getScore() ? 1 : 0;
				}

			});
			RankingAdapter adapter = new RankingAdapter(getApplicationContext());
			rankingList.setAdapter(adapter);
		}
	}
}
