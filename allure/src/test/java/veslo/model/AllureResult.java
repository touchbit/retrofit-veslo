/*
 * Copyright 2021 Shaburov Oleg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package veslo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AllureResult {

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