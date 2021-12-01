package org.touchbit.retrofit.ext.dmr.allure.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StepsItem{

	@JsonProperty("attachments")
	private List<AttachmentsItem> attachments;

	@JsonProperty("stage")
	private String stage;

	@JsonProperty("stop")
	private long stop;

	@JsonProperty("name")
	private String name;

	@JsonProperty("start")
	private long start;

	@JsonProperty("steps")
	private List<Object> steps;

	@JsonProperty("parameters")
	private List<Object> parameters;

	@JsonProperty("status")
	private String status;

	public List<AttachmentsItem> getAttachments(){
		return attachments;
	}

	public String getStage(){
		return stage;
	}

	public long getStop(){
		return stop;
	}

	public String getName(){
		return name;
	}

	public long getStart(){
		return start;
	}

	public List<Object> getSteps(){
		return steps;
	}

	public List<Object> getParameters(){
		return parameters;
	}

	public String getStatus(){
		return status;
	}
}