package toptal.todo.service;

import org.springframework.stereotype.Service;
import toptal.todo.domain.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private Map<String, User> db;

    public UserService() {
        db = new HashMap<String, User>();
        initWithRoot();
    }

    private void initWithRoot() {
        User.Builder builder = new User.Builder();
        User root = builder.newUser("root").setPassword("qwerty").
                setFullname("Root").
                build();
        db.put(root.getNickname(), root);
    }

    public User auth(String nickname, String password) {
        if (!db.containsKey(nickname))
            throw notFound(nickname);
        User user = db.get(nickname);
        if (!user.getPassword().equals(password))
            throw badUserPassword(nickname);
        return user;
    }

    public User getUserByNickname(String nickname) {
        if (!db.containsKey(nickname))
            throw notFound(nickname);
        return db.get(nickname);
    }

    public List<User> getAllUsers() {
        List<User> users = new LinkedList<User>(db.values());
        return users;
    }

    public User createUser(User user) {
        if (db.containsKey(user.getNickname()))
            throw alreadyExists(user.getNickname());
        db.put(user.getNickname(), user);
        return user;
    }

    public void deleteUserByNickname(String nickname) {
        if (!db.containsKey(nickname))
            throw notFound(nickname);
        db.remove(nickname);
    }

    private IllegalArgumentException notFound(String nickname) {
        return new IllegalArgumentException(String.format("User with nickname=%1$s was not found", nickname));
    }

    private IllegalArgumentException alreadyExists(String nickname) {
        return new IllegalArgumentException(String.format("User with nickname=%1$s already exists", nickname));
    }

    private IllegalArgumentException badUserPassword(String nickname) {
        return new IllegalArgumentException(String.format("Bad user nickname=%1$s or/and password", nickname));
    }
}
