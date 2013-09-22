package toptal.todo.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Session {
    @Id
    private ObjectId id;

    @DBRef
    @Indexed
    private User user;

    @Indexed
    private long expire;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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
            session.setId(new ObjectId(token));
            return session;
        }
    }
}
