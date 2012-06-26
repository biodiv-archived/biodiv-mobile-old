package com.va.strand.biodiversity.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.va.strand.biodiversity.BioDiversityActivity;

public class GroupModel extends AbstractModel {

	private Map<String, ?> idToGroupMap;
	private Map<String, String> groupToIdMap;

	public GroupModel(Context context) {
		TYPE = "Group";
		SharedPreferences groupPreferences = context.getSharedPreferences(
				BioDiversityActivity.GROUPS_FILE, 0);
		idToGroupMap = groupPreferences.getAll();
		groupToIdMap = getReverseMapping(idToGroupMap);
		List<String> groups = new ArrayList<String>(groupToIdMap.keySet());
		Collections.sort(groups);
		GROUPS = groups.toArray(new String[0]);
	}

	private Map<String, String> getReverseMapping(Map<String, ?> map) {
		Map<String, String> reverse_map = new HashMap<String, String>();
		for (String key : map.keySet()) {
			reverse_map.put((String) map.get(key), key);
		}
		return reverse_map;
	}

	public String[] GROUPS;

	@Override
	public boolean validate(Object group) {
		if (!(group instanceof String))
			return false;
		for (String g : GROUPS) {
			if (g.equals(group))
				return true;
		}
		return false;
	}

	public String getId(String group) {
		return groupToIdMap.get(group);
	}

	public String getGroup(String id) {
		return (String) idToGroupMap.get(id);
	}

}
