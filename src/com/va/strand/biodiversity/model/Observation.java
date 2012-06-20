package com.va.strand.biodiversity.model;

import java.net.URL;
import java.util.Date;

import android.location.Location;

public class Observation {

	private URL imageURL;
	private String name;
	private Date creationDate;
	private Date modificationDate;
	private String group;
	private String habitat;
	private Location location;

	protected Observation(String name, Date creationDate,
			Date modificationDate, String group, String habitat,
			Location location) {

	}
}
