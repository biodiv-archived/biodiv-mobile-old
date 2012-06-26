package com.va.strand.biodiversity.model;

public abstract class AbstractModel implements IParameterModel {

	protected String TYPE = "abstract";
	
	@Override
	public String getError() {
		return "Invalid data : " + TYPE;
	}

}
