package com.example.E_voting.service;

import com.example.E_voting.model.Candidate;
import com.example.E_voting.model.Election;
import com.example.E_voting.model.User;

import java.util.Map;
import java.io.InputStream;
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
    
    // Candidate management methods
    Candidate createCandidate(String name, Long electionId);
    
    List<Candidate> getAllCandidates();
    
    void deleteCandidate(Long candidateId);
    
    // Student/User management
    List<User> getAllUsers();
    User getUserByUsername(String username);
    void importStudentsFromCSV(InputStream csvFile) throws java.io.IOException;
    Map<String, String> importStudentsFromCSVWithResult(InputStream csvFile) throws java.io.IOException;
}
