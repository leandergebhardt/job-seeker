package com.jobseeker.service;

import com.jobseeker.dto.ExternalJobDto;
import com.jobseeker.dto.JobResponse;
import com.jobseeker.entity.Job;
import com.jobseeker.exception.JobNotFoundException;
import com.jobseeker.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobApiClient jobApiClient;
    private final JobRepository jobRepository;

    /**
     * Fetches jobs from RapidAPI and upserts them into the local DB.
     * Jobs that already exist (by externalId) are not duplicated.
     */
    @Transactional
    public List<JobResponse> searchAndStore(String title, String location, int limit, int offset) {
        List<ExternalJobDto> external = jobApiClient.fetchJobs(title, location, limit, offset);

        List<Job> jobs = external.stream()
                .filter(dto -> !jobRepository.existsByExternalId(dto.getId()))
                .map(this::toEntity)
                .toList();

        jobRepository.saveAll(jobs);
        log.info("Stored {} new jobs (skipped duplicates)", jobs.size());

        // Return all matching results (including already-stored ones)
        return external.stream()
                .map(dto -> jobRepository.findByExternalId(dto.getId())
                        .orElseGet(() -> toEntity(dto)))
                .map(JobResponse::from)
                .toList();
    }

    /** Returns all jobs the user has explicitly saved. */
    @Transactional(readOnly = true)
    public List<JobResponse> getSavedJobs() {
        return jobRepository.findBySavedTrue()
                .stream()
                .map(JobResponse::from)
                .toList();
    }

    /** Marks a job as saved (or unsaved). */
    @Transactional
    public JobResponse toggleSaved(UUID jobId, boolean saved) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));
        job.setSaved(saved);
        return JobResponse.from(jobRepository.save(job));
    }

    /** Returns a single job by its internal UUID. */
    @Transactional(readOnly = true)
    public JobResponse getById(UUID jobId) {
        return jobRepository.findById(jobId)
                .map(JobResponse::from)
                .orElseThrow(() -> new JobNotFoundException(jobId));
    }

    /** Removes a job and all linked applications from the DB. */
    @Transactional
    public void deleteJob(UUID jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new JobNotFoundException(jobId);
        }
        jobRepository.deleteById(jobId);
        log.info("Deleted job {}", jobId);
    }

    // --- helpers ---

    private Job toEntity(ExternalJobDto dto) {
        return Job.builder()
                .externalId(dto.getId())
                .title(dto.getTitle())
                .company(dto.getCompany())
                .location(dto.getLocations_raw())
                .description(dto.getDescription())
                .url(dto.getUrl())
                .datePosted(dto.getDatePosted())
                .build();
    }
}
