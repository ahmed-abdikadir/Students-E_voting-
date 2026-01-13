package com.example.E_voting.service.impl;

import com.example.E_voting.model.AuditLog;
import com.example.E_voting.repository.AuditLogRepository;
import com.example.E_voting.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public void logAction(String username, String action, String entityType, Long entityId, String details) {
        AuditLog auditLog = new AuditLog(username, action, entityType, entityId, details);
        auditLog.setStatus("SUCCESS");
        auditLogRepository.save(auditLog);
    }

    @Override
    public void logActionWithError(String username, String action, String entityType, Long entityId, String details, String errorMessage) {
        AuditLog auditLog = new AuditLog(username, action, entityType, entityId, details);
        auditLog.setStatus("FAILURE");
        auditLog.setErrorMessage(errorMessage);
        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public List<AuditLog> getAuditLogsByUsername(String username) {
        return auditLogRepository.findByUsername(username);
    }

    @Override
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    @Override
    public List<AuditLog> getAuditLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityType(entityType);
    }

    @Override
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }

    @Override
    public List<AuditLog> getAuditLogsByUsernameAndDateRange(String username, LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByUsernameAndTimestampBetween(username, start, end);
    }
}
