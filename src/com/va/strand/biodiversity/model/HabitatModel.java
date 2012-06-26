package com.va.strand.biodiversity.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.va.strand.biodiversity.BioDiversityActivity;

public class HabitatModel extends AbstractModel {

	public String[] HABITATS;
	private Map<String, ?> idToHabitatMap;
	private Map<String, String> habitatToIdMap;

	public HabitatModel(Context context) {
		TYPE = "Habitat";
		SharedPreferences habitatPreferences = context.getSharedPreferences(
				BioDiversityActivity.HABITATS_FILE, 0);
		idToHabitatMap = habitatPreferences.getAll();
		habitatToIdMap = getReverseMapping(idToHabitatMap);
		List<String> habitats = new ArrayList<String>(habitatToIdMap.keySet());
		Collections.sort(habitats);
		HABITATS = habitats.toArray(new String[0]);
	}

	private Map<String, String> getReverseMapping(Map<String, ?> map) {
		Map<String, String> reverse_map = new HashMap<String, String>();
		for (String key : map.keySet()) {
			reverse_map.put((String) map.get(key), key);
		}
		return reverse_map;
	}

	@Override
	public boolean validate(Object habitat) {
		if (!(habitat instanceof String))
			return false;
		for (String h : HABITATS) {
			if (h.equals(habitat))
				return true;
		}
		return false;
	}

	public String getId(String habitat) {
		return habitatToIdMap.get(habitat);
	}

	public String getHabitat(String id) {
		return (String) idToHabitatMap.get(id);
	}

}
