package com.va.strand.biodiversity.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.va.strand.biodiversity.BioDiversityActivity;
import com.va.strand.biodiversity.net.ConnectionManager;

public class ObservationManager {

	private static final String FETCH_OBS_LIST_URL = "http://" + BioDiversityActivity.HOST + "/biodiv/observation/getList";
	private static final String UPLOAD_OBS_URL = "http://"
			+ BioDiversityActivity.HOST + "/biodiv/observation/save";
	private static final String TAG = "BioDiversity";
	protected static final String UPLOAD_RESOURCE_URL = "http://"
			+ BioDiversityActivity.HOST + "/biodiv/observation/upload_resource";
	private static ObservationManager instance;

	/**
	 * Given data from the server, it creates the Observation object which can
	 * then be used in the app.
	 * 
	 * @param photosFiles
	 * @param notes
	 * @param date
	 * @param commonName
	 * @param speciesName
	 * @param groupId
	 * @param habitatId
	 * @return
	 */
	public Observation createObservation(String habitatId, String groupId,
			String speciesName, String commonName, String language,
			String date, String notes, File[] photosFiles) {
		return new Observation(habitatId, groupId, speciesName, commonName,
				language, date, notes, photosFiles);
	}

	public ObservationList getObservationList(Context context, int offset, int max) {
		FetchObservationListTask task = new FetchObservationListTask(context);
		task.execute(offset, max);
		ObservationList observations = task.getObservations();
		return observations;
	}

	public static ObservationManager getInstance() {
		if (instance == null) {
			instance = new ObservationManager();
		}
		return instance;
	}

