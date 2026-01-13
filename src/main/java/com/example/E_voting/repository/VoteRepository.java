package com.example.E_voting.repository;

import com.example.E_voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByStudentIdAndElection_Id(String studentId, Long electionId);
    List<Vote> findByElection_Id(Long electionId);
    List<Vote> findByCandidate_Id(Long candidateId);
    void deleteByElection_Id(Long electionId);
}
