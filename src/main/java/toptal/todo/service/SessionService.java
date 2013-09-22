package toptal.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toptal.todo.domain.Session;
import toptal.todo.domain.User;
import toptal.todo.mongo.SessionRepository;

@Service
public class SessionService {
    @Autowired
    SessionRepository sessionRepository;

    public static final int expireMinutes = 5;

    public SessionService() {
    }

    public String generateToken(User user) {
        Session session = new Session.Builder().newSession(user);
        session = sessionRepository.save(session);
        return session.getId().toString();
    }

    private void extendToken(Session session) {
        session.setExpire(System.currentTimeMillis());
        sessionRepository.save(session);
    }

    public void validateToken(String token) {
        Session session = sessionRepository.findOne(token);
        if (session == null)
            throw notAuth();

        long millis = session.getExpire();
        if (System.currentTimeMillis() - millis > minutesToMillis(expireMinutes)) {
            invalidateToken(token);
            throw notAuth();
        }

        extendToken(session);
    }

    public void invalidateToken(String token) {
        sessionRepository.delete(token);
    }

    private IllegalArgumentException notAuth() {
        return new IllegalArgumentException(String.format("You need to login first"));
    }

    private long minutesToMillis(int minutes) {
        return minutes * 60 * 1000;
    }
}
