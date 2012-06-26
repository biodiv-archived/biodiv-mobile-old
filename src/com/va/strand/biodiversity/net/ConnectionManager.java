package com.va.strand.biodiversity.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class ConnectionManager {
	
	private static ConnectionManager instance = null;
	
	public static ConnectionManager getInstance() {
		if (instance == null)
			instance = new ConnectionManager();
		return instance;
	}

	private DefaultHttpClient httpClient;
	
	public ConnectionManager() {
		httpClient = new DefaultHttpClient();
	}
	
	public DefaultHttpClient getHttpClient() {
		return httpClient;
	}
	
	public synchronized String getJSON(String urlAddress, LinkedHashMap<String, String> parameters) {
		InputStream is = null;
		String query = "";
		try {
		if (parameters == null || parameters.isEmpty()) {
			query = "";
		} else {
			query = "?";
			for (String key : parameters.keySet()) {
				if (!query.equals("?"))
					query += "&";
				query += key + "=" + parameters.get(key);
			}
		}
		Log.d("TAG", urlAddress + query);
		HttpGet httpGet = new HttpGet(urlAddress + query);
		HttpResponse httpResponse;
		httpResponse = httpClient.execute(httpGet);
		HttpEntity entity = httpResponse.getEntity();
		httpGet.setHeader("Content-type", "application/json");
		
		is = entity.getContent();
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if (is == null)
			return null;
		
		return dataFromInputStream(is);
	}

	private String dataFromInputStream(InputStream is) {
		StringBuilder sb;
		String result = null;
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			return null;
		}
		
		return result;
	}

	public synchronized String postData(String urlAddress, LinkedHashMap<String, String> params) {
		List<NameValuePair> parameters = getParametersFromMap(params);
		InputStream is = null;
		try {
			HttpPost httpPost = new HttpPost(urlAddress);
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			is = entity.getContent();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		if (is == null)
			return null;
		
		return dataFromInputStream(is);
	}

	private List<NameValuePair> getParametersFromMap(
			Map<String, String> params) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (String param : params.keySet()) {
			nameValuePairs.add(new BasicNameValuePair(param, params.get(param)));
		}
		return nameValuePairs;
	}

//	
//	public String postData(String urlAddress, Map<String, String> params) {
//		List<NameValuePair> parameters = getParametersFromMap(params);
//		InputStream is = null;
//		try {
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(urlAddress);
//			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			HttpEntity entity = httpResponse.getEntity();
//			is = entity.getContent();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//		
//		if (is == null)
//			return null;
//		
//		return dataFromInputStream(is);
//	}
//
//	private List<NameValuePair> getParametersFromMap(
//			Map<String, String> params) {
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		for (String param : params.keySet()) {
//			nameValuePairs.add(new BasicNameValuePair(param, params.get(param)));
//		}
//		return nameValuePairs;
//	}
//
//	private String dataFromInputStream(InputStream is) {
//		StringBuilder sb;
//		String result = null;
//		// convert response to string
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//			sb = new StringBuilder();
//			sb.append(reader.readLine() + "\n");
//			String line = "0";
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			is.close();
//			result = sb.toString();
//		} catch (Exception e) {
//			return null;
//		}
//		
//		return result;
//	}

}
