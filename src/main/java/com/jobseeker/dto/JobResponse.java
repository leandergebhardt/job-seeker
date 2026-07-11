package com.jobseeker.dto;

import com.jobseeker.entity.Job;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/** What our API sends back to the frontend for a job. */
@Data
@Builder
public class JobResponse {

    private UUID id;
    private String externalId;
    private String title;
    private String company;
    private String location;
    private String description;
    private String url;
    private String datePosted;
    private boolean saved;
    private Instant createdAt;

    public static JobResponse from(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .externalId(job.getExternalId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .description(job.getDescription())
                .url(job.getUrl())
                .datePosted(job.getDatePosted())
                .saved(job.isSaved())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
