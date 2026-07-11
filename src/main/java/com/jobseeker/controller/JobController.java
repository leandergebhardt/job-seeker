package com.jobseeker.controller;

import com.jobseeker.dto.JobResponse;
import com.jobseeker.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /**
     * GET /api/jobs/search?title=Data Engineer&location=United States&limit=10&offset=0
     * Fetches from RapidAPI and stores results locally.
     */
    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> search(
            @RequestParam(defaultValue = "Software Engineer") String title,
            @RequestParam(defaultValue = "\"United States\" OR \"United Kingdom\"") String location,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        return ResponseEntity.ok(jobService.searchAndStore(title, location, limit, offset));
    }

    /**
     * GET /api/jobs/saved
     * Returns all jobs the user has saved.
     */
    @GetMapping("/saved")
    public ResponseEntity<List<JobResponse>> getSaved() {
        return ResponseEntity.ok(jobService.getSavedJobs());
    }

    /**
     * GET /api/jobs/{id}
     * Returns a single job by its internal UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    /**
     * POST /api/jobs/{id}/save
     * Marks a job as saved.
     */
    @PostMapping("/{id}/save")
    public ResponseEntity<JobResponse> save(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.toggleSaved(id, true));
    }

    /**
     * DELETE /api/jobs/{id}/save
     * Unmarks a job as saved.
     */
    @DeleteMapping("/{id}/save")
    public ResponseEntity<JobResponse> unsave(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.toggleSaved(id, false));
    }

    /**
     * DELETE /api/jobs/{id}
     * Permanently removes a job (and its applications) from the DB.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}
