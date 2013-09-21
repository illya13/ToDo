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

    public User auth(String id, String password) {
        if (!db.containsKey(id))
            throw notFound(id);
        User user = db.get(id);
        if (!user.getPassword().equals(password))
            throw badUserPassword(id);
        return user;
    }

    public User getUserById(String id) {
        if (!db.containsKey(id))
            throw notFound(id);
        return db.get(id);
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

    public void deleteUserById(String id) {
        if (!db.containsKey(id))
            throw notFound(id);
        db.remove(id);
    }

    private IllegalArgumentException notFound(String id) {
        return new IllegalArgumentException(String.format("User with id=%1$s was not found", id));
    }

    private IllegalArgumentException alreadyExists(String id) {
        return new IllegalArgumentException(String.format("User with id=%1$s already exists", id));
    }

    private IllegalArgumentException badUserPassword(String id) {
        return new IllegalArgumentException(String.format("Bad user id=%1$s or/and password", id));
    }
}
