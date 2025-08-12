package com.example.E_voting.service.impl;

import com.example.E_voting.model.Candidate;
import com.example.E_voting.model.Election;
import com.example.E_voting.model.Vote;
import com.example.E_voting.repository.CandidateRepository;
import com.example.E_voting.repository.ElectionRepository;
import com.example.E_voting.repository.VoteRepository;
import com.example.E_voting.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            candidateRepository.save(new Candidate("Candidate A", election1.getId()));
            candidateRepository.save(new Candidate("Candidate B", election1.getId()));
            candidateRepository.save(new Candidate("Candidate C", election2.getId()));
            candidateRepository.save(new Candidate("Candidate D", election2.getId()));
        }
    }

    @Override
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    @Override
    public Election getElectionById(Long electionId) {
        return electionRepository.findById(electionId).orElse(null);
    }

    @Override
    public List<Candidate> getCandidatesByElectionId(Long electionId) {
        return candidateRepository.findByElectionId(electionId);
    }

    @Override
    public boolean hasVoted(String studentId, Long electionId) {
        return voteRepository.existsByStudentIdAndElectionId(studentId, electionId);
    }

    @Override
    public void castVote(String studentId, Long electionId, Long candidateId) {
        if (hasVoted(studentId, electionId)) {
            // Optionally, you can throw a custom exception here and handle it in the controller
            // For now, we'll just prevent saving the duplicate vote
            return;
        }
        Vote vote = new Vote();
        vote.setStudentId(studentId);
        vote.setElectionId(electionId);
        vote.setCandidateId(candidateId);
        voteRepository.save(vote);
    }

    @Override
    public Map<String, Long> getElectionResults(Long electionId) {
        List<Vote> votes = voteRepository.findByElectionId(electionId);
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
        return candidateRepository.findById(candidateId).orElse(null);
    }
}
