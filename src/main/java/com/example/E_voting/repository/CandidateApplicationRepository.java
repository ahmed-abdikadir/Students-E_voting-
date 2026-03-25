package com.example.E_voting.repository;

import com.example.E_voting.model.CandidateApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateApplicationRepository extends JpaRepository<CandidateApplication, Long> {
    List<CandidateApplication> findByStatus(CandidateApplication.Status status);

    List<CandidateApplication> findByUserUsername(String username);

    List<CandidateApplication> findByUserUsernameAndElectionId(String username, Long electionId);
    
    List<CandidateApplication> findByElectionIdAndStatus(Long electionId, CandidateApplication.Status status);
}
