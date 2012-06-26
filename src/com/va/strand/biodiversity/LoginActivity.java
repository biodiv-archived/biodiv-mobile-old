package com.va.strand.biodiversity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements ILoginPage {

	private EditText login;
	private EditText password;
	private CheckBox saveLoginCheckBox;
	private Button loginButton;
	private TextView loginMessage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		login = (EditText) findViewById(R.id.login);
		password = (EditText) findViewById(R.id.password);
		saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLogin);
		loginButton = (Button) findViewById(R.id.login_button);
		loginMessage = (TextView) findViewById(R.id.login_message);

		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = login.getText().toString();
				String pass = password.getText().toString();

				tryLogin(username, pass);
			}
		});
	}

	/**
	 * method called when login is successful.
	 */
	@Override
	public void loginSuccessful(String login, String password) {
		loginMessage.setText("");
		if (saveLoginCheckBox.isChecked()) {
			SharedPreferences loginPreferences = getSharedPreferences(BioDiversityActivity.LOGIN_FILE, 0);
			Editor editor = loginPreferences.edit();
			editor.putString("login", login);
			editor.putString("password", password);
			editor.commit();
		}
		Intent intent = new Intent(this, ObservationActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * method called when the login failed.
	 */
	@Override
	public void loginFailed() {
		loginMessage.setText("Login failed.");
		loginMessage.setTextColor(Color.RED);
	}

	protected void tryLogin(String login, String password) {
		if (login == null || login.equals(""))
			loginFailed();
		if (password == null || password.equals(""))
			loginFailed();
		new LoginAsyncTask(this).execute(login, password);
	}

}
