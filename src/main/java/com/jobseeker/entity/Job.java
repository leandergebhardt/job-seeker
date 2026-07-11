package com.jobseeker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** ID returned by the external API — used to detect duplicates */
    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(nullable = false)
    private String title;

    private String company;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String url;

    /** When the job was originally posted (from API) */
    private String datePosted;

    /** True once the user explicitly saves the job */
    @Column(nullable = false)
    @Builder.Default
    private boolean saved = false;

    /** When the record was first persisted locally */
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<JobApplication> applications = new java.util.ArrayList<>();
}
