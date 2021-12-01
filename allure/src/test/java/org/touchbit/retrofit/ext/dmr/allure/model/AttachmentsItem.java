package org.touchbit.retrofit.ext.dmr.allure.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AttachmentsItem{

	@JsonProperty("name")
	private String name;

	@JsonProperty("source")
	private String source;

	@JsonProperty("type")
	private String type;

	public String getName(){
		return name;
	}

	public String getSource(){
		return source;
	}

	public String getType(){
		return type;
	}
}