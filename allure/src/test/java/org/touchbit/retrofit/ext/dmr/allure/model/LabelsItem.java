package org.touchbit.retrofit.ext.dmr.allure.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LabelsItem{

	@JsonProperty("name")
	private String name;

	@JsonProperty("value")
	private String value;

	public String getName(){
		return name;
	}

	public String getValue(){
		return value;
	}
}