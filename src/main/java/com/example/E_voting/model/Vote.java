package com.example.E_voting.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;
    
    private LocalDateTime timestamp;

    public Vote() {
        this.timestamp = LocalDateTime.now();
    }

    public Vote(String studentId) {
        this.studentId = studentId;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Long getElectionId() {
        return election != null ? election.getId() : null;
    }

    public void setElectionId(Long electionId) {
        if (this.election == null) {
            this.election = new Election();
        }
        this.election.setId(electionId);
    }

    public Long getCandidateId() {
        return candidate != null ? candidate.getId() : null;
    }

    public void setCandidateId(Long candidateId) {
        if (this.candidate == null) {
            this.candidate = new Candidate();
        }
        this.candidate.setId(candidateId);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
