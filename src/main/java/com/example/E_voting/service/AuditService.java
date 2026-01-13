package com.example.E_voting.service;

import com.example.E_voting.model.AuditLog;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditService {
    void logAction(String username, String action, String entityType, Long entityId, String details);
    void logActionWithError(String username, String action, String entityType, Long entityId, String details, String errorMessage);
    List<AuditLog> getAuditLogs();
    List<AuditLog> getAuditLogsByUsername(String username);
    List<AuditLog> getAuditLogsByAction(String action);
    List<AuditLog> getAuditLogsByEntityType(String entityType);
    List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end);
    List<AuditLog> getAuditLogsByUsernameAndDateRange(String username, LocalDateTime start, LocalDateTime end);
}
