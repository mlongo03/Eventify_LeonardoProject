package com.eventify.app.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class ParticipantsResponse {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	public Long getId() {
        return id;
    }

    public String getTitle() {
        return name;
    }
}
