package toptal.todo.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SessionService {
    public static final int expireMinutes = 5;

    private Map<String, Long> sessions;

    public SessionService() {
        sessions = new HashMap<String, Long>();
    }

    public String generateToken(String id) {
        // TODO: think about of using id
        UUID uuid = UUID.randomUUID();
        sessions.put(uuid.toString(), System.currentTimeMillis());
        return uuid.toString();
    }

    public void extendToken(String token) {
        sessions.put(token, System.currentTimeMillis());
    }

    public void validateToken(String token) {
        if (!sessions.containsKey(token))
            throw notAuth();

        long millis = sessions.get(token);
        if (System.currentTimeMillis() - millis > minutesToMillis(expireMinutes)) {
            invalidateToken(token);
            throw notAuth();
        }
    }

    public void invalidateToken(String token) {
        sessions.remove(token);
    }

    private IllegalArgumentException notAuth() {
        return new IllegalArgumentException(String.format("You need to login first"));
    }

    private long minutesToMillis(int minutes) {
        return minutes * 60 * 1000;
    }
}
