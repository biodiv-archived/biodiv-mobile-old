package com.va.strand.biodiversity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class BioDiversityActivity extends Activity implements ILoginPage {

	//public static final String HOST = "thewesternghats.in";
	public static final String HOST = "indiabiodiversity.org";
	public static final String GROUPS_FILE = "biodiv.groups";
	public static final String HABITATS_FILE = "biodiv.habitats";
	public static final String LOGIN_FILE = "biodiv.login";


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		SharedPreferences loginPreferences = getSharedPreferences(LOGIN_FILE, 0);
		String login = loginPreferences.getString("login", "");
		String password = loginPreferences.getString("password", "");
		new LoginAsyncTask(this).execute(login, password);
	}


	@Override
	public void loginSuccessful(String login, String password) {
		Intent intent = new Intent(this, ObservationActivity.class);
		startActivity(intent);
		finish();
	}


	@Override
	public void loginFailed() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}


}