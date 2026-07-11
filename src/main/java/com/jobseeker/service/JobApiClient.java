package com.jobseeker.service;

import com.jobseeker.dto.ExternalJobDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobApiClient {

    private final WebClient rapidApiWebClient;

    @Value("${jobapi.defaults.time-frame}")
    private String defaultTimeFrame;

    @Value("${jobapi.defaults.limit}")
    private int defaultLimit;

    @Value("${jobapi.defaults.description-format}")
    private String defaultDescriptionFormat;

    /**
     * Fetches active job listings from the RapidAPI Active Jobs DB.
     *
     * @param title    job title query, e.g. "Data Engineer"
     * @param location location filter, e.g. "United States" OR "United Kingdom"
     * @param limit    max results to return
     * @param offset   pagination offset
     * @return list of external job DTOs
     */
    public List<ExternalJobDto> fetchJobs(String title, String location, int limit, int offset) {
        log.info("Fetching jobs from RapidAPI — title: '{}', location: '{}', limit: {}", title, location, limit);

        try {
            List<ExternalJobDto> results = rapidApiWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/active-ats")
                            .queryParam("time_frame", defaultTimeFrame)
                            .queryParam("limit", limit > 0 ? limit : defaultLimit)
                            .queryParam("offset", offset)
                            .queryParam("description_format", defaultDescriptionFormat)
                            .queryParam("title", "\"" + title + "\"")
                            .queryParam("location", location)
                            .build())
                    .retrieve()
                    .bodyToFlux(ExternalJobDto.class)
                    .collectList()
                    .block();

            log.info("Received {} jobs from RapidAPI", results != null ? results.size() : 0);
            return results != null ? results : Collections.emptyList();

        } catch (Exception e) {
            log.error("Failed to fetch jobs from RapidAPI: {}", e.getMessage(), e);
            throw new RuntimeException("Could not fetch jobs from external API", e);
        }
    }
}
