package toptal.todo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Session {
    @Id
    private String id;

    @DBRef
    private User user;

    private long expire;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public static class Builder {
        public Builder() {
        }

        public Session newSession(User user) {
            Session session = new Session();
            session.setUser(user);
            session.setExpire(System.currentTimeMillis());
            return session;
        }

        public Session newSession(User user, String token) {
            Session session = newSession(user);
            session.setId(token);
            return session;
        }
    }
}
