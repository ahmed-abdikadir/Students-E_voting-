package com.example.E_voting.repository;

import com.example.E_voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
        boolean existsByStudentIdAndElectionId(String studentId, Long electionId);
    List<Vote> findByElectionId(Long electionId);
}
