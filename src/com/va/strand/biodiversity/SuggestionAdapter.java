package com.va.strand.biodiversity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.va.strand.biodiversity.net.ConnectionManager;

public class SuggestionAdapter extends ArrayAdapter<Spanned> {

	private static final String SUGGESTION_URL = "http://"
			+ BioDiversityActivity.HOST + "/biodiv/recommendation/suggest";
	protected static final String TAG = "BioDiversity";
	private List<Spanned> suggestions;
	private String nameFilter;

	public SuggestionAdapter(Activity context, String nameFilter) {
		super(context, android.R.layout.simple_dropdown_item_1line);
		suggestions = new ArrayList<Spanned>();
		this.nameFilter = nameFilter;
	}

	@Override
	public int getCount() {
		return suggestions.size();
	}

	@Override
	public Spanned getItem(int index) {
		return suggestions.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter myFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// A class that queries a web API, parses the data and
					// returns an ArrayList<Style>
					List<Spanned> new_suggestions = getSuggestions(constraint);
					suggestions.clear();
					for (Spanned sug : new_suggestions) {
						suggestions.add(sug);
					}
					// Now assign the values and count to the FilterResults
					// object
					filterResults.values = suggestions;
					filterResults.count = suggestions.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence contraint,
					FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return myFilter;
	}

	protected List<Spanned> getSuggestions(CharSequence constraint) {
		List<Spanned> suggestions = new ArrayList<Spanned>();
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("term", constraint.toString());
		parameters.put("nameFilter", nameFilter);
		String json = ConnectionManager.getInstance().getJSON(SUGGESTION_URL,
				parameters);
		try {
			Log.d(TAG, json);
			JSONArray array = new JSONArray(json);
			JSONObject object;
			for (int i = 0; i < array.length(); i++) {
				object = array.getJSONObject(i);
				String name = object.getString("label");
				suggestions.add(Html.fromHtml(name));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return suggestions;
	}

}
