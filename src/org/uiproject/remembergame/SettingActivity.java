package org.uiproject.remembergame;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity {

	static int musicIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		ListPreference musicList = (ListPreference) findPreference("musicList");
		musicList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				ListPreference mPreference = (ListPreference) preference;
						musicIndex = mPreference.findIndexOfValue(newValue
								.toString());

				return true;
			}
		});
	}
}