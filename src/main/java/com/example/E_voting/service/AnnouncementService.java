package com.example.E_voting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.E_voting.model.Announcement;
import com.example.E_voting.repository.AnnouncementRepository;
import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    public List<Announcement> getActiveAnnouncements() {
        return announcementRepository.findByActiveTrueOrderByCreatedAtDesc();
    }
    
    public Announcement saveAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }
    
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}
