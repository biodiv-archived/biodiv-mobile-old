package com.va.strand.biodiversity.model;

public class ObservationList {
	
	private DemoObservation[] observations;
	
	public Observation getObservation(int index) {
		if (observations.length <= index || index < 0)
			throw new BioDiversityException("Index out of bounds");
		return observations[index];
	}
	
	public int size() {
		return observations.length;
	}

}