	public void uploadObservation(final Observation observation, final Context context) {
		new AsyncTask<Void, Void, Void>() {
			
			private ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(context);
				progressDialog.setMessage("Uploading ...");
				progressDialog.setCancelable(false);
				progressDialog.show();
			}
			
			@Override
			protected void onPostExecute(Void param) {
				progressDialog.dismiss();
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				uploadResources();
				uploadData();
				return null;
			}

			private void uploadResources() {
				File[] photos = observation.getClientPhotos();
				List<String> serverFiles = new ArrayList<String>();
				for (File photo : photos) {
					try {
						String serverPath = uploadPhoto(photo);
						serverFiles.add(serverPath);
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				observation.setServerPhotos(serverFiles);
			}

			private String uploadPhoto(File photo)
					throws ClientProtocolException, IOException {
				String path = "";
				DefaultHttpClient client = ConnectionManager.getInstance()
						.getHttpClient();
				HttpPost request = new HttpPost(UPLOAD_RESOURCE_URL);

				MultipartEntity imageEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				String mimeType = "image/" + getImageExtension(photo);
				FileBody body = new FileBody(photo, "image/jpg");
				imageEntity.addPart("resources", body);
				request.setEntity(imageEntity);
				HttpResponse response = client.execute(request);

				HttpEntity resEntity = response.getEntity();

				String xml = EntityUtils.toString(resEntity);
				Log.d(TAG, xml);
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
					InputSource is = new InputSource();
					is.setCharacterStream(new StringReader(xml));
					Document doc = db.parse(is);

					NodeList dirTag = doc.getElementsByTagName("dir");
					if (dirTag.getLength() > 0) {
						NodeList dirNode = dirTag.item(0).getChildNodes();
						if (dirNode.getLength() > 0) {
							path = dirNode.item(0).getNodeValue();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return path + "/" + photo.getName();
			}

			private String getImageExtension(File photo) {
				String name = photo.getName();
				int dot = name.lastIndexOf(".");
				String ext = name.substring(dot + 1);
				return ext;
			}

			private void uploadData() {
				LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
//				
//				data.put("group_id", "841");
//				data.put("habitat_id", "267836");
//				data.put("recoName", "Tigers_test");
//				data.put("canName", "");
//				data.put("observedOn", "01/06/2012");
//				data.put("file_1", observation.getServerPhoto(0));
//				data.put("license_1", "");
//				data.put("place_name", "");
//				data.put("location_accuracy", "Approximate");
//				data.put("reverse_geocoded_name", "National Highway 6, Maharashtra, India");
//				data.put("latitude", "21.07");
//				data.put("latitude_deg", "21");
//				data.put("latitude_min", "4");
//				data.put("latitude_sec", "12.000000000001023");
//				data.put("latitude_direction", "N");
//				data.put("longitude", "79.26999999999998");
//				data.put("longitude_deg", "79");
//				data.put("longitude_min", "16");
//				data.put("longitude_sec", "11.999999999934516");
//				data.put("longitude_direction", "E");
//				data.put("notes", "");
//
//				
				data.put("group_id", observation.getGroupId());
				data.put("habitat_id", observation.getHabitatId());
				data.put("observedOn", observation.getDate());
				data.put("recoName", observation.getSpeciesName());
				data.put("canName", observation.getSpeciesName());
				data.put("commonName", observation.getCommonName());
				data.put("languageName", observation.getLanguageName());
				data.put("recoComment", observation.getRecoComment());
				for (int i = 0; i < observation.getServerPhotos().size(); i++) {
					data.put("file_" + (i + 1), observation.getServerPhoto(i));
					data.put("license_" + (i + 1), observation.getLicense(i));
				}
				data.put("place_name", observation.getPlaceName());
				data.put("location_accuracy", observation.getLocationAccuracy());
				data.put("reverse_geocoded_name",
						observation.getReverseGeocodedName());
				data.put("latitude", observation.getLatitude());
				data.put("latitude_deg", observation.getLatitudeDegrees());
				data.put("latitude_min", observation.getLatitudeMinutes());
				data.put("latitude_sec", observation.getLatitudeSeconds());
				data.put("latitude_direction",
						observation.getLatitudeDirection());
				data.put("longitude", observation.getLongitude());
				data.put("longitude_deg", observation.getLongitudeDegrees());
				data.put("longitude_min", observation.getLongitudeMinutes());
				data.put("longitude_sec", observation.getLongitudeSeconds());
				data.put("longitude_direction",
						observation.getLongitudeDirection());
				data.put("notes", observation.getNotes());
				data.put("isMobileApp", "true");
				String response = ConnectionManager.getInstance().postData(
						UPLOAD_OBS_URL, data);
				Log.d(TAG, response);
			}

		}.execute();
	}
	
	private class FetchObservationListTask extends AsyncTask<Integer, Void, String> {

		private Context context;
		private ProgressDialog progressDialog;
		private ObservationList observationList;

		public FetchObservationListTask(Context context) {
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Fetching observations ...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			observationList = new ObservationList();
		}
		
		@Override
		protected void onPostExecute(String json) {
			try {
				JSONObject result = new JSONObject(json);
				JSONArray observationsJSON = result.getJSONArray("observationInstanceList");
				int count = observationsJSON.length();
				for (int i=0; i<count; i++) {
					JSONObject observationJSON = observationsJSON.getJSONObject(i);
					JSONObject habitatJSON = observationJSON.getJSONObject("habitat");
					JSONObject groupJSON = observationJSON.getJSONObject("group");
					JSONArray resourcesJSON = observationJSON.getJSONArray("resource");
					int id = observationJSON.getInt("id");
					int habitat_id = habitatJSON.getInt("id");
					int group_id = groupJSON.getInt("id");
					String name = observationJSON.getString("maxVotedSpeciesName");
					int resource_id = resourcesJSON.getJSONObject(0).getInt("id");
					observationList.addObservation(id, habitat_id, group_id, name, resource_id);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			progressDialog.dismiss();
		}

		@Override
		protected String doInBackground(Integer... params) {
			int offset = params[0];
			int max = params[1];
			LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
			parameters.put("offset", offset + "");
			parameters.put("max", max + "");
			String json = ConnectionManager.getInstance().getJSON(FETCH_OBS_LIST_URL, parameters);
			
			// TODO temporary code
			try {
				DefaultHttpClient client = ConnectionManager.getInstance().getHttpClient();
				HttpGet request = new HttpGet("http://wgp.saturn.strandls.com/observation/getFullObvImage?id=316098");
				HttpResponse res;
				res = client.execute(request);
				InputStream is = res.getEntity().getContent();
				BufferedInputStream bis = new BufferedInputStream(is);
				Bitmap bmp = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
		
		public ObservationList getObservations() {
			return observationList;
		}
		
	}
}
