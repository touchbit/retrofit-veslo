package org.touchbit.retrofit.ext.dmr.allure.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AllureResult{

	@JsonProperty("attachments")
	private List<Object> attachments;

	@JsonProperty("start")
	private long start;

	@JsonProperty("description")
	private String description;

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("steps")
	private List<StepsItem> steps;

	@JsonProperty("labels")
	private List<LabelsItem> labels;

	@JsonProperty("stage")
	private String stage;

	@JsonProperty("stop")
	private long stop;

	@JsonProperty("historyId")
	private String historyId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("links")
	private List<Object> links;

	@JsonProperty("parameters")
	private List<Object> parameters;

	@JsonProperty("status")
	private String status;

	public List<Object> getAttachments(){
		return attachments;
	}

	public long getStart(){
		return start;
	}

	public String getDescription(){
		return description;
	}

	public String getUuid(){
		return uuid;
	}

	public List<StepsItem> getSteps(){
		return steps;
	}

	public List<LabelsItem> getLabels(){
		return labels;
	}

	public String getStage(){
		return stage;
	}

	public long getStop(){
		return stop;
	}

	public String getHistoryId(){
		return historyId;
	}

	public String getName(){
		return name;
	}

	public List<Object> getLinks(){
		return links;
	}

	public List<Object> getParameters(){
		return parameters;
	}

	public String getStatus(){
		return status;
	}
}