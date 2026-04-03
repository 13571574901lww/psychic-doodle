package com.lww.medical.session;

import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 会话管理器
 */
@Component
public class SessionManager {

    private final Map<String, Session> sessions = new HashMap<>();

    public Session createSession(String patientId) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId, patientId);
        sessions.put(sessionId, session);
        return session;
    }

    public Session getSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            if (session.isExpired()) {
                session.setStatus(SessionStatus.PAUSED);
            } else {
                session.updateActivity();
            }
        }
        return session;
    }

    public boolean checkAccess(String userId, String sessionId) {
        Session session = sessions.get(sessionId);
        return session != null && session.getPatientId().equals(userId);
    }

    public void closeSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            session.setStatus(SessionStatus.CLOSED);
        }
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
