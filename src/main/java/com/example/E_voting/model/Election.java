package com.example.E_voting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ElectionStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    public Election() {
        this.createdAt = LocalDateTime.now();
    }

    public Election(String name) {
        this.name = name;
        this.status = ElectionStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public Election(String name, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.status = ElectionStatus.CREATED;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ElectionStatus getStatus() {
        return status;
    }

    public void setStatus(ElectionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == ElectionStatus.OPEN && 
               (startTime == null || now.isAfter(startTime)) && 
               (endTime == null || now.isBefore(endTime));
    }

    public boolean hasStarted() {
        if (startTime == null) return true;
        return LocalDateTime.now().isAfter(startTime);
    }

    public boolean hasEnded() {
        if (endTime == null) return false;
        return LocalDateTime.now().isAfter(endTime);
    }

    public enum ElectionStatus {
        CREATED,
        OPEN,
        CLOSED
    }
}

