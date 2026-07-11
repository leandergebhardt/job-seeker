package com.jobseeker.repository;

import com.jobseeker.entity.ApplicationStatus;
import com.jobseeker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<JobApplication, UUID> {

    List<JobApplication> findByJobId(UUID jobId);

    List<JobApplication> findByStatus(ApplicationStatus status);
}
