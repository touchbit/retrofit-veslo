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

public class StepsItem {

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