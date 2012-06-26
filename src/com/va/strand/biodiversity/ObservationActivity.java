package com.va.strand.biodiversity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TabHost;

import com.va.strand.biodiversity.net.ConnectionManager;

public class ObservationActivity extends TabActivity {

	private static final String GET_HABITAT_ADDRESS = "http://"
			+ BioDiversityActivity.HOST + "/biodiv/observation/getHabitatList";
	private static final String GET_GROUP_ADDRESS = "http://"
			+ BioDiversityActivity.HOST + "/biodiv/observation/getGroupList";
	protected static final String TAG = "BioDiversity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.observation_activity);

		setStaticDataForApp();

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, CreateObservationActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost
				.newTabSpec("createObservation")
				.setIndicator("New",
						res.getDrawable(R.drawable.icon_tab_observation))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, BrowseObservationActivity.class);
		spec = tabHost
				.newTabSpec("browseObservation")
				.setIndicator("Browse",
						res.getDrawable(R.drawable.icon_tab_observation))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(1);
	}

	private void setStaticDataForApp() {
		new AsyncTask<Void, Void, Void>() {
			
			private ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(ObservationActivity.this);
				progressDialog.setMessage("Launching ...");
				progressDialog.setCancelable(false);
				progressDialog.show();
			}

			@Override
			protected Void doInBackground(Void... args) {
				SharedPreferences groupPreferences = ObservationActivity.this.getSharedPreferences(BioDiversityActivity.GROUPS_FILE, 0);
				SharedPreferences habitatPreferences = ObservationActivity.this.getSharedPreferences(BioDiversityActivity.HABITATS_FILE, 0);
				
				if (!groupPreferences.getAll().isEmpty() && !habitatPreferences.getAll().isEmpty()) {
					return null;
				}
				
				String habitatsJSON = ConnectionManager.getInstance().getJSON(
						GET_HABITAT_ADDRESS, null);
				String groupsJSON = ConnectionManager.getInstance().getJSON(
						GET_GROUP_ADDRESS, null);
				Map<String, String> habitats = new HashMap<String, String>();
				Map<String, String> groups = new HashMap<String, String>();
				try {
					JSONObject habitatsJSONObject = (JSONObject) new JSONTokener(
							habitatsJSON).nextValue();
					JSONObject groupsJSONObject = (JSONObject) new JSONTokener(
							groupsJSON).nextValue();

					JSONArray habitat_ids = habitatsJSONObject.names();
					JSONArray group_ids = groupsJSONObject.names();

					for (int i = 0; i < habitat_ids.length(); i++) {
						String name = habitat_ids.getString(i);
						String value = (String) habitatsJSONObject.get(name);
						habitats.put(name, value);
					}
					for (int i = 0; i < group_ids.length(); i++) {
						String name = group_ids.getString(i);
						String value = (String) groupsJSONObject.get(name);
						groups.put(name, value);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				Editor habitatsEditor = habitatPreferences.edit();
				for (String hab_id : habitats.keySet()) {
					habitatsEditor.putString(hab_id, habitats.get(hab_id));
				}
				habitatsEditor.commit();
				
				Editor groupsEditor = groupPreferences.edit();
				for (String group_id : groups.keySet()) {
					groupsEditor.putString(group_id, groups.get(group_id));
				}
				groupsEditor.commit();
				return null;
			}
			
			@Override
			protected void onPostExecute(Void param) {
				progressDialog.dismiss();
			}

		}.execute();
	}
}