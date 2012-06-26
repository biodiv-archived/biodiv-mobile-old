package com.va.strand.biodiversity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.va.strand.biodiversity.net.ConnectionManager;

public class BioDiversityActivity extends Activity {

	private static final String TAG = "BioDiversity";
	// public static final String HOST = "thewesternghats.in";
	public static final String HOST = "wgp.saturn.strandls.com";
	private static final String LOGIN_URL = "http://" + HOST
			+ "/biodiv/j_spring_security_check";
	public static final String GROUPS_FILE = "biodiv.groups";
	public static final String HABITATS_FILE = "biodiv.habitats";
	private EditText login;
	private EditText password;
	private Button loginButton;
	private TextView loginMessage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		login = (EditText) findViewById(R.id.login);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.login_button);
		loginMessage = (TextView) findViewById(R.id.login_message);

		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tryLogin(login.getText().toString(), password.getText()
						.toString());
			}
		});
	}

	/**
	 * method called when login is successful.
	 */
	protected void loginSuccesful() {
		loginMessage.setText("");
		ConnectionManager.getInstance().setLoginState(true);
		Intent intent = new Intent(this, ObservationActivity.class);
		startActivity(intent);
	}

	/**
	 * method called when the login failed.
	 */
	public void loginFailed() {
		ConnectionManager.getInstance().setLoginState(false);
		loginMessage.setText("Login failed.");
		loginMessage.setTextColor(Color.RED);
	}

	protected void tryLogin(String login, String password) {
		if (login == null || login.equals(""))
			loginFailed();
		if (password == null || password.equals(""))
			loginFailed();
		login = "varun729@gmail.com";
		password = "varun123";
		new LoginAsyncTask().execute(login, password);
	}

	private class LoginAsyncTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(BioDiversityActivity.this);
			progressDialog.setMessage("Logging you in ...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... parameters) {
			try {
				DefaultHttpClient client = ConnectionManager.getInstance()
						.getHttpClient();
				HttpPost request = new HttpPost(LOGIN_URL);
				HttpClientParams.setRedirecting(client.getParams(), false);
				String username = parameters[0];
				String password = parameters[1];

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("j_username", username));
				params.add(new BasicNameValuePair("j_password", password));
				request.setEntity(new UrlEncodedFormEntity(params));

				// Process the request of logging in
				HttpResponse response = client.execute(request);
				Header[] cookieHeaders = response.getHeaders("Set-Cookie");
				Log.d(TAG, "checking login");
				for (Header h : cookieHeaders) {
					if (h.getValue().contains("login=true")) {
						return true;
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean loggedIn) {
			progressDialog.dismiss();
			if (loggedIn) {
				loginSuccesful();
			} else {
				loginFailed();
			}
		}

	}

}