package com.va.strand.biodiversity.model;

public interface IParameterModel {
	
	boolean validate(Object obj);

	String getError();

}
