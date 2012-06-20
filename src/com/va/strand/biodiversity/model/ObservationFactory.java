package com.va.strand.biodiversity.model;


public class ObservationFactory {
	
	private static ObservationFactory instance;

	/**
	 * Given data from the server, it creates the Observation object
	 * which can then be used in the app.
	 * @return
	 */
	public Observation createObservation() {
		return null;
	}
	
	/**
	 * Given an observation, convert it to a format which can then be 
	 * transferred to the server easily.
	 * @return
	 */
	public String createTransferObject(Observation observation) {
		return null;
	}
	
	public ObservationList getObservationList(int offset, int count) {
		return null;
	}

	public static ObservationFactory getInstance() {
		if (instance == null) {
			instance = new ObservationFactory();
		}
		return instance;
	}
}
