package com.va.strand.biodiversity.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Observation {

	private static final String EMPTY = "";
	private String habitatId;
	private String groupId;
	private String speciesName;
	private String commonName;
	private String date;
	private String notes;
	private Date dateObject;
	private File[] photosFiles;
	private String language;
	private List<String> serverPhotos = new ArrayList<String>();

	protected Observation(String habitatId, String groupId, String speciesName,
			String commonName, String language, String date, String notes, File[] photosFiles) {
		this.habitatId = habitatId;
		this.groupId = groupId;
		this.speciesName = speciesName;
		this.commonName = commonName;
		this.language = language;
		this.date = date;
		this.dateObject = DateModel.getDateObject(date);
		this.notes = notes;
		this.photosFiles = photosFiles;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getHabitatId() {
		return habitatId;
	}

	public String getDate() {
		return date;
	}

	public String getSpeciesName() {
		return speciesName;
	}
	
	public String getCommonName() {
		return commonName;
	}

	public String getLanguageName() {
		return language;
	}
	
	public File[] getClientPhotos() {
		return photosFiles;
	}
	
	public void setServerPhotos(List<String> photos) {
		this.serverPhotos = photos;
	}
	
	public List<String> getServerPhotos() {
		return serverPhotos;
	}
	
	public String getServerPhoto(int index) {
		return serverPhotos.get(index);
	}
	
	public String getLicense(int index) {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getRecoComment() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getPlaceName() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLocationAccuracy() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getReverseGeocodedName() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLatitude() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLatitudeDegrees() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLatitudeMinutes() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLatitudeSeconds() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLatitudeDirection() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLongitude() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLongitudeDegrees() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLongitudeMinutes() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLongitudeSeconds() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getLongitudeDirection() {
		// TODO Auto-generated method stub
		return EMPTY;
	}

	public String getNotes() {
		return notes;
	}
}
