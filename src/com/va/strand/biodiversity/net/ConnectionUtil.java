package com.va.strand.biodiversity.net;

import org.apache.http.client.HttpClient;

import android.content.Context;
import android.net.http.AndroidHttpClient;

/**
 * Utility methods to facilitate transfer of data to and from the 
 * server and Android app.
 * @author varun
 *
 */
public class ConnectionUtil {
	
	private static final String USER_AGENT = "BioDiversity Observation Portal";
	private static ConnectionUtil instance = null;
	
	
	
	public static ConnectionUtil getInstance() {
		if (instance == null) {
			instance = new ConnectionUtil();
		}
		return instance;
	}
	
	public int postData(Context context) {
		HttpClient client = AndroidHttpClient.newInstance(USER_AGENT, context);	
		
		return 0;
	}
	
}
