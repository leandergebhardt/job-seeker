package com.jobseeker.service;

import com.jobseeker.dto.ApplicationRequest;
import com.jobseeker.dto.ApplicationResponse;
import com.jobseeker.entity.JobApplication;
import com.jobseeker.exception.ApplicationNotFoundException;
import com.jobseeker.exception.JobNotFoundException;
import com.jobseeker.repository.ApplicationRepository;
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
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    @Transactional
    public ApplicationResponse create(ApplicationRequest request) {
        var job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new JobNotFoundException(request.getJobId()));

        var application = JobApplication.builder()
                .job(job)
                .status(request.getStatus())
                .notes(request.getNotes())
                .resumeUrl(request.getResumeUrl())
                .coverLetter(request.getCoverLetter())
                .build();

        var saved = applicationRepository.save(application);
        log.info("Created application {} for job '{}'", saved.getId(), job.getTitle());
        return ApplicationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAll() {
        return applicationRepository.findAll()
                .stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getById(UUID id) {
        return applicationRepository.findById(id)
                .map(ApplicationResponse::from)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getByJobId(UUID jobId) {
        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    @Transactional
    public ApplicationResponse update(UUID id, ApplicationRequest request) {
        var application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));

        if (request.getStatus() != null) application.setStatus(request.getStatus());
        if (request.getNotes() != null)  application.setNotes(request.getNotes());
        if (request.getResumeUrl() != null) application.setResumeUrl(request.getResumeUrl());
        if (request.getCoverLetter() != null) application.setCoverLetter(request.getCoverLetter());

        return ApplicationResponse.from(applicationRepository.save(application));
    }

    @Transactional
    public void delete(UUID id) {
        if (!applicationRepository.existsById(id)) {
            throw new ApplicationNotFoundException(id);
        }
        applicationRepository.deleteById(id);
        log.info("Deleted application {}", id);
    }
}
