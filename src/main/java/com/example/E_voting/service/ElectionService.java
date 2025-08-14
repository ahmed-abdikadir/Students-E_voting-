package com.example.E_voting.service;

import com.example.E_voting.model.Candidate;
import com.example.E_voting.model.Election;

import java.util.Map;

import java.util.List;

public interface ElectionService {
    List<Election> getAllElections();
    Election getElectionById(Long electionId);
    List<Candidate> getCandidatesByElectionId(Long electionId);
    Candidate getCandidateById(Long candidateId);
    boolean hasVoted(String studentId, Long electionId);
    void castVote(String studentId, Long electionId, Long candidateId);

    Map<String, Long> getElectionResults(Long electionId);

    Election createElection(String name);

    Election openElection(Long electionId);

    Election closeElection(Long electionId);
    
    void deleteElection(Long electionId);
}
