package com.va.strand.biodiversity.model;

import java.util.ArrayList;
import java.util.List;

import com.va.strand.biodiversity.ObservationListListener;

public class ObservationList {
	
	
	private List<DummyObservation> observations = new ArrayList<DummyObservation>();
	private List<ObservationListListener> listeners;

	public DummyObservation getObservation(int index) {
		if (observations.size() <= index || index < 0)
			throw new BioDiversityException("Index out of bounds");
		return observations.get(index);
	}
	
	public int size() {
		return observations.size();
	}
	
	public int getId(int index) {
		return observations.get(index).id;
	}
	
	public DummyObservation getData(int index) {
		return observations.get(index);
	}

	public void addObservation(int id, int habitat_id, int group_id, String name, int resource_id) {
		observations.add(new DummyObservation(id, habitat_id, group_id, name, resource_id));
		notifyListeners();
	}

	public class DummyObservation {
		public int id;
		public int habitat_id;
		public int group_id;
		public String name;
		public int resource_id;
		
		public DummyObservation(int id, int habitat_id, int group_id, String name, int resource_id) {
			this.id = id;
			this.habitat_id = habitat_id;
			this.group_id = group_id;
			this.name = name;
			this.resource_id = resource_id;
		}
	}

	public void addListener(ObservationListListener listener) {
		if (listeners == null || listeners.isEmpty()) {
			listeners = new ArrayList<ObservationListListener>();
		}
		listeners.add(listener);
	}
	
	private void notifyListeners() {
		for (ObservationListListener listener : listeners) {
			listener.listUpdated();
		}
	}

}
