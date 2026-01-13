package com.example.E_voting.service.impl;

import com.example.E_voting.model.Candidate;
import com.example.E_voting.model.Election;
import com.example.E_voting.model.Vote;
import com.example.E_voting.model.User;
import org.springframework.transaction.annotation.Transactional;
import com.example.E_voting.repository.CandidateRepository;
import com.example.E_voting.repository.ElectionRepository;
import com.example.E_voting.repository.VoteRepository;
import com.example.E_voting.repository.UserRepository;
import com.example.E_voting.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ElectionServiceImpl implements ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ElectionServiceImpl(ElectionRepository electionRepository, CandidateRepository candidateRepository, VoteRepository voteRepository) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
        seedDatabase();
    }

    private void seedDatabase() {
        if (electionRepository.count() == 0) {
            Election election1 = electionRepository.save(new Election("Presidential Election"));
            Election election2 = electionRepository.save(new Election("Student Union Election"));

            Candidate candidate1 = new Candidate();
            candidate1.setName("Candidate A");
            candidate1.setElection(election1);
            candidateRepository.save(candidate1);

            Candidate candidate2 = new Candidate();
            candidate2.setName("Candidate B");
            candidate2.setElection(election1);
            candidateRepository.save(candidate2);

            Candidate candidate3 = new Candidate();
            candidate3.setName("Candidate C");
            candidate3.setElection(election2);
            candidateRepository.save(candidate3);

            Candidate candidate4 = new Candidate();
            candidate4.setName("Candidate D");
            candidate4.setElection(election2);
            candidateRepository.save(candidate4);
        }
    }

    @Override
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    @Override
    public Election getElectionById(Long electionId) {
        if (electionId == null) {
            return null;
        }
        return electionRepository.findById(electionId).orElse(null);
    }

    @Override
    public List<Candidate> getCandidatesByElectionId(Long electionId) {
        return candidateRepository.findByElection_Id(electionId);
    }

    @Override
    public boolean hasVoted(String studentId, Long electionId) {
        return voteRepository.existsByStudentIdAndElection_Id(studentId, electionId);
    }

    @Override
    @Transactional
    public void castVote(String studentId, Long electionId, Long candidateId) {
        if (hasVoted(studentId, electionId)) {
            return;
        }
        
        if (electionId == null || candidateId == null) {
            throw new IllegalArgumentException("Election ID and Candidate ID cannot be null");
        }
        
        Election election = electionRepository.findById(electionId)
            .orElseThrow(() -> new IllegalArgumentException("Election not found with id: " + electionId));
        
        Candidate candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + candidateId));
        
        Vote vote = new Vote(studentId);
        vote.setElection(election);
        vote.setCandidate(candidate);
        voteRepository.save(vote);
    }

    @Override
    public Map<String, Long> getElectionResults(Long electionId) {
        List<Vote> votes = voteRepository.findByElection_Id(electionId);
        List<Candidate> candidates = getCandidatesByElectionId(electionId);

        // Group votes by candidateId and count them
        Map<Long, Long> voteCounts = votes.stream()
                .collect(Collectors.groupingBy(Vote::getCandidateId, Collectors.counting()));

        // Map candidate names to their vote counts
        return candidates.stream()
                .collect(Collectors.toMap(
                        Candidate::getName,
                        candidate -> voteCounts.getOrDefault(candidate.getId(), 0L)
                ));
    }

    @Override
    public Candidate getCandidateById(Long candidateId) {
        if (candidateId == null) {
            return null;
        }
        return candidateRepository.findById(candidateId).orElse(null);
    }

    @Override
    public Election createElection(String name) {
        Election newElection = new Election(name);
        return electionRepository.save(newElection);
    }

    @Override
    public Election openElection(Long electionId) {
        Election election = getElectionById(electionId);
        if (election != null) {
            election.setStatus(Election.ElectionStatus.OPEN);
            return electionRepository.save(election);
        }
        return null;
    }

    @Override
    public Election closeElection(Long electionId) {
        Election election = getElectionById(electionId);
        if (election != null) {
            election.setStatus(Election.ElectionStatus.CLOSED);
            return electionRepository.save(election);
        }
        return null;
    }
    
    @Override
    @Transactional
    public void deleteElection(Long electionId) {
        try {
            if (electionId == null) {
                throw new IllegalArgumentException("Election ID cannot be null");
            }
            
            // First check if election exists
            Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found with id: " + electionId));
            
            // Delete all votes for this election
            voteRepository.deleteByElection_Id(electionId);
            
            // Delete all candidates for this election using the corrected method name
            candidateRepository.deleteByElection_Id(electionId);
            
            // Finally delete the election
            if (election != null) {
                electionRepository.delete(election);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting election with id " + electionId + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Candidate createCandidate(String name, Long electionId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Candidate name cannot be empty");
        }
        
        if (electionId == null) {
            throw new IllegalArgumentException("Election ID cannot be null");
        }
        
        Election election = electionRepository.findById(electionId)
            .orElseThrow(() -> new IllegalArgumentException("Election not found with id: " + electionId));
        
        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setElection(election);
        
        return candidateRepository.save(candidate);
    }
    
    @Override
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }
    
    @Override
    @Transactional
    public void deleteCandidate(Long candidateId) {
        if (candidateId == null) {
            throw new IllegalArgumentException("Candidate ID cannot be null");
        }
        
        Candidate candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + candidateId));
        
        // Delete all votes for this candidate
        List<Vote> votes = voteRepository.findByCandidate_Id(candidateId);
        if (votes != null && !votes.isEmpty()) {
            voteRepository.deleteAll(votes);
        }
        
        // Delete the candidate
        if (candidate != null) {
            candidateRepository.delete(candidate);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.findById(username).orElse(null);
    }

    @Override
    public Map<String, String> importStudentsFromCSVWithResult(InputStream csvFile) throws java.io.IOException {
        Map<String, String> results = new java.util.HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        StringBuilder errorMessages = new StringBuilder();
        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(csvFile))) {
            String line;
            int lineNumber = 0;
            
            // Skip header line
            if (reader.readLine() != null) {
                lineNumber++;
            }
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    errorMessages.append("Line ").append(lineNumber).append(": Invalid format\n");
                    failureCount++;
                    continue;
                }
                
                String username = parts[0].trim();
                String roleStr = parts[2].trim().toUpperCase();
                
                if (username.isEmpty() || roleStr.isEmpty()) {
                    errorMessages.append("Line ").append(lineNumber).append(": Empty fields\n");
                    failureCount++;
                    continue;
                }
                
                User.Role role;
                try {
                    role = User.Role.valueOf(roleStr);
                } catch (IllegalArgumentException e) {
                    errorMessages.append("Line ").append(lineNumber).append(": Invalid role '").append(roleStr).append("'\n");
                    failureCount++;
                    continue;
                }
                
                if (userRepository.existsById(username)) {
                    errorMessages.append("Line ").append(lineNumber).append(": User '").append(username).append("' exists\n");
                    failureCount++;
                    continue;
                }
                
                User user = new User();
                user.setUsername(username);
                user.setRole(role);
                user.setPassword(passwordEncoder.encode(username));
                
                userRepository.save(user);
                successCount++;
            }
        }
        
        results.put("success", String.valueOf(successCount));
        results.put("failure", String.valueOf(failureCount));
        results.put("errors", errorMessages.toString());
        results.put("total", String.valueOf(successCount + failureCount));
        
        return results;
    }

    @Override
    public void importStudentsFromCSV(InputStream csvFile) throws java.io.IOException {
        importStudentsFromCSVWithResult(csvFile);
    }
}
