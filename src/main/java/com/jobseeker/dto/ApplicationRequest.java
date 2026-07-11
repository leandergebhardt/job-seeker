package com.jobseeker.dto;

import com.jobseeker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplicationRequest {

    @NotNull(message = "jobId is required")
    private UUID jobId;

    private ApplicationStatus status = ApplicationStatus.APPLIED;

    private String notes;
    private String resumeUrl;
    private String coverLetter;
}
