package com.jobseeker.dto;

import com.jobseeker.entity.ApplicationStatus;
import com.jobseeker.entity.JobApplication;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ApplicationResponse {

    private UUID id;
    private UUID jobId;
    private String jobTitle;
    private String company;
    private ApplicationStatus status;
    private String notes;
    private String resumeUrl;
    private String coverLetter;
    private Instant appliedAt;
    private Instant updatedAt;

    public static ApplicationResponse from(JobApplication app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .company(app.getJob().getCompany())
                .status(app.getStatus())
                .notes(app.getNotes())
                .resumeUrl(app.getResumeUrl())
                .coverLetter(app.getCoverLetter())
                .appliedAt(app.getAppliedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}
