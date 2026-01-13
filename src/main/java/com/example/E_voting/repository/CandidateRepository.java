package com.example.E_voting.repository;

import com.example.E_voting.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByElection_Id(Long electionId);
    void deleteByElection_Id(Long electionId);
}
