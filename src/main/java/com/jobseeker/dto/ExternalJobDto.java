package com.jobseeker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Maps a single job object from the active-jobs-db.p.rapidapi.com response.
 * Unknown fields are ignored to stay resilient against API changes.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalJobDto {

    private String id;
    private String title;

    @JsonProperty("organization")
    private String company;

    private String locations_raw;

    @JsonProperty("description_text")
    private String description;

    @JsonProperty("url")
    private String url;

    @JsonProperty("date_posted")
    private String datePosted;
}
