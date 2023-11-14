package com.eventify.app.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class ResponseGetEvents {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("category")
	private String category;

	@JsonProperty("description")
	private String description;

	@JsonProperty("address")
	private String address;

	@JsonProperty("date")
	private String date;

	@JsonProperty("imageURL")
    private String imageURL;

	public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }
}
