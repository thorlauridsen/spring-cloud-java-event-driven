package com.github.thorlauridsen.deduplication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepo extends JpaRepository<ProcessedEventEntity, UUID> {
}
