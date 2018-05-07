package org.uiproject.remembergame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AlertDFragment extends DialogFragment {
	View view;
	phpDown2 down;
	private int score;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		view = inflater.inflate(R.layout.fragment, null);
		builder.setView(view)
				.setPositiveButton("Regist", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText name = (EditText) view.findViewById(R.id.name);
						String UserName = name.getText().toString();
						registToServer(UserName);
					}
				}).setNegativeButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).setTitle("등록할 이름 입력");

		return builder.create();
	}

	public void registToServer(String name) {
		down = new phpDown2();

		down.execute("http://ljs93kr.cafe24.com/reminput.php?name=" + name
				+ "&score=" + getScore());
		Intent i = new Intent(getActivity(), RankingActivity.class);
		startActivity(i);
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
