package com.jobseeker.controller;

import com.jobseeker.dto.ApplicationRequest;
import com.jobseeker.dto.ApplicationResponse;
import com.jobseeker.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * POST /api/applications
     * Record a new job application.
     */
    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.create(request));
    }

    /**
     * GET /api/applications
     * List all applications.
     */
    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAll() {
        return ResponseEntity.ok(applicationService.getAll());
    }

    /**
     * GET /api/applications/{id}
     * Get a single application by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationService.getById(id));
    }

    /**
     * GET /api/applications/job/{jobId}
     * Get all applications for a specific job.
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getByJobId(@PathVariable UUID jobId) {
        return ResponseEntity.ok(applicationService.getByJobId(jobId));
    }

    /**
     * PUT /api/applications/{id}
     * Update status, notes, or other fields on an existing application.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> update(
            @PathVariable UUID id,
            @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.update(id, request));
    }

    /**
     * DELETE /api/applications/{id}
     * Remove an application record.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
