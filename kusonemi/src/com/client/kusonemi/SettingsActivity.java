package com.client.kusonemi;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setactionbar();

		if(savedInstanceState == null){
			getFragmentManager().beginTransaction()
			.replace(R.id.settings_container, new SettingsFragment())
			.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem Item){
		switch(Item.getItemId()){
		case android.R.id.home:
			finish();
			return true;
		default:
			//なにもしない
		}
		return super.onOptionsItemSelected(Item);
	}

	private void setactionbar(){
		//ToolBarをセット
		Toolbar tb = (Toolbar)findViewById(R.id.tool_bar);
		tb.setTitle(R.string.app_name);
		setSupportActionBar(tb);
		//ActionBarから戻るできるようにする???
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

	public class SettingsFragment extends PreferenceFragment{

		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings_screen);

			setDefaultaccount();
		}

		private void setDefaultaccount(){
			MySQLer ms = new MySQLer(getActivity().getApplicationContext()
					, MainActivity.OAUTH_DATA, null);
			List<String> scrname = new ArrayList<String>();
			List<String> id = new ArrayList<String>();
			int i = 1;
			do{
				String[] rawToken = ms.loadToken(String.valueOf(i));
				scrname.add(rawToken[3]);
				id.add(String.valueOf(i));
				i++;
			}while(ms.loadToken(String.valueOf(i)) != null);

			//key : defaultAccountpos
			ListPreference pref1 = (ListPreference)getPreferenceManager()
					.findPreference("defaultAccountpos");
			pref1.setEntries((CharSequence[])scrname.toArray(
					new CharSequence[0]));
			pref1.setEntryValues((CharSequence[])id.toArray(
					new CharSequence[0]));
		}

	}
}
