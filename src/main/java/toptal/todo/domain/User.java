package toptal.todo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {
    @Id
    private String nickname;
    private String fullname;
    private String password;

    private User() {
    }

    public String getNickname() {
        return nickname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder {
        private User user;

        public Builder() {
        }

        public Builder newUser(String nickname) {
            user = new User();
            user.nickname = nickname;
            return this;
        }

        public Builder setPassword(String password) {
            user.password = password;
            return this;
        }

        public Builder setFullname(String fullname) {
            user.fullname = fullname;
            return this;
        }

        public User build() {
            return user;
        }
    }
}
