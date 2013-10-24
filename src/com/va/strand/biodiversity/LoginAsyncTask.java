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
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.va.strand.biodiversity.net.ConnectionManager;

public class LoginAsyncTask extends AsyncTask<String, Void, Boolean> {

	private static final String TAG = "BioDiversity";
	private static final String LOGIN_URL = "http://"
			+ BioDiversityActivity.HOST + "/biodiv/j_spring_security_check";
	private ProgressDialog progressDialog;
	private Activity activity;
	private String login;
	private String password;

	public LoginAsyncTask(Activity activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(activity);
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
			login = parameters[0];
			password = parameters[1];

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("j_username", login));
			params.add(new BasicNameValuePair("j_password", password));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
			request.setEntity(urlEncodedFormEntity);

			Log.d(TAG, "logging in at "+LOGIN_URL);
			// Process the request of logging in
			//urlEncodedFormEntity.consumeContent();
			HttpResponse response = client.execute(request);
			
			Header[] cookieHeaders = response.getHeaders("Set-Cookie");
			Log.d(TAG, "checking login");
			for (Header h : cookieHeaders) {
				if (h.getValue().contains("login=true")) {
					return true;
				}
			}
			Log.d(TAG, "consuming content");
			response.getEntity().consumeContent();
			
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
		if (activity instanceof ILoginPage) {
			if (loggedIn) {
				((ILoginPage) activity).loginSuccessful(login, password);
			} else {
				((ILoginPage) activity).loginFailed();
			}
		}
	}

}